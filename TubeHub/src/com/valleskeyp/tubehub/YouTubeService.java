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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.valleskeyp.tubehub.MainActivity.ResponseReceiver;

import android.app.IntentService;
import android.content.Intent;

public class YouTubeService extends IntentService {
	public static final String PARAM_IN_MSG = "imsg";
	public static final String PARAM_OUT_MSG = "omsg";


	public YouTubeService() {
		super("YouTubeService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String refresh_token = intent.getStringExtra(PARAM_IN_MSG);

		HttpClient httpClient = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost("https://accounts.google.com/o/oauth2/token");

		BasicNameValuePair codeBasicNameValuePair = new BasicNameValuePair("refresh_token", refresh_token);
		BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair("client_id", "799487565850.apps.googleusercontent.com");
		BasicNameValuePair secretBasicNameValuePair = new BasicNameValuePair("client_secret", "68-Az4WTeSS0YYv5we5iomd1");
		BasicNameValuePair grantBasicNameValuePair = new BasicNameValuePair("grant_type", "refresh_token");

		List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
		nameValuePairList.add(codeBasicNameValuePair);
		nameValuePairList.add(idBasicNameValuePair);
		nameValuePairList.add(grantBasicNameValuePair);
		nameValuePairList.add(secretBasicNameValuePair);

		String access_token = null;
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
		nameValuePairList = null;
		if (access_token != null) {
			try {
				JSONObject json = new JSONObject(access_token);
				String accessToken = json.getString("access_token");

				httpClient = new DefaultHttpClient();

				HttpGet httpGet = new HttpGet("https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true&access_token=" + accessToken);

				String youTubeChannels = null;
				try {
					HttpResponse httpResponse = httpClient.execute(httpGet);
					InputStream inputStream = httpResponse.getEntity().getContent();
					InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
					StringBuilder stringBuilder = new StringBuilder();
					String bufferedStrChunk = null;
					while((bufferedStrChunk = bufferedReader.readLine()) != null) {
						stringBuilder.append(bufferedStrChunk);
					}
					youTubeChannels = stringBuilder.toString();

				} catch (ClientProtocolException cpe) {
					System.out.println("First Exception cause of HttpResponese :" + cpe);
					cpe.printStackTrace();
				} catch (IOException ioe) {
					System.out.println("Second Exception cause of HttpResponse :" + ioe);
					ioe.printStackTrace();
				}


				if (youTubeChannels != null) {

					try {
						json = new JSONObject(youTubeChannels);
						String uploads = json.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getJSONObject("relatedPlaylists").getString("uploads");

						httpClient = new DefaultHttpClient();

						httpGet = new HttpGet("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&playlistId=" + uploads + "&access_token=" + accessToken);

						String userUploads = null;
						try {
							HttpResponse httpResponse = httpClient.execute(httpGet);
							InputStream inputStream = httpResponse.getEntity().getContent();
							InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
							BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
							StringBuilder stringBuilder = new StringBuilder();
							String bufferedStrChunk = null;
							while((bufferedStrChunk = bufferedReader.readLine()) != null) {
								stringBuilder.append(bufferedStrChunk);
							}
							userUploads = stringBuilder.toString();

						} catch (ClientProtocolException cpe) {
							System.out.println("First Exception cause of HttpResponese :" + cpe);
							cpe.printStackTrace();
						} catch (IOException ioe) {
							System.out.println("Second Exception cause of HttpResponse :" + ioe);
							ioe.printStackTrace();
						}


						if (userUploads != null) {
							Intent broadcastIntent = new Intent();
							broadcastIntent.setAction(ResponseReceiver.ACTION_RESPONSE);
							broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
							broadcastIntent.putExtra(PARAM_OUT_MSG, userUploads);
							broadcastIntent.putExtra("access_token", accessToken);
							sendBroadcast(broadcastIntent);
							}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

}
