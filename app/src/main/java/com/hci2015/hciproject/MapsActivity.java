package com.hci2015.hciproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final LatLng STARTING_POINT = new LatLng(40.827924, 14.193018); //Coordinate iniziali
    public Marker io = null;
    //variabili geolocalizazione
    private String providerId = LocationManager.GPS_PROVIDER;//Tipo di provider gps o network
    private List<PuntoSuMappa> dati = new ArrayList<PuntoSuMappa>();
    private LocationManager locationManager = null;
    private static final int MIN_DIST = 10; //Distanza minima aggoirnamento
    private static final int MIN_PERIOD = 0; //tempo minimo aggiornamento
    private View infowindow;
    public TextView t;
    public ImageView img;
    private Map<String, String> Id = new HashMap<String, String>();
    private Map<String, String> NuovoId = new HashMap<String, String>();
    private Map<String, Bitmap> immagine_marker = new HashMap<String, Bitmap>();
    public Button locMy;

    //prova ricerca
    public EditText et;
    public ListView lv;
    public ArrayList<String> array_sort;
    int textlength = 0;
    public ArrayAdapter<String> adapter;
    public Map<Integer,PuntoSuMappa>associativo=new HashMap<Integer,PuntoSuMappa>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        infowindow = getLayoutInflater().inflate(R.layout.windowlayout, null);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoAdapter());
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                try {
                    if (!marker.getTitle().contentEquals("Sono qui")) {
                        Intent i = new Intent(MapsActivity.this, MainActivity.class);
                        i.putExtra("id", Id.get(marker.getId()));
                        i.putExtra("Nome", marker.getTitle());
                        startActivity(i);
                    }
                } catch (Exception e) {
                    Log.println(Log.ASSERT, "not exist", "elem non");
                }

            }
        });

        t = (TextView) infowindow.findViewById(R.id.NomeLuogo);
        img = (ImageView) infowindow.findViewById(R.id.imgMarker);
        locMy = (Button) findViewById(R.id.dvS);
        //prova ricerca
        et = (EditText) findViewById(R.id.EditText01);
        lv = (ListView) findViewById(R.id.list);
        array_sort = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array_sort);
        et.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Abstract Method of TextWatcher Interface.
            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {
                // Abstract Method of TextWatcher Interface.
            }

            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                Log.println(Log.ASSERT, "textch", "test");
                textlength = et.getText().length();
                array_sort.clear();
                associativo.clear();
                int i = 0;
                if (dati.size() > 0) {

                    for (PuntoSuMappa luoghi : dati) {

                        if (textlength <= luoghi.nome_luogo.length()) {
                            /***
                             * If you choose the below block then it will act like a
                             * Like operator in the Mysql
                             */
                            if (luoghi.nome_luogo.toLowerCase().contains(
                                    et.getText().toString().toLowerCase().trim())) {
                                array_sort.add(i, luoghi.nome_luogo);
                                associativo.put(i, luoghi);
                                i++;
                            }
                        }
                    }
                }
                AppendList(array_sort);
                if (et.getText().toString().matches("")) {
                    array_sort.clear();
                    associativo.clear();
                    AppendList(array_sort);
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0,
                                    View arg1, int position, long arg3) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(associativo.get(position).latitudine,
                        associativo.get(position).longitudine), 15));
                array_sort.clear();
                associativo.clear();
                AppendList(array_sort);
                et.setText("");
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        locMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(io.getPosition().latitude,io.getPosition().longitude), 15));
            }
        });
    }

    public void AppendList(ArrayList<String> str) {
        lv.setAdapter(adapter);
    }


    class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoContents(Marker arg0) {
            t.setText(arg0.getTitle());
            img.setImageBitmap(immagine_marker.get(arg0.getId()));
            return infowindow;
        }

        @Override
        public View getInfoWindow(Marker arg0) {
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        //Posizione di start della mappa
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(STARTING_POINT, 5));
        //Animazione per lo zoom
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 1000, null);
        //Aggiungo punti standart sulla mappa
        CaricaMappe cm = new CaricaMappe();
        cm.execute();
        LocationManager netLog = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!netLog.isProviderEnabled(providerId)) {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        } else {
            netLog.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, netLoglist, null);
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(providerId)) {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        } else {
            locationManager.requestLocationUpdates(providerId, MIN_PERIOD, MIN_DIST, this.locationListener);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this.locationListener);
    }

    private LocationListener netLoglist = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
            if (io != null) {
                io.remove();
            }
            io = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Sono qui")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo))

            );
        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            if (io != null) {
                io.remove();
            }
            io = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude()))
                            .title("Sono qui")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo))
            );
        }
    };


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setOnMarkerClickListener(this);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    public class PuntoSuMappa {
        public int id;
        public float latitudine;
        public float longitudine;
        public String nome_luogo;
        public String img;
    }

    public class CaricaMappe extends AsyncTask<String, Integer, String> {

        private StringBuffer chaine = new StringBuffer("");



        @Override
        protected String doInBackground(String... sUrl) {
            HttpURLConnection con;
            InputStream fileIn;
            OutputStream saveFl;
            Gson gson = new Gson();

            byte inp[] = new byte[2048];
            int count;

            try {
                URL add = new URL("http://www.ilpatibolo.it//app/prelevaMarker");
                // String daInviare = "ANNO=" + AnnoMappa;
                con = (HttpURLConnection) add.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(
                        con.getOutputStream());
                wr.flush();
                wr.close();

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    Reader r = new InputStreamReader(con.getInputStream());
                    dati = Arrays.asList(gson.fromJson(r, PuntoSuMappa[].class));
                    con.disconnect();

                }

            } catch (Exception ex) {
                Log.println(Log.ERROR, "json", "Failed to parse JSON due to: " + ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            for (PuntoSuMappa pe : dati) {
                Marker p = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(pe.latitudine, pe.longitudine))
                                .title(pe.nome_luogo)
                );
                Id.put(p.getId(), Integer.toString(pe.id));
                NuovoId.put(Integer.toString(pe.id), p.getId());

            }


            try {
                new DownloadImageTask().execute();


            } catch (Exception e) {
                Log.println(Log.ERROR, "fail", "Errore : " + e);
            }

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, String> {


        protected String doInBackground(String... params) {
            Bitmap mIcon11 = null;
            try {
                for (PuntoSuMappa pe : dati) {
                    String elem = "http://www.ilpatibolo.it//" + pe.img;
                    InputStream in = new java.net.URL(elem).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    immagine_marker.put(NuovoId.get(Integer.toString(pe.id)), mIcon11);
                }


            } catch (Exception e) {
                Log.e("ErrorDownl", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String str) {
        }
    }
}

