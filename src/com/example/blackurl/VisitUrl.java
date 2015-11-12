package com.example.blackurl;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VisitUrl extends Activity {
	
	private WebView webView;
	@SuppressLint("SetJavaScriptEnabled")

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.innerbrower_layout);
		webView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true);
		webSettings.setBuiltInZoomControls(true);
		Intent intent = getIntent();
		String url = intent.getStringExtra("url");
		webView.loadUrl(url);
		webView.setWebViewClient(new webViewClient ());
	}
	
	private class webViewClient extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url){
			view.loadUrl(url);
			return true;
		}
	}

}
