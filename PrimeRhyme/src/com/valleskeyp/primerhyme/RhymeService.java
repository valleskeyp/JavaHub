package com.valleskeyp.primerhyme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.valleskeyp.primerhyme.CreateView.ResponseReceiver;


import android.app.IntentService;
import android.content.Intent;

public class RhymeService extends IntentService {

	public RhymeService() {
		super("RhymeService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String word = intent.getStringExtra("word");
		
		HttpClient httpClient = new DefaultHttpClient();

		HttpPost httpPost = new HttpPost("http://rhymebrain.com/talk?function=getRhymes&word=" + word);
		
		String rhymeList;
		
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
		rhymeList = stringBuilder.toString();
			if (rhymeList != null) {
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction(ResponseReceiver.RHYME_RESPONSE);
				broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
				broadcastIntent.putExtra("rhymeList", rhymeList);
				sendBroadcast(broadcastIntent);
			}
		} catch (ClientProtocolException cpe) {
			System.out.println("First Exception cause of HttpResponese :" + cpe);
			cpe.printStackTrace();
		} catch (IOException ioe) {
			System.out.println("Second Exception cause of HttpResponse :" + ioe);
			ioe.printStackTrace();
		}
	}
}
