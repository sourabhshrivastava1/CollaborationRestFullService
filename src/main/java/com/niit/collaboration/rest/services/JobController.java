package com.niit.collaboration.rest.services;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.niit.Collaboration.DAO.JobApplicationDAO;
import com.niit.Collaboration.DAO.JobDAO;
import com.niit.Collaboration.model.Job;
import com.niit.Collaboration.model.JobApplication;
import com.niit.Collaboration.model.User;

@RestController
public class JobController {

	@Autowired
	private Job job;

	@Autowired
	private JobDAO jobDAO;

	@Autowired
	private JobApplicationDAO jobApplicationDAO;

	@Autowired
	private JobApplication jobApplication;

	@Autowired
	private HttpSession session;

	@Autowired
	private User user;

	@RequestMapping("jobs")
	public ResponseEntity<List<Job>> getAllJob() {

		List<Job> jobList = jobDAO.list();
		return new ResponseEntity<List<Job>>(jobList, HttpStatus.OK);
	}

	@RequestMapping("job/apply")
	public ResponseEntity<JobApplication> applyJob(@RequestBody JobApplication jobApplication) {
		user = (User) session.getAttribute("user");
		//jobApplication.setJob_id("jmn00");
		jobApplication.setUser_id(user.getId());
		jobApplication.setStatus('A');
		
		Long d = System.currentTimeMillis();
		Date today = new Date(d);
		jobApplication.setDate_time(today);
		System.out.println("abcd"+jobApplication.getId());
			if (jobApplicationDAO.save(jobApplication) == true) {
				jobApplication.setErrorCode("200");
				jobApplication.setErrorMessage("Job Applied");
			} else {
				jobApplication.setErrorCode("404");
				jobApplication.setErrorMessage("Job Applied Failed");
			}
			return new ResponseEntity<JobApplication>(jobApplication, HttpStatus.OK);
		
		

	}

	@RequestMapping("applied-jobs")
	public ResponseEntity<List<JobApplication>> getAllAppledJob() {

		List<JobApplication> jobAppledList = jobApplicationDAO.list();
		return new ResponseEntity<List<JobApplication>>(jobAppledList, HttpStatus.OK);
	}

	@GetMapping("job-applied/{user_id}")
	public ResponseEntity<List<JobApplication>> getUserAppliedJob(@PathVariable("user_id") String user_id) {

		List<JobApplication> userAppliedJobs = jobApplicationDAO.getList(user_id);
		return new ResponseEntity<List<JobApplication>>(userAppliedJobs, HttpStatus.OK);
	}

	@PostMapping("job/create")
	public ResponseEntity<Job> createJob(@RequestBody Job job) {
		if (jobDAO.getJobById(job.getId()) == null) {
			if (jobDAO.save(job) == true) {
				job.setErrorCode("200");
				job.setErrorMessage("Job created");
			} else {
				job.setErrorCode("404");
				job.setErrorMessage("Failed to create Job");
			}
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		job.setErrorCode("404");
		job.setErrorMessage("Job exist with this id " + job.getId());
		return new ResponseEntity<Job>(job, HttpStatus.OK);
	}

	@GetMapping("job/{id}")
	public ResponseEntity<Job> getJobById(@PathVariable("id") String id) {
		job = jobDAO.getJobById(id);

		if (job != null) {
			job.setErrorCode("200");
			job.setErrorMessage("Job found");

		} else {
			job = new Job();
			job.setErrorCode("404");
			job.setErrorMessage("Job not found with this id " + id);
		}
		return new ResponseEntity<Job>(job, HttpStatus.OK);
	}

	@RequestMapping("job/update")
	public ResponseEntity<Job> updateJob(@RequestBody Job job) {
		if (jobDAO.getJobById(job.getId()) != null) {
			if (jobDAO.saveOrupdate(job) == true) {
				job.setErrorCode("200");
				job.setErrorMessage("Job Updated");

			} else {
				job.setErrorCode("404");
				job.setErrorMessage("Job Updation Failed");
			}
			return new ResponseEntity<Job>(job, HttpStatus.OK);
		}
		job.setErrorCode("404");
		job.setErrorMessage("Job doesn't exist with this id " + job.getId());
		return new ResponseEntity<Job>(job, HttpStatus.OK);
	}
}