package com.hci2015.hciproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SplashScreen extends Activity {

    private TextView mText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mText=(TextView)findViewById(R.id.splash_text);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/test.ttf");
        mText.setTypeface(type);
        if(isOnline()){
            new LoadViewTask().execute();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Attenzione l'app richiede una connessione ad internet")
                    .setPositiveButton("reset", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            recreate();
                        }
                    });
            builder.create().show();
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    //To use the AsyncTask, it must be subclassed
    public class LoadViewTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread


        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            /* This is just a code that delays the thread execution 100 times,
             * during 30 milliseconds and updates the current progress. This
             * is where the code that is going to be executed on a background
             * thread must be placed.
             */
            try
            {
                //Get the current thread's token
                synchronized (this)
                {
                    //Initialize an integer (that will act as a counter) to zero
                    int counter = 0;
                    //While the counter is smaller than 100
                    while(counter < 50)
                    {
                        //Wait 30 milliseconds
                        this.wait(30);
                        //Increment the counter
                        counter++;
                        //Set the current progress.
                        //This value is going to be passed to the onProgressUpdate() method.



                    }
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        //Update the progress

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            Intent intent = new Intent(SplashScreen.this,
                    MapsActivity.class);
            startActivity(intent);
            SplashScreen.this.finish();

        }
    }

}
