package boot.camp.android;

import android.graphics.Bitmap;
import android.text.format.Time;

public class Tweet {
	private String content;
	private String timestamp;
	private String user;
	
	public Tweet(){}
	
	public Tweet setContent(String content){
		this.content = content;
		return this;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public Tweet setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		return this;
	}

	public String getUser() {
		return user;
	}

	public Tweet setUser(String user) {
		this.user = user;
		return this;
	}

}
