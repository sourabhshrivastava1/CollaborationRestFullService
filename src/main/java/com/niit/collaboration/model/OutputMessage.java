package com.niit.collaboration.model;

import java.util.Date;

public class OutputMessage extends Message {

	private Date time;



	public OutputMessage(Message original,Date date) {
		super(original.getId(), original.getMessage());
		this.time = date;
		
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
