package com.dev.jdoh.grabbag;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GetFlickrJsonData.OnDataAvailable {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        GetRawData getRawData = new GetRawData(this);
//        getRawData.execute("https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();
        GetFlickrJsonData getFlickrJsonData =
                new GetFlickrJsonData(
                        "en-us",
                        true,
                        this,
                        "https://api.flickr.com/services/feeds/photos_public.gne");
        getFlickrJsonData.executeOnSameThread("rocket, ship");
        Log.d(TAG, "onResume: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status) {
        if (status == DownloadStatus.OK) {
            Log.d(TAG, "onDataAvailable: data is " + data);
        } else {
            Log.e(TAG, "onDataAvailable failed with status" + status);
        }
    }
}
