package libre.taxi.taxilibre;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.sf.json.JSONObject;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;


public class InscClients extends Activity implements TextWatcher {

    private EditText textNomClient;
    private EditText textPrenomClient;
    private EditText textPasseClient;
    private EditText textTelClient;
    private EditText textCourriel;

    String nomClient;
    String prenomClient;
    String telClient;
    String passeClient;
    String courriel;
    Button retourInsc = null;
    Context context = this;
    static JSONObject inscriptionClient = new JSONObject();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insc_client);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        final Button button = (Button) findViewById(R.id.button_send);

        textNomClient = (EditText) findViewById(R.id.nomClient);
        textNomClient.addTextChangedListener(this);

        textPrenomClient = (EditText) findViewById(R.id.prenomClient);
        textPrenomClient.addTextChangedListener(this);

        textTelClient = (EditText) findViewById(R.id.telClient);
        textTelClient.addTextChangedListener(this);

        textCourriel = (EditText) findViewById(R.id.courriel);
        textCourriel.addTextChangedListener(this);

        textPasseClient = (EditText) findViewById(R.id.passeClient);
        textPasseClient.addTextChangedListener(this);

        final TextView resulEnreg = (TextView) findViewById(R.id.resultat);
        retourInsc = (Button) findViewById(R.id.retourInsc);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Utilisateurs.isOnline(context)) {
                    nomClient = textNomClient.getText().toString();
                    prenomClient = textPrenomClient.getText().toString();
                    telClient = textTelClient.getText().toString();
                    courriel = textCourriel.getText().toString();
                    passeClient = textPasseClient.getText().toString();

                    Clients client = new Clients(nomClient, prenomClient, telClient, passeClient, courriel);
                    inscriptionClient.put("nom", client.nom);
                    inscriptionClient.put("prenom", client.prenom);
                    inscriptionClient.put("telephone", client.telephone);
                    inscriptionClient.put("type", "client");
                    inscriptionClient.put("motDePasse", client.motDePasse);
                    inscriptionClient.put("nomUtilisateur", client.courriel);
                    if (!inscriptionClient.getString("nom").equals("") &&
                            !inscriptionClient.getString("prenom").equals("") &&
                            !inscriptionClient.getString("telephone").equals("") &&
                            !inscriptionClient.getString("motDePasse").equals("") &&
                            !inscriptionClient.getString("nomUtilisateur").equals(""))
                        new MyAsyncTask().execute();
                    else
                        resulEnreg.setText("Un ou plusieurs champs vide!!!");
                } else
                    resulEnreg.setText("Verifier votre connexion internet!!!");
            }
        });

        retourInsc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, Inscription.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        textNomClient.setError(null);
        textPrenomClient.setError(null);
        textTelClient.setError(null);
        textCourriel.setError(null);
        textPasseClient.setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!Utilisateurs.estUneEntreeValide(textNomClient.getText().toString()) && s == textNomClient.getEditableText()) {
            textNomClient.setError("Veuillez entrer au moins 2 caracteres");
        } else if (!Utilisateurs.estUneEntreeValide(textPrenomClient.getText().toString()) && s == textPrenomClient.getEditableText()) {
            textPrenomClient.setError("Veuillez entrer au moins 2 caracteres");
        } else if (!Utilisateurs.estUnNumeroDeTelValide(textTelClient.getText().toString()) && s == textTelClient.getEditableText()) {
            textTelClient.setError("Veuillez entrer un numero de telephone valide");
        } else if (!Utilisateurs.estUnCourrielValide(textCourriel.getText().toString()) && s == textCourriel.getEditableText()) {
            textCourriel.setError("Veuillez entrer une addresse courriel valide");
        } else if (!Utilisateurs.estUnMotDePasse(textPasseClient.getText().toString()) && s == textPasseClient.getEditableText()) {
            textPasseClient.setError("Veuillez entrez un mot de passe entre 4 et 8 caracteres");
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int result = 0;
            try {
                result = postData(inscriptionClient);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 201) {
                Intent intent = new Intent(context, Commande.class);
                startActivity(intent);
            } else {
                TextView resulEnreg = (TextView) findViewById(R.id.resultat);
                resulEnreg.setText("Nom d'utilisateur existe!!!!");
            }
        }
    }

    public int postData(JSONObject inscriptionClient) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;
        DataInputStream input;

        url = new URL("http://libretaxi-env.elasticbeanstalk.com/");
        urlConn = (HttpURLConnection) url.openConnection();

        urlConn.setDoInput(true);
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
        String response = Utilisateurs.convertStreamToString(input);
        System.out.println(response);
        return urlConn.getResponseCode();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(context, Inscription.class);
        startActivity(intent);
        finish();
    }
}