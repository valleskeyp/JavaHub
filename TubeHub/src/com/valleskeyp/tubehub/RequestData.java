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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class RequestData {

	public RequestData() {
	
	}


public void AsyncRequest(String access_token) {
	
	
	class GetDataAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
        	
            String token = params[0];

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost("https://www.googleapis.com/youtube/v3/playlistItems?access_token=" + token);

            BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair("part", "snippet");
            BasicNameValuePair secretBasicNameValuePair = new BasicNameValuePair("maxResults", "5");
            

            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
            nameValuePairList.add(idBasicNameValuePair);
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

                    return stringBuilder.toString();

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

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("POST_EXECUTE", result);
            
            try {
				JSONObject json = new JSONObject(result);
				Log.i("DATA REQUEST STRING", result);
					
			} catch (JSONException e) {
				e.printStackTrace();
			}
            // parse user channels to find uploads channel ID ?
            
            // request that data -> new http that parses that data
        }
    }

    GetDataAsyncTask sendPostReqAsyncTask = new GetDataAsyncTask();
    sendPostReqAsyncTask.execute(access_token);
}
}