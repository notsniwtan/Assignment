package Servlet;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.tartarus.snowball.ext.porterStemmer;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Stopwords;
import weka.core.tokenizers.WordTokenizer;



public class Index extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Stopwords stopWords;	
	static AdaBoostM1 adaBoost;
	static Instances instances;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String indexTerm = request.getParameter("indexTerm");
		
		//setup the attributes for the instance
		// Create the attributes, class and text
		FastVector fvNominalVal = new FastVector(4);
		fvNominalVal.addElement("Politics");
		fvNominalVal.addElement("Technology");
		fvNominalVal.addElement("Economy");
		fvNominalVal.addElement("Social");
		Attribute classAttr = new Attribute("class", fvNominalVal);
		Attribute idAttr = new Attribute("id");
		Attribute contentAttr = new Attribute("content",(FastVector) null);
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(3);
		fvWekaAttributes.addElement(classAttr);
		fvWekaAttributes.addElement(idAttr);
		fvWekaAttributes.addElement(contentAttr);
		instances = new Instances("classify", fvWekaAttributes, 1);
		instances.setClassIndex(0);
		
		Twitter twitter = setup();
		try{
		HttpSolrServer server = new HttpSolrServer("http://localhost:8983/solr/");
		Query query = new Query(indexTerm);
		//retrieve only english tweets
		query.setLang("en");
		//set number of tweets to retrieve per requests
		query.setCount(100);
		QueryResult result;
		List<SolrInputDocument> documentList = new ArrayList<SolrInputDocument>();
		//int count = 0;
		stopWords = new Stopwords();
		stopWords.clear();
		stopWords.read("stopwords.txt");
		
		loadModel("trainedClassifier.dat");
		System.out.println(adaBoost.getTechnicalInformation());
		Instance current = new DenseInstance(3);
		current.setDataset(instances);
		do{
			result = twitter.search(query);
			List<Status> tweets = result.getTweets();
			
			for (Status tweet : tweets) {
				SolrInputDocument doc = new SolrInputDocument();
		
				doc.addField("id", String.valueOf(tweet.getId()));
				System.out.println(String.valueOf(tweet.getId()));
				doc.addField("author", (String)tweet.getUser().getScreenName());
				doc.addField("authorProfilePic", tweet.getUser().getProfileImageURL());
				doc.addField("date", parseDate(tweet.getCreatedAt()));
				doc.addField("favcount", tweet.getFavoriteCount());
				doc.addField("retweetcount", tweet.getRetweetCount());
				
				ArrayList hashList = new ArrayList();
				for(HashtagEntity hte : tweet.getHashtagEntities()){
					hashList.add(hte.getText());
				}
				doc.addField("hashtag", hashList);
				
				
				for(MediaEntity me:tweet.getMediaEntities()){
					if(me.getType().equals("photo")){
						//tweet photo
						doc.addField("image", me.getMediaURL());
						doc.addField("hasImage","true");
					}
				}
				
				String content = tweet.getText();
				int time = 0;
				for(URLEntity ue:tweet.getURLEntities()){
					if(content.contains((CharSequence) ue.getText())){
						content = content.replace((CharSequence) ue.getText(), "");
						System.out.println("URL: "+ue.getText());
					}
					time++;
					if(time == tweet.getURLEntities().length){
						//tweet url
						doc.addField("url", ue.getText());
					}
				}
				//tweet content
				doc.addField("content", content);

				
				if(tweet.getPlace()!=null){
					//tweet country name
					doc.addField("country", tweet.getPlace().getCountry());
					if(tweet.getGeoLocation()!=null){
					//tweet geolocation
						doc.addField("geolong", tweet.getGeoLocation().getLongitude());
						doc.addField("geolang", tweet.getGeoLocation().getLatitude());
					}
				}
				doc.addField("popcount", tweet.getFavoriteCount() + tweet.getRetweetCount());
				
				current.setValue(idAttr,(long)tweet.getId());
				current.setValue(contentAttr, preprocess(content));
				current.setClassMissing();
				System.out.println("String: "+preprocess(content));

				String category = retrieveCategory(current);
				System.out.println("Cat: "+category);

				doc.addField("category",category);
				
				documentList.add(doc);
				/*count++;
				if(count > 5) break;*/
			}
			
		}
		while ((query = result.nextQuery()) != null /*&& count < 5*/);
		server.add(documentList);
		server.commit();
		System.out.println("successfully indexed.");
		//RequestDispatcher rd = getServletContext().getRequestDispatcher("/home.jsp");
		//rd.forward(request, response);
		response.sendRedirect("/IRWeb/home.jsp");
		
		}catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to search tweets: " + te.getMessage());
			/*RequestDispatcher rd = getServletContext().getRequestDispatcher("/home.jsp");
			rd.forward(request, response);*/
			response.sendRedirect("/IRWeb/home.jsp");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IO Exception");
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}
	
	public Twitter setup(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("eE0Qf9NAH0nF1PdH24btvY6E0")
		  .setOAuthConsumerSecret("ilkdzUhMPtvDzAQbjJYElcHlo1u91pGfUz6ey5849XwEwstewk")
		  .setOAuthAccessToken("934520347-b53ZuwASVjG4I2Ck99DHhM8bouzjWgnX4MH8MxQB")
		  .setOAuthAccessTokenSecret("2YzoKIOLJGHFy5vqLypTOkN4q0CqGmkl9npWkw0n0q0UW");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		return twitter;
	}
	
	public static String preprocess(String content){
		porterStemmer stem = new porterStemmer();
		// Preprocess to remove
		// (1) /n
		// (2) /r
		// (3) comma
		// (4) singleQuote
		// (5) toLowerCase()
		// (6) http				
		// (7) whitespace
		content = content.replace("\\n", " ");
		content = content.replace("\r", "");
		content = content.replace(",", "");
		content = content.replace("'", "");				
		content = content.toLowerCase();
		content = content.replaceAll("http.*", "");
		// Preprocessing for anything besides whitespace and alphabets
		String pattern = "[^a-zA-Z\\s@]";
		content = content.replaceAll(pattern, "");
		// Preprocessing for @users
		String userPattern = "@[a-zA-Z0-9]*";
		content = content.replaceAll(userPattern, "");
		content = content.trim();
		
		
		WordTokenizer t = new WordTokenizer();
		t.tokenize(content);
		StringBuffer sb = new StringBuffer();
		while(t.hasMoreElements()){
			String s = (String) t.nextElement();
			if(stopWords.is(s)){
				continue;
			}
			stem.setCurrent(s);
			stem.stem();
			s=stem.getCurrent();
			sb.append(s+ " ");
		}
		
		content = sb.toString();
		return content = content.trim();
	}
	
	public String parseDate(Date d){
		//date into solr format
		StringBuilder dateBuilder = new StringBuilder();
		dateBuilder.append(d.getYear()+1900);
		dateBuilder.append("-");
		if(Integer.toString(d.getMonth()).length() == 1){
			dateBuilder.append("0"+d.getMonth());
		}
		else{
			dateBuilder.append(d.getMonth());
		}
		dateBuilder.append("-");
		if(Integer.toString(d.getDay()).length() == 1){
			dateBuilder.append("0"+d.getDay());
		}
		else{
			dateBuilder.append(d.getDay());
		}
		dateBuilder.append("T");
		dateBuilder.append(d.getHours());
		dateBuilder.append(":");
		dateBuilder.append(d.getMinutes());
		dateBuilder.append(":");
		dateBuilder.append(d.getSeconds());
		dateBuilder.append("Z");
		String date = dateBuilder.toString();
		return date;
	}
	
	public static String retrieveCategory(Instance instance) throws Exception{
		double pred = adaBoost.classifyInstance(instance);
		return instances.classAttribute().value((int) pred);
	}
	
	public static void loadModel(String fileName) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
			Object tmp = in.readObject();
			adaBoost = (AdaBoostM1) tmp;
			in.close();
			System.out.println("===== Loaded TrainedClassifier: " + fileName + " =====");
		}
		catch (Exception e) {
			// Given the cast, a ClassNotFoundException must be caught along with the IOException
			System.out.println("Problem found when reading: " + fileName);
		}
	}
	

}
