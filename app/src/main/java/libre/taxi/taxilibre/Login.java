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
import android.widget.TextView;
import net.sf.json.JSONObject;
import java.io.DataInputStream;
import java.io.OutputStream;
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
    TextView resulEnr = null;
    static JSONObject loginData = new JSONObject();
    final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        retour = (Button) findViewById(R.id.retourMain);
        login = (Button) findViewById(R.id.authentication);
        final TextView resulEnreg = (TextView) findViewById(R.id.resultat);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nomUtil = (EditText) findViewById(R.id.nomUtilisateur);
                motPasse = (EditText) findViewById(R.id.motDePasse);

                nomUtilisateur = nomUtil.getText().toString();
                motDePasse = motPasse.getText().toString();
                loginData.put("nomUtilisateur", nomUtilisateur);
                loginData.put("motDePasse", motDePasse);

                if (!loginData.getString("nomUtilisateur").equals("") &&
                        !loginData.getString("motDePasse").equals("") &&
                        (typeChoisi.equals("client") || typeChoisi.equals("chauffeur")))
                new MyAsyncTask().execute();
                else
                resulEnreg.setText("Un ou plusieurs champs vide!!!");
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

    private class MyAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int result = 0;
            try {
                result = postData(loginData);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 200) {
                if (typeChoisi.equals("client")) {
                    Intent intent = new Intent(context, Commande.class);
                    startActivity(intent);
                } else if (typeChoisi.equals("chauffeur")) {
                    Intent intent = new Intent(context, ChauffeurGUI.class);
                    startActivity(intent);
                }
            }else{
                TextView resulEnreg = (TextView) findViewById(R.id.resultat);
                resulEnreg.setText("Nom d'utilisateur n'existe pas!!!!");
            }
        }
    }

    public int postData(JSONObject loginData) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;
        DataInputStream input;

        url = new URL ("http://libretaxi-env.elasticbeanstalk.com/login");
        urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoInput (true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConn.setRequestProperty("Accept", "application/json");
        urlConn.setRequestMethod("POST");

        // Send POST output.
        OutputStream os = urlConn.getOutputStream();
        os.write(loginData.toString().getBytes());
        System.out.println(loginData.toString());

        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println("OK");
        } else {
            System.out.println(urlConn.getResponseCode());
        }

        input = new DataInputStream(urlConn.getInputStream());
        String  response = Utilisateurs.convertStreamToString(input);
        System.out.println(response);
        System.out.println(urlConn.getResponseCode());
        return urlConn.getResponseCode();
    }
}