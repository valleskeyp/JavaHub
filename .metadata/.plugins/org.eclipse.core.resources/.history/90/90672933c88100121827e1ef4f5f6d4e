package com.valleskeyp.tubehub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Builder;

import android.app.IntentService;
import android.content.Intent;

public class UploadService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";
	
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
	private static Builder youtube;
	
	public UploadService() {
		super("UploadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String access_token = intent.getStringExtra(PARAM_IN_MSG);

		// path is the location of the selected video
		String videoPath = intent.getStringExtra("path");
		
		
		
		/*
		 Really though.. I probably need to send the access_token and the videoPath to the EditActivity.java
		 This is because the user must choose a video to upload, type in the required attributes
		 and --then-- I can call this service to upload the video.
		*/
		
		
		
		
		
		Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(access_token);
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential);
		
		/*
		 trying to brute force my way back into the API's
		 following cmd-line code at:   https://developers.google.com/youtube/v3/docs/videos/insert
		*/
		
		HttpClient httpClient = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost("https://www.googleapis.com/youtube/v3/videos?part=snippet,status,recordingDetails" + access_token);

		BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair("client_id", "799487565850.apps.googleusercontent.com");
		BasicNameValuePair secretBasicNameValuePair = new BasicNameValuePair("client_secret", "68-Az4WTeSS0YYv5we5iomd1");
		BasicNameValuePair grantBasicNameValuePair = new BasicNameValuePair("grant_type", "refresh_token");

		List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		nameValuePairList.add(idBasicNameValuePair);
		nameValuePairList.add(grantBasicNameValuePair);
		nameValuePairList.add(secretBasicNameValuePair);

		try { 
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(nameValuePairList);
			httpPost.setEntity(urlEncodedFormEntity);
			try {
				HttpResponse httpResponse = httpClient.execute(httpPost);
				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while((bufferedStrChunk = bufferedReader.readLine()) != null){
					stringBuilder.append(bufferedStrChunk);
				}

				access_token = stringBuilder.toString();

			} catch (ClientProtocolException cpe) {
				System.out.println("First Exception cause of HttpResponese :" + cpe);
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out.println("Second Exception cause of HttpResponse :" + ioe);
				ioe.printStackTrace();
			}

		} catch (UnsupportedEncodingException uee) {
			System.out.println("An Exception given because of UrlEncodedFormEntity argument :" + uee);
			uee.printStackTrace();
		}
		
	}

}
