package com.himanshu.jobms.job.Impl;

import com.himanshu.jobms.job.Job;
import com.himanshu.jobms.job.JobRepository;
import com.himanshu.jobms.job.JobService;
import com.himanshu.jobms.job.client.CompanyClient;
import com.himanshu.jobms.job.client.ReviewClient;
import com.himanshu.jobms.job.dto.JobDTO;
import com.himanshu.jobms.job.external.Company;
import com.himanshu.jobms.job.external.Review;
import com.himanshu.jobms.job.mapper.JobMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {
    JobRepository jobRepository;

    @Autowired
    RestTemplate restTemplate;

    int attempt=0;

    private CompanyClient companyClient;
    private ReviewClient reviewClient;
    public JobServiceImpl(JobRepository jobRepository,CompanyClient companyClient,ReviewClient reviewClient) {
        this.jobRepository = jobRepository;
        this.companyClient=companyClient;
        this.reviewClient=reviewClient;
    }
    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }
    @Override
//    @CircuitBreaker(name = "companyBreaker",fallbackMethod ="companyBreakerFallBack" )
//    @Retry(name="companyBreaker",fallbackMethod = "companyBreakerFallBack")
    @RateLimiter(name="companyBreaker",fallbackMethod = "companyBreakerFallBack")
    public List<JobDTO> findAllJob() {
        System.out.println("Attempt:  "+ ++attempt);
        List<Job>jobs= jobRepository.findAll();
        List<JobDTO> jobDTOS = new ArrayList<>();
       return jobs.stream().map(this::ConvertTODto).collect(Collectors.toList());
    }

    public List<String> companyBreakerFallBack(Exception e){
        List<String> list= new ArrayList<>();
        list.add("Dummy");
        return  list;
    }
    private JobDTO ConvertTODto(Job job){
        Company company=companyClient.getCompany(job.getCompanyId());
        List<Review>reviews=reviewClient.getReviews(job.getCompanyId());
        JobDTO jobDTO = JobMapper.mapToJobWithCompanyDto(job,company,reviews);
//        jobDTO.setCompany(company);
        return jobDTO;
    }

    @Override
    public JobDTO getById(Long id) {
        Job job= jobRepository.findById(id).orElse(null);
        return ConvertTODto(job);
    }
    @Override
    public boolean DeleteJob(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        }
        catch (Exception e){
            return  false;
        }
    }
    @Override
    public boolean UpdateJob(Long id,Job updatedJob) {
        Optional<Job> jobOptional=jobRepository.findById(id);
        if(jobOptional.isPresent()){
            Job job= jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setLocation(updatedJob.getLocation());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setMinSalary(updatedJob.getMinSalary());
            jobRepository.save(job);
            return true;
        }
        return false;
    }
}
