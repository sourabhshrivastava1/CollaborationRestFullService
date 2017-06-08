package com.niit.collaboration.rest.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.Collaboration.DAO.FriendDAO;
import com.niit.Collaboration.DAO.UserDAO;
import com.niit.Collaboration.model.Friend;
import com.niit.Collaboration.model.User;

@RestController
public class FriendController {

	@Autowired
	private HttpSession session;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private User user;

	private static final Logger logger = LoggerFactory.getLogger(FriendController.class);
	@Autowired
	private Friend friend;

	@Autowired
	private FriendDAO friendDAO;

	// Need to change it as RequestMapping and ID should take from session
	@GetMapping("/friends/{id}")
	public ResponseEntity<List<Friend>> getMyFriend(@PathVariable("id") String id) {

		List<Friend> friend = friendDAO.listMyFriend(id);
		return new ResponseEntity<List<Friend>>(friend, HttpStatus.OK);

	}

	@RequestMapping(value = "/addFriend/{friendID}", method = RequestMethod.GET)
	public ResponseEntity<Friend> sendFriendRequest(@PathVariable("friendID") String friendID) {
		logger.debug("->->->->calling method sendFriendRequest");
		// String loggedInUserID = (String)
		// httpSession.getAttribute("loggedInUserID");
		user = (User) session.getAttribute("user");
		String logged_user_id = user.getId();
		friend.setUser_id(user.getId());
		friend.setFriend_id(friendID);
		friend.setStatus('R'); // N - New, R->Rejected, A->Accepted

		// Is the user already sent the request previous?

		// check whether the friend exist in user table or not
		if (isUserExist(friendID) == false) {
			friend.setErrorCode("404");
			friend.setErrorMessage("No user exist with the id :" + friendID);
		}

		else if (friendDAO.get(friendID, logged_user_id) != null) {
			logger.debug("Enter into Friend Request already send");
			friend.setErrorCode("404");
			friend.setErrorMessage("You already sent the friend request to " + friendID);

		} else {
			friendDAO.save(friend);

			friend.setErrorCode("200");
			friend.setErrorMessage("Friend request successfull.." + friendID);
		}

		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}

	private boolean isUserExist(String id) {
		if (userDAO.get(id) == null)
			return false;
		else
			return true;
	}

	@RequestMapping(value = "/getMyFriendRequests/", method = RequestMethod.GET)
	public ResponseEntity<List<Friend>> getMyFriendRequests() {
		logger.debug("->->->->calling method getMyFriendRequests");
		user = (User) session.getAttribute("user");
		String loggedInUserID = user.getId();
		List<Friend> myFriendRequests = friendDAO.getNewFriendRequests(loggedInUserID);

		return new ResponseEntity<List<Friend>>(myFriendRequests, HttpStatus.OK);

	}

	@RequestMapping(value = "/myFriends", method = RequestMethod.GET)
	public ResponseEntity<List<Friend>> getMyFriends() {
		logger.debug("->->->->calling method getMyFriends");
		user = (User) session.getAttribute("user");
		String loggedInUserID = user.getId();
		List<Friend> myFriends = new ArrayList<Friend>();
		if (loggedInUserID == null) {
			friend.setErrorCode("404");
			friend.setErrorMessage("Please login to know your friends");
			myFriends.add(friend);
			return new ResponseEntity<List<Friend>>(myFriends, HttpStatus.OK);

		}

		logger.debug("getting friends of : " + loggedInUserID);
		myFriends = friendDAO.listMyFriend(loggedInUserID);
		logger.debug("getting friends size : " + myFriends.size());
		
		if (myFriends.isEmpty()) {
			logger.debug("Friends does not exsit for the user : " + loggedInUserID);
			friend.setErrorCode("404");
			friend.setErrorMessage("You does not have any friends");
			myFriends.add(friend);
		}
		logger.debug("Send the friend list ");
		return new ResponseEntity<List<Friend>>(myFriends, HttpStatus.OK);
	}

	

