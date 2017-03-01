package eu.telecomnancy.tncyiot.Util;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * set the httpconnection for for REST call
 * no business here just technical
 *
 */
public class RestTask extends AsyncTask<URL, Void, String> {
    //client Http
    private HttpURLConnection urlConnection;
    // This is the reference to the associated listener
    private final TaskListener taskListener;
    //context de l'application
    private Context mContext;

    public RestTask(Context appliContext, TaskListener listener) {
        this.mContext = appliContext;
        // The listener reference is passed in through the constructor
        this.taskListener = listener;
    }

    public interface TaskListener {
        void onFinished(String result);
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

        if (result.isEmpty()){
            Toast.makeText(mContext,"No result found", Toast.LENGTH_LONG).show();
        }
        // In onPostExecute we check if the listener is valid
        if(this.taskListener != null) {

            // And if it is we call the callback function on it.
            this.taskListener.onFinished(result);
        }
    }
}
