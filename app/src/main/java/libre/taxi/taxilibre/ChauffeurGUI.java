package libre.taxi.taxilibre;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.location.Location;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import android.location.Criteria;
import android.location.LocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import net.sf.json.JSONObject;

public class ChauffeurGUI extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMarkerClickListener {

    GoogleMap googleMap;
    TextView bienvenue = null;
    Button retour = null;
    Button accepter = null;
    Button refuser = null;
    static JSONObject positionAjour = new JSONObject();
    Double longitude;
    Double latitude;
    Double longDest;
    Double latDest;
    TextView result = null;
    protected PowerManager.WakeLock mWakeLock;
    Context context = this;
    static Marker myMarker;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 60000;
    private static final long FASTEST_INTERVAL = 60000;
    TextView location;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    String matriculeChauffeur;
    String motDePasseChauffeur;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setSmallestDisplacement(30);
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chauffeur);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        result = (TextView) findViewById(R.id.resultat);

        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        /* Afficher google map */
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        ViewGroup.LayoutParams params = supportMapFragment.getView().getLayoutParams();
        params.height = 540;
        supportMapFragment.getView().setLayoutParams(params);

        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location locationMap = locationManager.getLastKnownLocation(bestProvider);

        if (location != null) {
            onLocationChanged(locationMap);
        }

        bienvenue = (TextView) findViewById(R.id.bienvenue);
        location = (TextView) findViewById(R.id.location);
        retour = (Button) findViewById(R.id.deconnecter);
        accepter = (Button) findViewById(R.id.accept);
        accepter.setVisibility(View.INVISIBLE);
        refuser = (Button) findViewById(R.id.refuse);
        refuser.setVisibility(View.INVISIBLE);

        if (!Login.loginData.isEmpty() && !Login.loginData.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + Login.loginData.getString("nomUtilisateur"));
            matriculeChauffeur = Login.loginData.getString("nomUtilisateur");
            motDePasseChauffeur = Login.loginData.getString("motDePasse");
            Login.loginData.put("nomUtilisateur", "");
        }
        else if (!InscChauffeurs.inscriptionChauffeur.isEmpty() && !InscChauffeurs.inscriptionChauffeur.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + InscChauffeurs.inscriptionChauffeur.getString("nomUtilisateur"));
            matriculeChauffeur = InscChauffeurs.inscriptionChauffeur.getString("nomUtilisateur");
            motDePasseChauffeur = InscChauffeurs.inscriptionChauffeur.getString("motDePasse");
            InscChauffeurs.inscriptionChauffeur.put("nomUtilisateur", "");
        }

        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

                final Context context = this;

        /**
         * s'executent a la reception de la commande
         */
        latDest = 45.50866;
        longDest = -73.56849;
        googleMap.setOnMarkerClickListener(this);
        accepter.setVisibility(View.VISIBLE);
        refuser.setVisibility(View.VISIBLE);


        accepter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myMarker = taxi(latDest, longDest);
                accepter.setVisibility(View.INVISIBLE);
                refuser.setVisibility(View.INVISIBLE);
            }
        });

        refuser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                accepter.setVisibility(View.INVISIBLE);
                refuser.setVisibility(View.INVISIBLE);
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TaxiLibre.class);
                startActivity(intent);
                positionAjour.put("nomUtilisateur", matriculeChauffeur);
                positionAjour.put("motDePasse", motDePasseChauffeur);
                positionAjour.put("longitude", 0.0);
                positionAjour.put("latitude", 0.0);
                positionAjour.put("disponible", "N");
                new MyAsyncTask().execute();
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
        positionAjour.put("nomUtilisateur", matriculeChauffeur);
        positionAjour.put("motDePasse", motDePasseChauffeur);
        positionAjour.put("longitude", 0.0);
        positionAjour.put("latitude", 0.0);
        positionAjour.put("disponible", "N");
        new MyAsyncTask().execute();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView locationTv = (TextView) findViewById(R.id.latlongLocation);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        positionAjour.put("nomUtilisateur", matriculeChauffeur);
        positionAjour.put("motDePasse", motDePasseChauffeur);
        positionAjour.put("longitude", longitude);
        positionAjour.put("latitude", latitude);
        positionAjour.put("disponible", "Y");
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
        new MyAsyncTask().execute();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(myMarker))
        {
            String uri = "http://maps.google.com/maps?saddr="
                    + positionAjour.getString("latitude") + ","
                    + positionAjour.getString("longitude") + "&daddr="
                    + latDest + "," + longDest;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }
        return true;
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int result = 0;
            try {
                result = postData(positionAjour);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            final TextView resulEnreg = (TextView) findViewById(R.id.resultat);

            long currentTime=System.currentTimeMillis(); //getting current time in millis
            //converting it into user readable format
            Calendar cal=Calendar.getInstance();
            cal.setTimeInMillis(currentTime);
            String showTime=String.format("%1$tI:%1$tM:%1$tS %1$Tp",cal);//shows time in format 10:30:45 am
            if (result == 202) {
                resulEnreg.setText("Derniere mise a jour de Position" + " " + showTime);
            }
            else {
                if (Utilisateurs.isOnline(context))
                    resulEnreg.setText("Position non disponible!!!");
                else
                    resulEnreg.setText("Verifier votre connexion internet!!!");
            }
            resulEnreg.setVisibility(View.VISIBLE);
            resulEnreg.postDelayed(new Runnable() {
                public void run() {
                    resulEnreg.setVisibility(View.INVISIBLE);
                }
            }, 7000);
        }
    }

    public int postData(JSONObject positionAjour) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;

        url = new URL ("http://libretaxi-env.elasticbeanstalk.com/chauffeur");
        urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConn.setRequestProperty("Accept", "application/json");
        urlConn.setRequestMethod("PUT");

        // Send POST output.
        OutputStream os = urlConn.getOutputStream();
        os.write(positionAjour.toString().getBytes());
        System.out.println(positionAjour.toString());

        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_ACCEPTED) {
            System.out.println("J'ai reçu la mise à jour!");
        } else {
            System.out.println(urlConn.getResponseCode());
        }

        return urlConn.getResponseCode();
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, TaxiLibre.class);
        startActivity(intent);
        this.finish();
    }

    public Marker taxi(Double latitude, Double longitude){

        Marker destinationMarker;
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
        LatLng latLngDest = new LatLng(latitude, longitude);
        destinationMarker = googleMap.addMarker(new MarkerOptions().position(latLngDest).title("Customer location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngDest));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mp.start();
        return destinationMarker;
    }
}