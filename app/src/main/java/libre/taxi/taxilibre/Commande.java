package libre.taxi.taxilibre;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.location.Criteria;
import android.location.LocationManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import net.sf.json.JSONObject;

public class Commande extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap googleMap;
    TextView bienvenue = null;
    Button commander = null;
    Button retour = null;
    static JSONObject commanderCh = new JSONObject();
    Double longitude;
    Double latitude;
    TextView result = null;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    Button btnFusedLocation;
    TextView location;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    String courrielClient;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commande);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        result = (TextView) findViewById(R.id.resultat);

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
        commander = (Button) findViewById(R.id.commande);
        location = (TextView) findViewById(R.id.location);
        retour = (Button) findViewById(R.id.retourInsc);

        if (!Login.loginData.isEmpty() && !Login.loginData.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + Login.loginData.getString("nomUtilisateur")
                    /*+ " "+ Login.loginData.getString("prenom")*/);
            courrielClient = Login.loginData.getString("nomUtilisateur");
            Login.loginData.put("nomUtilisateur", "");
            //Login.loginData.put("prenom", "");
        } else if (!InscClients.inscriptionClient.isEmpty() && !InscClients.inscriptionClient.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + InscClients.inscriptionClient.getString("nomUtilisateur")
                    /*+" " + InscClients.inscriptionClient.getString("prenom")*/);
            courrielClient = InscClients.inscriptionClient.getString("nomUtilisateur");
            InscClients.inscriptionClient.put("nomUtilisateur", "");
            //InscClients.inscriptionClient.put("prenom", "");
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

        btnFusedLocation = (Button) findViewById(R.id.commande);
        btnFusedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                commanderCh.put("nomUtilisateur", courrielClient);
                if (!commanderCh.getString("nomUtilisateur").equals("") &&
                        !commanderCh.getString("latitude").equals("") &&
                        !commanderCh.getString("longitude").equals("")) {
                    new MyAsyncTask().execute();
                    result.setText("Commande envoyee!!!");
                }
                else
                    result.setText("Position non disponible!!!");
            }
        });

        final Context context = this;
        retour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TaxiLibre.class);
                startActivity(intent);
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
        commanderCh.put("longitude", longitude);
        commanderCh.put("latitude", latitude);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        locationTv.setText("Latitude:" + latitude + ", Longitude:" + longitude);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int result = 0;
            try {
                result = postData(commanderCh);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {

        }
    }

    public int postData(JSONObject inscriptionClient) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;
        DataInputStream input;

        url = new URL ("http://libretaxi-env.elasticbeanstalk.com/");
        urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoInput (true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConn.setRequestProperty("Accept", "application/json");
        urlConn.setRequestMethod("POST");

        // Send POST output.
        OutputStream os = urlConn.getOutputStream();
        os.write(inscriptionClient.toString().getBytes());
        System.out.println(inscriptionClient.toString());

        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("OK");
        } else {
            System.out.println(urlConn.getResponseCode());
        }

        input = new DataInputStream(urlConn.getInputStream());
        String  response = Utilisateurs.convertStreamToString(input);
        System.out.println(response);
        return urlConn.getResponseCode();
    }
}