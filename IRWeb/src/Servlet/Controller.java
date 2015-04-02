package Servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Controller
 */

public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if(action.equals("Search")){
			String queryString = request.getParameter("query");
			String queryTypeString = request.getParameter("queryType");

			String target = "";
			String jspTarget = "";
			
			switch(queryTypeString){
			case "default": target = "/processquery";
				jspTarget="resultSection.jsp";
				break;
			case "image": target = "/processimagequery";
				jspTarget="resultImageSection.jsp";
				break;
			}
			
			request.setAttribute("query", queryString);
			request.setAttribute("queryDisplayTarget", jspTarget);
			RequestDispatcher rd = getServletContext().getRequestDispatcher(target);
			rd.forward(request, response);
		}else if(action.equals("Index")){
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/index.jsp");
			rd.forward(request, response);
		}
		
		
		
		
	}

}
