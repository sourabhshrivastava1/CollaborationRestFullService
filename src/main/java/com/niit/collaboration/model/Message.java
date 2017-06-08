package com.niit.collaboration.model;

public class Message {

	private String message;

	private long id;
	public Message(Long id, String message) {
		this.id = id;
		this.message = message;
	}
	public Message() {
	
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	

	
}
