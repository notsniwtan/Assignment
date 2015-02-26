import java.io.File;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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


public class Main {
	static SolrServer server = null;
	//static IndexWriter indexWriter = null;
	public static void main(String[] args) throws SolrServerException, IOException{
		//createIndex();
		
		//search based on query
		setUpServer("http://localhost:8983/solr/");
		SolrQuery query = new SolrQuery();
		query.set("q", "union");
		query.set("qt", "/select");
		QueryResponse response = server.query(query);
		SolrDocumentList list = response.getResults();
		for(SolrDocument sd:list){
			System.out.println(sd.getFieldValue("id"));
			System.out.println(sd.getFieldValue("content"));
		}
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
	
	public static void addDocuments(JSONArray documentList){
		System.out.println("Adding Documents");
		Iterator<JSONObject> iterator = documentList.iterator();
		int count = 0;
		try {
			while(iterator.hasNext()){
				JSONObject innerObj = (JSONObject) iterator.next();
				String content = (String) innerObj.get("Content");
				SolrInputDocument doc = new SolrInputDocument();
				doc.addField("id", count++);
				doc.addField("content", content);
				server.add(doc);
			}
		    server.commit();
			} catch (SolrServerException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void createIndex(){
		JSONArray documentList = parseJSONFile();
		setUpServer("http://localhost:8983/solr/");
		addDocuments(documentList);
	}
}
