package ftn.UppProject.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	
	public static String loggedUserId;
	
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public String login() {
		return "redirect:http://localhost:9000/login";
	}
	
	@RequestMapping(value="/submitLogin", method = RequestMethod.GET)
	public String submitLogin() {
		loggedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

		System.out.println("+++++Ulogovani user je: " + loggedUserId);
		
		return "redirect:http://localhost:9000/goToClientHomePage";
	}
	
}