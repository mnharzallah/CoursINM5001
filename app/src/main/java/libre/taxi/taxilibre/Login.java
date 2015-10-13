/*
 * copyright Mohamad Yehia.
 */
package libre.taxi.taxilibre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.os.AsyncTask;
import android.widget.EditText;
import net.sf.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;
/**
 *
 * @author Mohamad
 */
public class Login extends Activity {

    Button retour = null;
    Button login = null;
    EditText nomUtil = null;
    EditText motPasse = null;
    String nomUtilisateur;
    String motDePasse;
    String typeChoisi = "";
    JSONObject loginData = new JSONObject();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        retour = (Button) findViewById(R.id.retourMain);
        login = (Button) findViewById(R.id.authentication);
        final Context context = this;

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nomUtil = (EditText) findViewById(R.id.nomUtilisateur);
                motPasse = (EditText) findViewById(R.id.motDePasse);

                nomUtilisateur = nomUtil.getText().toString();
                motDePasse = motPasse.getText().toString();
                loginData.put("nomUtilisateur", nomUtilisateur);
                loginData.put("motDePasse", motDePasse);
                if (typeChoisi.equals("client"))
                    loginData.put("type", "client");
                else if (typeChoisi.equals("chauffeur"))
                    loginData.put("type", "chauffeur");

                new MyAsyncTask().execute();
            }
        });

        retour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, TaxiLibre.class);
                startActivity(intent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.cocherClient:
                if (checked) 
                {
                    typeChoisi = "client";
                    break;
                }
            case R.id.cocherChauffeur:
                if (checked) 
                {
                    typeChoisi = "chauffeur";
                    break;
                }
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                postData(loginData);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void postData(JSONObject loginData) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;
        DataOutputStream printout = null;
        DataInputStream input;

        url = new URL ("http://libretaxi-env.elasticbeanstalk.com/login");
        urlConn = (HttpURLConnection) url.openConnection();

        //urlConn.setDoInput (true);
        urlConn.setDoOutput (true);
        urlConn.setUseCaches(false);
        urlConn.setRequestProperty("Content-Type", "application/json");

        // Send POST output.
        printout = new DataOutputStream(urlConn.getOutputStream ());
        printout.writeUTF(loginData.toString());

        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("OK");
        } else {
            System.out.println("NOT OK");
        }

        input = new DataInputStream(urlConn.getInputStream());
        String  response = Utilisateurs.convertStreamToString(input);
        System.out.println(response);

        printout.flush();
        printout.close();

    }
}