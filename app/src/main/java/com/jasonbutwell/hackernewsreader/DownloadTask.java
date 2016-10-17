package com.jasonbutwell.hackernewsreader;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by J on 17/10/2016.
 */

public class DownloadTask extends AsyncTask<String, Void, String> {

    private int data;
    private URL url;
    private HttpURLConnection urlConnection= null;
    private InputStream in;
    private InputStreamReader reader;
    private StringBuilder contentBuilder;

    private String result;

    // Constructor in case we want to pass in some params
    public DownloadTask() {
        result = "";
        int data = 0;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            url = new URL( params[0] );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            urlConnection = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in = urlConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader = new InputStreamReader(in);
        contentBuilder = new StringBuilder();

        try {
            while ( (data = in.read() ) != -1)
                contentBuilder.append((char)data);

            result = contentBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

    return result;
    }

    protected void onPostExecute(String result ) {
        super.onPostExecute( result );
    }
}
