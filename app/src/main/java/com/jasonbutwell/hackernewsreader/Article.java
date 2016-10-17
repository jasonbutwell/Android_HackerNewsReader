package com.jasonbutwell.hackernewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Article extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Intent intent = getIntent();                                    // grabs the intent
        String articleURL = intent.getStringExtra("articleURL");        // try to grab our URL passed in

        WebView webView = (WebView) findViewById(R.id.articleWebView);  // obtain resource ID of web View
        webView.getSettings().setJavaScriptEnabled(true);               // Enable the Java script
        webView.setWebViewClient(new WebViewClient());                  // Stops the default browser opening behaviour

        //Log.i("URL",articleURL);

        if ( articleURL != "" )
            webView.loadUrl( articleURL );                          // Tell the webview to look at our URL
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
