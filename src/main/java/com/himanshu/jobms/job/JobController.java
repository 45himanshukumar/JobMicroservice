package com.himanshu.jobms.job;

import com.himanshu.jobms.job.dto.JobDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<List<JobDTO>> findAll(){
        return new ResponseEntity<>(jobService.findAllJob(), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<String> createJob(@RequestBody Job job){
        jobService.createJob(job);
        return new ResponseEntity<>("job created successfully", HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getById(@PathVariable Long id){
        JobDTO jobDTO =  jobService.getById(id);
        if(jobDTO !=null) {
            return new ResponseEntity<>(jobDTO, HttpStatus.OK);
        }
        else{
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> DeleteJob(@PathVariable Long id){
        boolean deleted = jobService.DeleteJob(id);
        if (deleted) {
            return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Job not found or could not be deleted", HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> UpdateJob(@PathVariable Long id,@RequestBody Job job) {
        boolean updated = jobService.UpdateJob(id, job);
        if (updated) {
            return  new ResponseEntity<>("job update successfully" +
                    "",HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
