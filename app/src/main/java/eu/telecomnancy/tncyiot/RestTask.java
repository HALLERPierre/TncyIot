package eu.telecomnancy.tncyiot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by kromer1u on 24/01/17.
 */

public class RestTask extends AsyncTask<URL, Void, String> {
    private HttpURLConnection urlConnection;
    // This is the reference to the associated listener
    private final TaskListener taskListener;


    public RestTask(TaskListener listener) {
        // The listener reference is passed in through the constructor
        this.taskListener = listener;
    }

    public interface TaskListener {
        public void onFinished(String result);
    }



    @Override
    protected String  doInBackground(URL... urls)
    {
        StringBuilder result = new StringBuilder();

        try
        {
            if (urls.length == 0){
                // TODO handle this properly

                throw new Exception("");
            }

            urlConnection = (HttpURLConnection) urls[0].openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(20 * 1000);
            urlConnection.setReadTimeout(20 * 1000);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            return result.toString();
        }
        catch (Exception e)
        {
            // TODO handle this properly
            e.printStackTrace();
            return result.toString();
        }finally {
            urlConnection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        super.onPostExecute(result);
        // In onPostExecute we check if the listener is valid
        if(this.taskListener != null) {

            // And if it is we call the callback function on it.
            this.taskListener.onFinished(result);
        }
    }
}
