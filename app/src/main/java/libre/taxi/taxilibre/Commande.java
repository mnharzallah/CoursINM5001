package libre.taxi.taxilibre;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.EditText;
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
    //Double longitude = -73.56849;
    //Double latitude = 45.50866;
    Double longitude = -73.604245;
    Double latitude = 45.626550;
    TextView result = null;
    EditText commentaire = null;
    String commentaireClient="";
    Context context = this;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    Button btnFusedLocation;
    TextView location;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    String courrielClient;
    String motDePasse;

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
        //commanderCh.put("longitude", -73.56849);
        //commanderCh.put("latitude", 45.50866);
        commanderCh.put("longitude", -73.624867);
        commanderCh.put("latitude", 45.602742);
        //commanderCh.put("longitude", -73.53916);
        //commanderCh.put("latitude", 45.5911);

        /* Afficher google map */
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        ViewGroup.LayoutParams params = supportMapFragment.getView().getLayoutParams();
        params.height = 440;
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
        commentaire = (EditText) findViewById(R.id.commentaire);
        retour = (Button) findViewById(R.id.deconnecter);

        if (!Login.loginData.isEmpty() && !Login.loginData.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + Login.loginData.getString("nomUtilisateur"));
            courrielClient = Login.loginData.getString("nomUtilisateur");
            motDePasse = Login.loginData.getString("motDePasse");
            Login.loginData.put("nomUtilisateur", "");
        } else if (!InscClients.inscriptionClient.isEmpty() && !InscClients.inscriptionClient.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + InscClients.inscriptionClient.getString("nomUtilisateur"));
            courrielClient = InscClients.inscriptionClient.getString("nomUtilisateur");
            motDePasse = InscClients.inscriptionClient.getString("motDePasse");
            InscClients.inscriptionClient.put("nomUtilisateur", "");
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
                if (Utilisateurs.isOnline(context)) {
                    commanderCh.put("nomUtilisateur", courrielClient);
                    commanderCh.put("motDePasse", motDePasse);
                    if (!commanderCh.getString("nomUtilisateur").equals("") &&
                            !commanderCh.getString("motDePasse").equals("") &&
                            !commanderCh.getString("latitude").equals("") &&
                            !commanderCh.getString("longitude").equals("")) {
                        commentaireClient = commentaire.getText().toString();
                        commanderCh.put("commentaireClient",commentaireClient);
                        new MyAsyncTask().execute();
                    } else
                        result.setText("Position non disponible!!!");
                } else
                    result.setText("Verifier votre connexion internet!!!");
            }
        });

        final Context context = this;
        retour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TaxiLibre.class);
                startActivity(intent);
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

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String result = "";
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
        protected void onPostExecute(String result) {
            TextView resulEnreg = (TextView) findViewById(R.id.resultat);
            if (result.substring(0,3).equals("202")) {
                resulEnreg.setText(result.substring(result.indexOf("|") + 1, result.length()));
            }else if (result.substring(0,3).equals("503")){
                resulEnreg.setText("Aucun chauffeur n'est disponible dans votre secteur!!!");
            }
        }
    }

    public String postData(JSONObject commanderCh) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;
        DataInputStream input;

        url = new URL("http://libretaxi-env.elasticbeanstalk.com/commande");
        urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConn.setRequestProperty("Accept", "application/json");
        urlConn.setRequestMethod("POST");

        // Send POST output.
        OutputStream os = urlConn.getOutputStream();
        os.write(commanderCh.toString().getBytes());
        System.out.println(commanderCh.toString());

        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            input = new DataInputStream(urlConn.getInputStream());
            String response = Utilisateurs.convertStreamToString(input);
            System.out.println(response);
            System.out.println(urlConn.getResponseCode());
            return urlConn.getResponseCode() + "|" + response;
        } else {
            System.out.println(urlConn.getResponseCode());
            return urlConn.getResponseCode()+"";
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, TaxiLibre.class);
        startActivity(intent);
        this.finish();
    }
}