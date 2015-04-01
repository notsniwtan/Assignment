import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import twitter4j.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
	public final static int MAX_NUM_TWEETS = 12000;
	public final static String OUTPUT_FILE = "results/corpus.txt";
	
	public static void main(String args[]) throws TwitterException{
		
		//setup the authentication details for api
		Twitter twitter = AuthSetup.setup();
		
		//crawl tweets in twitter that contains the word
		//crawl(twitter,"European Union");
		print();
	}
	
	public static void print(){
		JSONParser parser = new JSONParser();
		int count = 0;
        
		try {
 
            Object obj = parser.parse(new FileReader(
                    OUTPUT_FILE));
 
            JSONObject jsonObject = (JSONObject) obj;
 
            String name = (String) jsonObject.get("Name");
            JSONArray documentList = (JSONArray) jsonObject.get("Documents");
            
            Iterator<JSONObject> iterator = documentList.iterator();
            while(iterator.hasNext()){
            	JSONObject innerObj = (JSONObject) iterator.next();
            	System.out.println("ID: " + innerObj.get("Id"));
            	System.out.println("Content: " + innerObj.get("Content"));
            	System.out.println("Author: " + innerObj.get("Author"));
            	System.out.println("Date: " + innerObj.get("Date"));
            	System.out.println("Fav Counts: " + innerObj.get("Fav Counts"));
            	
            	JSONArray ja = (JSONArray)innerObj.get("Hashtags");
            	if(ja != null){
	            	for(Object o:ja){
	            		String s= (String)o;
	            		System.out.println(s);
	            	}
            	}
            }
            
        }  catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println("IO Exception");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void crawl(Twitter twitter, String queryWord){
		JSONObject corpus = new JSONObject();
		//document as tweet
		JSONObject document;
		//array to store documents(tweets)
		JSONArray documentList = new JSONArray();
		int count = 0;
		
		corpus.put("Name","Tweets as Documents");
		System.out.println("Starting to crawl..");
		
		try {
			Query query = new Query(queryWord);
			//retrieve only english tweets
			query.setLang("en");
			//set number of tweets to retrieve per requests
			query.setCount(100);
			
			QueryResult result;
			FileWriter file = new FileWriter(OUTPUT_FILE);
			
			do {
				result = twitter.search(query);
				List<Status> tweets = result.getTweets();
				for (Status tweet : tweets) {
					//place the tweets as json object
					document = new JSONObject();
					
					//if tweet is a RT, skip as it could be a a duplicate
					if(tweet.isRetweet()){
						continue;
					}
					
					System.out.println("==========================");
					//tweet id
					document.put("Id",tweet.getId());
					System.out.println("ID: "+tweet.getId());
					//tweet author name
					document.put("Author",tweet.getUser().getScreenName());
					System.out.println("Author: "+tweet.getUser().getScreenName());
					//tweet created date
					document.put("Date",parseDate((Date)tweet.getCreatedAt()));
					System.out.println("Date: "+tweet.getCreatedAt());
					
					//tweet fav count
					document.put("Fav Counts",tweet.getFavoriteCount());
					System.out.println("Fav Count: "+tweet.getFavoriteCount());
					//tweet retweet count
					document.put("Retweet Counts",tweet.getRetweetCount());
					System.out.println("Retweet Count: "+tweet.getRetweetCount());
					//tweet hashtag
					JSONArray jArray = new JSONArray();					
					for(HashtagEntity hte : tweet.getHashtagEntities()){
						jArray.add(hte.getText());
						System.out.println("Hashtag: " + hte.getText());
					}
					document.put("Hashtags", jArray);
					
	
					for(MediaEntity me:tweet.getMediaEntities()){
						if(me.getType().equals("photo")){
							//tweet photo
							document.put("Photo", me.getURL());
							System.out.println("Photo: "+me.getURL());
						}
					}
					
					//tweet user profile pic
					document.put("User Profile Pic",tweet.getUser().getProfileImageURL());;
					System.out.println("Profile URL: "+tweet.getUser().getProfileImageURL());
					
					
					for(URLEntity ue:tweet.getURLEntities()){
						if(tweet.getText().contains((CharSequence) ue.getText())){
							//tweet content
							document.put("Content",tweet.getText().replace((CharSequence) ue.getText(), ""));
							System.out.println("Content: "+tweet.getText().replace((CharSequence) ue.getText(), ""));
							//tweet url
							document.put("URL", ue.getText());
							System.out.println("URL: "+ue.getText());
						}
						else{
							document.put("Content", tweet.getText());
							System.out.println("Content: "+tweet.getText());
						}
					}
					
					if(tweet.getPlace()!=null){
						//tweet country name
						document.put("Country", tweet.getPlace().getCountry());
						System.out.println("Country: "+tweet.getPlace().getCountry());
						//tweet geolocation
						document.put("Geolang", tweet.getGeoLocation().getLatitude());
						document.put("Geolong", tweet.getGeoLocation().getLongitude());
						System.out.println("Geolocation: "+tweet.getGeoLocation().getLatitude() + " " + tweet.getGeoLocation().getLongitude());
					}

					System.out.println("==========================");
					count++;
					documentList.add(document);
					System.out.println("COUNT: "+count);
				}
				
				//while query still has results or number of tweets retrieved has not been hit
			} while ((query = result.nextQuery()) != null && count != MAX_NUM_TWEETS);
				corpus.put("Documents", documentList);
				System.out.println("Writing to " + OUTPUT_FILE);
				file.write(corpus.toJSONString());
				file.close();
				System.out.println("Crawling done!");
				System.exit(0);
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to search tweets: " + te.getMessage());
				System.exit(-1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("IO Exception");
				e.printStackTrace();
			}
	}
	
	public static String parseDate(Date d){
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
}
