package com.valleskeyp.tubehub;

import java.io.IOException;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Videos.Delete;
import com.google.api.services.youtube.model.VideoListResponse;
import com.valleskeyp.tubehub.EditActivity.ResponseReceiver;

import android.app.IntentService;
import android.content.Intent;

public class DeleteService extends IntentService {

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static YouTube youtube;
	
	public DeleteService() {
		super("DeleteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String access_token = intent.getStringExtra("accessToken");
		String id = intent.getStringExtra("id");
		
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(access_token);
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("TubeHub").build();
		

		try {
			youtube.videos().delete(id).execute();
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
