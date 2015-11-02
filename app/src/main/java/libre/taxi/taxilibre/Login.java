/*
 * copyright Mohamad Yehia.
 */
package libre.taxi.taxilibre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
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

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author Mohamad
 */
public class Login extends Activity implements TextWatcher {

    Button retour = null;
    Button login = null;

    EditText nomUtil = null;
    EditText motPasse = null;
    TextView resulEnr = null;

    String nomUtilInput;
    String motDePasse;
    String typeChoisi = "client";

    static JSONObject loginData = new JSONObject();
    final Context context = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        retour = (Button) findViewById(R.id.retourMain);
        login = (Button) findViewById(R.id.authentication);
        final TextView resulEnreg = (TextView) findViewById(R.id.resultat);

        nomUtil = (EditText) findViewById(R.id.nomUtil);
        nomUtil.addTextChangedListener(this);
        motPasse = (EditText) findViewById(R.id.motDePasse);

        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Utilisateurs.isOnline(context)) {
                    nomUtilInput = nomUtil.getText().toString();
                    loginData.put("nomUtilisateur", nomUtilInput);

                    motDePasse = motPasse.getText().toString();
                    loginData.put("motDePasse", motDePasse);

                    if (!loginData.getString("nomUtilisateur").equals("") &&
                            !loginData.getString("motDePasse").equals("") &&
                            (typeChoisi.equals("client") || typeChoisi.equals("chauffeur")))
                        new MyAsyncTask().execute();
                    else
                        resulEnreg.setText("Un ou plusieurs champs vide!!!");
                } else
                    resulEnreg.setText("Verifier votre connexion internet!!!");
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
                if (checked) {
                    typeChoisi = "client";
                    nomUtil.setHint("Courriel");
                    nomUtil.setError("");
                    nomUtil.setText("");
                    break;
                }
            case R.id.cocherChauffeur:
                if (checked) {
                    typeChoisi = "chauffeur";
                    nomUtil.setHint("Matricule");
                    nomUtil.setError("");
                    nomUtil.setText("");
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
            } else {
                TextView resulEnreg = (TextView) findViewById(R.id.resultat);
                resulEnreg.setText("Nom d'utilisateur n'existe pas!!!!");
            }
        }
    }

    public int postData(JSONObject loginData) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;
        DataInputStream input;

        url = new URL("http://libretaxi-env.elasticbeanstalk.com/login");
        urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoInput(true);
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
        String response = Utilisateurs.convertStreamToString(input);
        System.out.println(response);
        System.out.println(urlConn.getResponseCode());
        return urlConn.getResponseCode();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        nomUtil.setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if (typeChoisi.equals("client")) {
            if (!Utilisateurs.estUnCourrielValide(nomUtil.getText().toString()) && s == nomUtil.getEditableText()) {
                nomUtil.setError("Veuillez entrer un courriel valide");
            }
        } else if (typeChoisi.equals("chauffeur")) {
            if (!Chauffeurs.validerMatricule(nomUtil.getText().toString()) && s == nomUtil.getEditableText()) {
                nomUtil.setError("Veuillez une matricule de 10 chiffres");
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}