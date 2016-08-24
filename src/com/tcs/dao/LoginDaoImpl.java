package com.tcs.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Repository;

import com.tcs.model.EmployeeAttempts;

@Repository
public class LoginDaoImpl extends JdbcDaoSupport implements LoginDaoInterface {
	
	@Autowired
	private DataSource dataSource;
	
	@PostConstruct
	public void initialize()
	{
		setDataSource(dataSource);
		/*
		 *  JdbcDaoSupport requires DataSource to be set providing JdbcTemplate through getJdbcTemplate() method.
		 */
	}
	
	public static final String GET_EMPLOYEE = "select count(*) from employees_table where username = ?";
	public static final String GET_EMPLOYEE_ATTEMPTS = "select * from employee_attempts_table where username = ?";
	public static final String INSERT_FAIL_ATTEMPTS = "insert into employee_attempts_table (username, attempts, lastModified)" +
			" values (?,?,?)";
	public static final String UPDATE_FAIL_ATTEMPTS = "update employee_attempts_table " +
			"set attempts = attempts + 1, lastModified = ? where username = ?";
	public static final int MAX_ATTEMPTS = 3;
	public static final String LOCK_ACCOUNT = "update employees_table " +
			"set accountNonLocked = ? where username = ?";
	public static final String RESET_FAIL_ATTEMPTS = "update employee_attempts_table " +
			"set attempts = ?, lastModified = null where username = ?";
	
	@Override
	public void updateFailAttempts(String username) {
		
		if(isExistsUsername(username))
		{
			EmployeeAttempts employeeAttempts = getEmployeeAttempts(username);
			if(employeeAttempts == null)
			{
				getJdbcTemplate().update(INSERT_FAIL_ATTEMPTS, new Object[]{username, 1, new Date()});
			}
			else
			{
				int attempts = employeeAttempts.getAttempts();
				if(attempts < MAX_ATTEMPTS)
				{
					getJdbcTemplate().update(UPDATE_FAIL_ATTEMPTS, new Object[]{new Date(), username});
				}
				else
				{
					getJdbcTemplate().update(LOCK_ACCOUNT, new Object[]{false, username});
					throw new LockedException("Employee Account is Locked !!");
				}
			}
		}
		else
		{
			System.out.println("Inside updateFailAttempts() method.");
			throw new BadCredentialsException("Invalid Username and Password !!");
		}
		

	}

	@Override
	public void resetFailAttempts(String username) {
		
		if(isExistsUsername(username))
		{
			getJdbcTemplate().update(RESET_FAIL_ATTEMPTS, new Object[]{0, username});
		}
		else
		{
			System.out.println("Inside resetFailAttempts() method.");
			throw new BadCredentialsException("Invalid Username and Password !!");
		}
		

	}

	@Override
	public EmployeeAttempts getEmployeeAttempts(String username) {
		
		try
		{
			return getJdbcTemplate().queryForObject(GET_EMPLOYEE_ATTEMPTS, new Object[]{username}, new RowMapper<EmployeeAttempts>(){
				@Override
				public EmployeeAttempts mapRow(ResultSet rs, int rowNum) throws SQLException {
					
					EmployeeAttempts employeeAttempts = new EmployeeAttempts();
					employeeAttempts.setId(rs.getInt("id"));
					employeeAttempts.setUsername(rs.getString("username"));
					employeeAttempts.setAttempts(rs.getInt("attempts"));
					employeeAttempts.setLastModified(rs.getDate("lastModified"));
					return employeeAttempts;
				}
			});
		}
		catch(EmptyResultDataAccessException exception)
		{
			System.out.println("Exception while getting EmployeeAttempts");
			System.out.println("No record in employee_attempts_table with username : " + username);
			return null;
		}
		
	}
	
	
	private boolean isExistsUsername(String username)
	{
		int count = 0;
		count = getJdbcTemplate().queryForObject(GET_EMPLOYEE, new Object[]{username}, Integer.class);
		if(count > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}