	@RequestMapping(value = "/accepttFriend/{friendID}", method = RequestMethod.PUT)
	public ResponseEntity<Friend> acceptFriendFriendRequest(@PathVariable("friendID") String friendID) {
		logger.debug("->->->->calling method acceptFriendFriendRequest");
        
		friend = updateRequest(friendID, 'Y');
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	private Friend updateRequest(String friendID, char status) {
		logger.debug("Starting of the method updateRequest");
		user = (User) session.getAttribute("user");
		String loggedInUserID = user.getId();
		logger.debug("loggedInUserID : " + loggedInUserID);
		
		if(isFriendRequestAvailabe(friendID)==false)
		{
			friend.setErrorCode("404");
			friend.setErrorMessage("The request does not exist.  So you can not update to "+status);
		}
		
		
		else
			friend = friendDAO.get(loggedInUserID, friendID);
		friend.setStatus(status); // N - New, R->Rejected, A->Accepted

		friendDAO.update(friend);

		friend.setErrorCode("200");
		friend.setErrorMessage(
				"Request from   " + friend.getUser_id() + " To " + friend.getFriend_id() + " has updated to :" + status);
		logger.debug("Ending of the method updateRequest");
		return friend;

	}
	private boolean isFriendRequestAvailabe(String friendID)
	{
		user = (User) session.getAttribute("user");
		String loggedInUserID = user.getId();
		
		if(friendDAO.get(loggedInUserID,friendID)==null)
			return false;
		else
			return true;
	}
	
	@RequestMapping(value = "/unFriend/{friendID}", method = RequestMethod.PUT)
	public ResponseEntity<Friend> unFriend(@PathVariable("friendID") String friendID) {
		logger.debug("->->->->calling method unFriend");
		updateRequest(friendID, 'U');
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/rejectFriend/{friendID}", method = RequestMethod.PUT)
	public ResponseEntity<Friend> rejectFriendFriendRequest(@PathVariable("friendID") String friendID) {
		logger.debug("->->->->calling method rejectFriendFriendRequest");

		updateRequest(friendID, 'C');
		return new ResponseEntity<Friend>(friend, HttpStatus.OK);

	}
}


/*package com.niit.collaboration.rest.services;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.niit.Collaboration.DAO.FriendDAO;
import com.niit.Collaboration.DAO.UserDAO;
import com.niit.Collaboration.model.Friend;

@RestController
public class FriendController {

//	private static final Logger logger=LoggerFactory.getLogger(FriendController.class);
	
	@Autowired
	FriendDAO friendDAO;
	
	@Autowired
	Friend friend;
	
	@Autowired
	HttpSession session;
	
	@Autowired
	UserDAO userDAO;
	
	private boolean isUserExist(String id)
	{
		if(userDAO.get(id)==null)
			return false;
		else
			return true;
	}
	
	@RequestMapping(value="/myFriends",method=RequestMethod.GET)
	public ResponseEntity<List> getMyFriends(){
		System.out.println("inside myfriends");
		String loggedInUserID=(String)session.getAttribute("loggedInUserID");
		List<Friend> myFriends=new ArrayList<Friend>();
		if(loggedInUserID==null)
		{
			friend.setErrorCode("404");
			friend.setErrorMessage("Please login to know your friend");
			myFriends.add(friend);
			return new ResponseEntity<List>(myFriends,HttpStatus.OK);
		}
		myFriends=friendDAO.getMyFriends(loggedInUserID);
		
		if(myFriends.isEmpty()){
			friend.setErrorCode("404");
			friend.setErrorMessage("you does not have any friends");
			myFriends.add(friend);
		}
		return new ResponseEntity<List>(myFriends,HttpStatus.OK);
	}
	
	@RequestMapping(value="/addFriend/{friendID}",method=RequestMethod.GET)
	public ModelAndView sendFriendRequest(@PathVariable("friendID") String friendID){
	 String loggedInUSerID=(String)session.getAttribute("loggedInUserID");
	 friend.setUserid(loggedInUSerID);
	 friend.setFriendID(friendID);
	 friend.setStatus('N');//N-New,A-Accepted,R-rejected
	 friend.setIsOnline('N');
	 //Is the user already sent the request previous?

	 if(friendDAO.get(loggedInUSerID, friendID)!=null){
		 friend.setErrorCode("404");
		 friend.setErrorMessage("you already sent the friend request");
		 ModelAndView obj=new ModelAndView("MessagePage").addObject("successmessage", "you already sent friend request to "+friendID);;
		 return obj;
	 }else{
		 friendDAO.save(friend);
		 friend.setErrorCode("200");
		 friend.setErrorMessage("friend request successfull..."+friendID);
		 ModelAndView obj=new ModelAndView("MessagePage").addObject("successmessage", "successfully sent friend request to "+friendID);;
		 return obj;
	 }
    }
	
	

	private Friend updateRequest(String friendID,String status){
		String loggedInUserID=(String)session.getAttribute("loggedInUserID");
		
		if(isFriendRequestAvailable(friendID)==false)
		{
			friend.setErrorCode("404");
			friend.setErrorMessage("the request does not exist,So you can not update to"+status);
		}
		if(status.equals("A")||status.equals("R"))
		{
			friend=friendDAO.get(loggedInUserID,friendID);
		System.out.println(friend.getUserid()+":"+friend.getErrorMessage());
		}
		else
		{
			friend=friendDAO.get(loggedInUserID, friendID);
		System.out.println(friend.getErrorCode()+":"+friend.getErrorMessage());
		}
		friend.setStatus(status.charAt(0));//N-new,R-rejected,A-accepted
		System.out.println(friend.getStatus());
		friendDAO.update(friend);
		
		friend.setErrorCode("200");
		friend.setErrorMessage("Request from"+friend.getUserid()+" to"+friend.getFriendID()+"has updated");
        return friend;
	}
	
	private boolean  isFriendRequestAvailable(String friendID)
	{
		String loggedInUserID=(String)session.getAttribute("loggedInUserID");
		if(friendDAO.get(loggedInUserID, friendID)==null)
			return false;
		else
			return true;
	}
	
	@RequestMapping(value="/unFriend/{friendID}",method=RequestMethod.GET)
	public ModelAndView unFriend(@PathVariable("friendID") String friendID){
		updateRequest(friendID,"U");
		return new ModelAndView("MessagePage");
	}

	@RequestMapping(value="/getMyFriendRequests/",method=RequestMethod.GET)
	public ResponseEntity<List<Friend>> getMyFriendRequests(){
		String loggedInUserID=(String)session.getAttribute("loggedInUserID");
		List<Friend> myFriendRequests=friendDAO.getRequestsSendByMe(loggedInUserID);
		return new ResponseEntity<List<Friend>>(myFriendRequests,HttpStatus.OK);
	}
	
	@RequestMapping("/getRequestsSendToMe")
	public ResponseEntity<List<Friend>> getRequestsSendToMe(){
	String loggedInUserID=(String) session.getAttribute("loggedInUserID");
	List<Friend> myFriendRequests=friendDAO.getNewFriendRequests(loggedInUserID);
	System.out.println(myFriendRequests);
	return new ResponseEntity<List<Friend>>(myFriendRequests,HttpStatus.OK);
   }
	
	@RequestMapping(value="/rejectFriend/{friendID}",method=RequestMethod.GET)
	public ModelAndView rejectFriend(@PathVariable("friendID") String friendID){
		updateRequest(friendID,"R");
		return new ModelAndView("MessagePage").addObject("successmessage", "friend request rejected for "+friendID);
		
	}
	
	@RequestMapping(value="/acceptFriend/{friendID}",method=RequestMethod.GET)
	public ModelAndView acceptFriendRequest(@PathVariable("friendID") String friendID){
		friend=updateRequest(friendID,"A");
		return new ModelAndView("MessagePage").addObject("successmessage", "friend request accepted of "+friendID);
	}
}

*/