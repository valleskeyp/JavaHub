package com.valleskeyp.tubehub;

import java.io.IOException;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.valleskeyp.tubehub.EditActivity.ResponseReceiver;

import android.app.IntentService;
import android.content.Intent;

public class VideoInfoService extends IntentService {
	
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static YouTube youtube;
	
	public VideoInfoService() {
		super("VideoInfoService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String id = intent.getStringExtra("id");
		String accessToken = intent.getStringExtra("accessToken");
		
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("TubeHub").build();
		

		YouTube.Videos.List videoList;
		
		try {
			videoList = youtube.videos().list(id, "snippet,recordingDetails,status");
			VideoListResponse video = videoList.execute();
			
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(ResponseReceiver.ACTION_INFO);
			broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
			broadcastIntent.putExtra("info", video.toString());
			sendBroadcast(broadcastIntent);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
