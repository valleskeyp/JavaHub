package com.valleskeyp.tubehub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.GeoPoint;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoRecordingDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatus;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class SingleVideoService extends IntentService{
	
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static YouTube youtube;
	
	public SingleVideoService() {
		super("SingleVideoService");	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String id = intent.getStringExtra("id");
		String accessToken = intent.getStringExtra("accessToken");
		String title = intent.getStringExtra("title");
		String description = intent.getStringExtra("description");
		String category = intent.getStringExtra("category");
		Boolean geolocation = intent.getBooleanExtra("geolocation", false);
		Double latitude = intent.getDoubleExtra("latitude", 0);
		Double longitude = intent.getDoubleExtra("longitude", 0);
		Boolean privateVideo = intent.getBooleanExtra("private", false);
		
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("TubeHub").build();
		
		Video videoObjectDefiningMetadata = new Video();
		VideoStatus status = new VideoStatus();
		if (privateVideo) {
			status.setPrivacyStatus("private");
		} else {
			status.setPrivacyStatus("public");
		}

		VideoSnippet snippet = new VideoSnippet();
		snippet.setTitle(title);
		snippet.setDescription(description);
		snippet.setCategoryId(category);
		
		VideoRecordingDetails details = new VideoRecordingDetails();
		if (geolocation) {
			GeoPoint geoPoint = new GeoPoint();
			geoPoint.setLatitude(latitude);
			geoPoint.setLongitude(longitude);
			details.setLocation(geoPoint);
		}
		
		videoObjectDefiningMetadata.setStatus(status);
		videoObjectDefiningMetadata.setSnippet(snippet);
		videoObjectDefiningMetadata.setRecordingDetails(details);
		videoObjectDefiningMetadata.setId(id);
		videoObjectDefiningMetadata.setKind("youtube#video");
		
		InputStreamContent mediaContent = null;
		try {
			
			Video videoUpdate;
			
			videoUpdate = youtube.videos().update("snippet,status,recordingDetails",
					videoObjectDefiningMetadata).execute();
			
			
	    	
	    	Log.i("UPDATESERVICE", "\n================== Returned Video ==================\n");
	    	Log.i("UPDATESERVICE", "  - Id: " + videoUpdate.getId());
	    	Log.i("UPDATESERVICE", "  - Title: " + videoUpdate.getSnippet().getTitle());
	    	Log.i("UPDATESERVICE", "  - Tags: " + videoUpdate.getSnippet().getTags());
	    	Log.i("UPDATESERVICE", "  - Privacy Status: " + videoUpdate.getStatus().getPrivacyStatus());
	    	Log.i("UPDATESERVICE", "  - Video Count: " + videoUpdate.getStatistics().getViewCount());

	    	
	    	
	    } catch (GoogleJsonResponseException e) {
	        System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
	        
	        Toast msg = Toast.makeText(getApplication(), "Unable to make edit", Toast.LENGTH_LONG);
			msg.setGravity(Gravity.TOP, msg.getXOffset() / 2, msg.getYOffset() / 2);
			msg.show();
			
			Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);
			
	        e.printStackTrace();
	      } catch (IOException e) {
	        System.err.println("IOException: " + e.getMessage());
	        
	        e.printStackTrace();
	      } catch (Throwable t) {
	        System.err.println("Throwable: " + t.getMessage());
	        
	        t.printStackTrace();
	      }
	}

}
