package com.valleskeyp.tubehub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

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


@SuppressLint("SetJavaScriptEnabled")
public class Login_Activity extends Activity {
	Context _context;
    EditText _accountField;
    EditText _passwordField;
    WebView webview;
    Boolean _running;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.login_activity);
    	_context = this;
    	_running = false;
    	
    	SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", 0);
    	if (pref.getString("access_token", null) != null) {
    		Intent i = new Intent(_context, MainActivity.class);
        	startActivityForResult(i, 1);
		}
    	
    	webview = (WebView) findViewById(R.id.webView);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
        	
        	@Override
        	public void onPageFinished(WebView view, String url) {
        		if (url.charAt(22) == '=') {
        			String str = url.substring(23);
        			
        			//take key and get token, also move to main list activity
        			if (!_running) {
        				webview.setVisibility(View.INVISIBLE);
        				sendPostRequest(str);
					}
        		} else if (url.charAt(23) == '=') {
        			Toast.makeText(getApplicationContext(), "Please allow access to proceed.", Toast.LENGTH_LONG).show();
        			webview.loadUrl("https://accounts.google.com/o/oauth2/auth?client_id=799487565850.apps.googleusercontent.com&redirect_uri=http://localhost&scope=https://www.googleapis.com/auth/youtube&response_type=code&access_type=offline");
        		}
        		super.onPageFinished(view, url);
        	}
        });
        webview.loadUrl("https://accounts.google.com/o/oauth2/auth?client_id=799487565850.apps.googleusercontent.com&redirect_uri=http://localhost&scope=https://www.googleapis.com/auth/youtube&response_type=code&access_type=offline");
    }
    
    //POST code and client data to Google API to get token data back
    private void sendPostRequest(String code) {

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {
            	_running = true;
            	
                String code = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("https://accounts.google.com/o/oauth2/token");

                BasicNameValuePair redirectBasicNameValuePair = new BasicNameValuePair("redirect_uri", "http://localhost");
                BasicNameValuePair codeBasicNameValuePair = new BasicNameValuePair("code", code);
                BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair("client_id", "799487565850.apps.googleusercontent.com");
                BasicNameValuePair secretBasicNameValuePair = new BasicNameValuePair("client_secret", "68-Az4WTeSS0YYv5we5iomd1");
                BasicNameValuePair grantBasicNameValuePair = new BasicNameValuePair("grant_type", "authorization_code");

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(codeBasicNameValuePair);
                nameValuePairList.add(idBasicNameValuePair);
                nameValuePairList.add(grantBasicNameValuePair);
                nameValuePairList.add(secretBasicNameValuePair);
                nameValuePairList.add(redirectBasicNameValuePair);
                
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
                if(!result.contains("error")){
                	try {
						JSONObject json = new JSONObject(result);
						String accessToken = json.getString("access_token");
						String refreshToken = json.getString("refresh_token");
						
						SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", 0);
						Editor editor = pref.edit();
						editor.putString("access_token", accessToken);
						editor.putString("refresh_token", refreshToken);
						editor.commit();
						
						Toast.makeText(getApplicationContext(), "Token received", Toast.LENGTH_LONG).show();
	                	Intent i = new Intent(_context, MainActivity.class);
	                	startActivityForResult(i, 1);
	                	_running = false;
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
                	
                	
                }else{
                	webview.setVisibility(View.VISIBLE);
                	_running = false;
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem with connecting.", Toast.LENGTH_LONG).show();
                    webview.loadUrl("https://accounts.google.com/o/oauth2/auth?client_id=799487565850.apps.googleusercontent.com&redirect_uri=http://localhost&scope=https://www.googleapis.com/auth/youtube&response_type=code&access_type=offline");
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(code);     
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.login_activity, menu);
    	return true;
    }
}
