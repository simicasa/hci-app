package com.hci2015.hciproject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends Activity implements View.OnClickListener {
    private TextView te;
    private ImageSwitcher sw;
    private TextSwitcher descr;
    private TextSwitcher date;
    private GestureDetector gestureDetector;
    private Animation inDaDes,inDaSin;
    private int pos;
    private String idest;
    private List<Immagini> dati = new ArrayList<Immagini>();
    private List<Drawable> immagini = new ArrayList<Drawable>();
    private  List<String> textToShow = new ArrayList<String>();
    private  List<String> dateToShow = new ArrayList<String>();
    private Animation outDaDes,outDaSin;
    private Integer ris=0;
    private TextView testoArea;
    private DialogFragment newFragment;
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle datipassati = getIntent().getExtras();
        idest = datipassati.getString("id");
        sw = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        te=(TextView)findViewById(R.id.Nome);
        descr=(TextSwitcher)findViewById(R.id.descr);
        date=(TextSwitcher)findViewById(R.id.datafoto);

        caricaImmagini ci = new caricaImmagini();
        ci.execute();
        te.setText(datipassati.getString("Nome"));
        inDaDes = AnimationUtils.loadAnimation(this, R.anim.dadesasin);
        inDaSin = AnimationUtils.loadAnimation(this, R.anim.dasinades);
        outDaDes = AnimationUtils.loadAnimation(this, R.anim.outdasinades);
        outDaSin = AnimationUtils.loadAnimation(this, R.anim.outdadesasin);
        gestureDetector = new GestureDetector(this,new MyGestureDetector());
        pos=0;
        gestureListener= new View.OnTouchListener(){
            public boolean onTouch(View v,MotionEvent event){
                return gestureDetector.onTouchEvent(event);
            }
        };

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        descr.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText1 = new TextView(MainActivity.this);
                myText1.setTextSize(15);
                ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                myText1.setLayoutParams(params);
                myText1.setBackgroundResource(R.drawable.placca);
                myText1.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                myText1.setPadding(30, 15, 30, 15);
                myText1.setTextColor(Color.parseColor("#004962"));
                return myText1;
            }
        });
        date.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                ImageSwitcher.LayoutParams params = new ImageSwitcher.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                TextView myText = new TextView(MainActivity.this);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(13);
                myText.setBackgroundResource(R.drawable.placca);
                myText.setPadding(30, 15, 30, 15);
                myText.setLayoutParams(params);
                myText.setTextColor(Color.parseColor("#004962"));
                return myText;
            }
        });

        sw.setOnTouchListener(gestureListener);
        sw.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setPadding(10, 10, 10, 10);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                return myView;
            }
        });
    }



    @Override
    public void onClick(View v) {

    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //float val = Math.abs(e1.getX()-e2.getX());

                if(e1.getX()<e2.getX()){
                    sw.setInAnimation(inDaDes);
                    descr.setInAnimation(inDaDes);
                    date.setInAnimation(inDaDes);
                    sw.setOutAnimation(outDaDes);
                    descr.setOutAnimation(outDaDes);
                    date.setOutAnimation(outDaDes);
                    sw.setImageDrawable(selIMG(1));
                    descr.setText(selText());
                    date.setText(selDate());
                }
                if(e1.getX()>e2.getX()){
                    sw.setInAnimation(inDaSin);
                    descr.setInAnimation(inDaSin);
                    date.setInAnimation(inDaSin);
                    sw.setOutAnimation(outDaSin);
                    descr.setOutAnimation(outDaSin);
                    date.setOutAnimation(outDaSin);
                    sw.setImageDrawable(selIMG(0));
                    descr.setText(selText());
                    date.setText(selDate());
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
    private Drawable selIMG(int dir){
        if(dir==1){
            pos=(pos+1);

        }
        if(dir==0){
            pos=(pos - 1);
        }
        ris=pos%immagini.size();
        ris=Math.abs(ris);
        return immagini.get(ris);
    }

    private String selText(){
        return textToShow.get(Math.abs(ris));
    }

    private String selDate(){
        return dateToShow.get(Math.abs(ris));
    }

    public class Immagini{
        public String Immagine;
        public String Testo;
        public String DataFoto;
    }
    public class caricaImmagini extends AsyncTask<String, Integer, String> {

        private StringBuffer chaine = new StringBuffer("");

        @Override
        protected void onPreExecute(){
            newFragment = new attDialog();
            newFragment.setCancelable(false);
            newFragment.show(getFragmentManager(), "Attendi");
        }

        @Override
        protected String doInBackground(String... sUrl) {
            HttpURLConnection con;
            InputStream fileIn;
            OutputStream saveFl;
            Gson gson = new Gson();

            byte inp[] = new byte[2048];
            int count;

            try {
                URL add = new URL("http://www.ilpatibolo.it//app/prelevaImmagini");
                String daInviare = "id=" + idest;
                con = (HttpURLConnection) add.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Length", "" + Integer.toString(daInviare.getBytes().length));
                con.setDoInput(true);
                con.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(
                        con.getOutputStream());
                wr.writeBytes(daInviare);
                wr.flush();
                wr.close();

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    Reader r = new InputStreamReader(con.getInputStream());
                    String line = "";
                    dati = Arrays.asList(gson.fromJson(r, Immagini[].class));
                    con.disconnect();

                }else{
                    Log.println(Log.ASSERT, "errore connessione",Integer.toString(con.getResponseCode()));

                }

            } catch (Exception ex) {
                Log.println(Log.ERROR, "json", "Failed to parse JSON due to: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            testoArea.setText("download 0/" + dati.size());
            try {
                new DownloadImageTask(sw)
                        .execute();
            } catch (Exception e) {
                Log.println(Log.ERROR, "fail", "Errore : " + e);
            }
        }
    }

    private class DownloadImageTask extends AsyncTask<String, String, String> {
        ImageSwitcher bmImage;

        public DownloadImageTask(ImageSwitcher bmImage) {
            this.bmImage = bmImage;
        }

        protected String doInBackground(String... params) {
            List<Immagini>  url = dati;
            Bitmap mIcon11 = null;
            int nelem=0;
            try {
                for (Immagini pe : url){
                    String elem = "http://www.ilpatibolo.it//" +  pe.Immagine;
                    InputStream in = new java.net.URL(elem).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    mIcon11.setDensity(Bitmap.DENSITY_NONE);
                    Drawable d = new BitmapDrawable(mIcon11);
                    immagini.add(d);
                    textToShow.add(pe.Testo);
                    String[] parts = pe.DataFoto.split("-");
                    dateToShow.add(parts[2] + "/" + parts[1] + "/" + parts[0]);
                    nelem=nelem+1;
                    publishProgress("" + immagini.size());
                }


            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        protected void onProgressUpdate(String... progress) {
            testoArea.setText("download" + progress[0]  + "/" + dati.size());
        }
        protected void onPostExecute(String str) {
            pos=0;
            bmImage.setImageDrawable(immagini.get(0));
            descr.setText(textToShow.get(0));
            date.setText(dateToShow.get(0));
            newFragment.dismiss();
        }
    }
    public class attDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View v = getLayoutInflater().inflate(R.layout.dialogatt, null);
            builder.setView(v);
            testoArea = (TextView) (v.findViewById(R.id.testAtt));
            return builder.create();
        }
    }

}