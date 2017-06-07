package com.niit.collaboration.rest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.niit.Collaboration.model.User;
@RestController
public class FileUploadController {

	@Autowired
	private FileUploadController fileUploadDao;
        
	private User user;
	
	
	@ResponseBody
	@RequestMapping(value = "/duUpload", method = RequestMethod.POST)
	public String postFile(@RequestParam(value="file", required=false) MultipartFile file,
	                       @RequestParam(value="data") Object data) throws Exception {
		MultipartFile aFile = file;
         
         System.out.println("Saving file: " + aFile.getName());
	    return "OK!";
	}
	
	
}

