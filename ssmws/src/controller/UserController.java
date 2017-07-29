package controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import service.RelationshipService;


@Controller
public class UserController {
	@Autowired
	RelationshipService relationshipService;
	
	@RequestMapping("/login")
	public String login(String username,HttpServletResponse response,HttpSession session,HttpServletRequest request) throws IOException{
		request.setAttribute("username", username);
		request.setAttribute("list",relationshipService.selectUserListbyUserA(username));
		return "index1.jsp";
	}
	
}
