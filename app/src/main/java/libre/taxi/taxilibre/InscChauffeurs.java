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
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;


public class InscChauffeurs extends Activity implements TextWatcher {

    private EditText textNomChauffeur;
    private EditText textPrenomChauffeur;
    private EditText textTelChauffeur;
    private EditText textMatChauffeur;
    private EditText textUtilChauffeur;
    private EditText textPasseChauffeur;


    String nomChauffeur;
    String prenomChauffeur;
    String matriculeChauffeur;
    String telChauffeur;
    String utilChauffeur;
    String passeChauffeur;
    final Context context = this;
    Button retourInsc = null;
    static JSONObject inscriptionChauffeur = new JSONObject();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insc_chauffeur);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        final Button button = (Button) findViewById(R.id.Enreg_Chauffeur);

        textNomChauffeur = (EditText) findViewById(R.id.nomChauffeur);
        textNomChauffeur.addTextChangedListener(this);

        textPrenomChauffeur = (EditText) findViewById(R.id.prenomChauffeur);
        textPrenomChauffeur.addTextChangedListener(this);

        textTelChauffeur = (EditText) findViewById(R.id.telChauffeur);
        textTelChauffeur.addTextChangedListener(this);

        textMatChauffeur = (EditText) findViewById(R.id.matriculeChauffeur);
        textMatChauffeur.addTextChangedListener(this);

        textPasseChauffeur = (EditText) findViewById(R.id.passeChauffeur);
        textPasseChauffeur.addTextChangedListener(this);

        final TextView resulEnreg = (TextView) findViewById(R.id.resultat);
        retourInsc = (Button) findViewById(R.id.retourInsc);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Utilisateurs.isOnline(context)) {
                    nomChauffeur = textNomChauffeur.getText().toString();
                    prenomChauffeur = textPrenomChauffeur.getText().toString();
                    telChauffeur = textTelChauffeur.getText().toString();
                    matriculeChauffeur = textMatChauffeur.getText().toString();
                    passeChauffeur = textPasseChauffeur.getText().toString();

                    Chauffeurs chauffeur = new Chauffeurs(nomChauffeur, prenomChauffeur, telChauffeur,
                            passeChauffeur, matriculeChauffeur);
                    inscriptionChauffeur.put("nom", chauffeur.nom);
                    inscriptionChauffeur.put("prenom", chauffeur.prenom);
                    inscriptionChauffeur.put("telephone", chauffeur.telephone);
                    inscriptionChauffeur.put("type", "chauffeur");
                    inscriptionChauffeur.put("motDePasse", chauffeur.motDePasse);
                    inscriptionChauffeur.put("nomUtilisateur", chauffeur.matricule);

                    if (!inscriptionChauffeur.getString("nom").equals("") &&
                            !inscriptionChauffeur.getString("prenom").equals("") &&
                            !inscriptionChauffeur.getString("telephone").equals("") &&
                            !inscriptionChauffeur.getString("motDePasse").equals("") &&
                            !inscriptionChauffeur.getString("nomUtilisateur").equals(""))
                        new MyAsyncTask().execute();
                    else
                        resulEnreg.setText("Un ou plusieurs champs vide!!!");
                } else
                    resulEnreg.setText("Verifier votre connexion internet!!!");
            }
        });

        retourInsc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, TaxiLibre.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        textNomChauffeur.setError(null);
        textPrenomChauffeur.setError(null);
        textTelChauffeur.setError(null);
        textMatChauffeur.setError(null);
        textPasseChauffeur.setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        textNomChauffeur.setError(null);
        textPrenomChauffeur.setError(null);
        textTelChauffeur.setError(null);
        textMatChauffeur.setError(null);
        textPasseChauffeur.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!Utilisateurs.estUneEntreeValide(textNomChauffeur.getText().toString()) && s == textNomChauffeur.getEditableText()) {
            textNomChauffeur.setError("Veuillez entrer au moins 2 caracteres");
        } else if (!Utilisateurs.estUneEntreeValide(textPrenomChauffeur.getText().toString()) && s == textPrenomChauffeur.getEditableText()) {
            textPrenomChauffeur.setError("Veuillez entrer au moins 2 caracteres");
        } else if (!Utilisateurs.estUnNumeroDeTelValide(textTelChauffeur.getText().toString()) && s == textTelChauffeur.getEditableText()) {
            textTelChauffeur.setError("Veuillez entrer un numero de telephone valide");
        } else if (!Chauffeurs.validerMatricule(textMatChauffeur.getText().toString()) && s == textMatChauffeur.getEditableText()) {
            textMatChauffeur.setError("Veuillez entrer 10 chiffres");
        } else if (!Utilisateurs.estUnMotDePasse(textPasseChauffeur.getText().toString()) && s == textPasseChauffeur.getEditableText()) {
            textPasseChauffeur.setError("Veuillez entrer entrer 4 et 8 caracteres");
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int resultat = 0;
            try {
                resultat = postData(inscriptionChauffeur);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resultat;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 201) {
                Intent intent = new Intent(context, ChauffeurGUI.class);
                startActivity(intent);
            } else {
                TextView resulEnreg = (TextView) findViewById(R.id.resultat);
                resulEnreg.setText("Nom d'utilisateur existe!!!!");
            }
        }
    }

    public int postData(JSONObject inscriptionChauffeur) throws IOException {

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
        os.write(inscriptionChauffeur.toString().getBytes());
        System.out.println(inscriptionChauffeur.toString());

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
        Intent intent = new Intent(context, TaxiLibre.class);
        startActivity(intent);
        finish();
    }
}