package com.valleskeyp.tubehub;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import com.valleskeyp.tubehub.MainActivity.ResponseReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class UploadService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static YouTube youtube;

	public UploadService() {
		super("UploadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String access_token = intent.getStringExtra(PARAM_IN_MSG);
		String title = intent.getStringExtra("title");
		String description = intent.getStringExtra("description");
		String category = intent.getStringExtra("category");
		Boolean geolocation = intent.getBooleanExtra("geolocation", false);
		Double latitude = intent.getDoubleExtra("latitude", 0);
		Double longitude = intent.getDoubleExtra("longitude", 0);
		Boolean privateVideo = intent.getBooleanExtra("private", false);
		
		// path is the location of the selected video
		String videoPath = intent.getStringExtra("path");
		File videoFile = new File(videoPath);

		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(access_token);
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("TubeHub").build();

		Video videoObjectDefiningMetadata = new Video();
		VideoStatus status = new VideoStatus();
		if (privateVideo) {
			status.setPrivacyStatus("private");
		} else {
			status.setPrivacyStatus("public");
		}
		videoObjectDefiningMetadata.setStatus(status);

		VideoSnippet snippet = new VideoSnippet();
		snippet.setTitle(title);
		snippet.setDescription(description);
		snippet.setCategoryId(category);
		
		List<String> tags = new ArrayList<String>();
		tags.add("TubeHub");
		tags.add("From phone");
		snippet.setTags(tags);
		VideoRecordingDetails details = new VideoRecordingDetails();
		if (geolocation) {
			GeoPoint geoPoint = new GeoPoint();
			geoPoint.setLatitude(latitude);
			geoPoint.setLongitude(longitude);
			details.setLocation(geoPoint);
		}
		

		videoObjectDefiningMetadata.setSnippet(snippet);
		videoObjectDefiningMetadata.setRecordingDetails(details);

		InputStreamContent mediaContent = null;
		try {
			
			mediaContent = new InputStreamContent("video/*",
					new BufferedInputStream(new FileInputStream(videoFile)));
			mediaContent.setLength(videoFile.length());


			YouTube.Videos.Insert videoInsert;
			
			videoInsert = youtube.videos().insert("snippet,statistics,status,recordingDetails",
					videoObjectDefiningMetadata,
					mediaContent);
			MediaHttpUploader uploader = videoInsert.getMediaHttpUploader();

			uploader.setDirectUploadEnabled(false);

			MediaHttpUploaderProgressListener progressListener =
					new MediaHttpUploaderProgressListener() {
				public void progressChanged(MediaHttpUploader uploader)
						throws IOException {
					switch (uploader.getUploadState()) {
					case INITIATION_STARTED:
						Log.i("UPLOAD_PROGRESS","Initiation Started");
						break;
					case INITIATION_COMPLETE:
						Log.i("UPLOAD_PROGRESS","Initiation Completed");
						Toast msg = Toast.makeText(getApplication(), "Uploading...", Toast.LENGTH_LONG);
						msg.setGravity(Gravity.CENTER, msg.getXOffset() / 2, msg.getYOffset() / 2);
						msg.show();
						Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
						dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getApplication().startActivity(dialogIntent);
						break;
					case MEDIA_IN_PROGRESS:
						Log.i("UPLOAD_PROGRESS","Upload In Progress");
						Log.i("UPLOAD_PROGRESS","Upload percentage: " + uploader.getProgress());
						break;
					case MEDIA_COMPLETE:
						Log.i("UPLOAD_PROGRESS","Upload Completed");
	    				break;
	    			case NOT_STARTED:
	    				Log.i("UPLOAD_PROGRESS","Upload Not Started");
	    				break;
	    			}
	    		}
	    	};
	    	uploader.setProgressListener(progressListener);
	    	
	    	Video returnedVideo = videoInsert.execute();
	    	
	    	Log.i("UPLOADSERVICE", "\n================== Returned Video ==================\n");
	    	Log.i("UPLOADSERVICE", "  - Id: " + returnedVideo.getId());
	    	Log.i("UPLOADSERVICE", "  - Title: " + returnedVideo.getSnippet().getTitle());
	    	Log.i("UPLOADSERVICE", "  - Tags: " + returnedVideo.getSnippet().getTags());
	    	Log.i("UPLOADSERVICE", "  - Privacy Status: " + returnedVideo.getStatus().getPrivacyStatus());
	    	Log.i("UPLOADSERVICE", "  - Video Count: " + returnedVideo.getStatistics().getViewCount());

	    	Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ResponseReceiver.ACTION_UPLOAD);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(PARAM_OUT_MSG, true);
			sendBroadcast(broadcastIntent);
	    	
	    } catch (GoogleJsonResponseException e) {
	        System.err.println("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
	        Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);
	        
	        Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ResponseReceiver.ACTION_UPLOAD);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(PARAM_OUT_MSG, false);
			sendBroadcast(broadcastIntent);
	        e.printStackTrace();
	      } catch (IOException e) {
	        System.err.println("IOException: " + e.getMessage());
	        Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);
	        
	        Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ResponseReceiver.ACTION_UPLOAD);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(PARAM_OUT_MSG, false);
			sendBroadcast(broadcastIntent);
	        e.printStackTrace();
	      } catch (Throwable t) {
	        System.err.println("Throwable: " + t.getMessage());
	        Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);
	        
	        Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ResponseReceiver.ACTION_UPLOAD);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra(PARAM_OUT_MSG, false);
			sendBroadcast(broadcastIntent);
	        t.printStackTrace();
	      }


		      
	    
	}

}
