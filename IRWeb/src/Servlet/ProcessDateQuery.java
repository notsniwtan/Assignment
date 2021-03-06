package Servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class ProcessDateQuery extends HttpServlet{
	SolrServer server = null;
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String queryString = (String) request.getAttribute("query");
		String queryTypeString = (String) request.getAttribute("queryDisplayTarget");
		String queryCountryString = (String) request.getAttribute("queryCountry");
		
		try {
			//setup connection
			setUpServer("http://localhost:8983/solr/");
			
			SolrQuery parameter = new SolrQuery();
			
			//sets the query string
			parameter.set("q", queryString);
			
			//uses the /select request handler in solrconfig.xml
			parameter.set("qt", "/date");
			parameter.set("sort", "date desc");
			String fq = "country:\""+queryCountryString+"\"";
			if (!(queryCountryString==(null)))
			{
			if (queryCountryString.equals("United States") ||
					queryCountryString.equals("United Kingdom") ||
					queryCountryString.equals("Ireland") || 
					queryCountryString.equals("Deutschland") || 
					queryCountryString.equals("France") ||
					queryCountryString.equals("Schweiz") ||
					queryCountryString.equals("Italia"))
				parameter.set("fq", fq);
			}
			//set to return relevancy score, use .score to retrieve value
			parameter.setIncludeScore(true);
			QueryResponse res = server.query(parameter);
			
			SolrDocumentList resultList = res.getResults();
			//check if its empty
			if(resultList.size() == 0){
				request.setAttribute("listEmpty", true);
			}
			else{
				request.setAttribute("resultList", resultList);
			}
			request.setAttribute("timeTaken", res.getElapsedTime());
			request.setAttribute("numHits", resultList.getNumFound());
			request.setAttribute("query", queryString);
			request.setAttribute("queryDisplayTarget", queryTypeString);
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/resultMain.jsp");
			rd.forward(request, response);
			
		} catch (SolrServerException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public void setUpServer(String url){	
		server = new HttpSolrServer(url);
	}
}
