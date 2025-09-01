package com.himanshu.jobms.job;

import com.himanshu.jobms.job.dto.JobDTO;

import java.util.List;

public interface JobService {

    void createJob(Job job) ;
    List<JobDTO> findAllJob();
    JobDTO getById(Long id);
    boolean DeleteJob(Long id);
    boolean UpdateJob(Long id, Job updateJob);

}
