package com.tcs.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
	
	@RequestMapping(value = "/")
	public ModelAndView start(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView model = new ModelAndView("redirect:/hello");
		return model;
	}
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public ModelAndView hello(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView model = new ModelAndView("hello");
		model.addObject("title", "Spring Security - Total Authentication !");
		model.addObject("message", "This page is for both the Users and the Admin guys !!");
		return model;
	}
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public ModelAndView admin(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView model = new ModelAndView("admin");
		model.addObject("title", "Spring Security - Total Authentication !");
		model.addObject("message", "This page is only for the Admin guys !!");
		return model;
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout)
	{
		ModelAndView model = new ModelAndView("login");
		HttpSession session = request.getSession(false);
		System.out.println("Session in login method : " + session);
		System.out.println("Error : " + error);
		System.out.println("Logout : " + logout);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth != null)
		{
			System.out.println("Auth in login method : " + auth.getName());
			if(isRememberMeAuthenticated())
			{
				model.addObject("loginUpdate", true);
				String targetUrl = getTargetUrlFromSession(request);
				if(targetUrl.equals(""))
				{
					
				}
				else
				{
					model.addObject("targetUrl", targetUrl);
				}
			}
		}
		
		if(error != null)
		{
			model.addObject("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
			String targetUrl = getTargetUrlFromSession(request);
			if(targetUrl.equals(""))
			{
				
			}
			else
			{
				model.addObject("loginUpdate", true);
				model.addObject("targetUrl", targetUrl);
			}
		}
		
		if(logout != null)
		{
			model.addObject("logout", "You have logged out successfully !!");
		}
		return model;
	}
	
	private String getErrorMessage(HttpServletRequest request, String key)
	{
		String error = "";
		HttpSession session = request.getSession(false);
		System.out.println("Session in getErrorMessage : " + session);
		Exception exception = (Exception) session.getAttribute(key);
		if(exception instanceof BadCredentialsException)
		{
			error = exception.getMessage();
		}
		else if(exception instanceof LockedException)
		{
			error = exception.getMessage();
		}
		else
		{
			error = "Invalid Username and Password !!";
		}
		return error;
	}
	
	@RequestMapping(value = "/admin/update", method = RequestMethod.GET)
	public ModelAndView update(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView model = null;
		if(isRememberMeAuthenticated())
		{
			saveTargetUrlToSession(request);
			model = new ModelAndView("redirect:/login");
			model.addObject("loginUpdate", true);
		}
		else
		{
			model = new ModelAndView("update");
			model.addObject("title", "Spring Security - Total Authentication");
			model.addObject("message", "This update page can be accessed only when the user is logged in through" +
					" username and password.");
		}
		return model;
	}
	
	private boolean isRememberMeAuthenticated()
	{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication == null)
		{
			return false;
		}
		return RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
	}
	
	private void saveTargetUrlToSession(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		System.out.println("Session in save method : " + session);
		session.setAttribute("targetUrl", "/admin/update");
		System.out.println("Target Url saved in session : " + session.getAttribute("targetUrl"));
	}
	
	private String getTargetUrlFromSession(HttpServletRequest request)
	{
		String targetUrl = "";
		HttpSession session = request.getSession(false);
		System.out.println("Session in get method : " + session);
		Object t = session.getAttribute("targetUrl");
		if(t == null)
		{
			targetUrl = "";
		}
		else
		{
			targetUrl = t.toString();
		}
		return targetUrl;
	}
	
	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public ModelAndView error(HttpServletRequest request, HttpServletResponse response)
	{
		HttpSession session = request.getSession();
		System.out.println("Session in error method : " + session);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserDetails user = (UserDetails) auth.getPrincipal();
		String username = user.getUsername();
		ModelAndView model = new ModelAndView("403");
		model.addObject("username", username);
		return model;
	}

}
