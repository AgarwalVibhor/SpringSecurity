package com.tcs.handler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.tcs.dao.LoginDaoInterface;
import com.tcs.model.EmployeeAttempts;

public class LoginAuthenticationProvider extends DaoAuthenticationProvider {
	
	@Autowired
	private LoginDaoInterface dao;
	
	@Autowired
	@Override
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		
		super.setUserDetailsService(userDetailsService);
	}
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		try
		{
			Authentication auth = super.authenticate(authentication);
			dao.resetFailAttempts(authentication.getName());
			return auth;
		}
		catch(BadCredentialsException exception)
		{
			System.out.println("Bad Credentials here...");
			dao.updateFailAttempts(authentication.getName());
			throw new BadCredentialsException("Invalid Username and Password !!");
		}
		catch(LockedException exception)
		{
			String error = "";
			EmployeeAttempts employeeAttempts = dao.getEmployeeAttempts(authentication.getName());
			if(employeeAttempts != null)
			{
				Date lastModified = employeeAttempts.getLastModified();
				error = "Employee Account is locked !! <br><br> Username : " + authentication.getName() + 
						" <br><br> Last Modified : " + lastModified;
			}
			else
			{
				error = exception.getMessage();
			}
			throw new LockedException(error);
		}
	}

}
