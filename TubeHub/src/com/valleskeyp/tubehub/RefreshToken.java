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
import android.widget.Toast;

public class RefreshToken {
	

	public void RequestTokenRefresh(String refresh_token) {
		class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {            	
                String code = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("https://accounts.google.com/o/oauth2/token");

                BasicNameValuePair codeBasicNameValuePair = new BasicNameValuePair("refresh_token", code);
                BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair("client_id", "799487565850.apps.googleusercontent.com");
                BasicNameValuePair secretBasicNameValuePair = new BasicNameValuePair("client_secret", "68-Az4WTeSS0YYv5we5iomd1");
                BasicNameValuePair grantBasicNameValuePair = new BasicNameValuePair("grant_type", "refresh_token");

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(codeBasicNameValuePair);
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
                if(result.contains("access_token")){
                	try {
						JSONObject json = new JSONObject(result);
						String accessToken = json.getString("access_token");
						
						// tried passing data back to MainActivity, also tried passing the data straight
						// into RequestData.  Tried to create an instance of the other classes, an interface, even
						// broadcast receiver.  can't seem to get past null pointer exception.  I do get the refreshed
						// token -- with which I can make requests to YouTube somehow. 
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
                	
                	
                }else{
                	MainActivity mainActivity = new MainActivity();
                    Toast.makeText(mainActivity, "Sorry, there was a problem with connecting.", Toast.LENGTH_LONG).show();
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(refresh_token);
	}
	
}