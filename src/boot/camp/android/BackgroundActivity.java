package boot.camp.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import sun.misc.IOUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class BackgroundActivity{
	List<Tweet> tweets;
	DownloadTweetTask background;
	
	public BackgroundActivity(){
		getTweets();
	}
	
	public List<Tweet> getTweets(){
		List<Tweet> tweet = new ArrayList();
		String encodedSearch;
		String test = "";
		try {
			encodedSearch = URLEncoder.encode("bieber", "UTF-8");
			String url = "http://search.twitter.com/search.json?q="+encodedSearch;
			background = (DownloadTweetTask) new DownloadTweetTask().execute(url,null,null);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return tweet;
	}
	
	class DownloadTweetTask extends AsyncTask<String, Void, String>{
		
		String jsonOutput = "";
		
	    @Override
	    protected String doInBackground(String... urls) {
	    	HttpClient httpclient = new DefaultHttpClient();
	    	String line;
	    	String json = "";
			try{
				HttpGet httpget = new HttpGet(urls[0]);
				//ResponseHandler<String> responseHandler = new BasicResponseHandler();
				HttpResponse response = httpclient.execute(httpget);
				final int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode != HttpStatus.SC_OK){
					Log.w(getClass().getSimpleName(),"Error " + statusCode + " for URL " + urls[0]);
					return null;
				}
				
				HttpEntity getResponseEntity = response.getEntity();
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(getResponseEntity.getContent()));
	            while ((line = rd.readLine()) != null) {
	                json += line + System.getProperty("line.separator");
	            }
	            
	            if (json != null) {    
	                Log.i("RESPONSE",json);
	            }
	            
	        }  catch (Exception e){
	            e.printStackTrace();
	        } 
			return json;
	    }
	    
	    
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(String result) {
	        //textView.setText(result);
	    	jsonOutput = result;
	   }
	}
	
}