//This Java Class is responsible for getting the raw JSON data from the flickr feed


package com.dev.jdoh.grabbag;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//create an enum to hold a list of download statuses
enum DownloadStatus { IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK }

class GetRawData extends AsyncTask<String, Void, String> {
    private static final String TAG = "GetRawData";

    //when you start a variable with an 'm' it means it's a member variable
    private DownloadStatus mDownloadStatus;

    //create constructer
    public GetRawData() {
        //initialize mDownloadStatus
        mDownloadStatus = DownloadStatus.IDLE;
    }
    //override required async methods
    @Override
    protected void onPostExecute(String s) {
        Log.d(TAG, "onPostExecute:  parameter = " + s);
//        super.onPostExecute(s);  //This line is not needed because it doesn't do anything
    }

    @Override
    protected String doInBackground(String... strings) {
        //create URL connection
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if(strings == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }
        //next we will make sure to catch possible errors that would otherwise stop the application
        try {
            //in this try block we will be changing our download status variable to processing
            mDownloadStatus = DownloadStatus.PROCESSING;
            //attempt to create the URL from the String parameter we passed in
            //there should only be one parameter passed in, this is why we use 0 to indicate
            //that we want the first element
            URL url = new URL(strings[0]);

            //we need to cast connection as an HttpURLConnection
            connection = (HttpURLConnection) url.openConnection();
            //we specify the type of connection we want (we want data so we use get)
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            //log the response code to see if we're getting an error
            Log.d(TAG, "doInBackground: The response code was " + response);

            //this variable will hold our result once it is downloaded
            StringBuilder result = new StringBuilder();

            //Using a bufferedreader to read data from the inputstream and then adding it to
            //the stringbuilder until we have no data

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //here we are reading a line at a time and then adding it to the stringbuilder
            //when we use Readline the new line characters are stripped off
            //so we use (\n).append to add the new line back in

//            String line;
//            while (null !=(line = reader.readLine())) {
            //this loop is also fine for this case ^ //we will continue to use the for loop
            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
                result.append(line).append("\n");
            }

            mDownloadStatus = DownloadStatus.OK;
            //right now our result is a StringBuilder, this will convert the result to a required string
            //then return it
            return result.toString();

        } catch(MalformedURLException e) {
            Log.e(TAG, "doInBackground: " + e.getMessage() );
        } catch(IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data" + e.getMessage() );
        } catch(SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs Permissions?" + e.getMessage() );
            //its good practice to close the reader and disconnect connections on finally blocks
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                    //reader.close could throw an exception so we need to catch it
                } catch(IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage() );
                }
            }
        }

        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}
