package com.tcs.dao;

import com.tcs.model.EmployeeAttempts;

public interface LoginDaoInterface {
	
	public void updateFailAttempts(String username);
	
	public void resetFailAttempts(String username);
	
	public EmployeeAttempts getEmployeeAttempts(String username);

}
