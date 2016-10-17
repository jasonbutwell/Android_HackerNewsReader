package com.jasonbutwell.hackernewsreader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    ArrayList<String> titles;
    ArrayAdapter titlesAdapter;

    SQLiteDatabase articlesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // For showing our articles in the UI list view
        ListView articlesListView = (ListView)findViewById(R.id.articlesListView);

        // create new array list and pair with array adapter
        titles = new ArrayList<String>();
        titlesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        articlesListView.setAdapter(titlesAdapter);

        String DBName = "Articles";
        String DBTableName = "articles";
        String createDataBaseSQL = "CREATE TABLE IF NOT EXISTS "+ DBTableName +" (id INT PRIMARY KEY, articleId INT, title VARCHAR, url VARCHAR, content VARCHAR)";
        String extractDataSQL = "SELECT * FROM " + DBTableName +" ORDER BY articleId DESC";
        String dataInsertSQL = "INSERT INTO "+ DBTableName +" (articleId, title, url) VALUES (?, ?, ?)";
        String dataBaseClearSQL = "DROP TABLE IF EXISTS " + DBTableName;

        articleIDs = new ArrayList<>();
        articleURLS = new HashMap<Integer, String>();
        articleTitles = new HashMap<Integer, String>();

        DownloadTask task = new DownloadTask();

        articlesDB = this.openOrCreateDatabase(DBName, MODE_PRIVATE, null);

        // Used to clear existing data from Database for testing
        articlesDB.execSQL(dataBaseClearSQL);

        articlesDB.execSQL(createDataBaseSQL);

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

                    // add data to our SQLite database - revised
                    // using a prepared statement to handle special characters
                    // to try to better safe guard against SQL injection woes

                    SQLiteStatement statement = articlesDB.compileStatement( dataInsertSQL );
                    statement.bindString(1,articleID);
                    statement.bindString(2,title);
                    statement.bindString(3,url);

                    statement.execute();

                    // Output everything to the log for testing for now.

//                    Log.i("article",Integer.toString(i));
//                    Log.i("article",jsonArray.getString(i));
//                    Log.i("article",title);
//                    Log.i("article",url);
                }

                // output everything we have in the array list and the hashmaps

//                Log.i("articleIds", articleIDs.toString());
//                Log.i("articleTitles",articleTitles.toString());
//                Log.i("articleURLs", articleURLS.toString());

                // Output the data from the database

                Cursor cursor = articlesDB.rawQuery(extractDataSQL, null);

                int articleIdIndex = cursor.getColumnIndex("articleId");
                int urlIndex = cursor.getColumnIndex("url");
                int titleIndex = cursor.getColumnIndex("title");

                cursor.moveToFirst();

                int count = cursor.getCount();
                int counter = 1;

                titles.clear(); // we clear the titles array list just in case

                while (cursor != null && count > 0 ) {

                    titles.add(cursor.getString(titleIndex));   // add the title to the

//                    Log.i("article",Integer.toString(counter++));
//                    Log.i("articleResults - id", Integer.toString(cursor.getInt(articleIdIndex)));
//                    Log.i("articleResults - title", cursor.getString(titleIndex));
//                    Log.i("articleResults - url", cursor.getString(urlIndex));
                    cursor.moveToNext();
                    count--;
                };

                titlesAdapter.notifyDataSetChanged(); // notify that we have changed our array so the adapter can update the view
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
