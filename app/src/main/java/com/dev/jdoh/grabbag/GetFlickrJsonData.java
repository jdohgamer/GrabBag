package com.dev.jdoh.grabbag;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    public static final String TAG = "GetFlickrJsonData";

    private List<Photo> mPhotoList = null;  //Stores list of Photo objects. Initialize to null
    private String mBaseURL;                //raw link that will get the data from Flickr
    private String mLanguage;               //allow lang to be specified
    private boolean mMatchAll;              //parameter to match all tag terms or some of them



    private final OnDataAvailable mCallback;

    //This class implements OnDownloadComplete so it can get callbacks
    //from GetRawData and it also defines its own interface OnDataAvailable
    //so it can send a callback to MainActivity when we have data to send back to it
    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }

    //generate constructor
    public GetFlickrJsonData(String language, boolean matchAll, OnDataAvailable callback, String baseURL) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallback = callback;
    }


    //method that MainActivity is going to call


    void executeOnSameThread (String searchCriteria) {                              //pass in some search criteria
        Log.d(TAG, "executeOnSameThread: starts");
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);    //

        //create new GetRawData obj and then call execute method
        GetRawData getRawData = new GetRawData(this);                     //passing 'this' so we can get a callback
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: end");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: starts");
        if(mCallback != null) {
            mCallback.onDataAvailable(mPhotoList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");
        return mPhotoList;
    }

    //creates the URL with the criteria we pass in so it displays the data we ask for
    private String createUri(String searchCriteria, String lang, boolean matchAll) {
        Log.d(TAG, "createUri: starts");

        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagsmode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }

    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete: starts" + status);

        if(status == DownloadStatus.OK) {
            mPhotoList = new ArrayList<>();

            try {
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                for(int i=0; i<itemsArray.length(); i++) {

                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");

                    //this replaces the suffix 'm' with 'b' so we get the larger image within the flickr database
                    String photoUrl = jsonMedia.getString("m");

                    String link = photoUrl.replaceFirst("_m.", "_b.");

                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    mPhotoList.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: complete" + photoObject.toString());
                }
            } catch(JSONException jsone) {
                jsone.printStackTrace();
                Log.e(TAG, "onDownloadComplete: error processing json data" + jsone.getLocalizedMessage() );
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if(mCallback != null) {
            //now inform the caller that processing is done or returning null if error
            mCallback.onDataAvailable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete: ends");
    }
}
