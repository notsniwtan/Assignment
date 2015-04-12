import java.io.File;

import weka.classifiers.meta.AdaBoostM1;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Stopwords;
import org.tartarus.snowball.ext.porterStemmer;


import weka.core.tokenizers.WordTokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.solr.common.util.*;




public class Main {
	static Stopwords stopWords;
	static SolrServer server = null;
	
	static AdaBoostM1 adaBoost;
	static Instances instances;

	//static IndexWriter indexWriter = null;
	public static void main(String[] args) throws SolrServerException, IOException, java.text.ParseException{
		createIndex();

		//search based on query
		/*setUpServer("http://localhost:8983/solr/");
		SolrQuery query = new SolrQuery();
		query.set("q", "union");
		query.set("qt", "/select");
		QueryResponse response = server.query(query);
		SolrDocumentList list = response.getResults();
		for(SolrDocument sd:list){
			System.out.println(sd.getFieldValue("id"));
			System.out.println(sd.getFieldValue("content"));
		}*/
	}
	
	public static JSONArray parseJSONFile(){
		JSONParser parser = new JSONParser();     
        Object obj;
        System.out.println("Parsing JSON file");
		try {
			obj = parser.parse(new FileReader("results/corpus.txt"));
			JSONObject jsonObject = (JSONObject) obj;
			String name = (String) jsonObject.get("Name");
			JSONArray documentList = (JSONArray) jsonObject.get("Documents");
			
		    return documentList;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}
	
	public static void setUpServer(String url){	
		server = new HttpSolrServer(url);
	}
	
	public static void addDocuments(JSONArray documentList) throws java.text.ParseException{
		System.out.println("Adding Documents");
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
		
		Iterator<JSONObject> iterator = documentList.iterator();
		try {
			stopWords = new Stopwords();
			stopWords.clear();
			stopWords.read("stopwords.txt");
			
			loadModel("trainedClassifier.dat");
			System.out.println(adaBoost.getTechnicalInformation());
			Instance current = new DenseInstance(3);
			current.setDataset(instances);
			while(iterator.hasNext()){
				JSONObject innerObj = (JSONObject) iterator.next();
            	
            	List hashList = new ArrayList();
            	JSONArray ja = (JSONArray)innerObj.get("Hashtags");
            	if(ja!=null){
            		for(Object o:ja){
	            		String s= (String)o;
	            		hashList.add(s);
	            	}
            	}
            	
            	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            	Date date = sf.parse((String)innerObj.get("Date"));
            	
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id", String.valueOf(innerObj.get("Id")));
				doc.addField("content", (String) innerObj.get("Content"));
				doc.addField("author", (String)innerObj.get("Author"));
				doc.addField("authorProfilePic", (String)innerObj.get("User Profile Pic"));
				doc.addField("date", date);
				doc.addField("favcount", (long)innerObj.get("Fav Counts"));
				doc.addField("retweetcount", (long)innerObj.get("Retweet Counts"));
				doc.addField("hashtag", hashList);
				doc.addField("country", (String)innerObj.get("Country"));
				if(innerObj.get("Geolong") != null){
					doc.addField("geolong", (double)innerObj.get("Geolong"));
					doc.addField("geolang", (double)innerObj.get("Geolang"));
				}
				if(innerObj.get("Photo") != null){
					doc.addField("image", (String) innerObj.get("Photo"));
					doc.addField("hasImage","true");
				}
				doc.addField("url", (String) innerObj.get("URL"));
				doc.addField("popcount", (long)innerObj.get("Fav Counts")+(long)innerObj.get("Retweet Counts"));
				
				current.setValue(idAttr,(long)(innerObj.get("Id")));
				current.setValue(contentAttr, preprocess((String) innerObj.get("Content")));
				current.setClassMissing();
				System.out.println("String: "+preprocess((String) innerObj.get("Content")));

				String category = retrieveCategory(current);
				System.out.println("Cat: "+category);

				doc.addField("category",category);
				
				server.add(doc);
			}
		    server.commit();
		    System.out.println("Documents indexed.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void createIndex() throws java.text.ParseException{
		JSONArray documentList = parseJSONFile();
		setUpServer("http://localhost:8983/solr/");
		addDocuments(documentList);
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
