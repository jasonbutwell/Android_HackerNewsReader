package com.jasonbutwell.hackernewsreader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final int maxNumberOfArticles = 20;

    private final String articleIDMarker = "<ID>";
    private String articleIDURL = "https://hacker-news.firebaseio.com/v0/topstories.json?prety=pretty";
    private String articleURL = "https://hacker-news.firebaseio.com/v0/item/" + "<ID>" + ".json?print=pretty";

    private HashMap<Integer, String> articleURLS;
    private HashMap<Integer, String> articleTitles;

    ArrayList<Integer> articleIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        articleURLS = new HashMap<Integer, String>();
        articleTitles = new HashMap<Integer, String>();
        articleIDs = new ArrayList<>();

        DownloadTask task = new DownloadTask();

        try {
            String result = task.execute(articleIDURL).get();

            //Log.i("result",result);

            if ( result != null ) {
                JSONArray jsonArray = new JSONArray(result);

                for (int i = 0; i < maxNumberOfArticles; i++) {

                    // execute a new download task for each article id
                    // passing in the url to grab the article info using the id

                    DownloadTask getArticleTask = new DownloadTask();
                    String articleInfo = getArticleTask.execute( articleURL.replace(articleIDMarker, jsonArray.getString(i)) ).get();

                    // create a new json object parser

                    JSONObject jsonObject = new JSONObject(articleInfo);

                    // grab the title and url fields

                    String articleID = jsonObject.getString("id");
                    String title = jsonObject.getString("title");
                    String url = jsonObject.getString("url");

                    // add data to list and maps

                    articleIDs.add(Integer.valueOf(articleID));
                    articleTitles.put(Integer.valueOf(articleID), title);
                    articleURLS.put(Integer.valueOf(articleID), url);

                    // Output everything to the log for testing for now.

//                    Log.i("article",Integer.toString(i));
//                    Log.i("article",jsonArray.getString(i));
//                    Log.i("article",title);
//                    Log.i("article",url);
                }

                // output everything we have in the array list and the hashmaps

                Log.i("articleIds", articleIDs.toString());
                Log.i("articleTitles",articleTitles.toString());
                Log.i("articleURLs", articleURLS.toString());

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
