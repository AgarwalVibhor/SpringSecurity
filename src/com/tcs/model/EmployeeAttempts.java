package com.tcs.model;

import java.util.Date;

public class EmployeeAttempts {
	

	
	private int id;
	private String username;
	private int attempts;
	private Date lastModified;
	
	public EmployeeAttempts() {
		super();
	}
		
	public EmployeeAttempts(int id, String username, int attempts,
			Date lastModified) {
		super();
		this.id = id;
		this.username = username;
		this.attempts = attempts;
		this.lastModified = lastModified;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getAttempts() {
		return attempts;
	}
	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	public Date getLastModified() {
		return lastModified;
	}
	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	

}
