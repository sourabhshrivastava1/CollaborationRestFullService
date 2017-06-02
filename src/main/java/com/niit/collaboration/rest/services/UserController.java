

package com.niit.collaboration.rest.services;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.niit.Collaboration.DAO.UserDAO;
import com.niit.Collaboration.model.User;

@RestController
public class UserController {

	@Autowired
	private User user;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private HttpSession session;
	
	private Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/hello")
	public String sayHello()
	{
		return "  Hello from User rest service Modifed message";
	}

	@GetMapping("/user/logout")
	public void logoutUser(){
		session.invalidate();
	}

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public ResponseEntity<List<User>> getAllUser() {
		List<User> userList = userDAO.list();
		user = (User) session.getAttribute("user");
		System.out.println("given id is "+user.getId());
		return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
	}

	/*@GetMapping("/user/{id}")
	public ResponseEntity<User> getUserByID(@PathVariable("id") String id) {

		user = userDAO.get(id);

		if (user == null) {
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist with the id :" + id);
		} else {
			user.setErrorCode("200");
			user.setErrorMessage("success");
		}

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}*/
	@GetMapping("/user/{id}")
	public ResponseEntity<User> getUser(@PathVariable("id") String id)
	{
		log.debug("**************Starting of the method getUserByID");
		log.info("***************Trying to get userdetails of the id " + id);
		user = userDAO.get(id);
		
		if(user==null)
		{
			user = new User();
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist with the id :" + id);
		}
		else
		{
			user.setErrorCode("200");
			user.setErrorMessage("success");
		}
		
		log.info("**************** Name of teh user is " + user.getName());
		log.debug("**************Ending of the method getUserByID");
	  return	new ResponseEntity<User>(user , HttpStatus.OK);
	}
	

	@PutMapping(value = "/user/update/")
	public ResponseEntity<User> updateUser(@RequestBody User user) {

		if (userDAO.get(user.getId()) == null) {

			user = new User(); // ?
			user.setErrorCode("404");
			user.setErrorMessage("User does not exist with id " + user.getId());
			return new ResponseEntity<User>(user, HttpStatus.OK);
		}

		userDAO.update(user);

		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/validate/", method = RequestMethod.POST)
	public ResponseEntity<User> validateUser(@RequestBody User user) {
		user = userDAO.isValidCredentials(user.getId(), user.getPassword());
		if (user != null) {
			user.setErrorCode("200");
			user.setErrorMessage("You have successfully logged in.");
			session.setAttribute("user", user);
		} else {
			user = new User(); // Do wee need to create new user?
			user.setErrorCode("404");
			user.setErrorMessage("Invalid Credentials.  Please enter valid credentials");
		}
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

/*
	@GetMapping("/validate/{id}/{password}")
	public User validateCredentials(@PathVariable("id") String id, @PathVariable("password") String pwd )
	{
		
		 if (userDAO.isValidCredentials(id, pwd) )
		 {
			user =  userDAO.get(id);
			 user.setErrorCode("200");
			 user.setErrorMessage("Valid credentials");
		 }
		 else
		 {
			 user.setErrorCode("404");
			 user.setErrorMessage("Invalid credentials");
		 }
		 
		 return user;
		
	}*/

	@RequestMapping(value = "/user/", method = RequestMethod.POST)
	public ResponseEntity<User> createUser(@RequestBody User user) {

		if (userDAO.get(user.getId()) == null) {

			if (userDAO.save(user) == true) {
				user.setErrorCode("200");
				user.setErrorMessage(
						"Thank you  for registration. You have successfully registered as " + user.getRole());
			} else {
				user.setErrorCode("404");
				user.setErrorMessage("Could not complete the operatin please contact Admin");

			}

			return new ResponseEntity<User>(user, HttpStatus.OK);
		}

		user.setErrorCode("404");
		user.setErrorMessage("User already exist with id : " + user.getId());
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
}
