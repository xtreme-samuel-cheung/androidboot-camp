package boot.camp.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import boot.camp.android.BackgroundActivity.DownloadTweetTask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	ArrayList<Tweet> tweetText = new ArrayList<Tweet>();
	ArrayList<Bitmap> tweetPicture = new ArrayList<Bitmap>();
	BackgroundActivity backgroundActivity;
	Context context;
	int width;
	int height;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String url = "http://search.twitter.com/search.json?q=#bieber&size=mini";
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		
		
		new DownloadTweetTask().execute(url);
		new DownloadPictureTask().execute(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	class DownloadTweetTask extends AsyncTask<String, Void, Void>{
		String jsonOutput = "";
	    @Override
	    protected Void doInBackground(String... urls) {
    		HttpClient httpclient = new DefaultHttpClient();
        	String line;
        	String json = "";
    		try{
    			HttpGet httpget = new HttpGet(urls[0]);
    			HttpResponse response = httpclient.execute(httpget);

    			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
    				Log.w(getClass().getSimpleName(),"Error " + response.getStatusLine().getStatusCode() + " for URL " + urls[0]);
    				return null;
    			}
    			
    			HttpEntity getResponseEntity = response.getEntity();
    			
    			BufferedReader rd = new BufferedReader(new InputStreamReader(getResponseEntity.getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line + System.getProperty("line.separator");
                }
                
                if (json != null) {
    	            JSONObject root = new JSONObject(json);
    	            JSONArray sessions = root.getJSONArray("results");
    	            
    	            for(int i = 0; i < sessions.length();i++){
    	            	JSONObject session = sessions.getJSONObject(i);
    	            	Tweet tweet = new Tweet();
    	            	tweet.setContent(session.getString("text"));
    	            	tweet.setUser(session.getString("from_user"));
    	            	tweet.setTimestamp(session.getString("created_at"));
    	            	
    	            	tweetText.add(tweet);
    	            }
    	            
                }
                
            }  catch (Exception e){
                e.printStackTrace();
            }
    		
    		return null;
    	}
	    
	    
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(Void result) {
            setListAdapter(new TweetAdapter(MainActivity.this, R.layout.list_item, tweetText,tweetPicture));
	   }
	}
	
	class DownloadPictureTask extends AsyncTask<String, Void, Void>{
		String jsonOutput = "";
	    @Override
	    protected Void doInBackground(String... urls) {
    		HttpClient httpclient = new DefaultHttpClient();
        	String line;
        	String json = "";
    		try{
    			HttpGet httpget = new HttpGet(urls[0]);
    			HttpResponse response = httpclient.execute(httpget);

    			if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
    				Log.w(getClass().getSimpleName(),"Error " + response.getStatusLine().getStatusCode() + " for URL " + urls[0]);
    				return null;
    				
    			}
    			
    			HttpEntity getResponseEntity = response.getEntity();
    			
    			BufferedReader rd = new BufferedReader(new InputStreamReader(getResponseEntity.getContent()));
                while ((line = rd.readLine()) != null) {
                    json += line + System.getProperty("line.separator");
                }
                
                if (json != null) {
                    //Log.i("RESPONSE",json);
    	            JSONObject root = new JSONObject(json);
    	            JSONArray sessions = root.getJSONArray("results");
    	            
    	            for(int i = 0; i < sessions.length();i++){
    	            	JSONObject session = sessions.getJSONObject(i);
    	            	Tweet tweet = new Tweet();
    	            	
    	            	URL imageUrl = new URL(session.getString("profile_image_url"));
    	            	Bitmap image = BitmapFactory.decodeStream(imageUrl.openStream());
    	            	tweetPicture.add(image);
    	            }
    	            
                }
                
            }  catch (Exception e){
                e.printStackTrace();
            }
    		
    		return null;
    	}
	    
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(Void result) {
            setListAdapter(new TweetAdapter(MainActivity.this, R.layout.list_item, tweetText,tweetPicture));
	   }
	}
	
	private class TweetAdapter extends ArrayAdapter<Tweet>{
		ArrayList<Tweet> tweets;
		ArrayList<Bitmap> pictures;
		public TweetAdapter(Context context, int textViewResourceId,ArrayList<Tweet> tweets,ArrayList<Bitmap>pictures) {
			super(context, textViewResourceId,tweets);
			this.tweets = tweets;
			this.pictures = pictures;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			View view = convertView;
			
			if (view == null) {
				LayoutInflater viewInflate = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = viewInflate.inflate(R.layout.list_item, null);
			}
			
			Tweet displayTweet = tweets.get(position);			
			TextView top = (TextView) view.findViewById(R.id.toptext);
			TextView bottom = (TextView) view.findViewById(R.id.bottomtext);
			TextView time = (TextView) view.findViewById(R.id.timetext);
			ImageView displayPick = (ImageView)view.findViewById(R.id.display_pick);
			
			top.setText(displayTweet.getUser());
			bottom.setText(displayTweet.getContent());
			time.setText(displayTweet.getTimestamp());
			
			
			if(pictures.size() > position){
				Bitmap picture = pictures.get(position);
				displayPick.setImageBitmap(picture);
			}
			
			return view;
			
		}
		
	}

}
