package com.valleskeyp.tubehub;

// Developer Key: AIzaSyBCJx9eFMjDT0CJZMj1tkHT0oY6o8DvU24

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


@SuppressLint("SetJavaScriptEnabled")
public class Login_Activity extends Activity {
	Context _context;
    EditText _accountField;
    EditText _passwordField;
    WebView webview;
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.login_activity);
    	_context = this;

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
        			view.stopLoading();
        			sendPostRequest(str);
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

                String code = params[0];

                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("https://accounts.google.com/o/oauth2/token");

//    			pairs.add(new BasicNameValuePair("redirect_uri", "http://localhost/oauth2callback"));
                
                BasicNameValuePair codeBasicNameValuePair = new BasicNameValuePair("code", code);
                BasicNameValuePair idBasicNameValuePair = new BasicNameValuePair("client_id", "799487565850.apps.googleusercontent.com");
                BasicNameValuePair grantBasicNameValuePair = new BasicNameValuePair("grant_type", "authorization_code");

                List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
                nameValuePairList.add(codeBasicNameValuePair);
                nameValuePairList.add(idBasicNameValuePair);
                nameValuePairList.add(grantBasicNameValuePair);

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
                	Toast.makeText(getApplicationContext(), "Token received", Toast.LENGTH_LONG).show();
                	Intent i = new Intent(_context, MainActivity.class);
                	startActivityForResult(i, 1);
                }else{
                    Toast.makeText(getApplicationContext(), "Token not received", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(_context, MainActivity.class);
                	startActivityForResult(i, 1);
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
