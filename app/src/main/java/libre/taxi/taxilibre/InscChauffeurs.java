package libre.taxi.taxilibre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.HttpURLConnection;


public class InscChauffeurs extends Activity implements TextWatcher{

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

        final Button button = (Button) findViewById(R.id.Enreg_Chauffeur);

        textNomChauffeur = (EditText) findViewById(R.id.nomChauffeur);
        textNomChauffeur.addTextChangedListener(this);

        textPrenomChauffeur = (EditText) findViewById(R.id.prenomChauffeur);
        textPrenomChauffeur.addTextChangedListener(this);

        textTelChauffeur = (EditText) findViewById(R.id.telChauffeur);
        textTelChauffeur.addTextChangedListener(this);

        textMatChauffeur = (EditText) findViewById(R.id.matriculeChauffeur);

        textUtilChauffeur = (EditText) findViewById(R.id.utilChauffeur);
        textUtilChauffeur.addTextChangedListener(this);

        textPasseChauffeur = (EditText) findViewById(R.id.passeChauffeur);
        textPasseChauffeur.addTextChangedListener(this);

        final TextView resulEnreg = (TextView) findViewById(R.id.resultat);
        retourInsc = (Button) findViewById(R.id.retourInsc);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                java.sql.Connection conn = null;
                java.sql.PreparedStatement stmt = null;

                nomChauffeur = textNomChauffeur.getText().toString();
                prenomChauffeur = textPrenomChauffeur.getText().toString();
                telChauffeur = textTelChauffeur.getText().toString();
                matriculeChauffeur = textMatChauffeur.getText().toString();
                utilChauffeur = textUtilChauffeur.getText().toString();
                passeChauffeur = textPasseChauffeur.getText().toString();

                Chauffeurs chauffeur = new Chauffeurs(nomChauffeur, prenomChauffeur, telChauffeur, utilChauffeur,
                        passeChauffeur, matriculeChauffeur);
                inscriptionChauffeur.put("nom", chauffeur.nom);
                inscriptionChauffeur.put("prenom", chauffeur.prenom);
                inscriptionChauffeur.put("telephone", chauffeur.telephone);
                inscriptionChauffeur.put("type", "chauffeur");
                inscriptionChauffeur.put("nomUtilisateur", chauffeur.nomUtilisateur);
                inscriptionChauffeur.put("motDePasse", chauffeur.motDePasse);
                inscriptionChauffeur.put("matricule", chauffeur.matricule);

                new MyAsyncTask().execute();
            }
        });


        retourInsc.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, Inscription.class);
                startActivity(intent);
            }
        });
    }
    //}
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){
        textNomChauffeur.setError(null);
        textPrenomChauffeur.setError(null);
        textTelChauffeur.setError(null);
        textUtilChauffeur.setError(null);
        textPasseChauffeur.setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){

        textNomChauffeur.setError(null);
        textPrenomChauffeur.setError(null);
        textTelChauffeur.setError(null);
        textUtilChauffeur.setError(null);
        textPasseChauffeur.setError(null);
    }

    @Override
    public void afterTextChanged(Editable s) {

        if (!Utilisateurs.estUneEntreeValide(textNomChauffeur.getText().toString()) && s == textNomChauffeur.getEditableText()) {

            textNomChauffeur.setError("SVP entrez votre nom correcte 2 caracter au minimum");

        } else if (!Utilisateurs.estUneEntreeValide(textPrenomChauffeur.getText().toString()) && s == textPrenomChauffeur.getEditableText()) {

            textPrenomChauffeur.setError("SVP entrez votre nom correcte 2 caracter au minimum");

        } else if (!Utilisateurs.estUnNumeroDeTelValide(textTelChauffeur.getText().toString()) && s == textTelChauffeur.getEditableText()) {

            textTelChauffeur.setError("SVP entrez un numero de telephone valide");

        } else if (!Utilisateurs.estUneEntreeValide(textUtilChauffeur.getText().toString()) && s == textUtilChauffeur.getEditableText()) {

            textUtilChauffeur.setError("SVP entrez votre nom correcte 2 caracter au minimum");

        } else if (!Utilisateurs.estUnMotDePasse(textPasseChauffeur.getText().toString()) && s == textPasseChauffeur.getEditableText()) {
            textPasseChauffeur.setError("SVP entrez un mot de passe entre 4 et 8 charactere");

        }
    }
    private class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                postData(inscriptionChauffeur);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void postData(JSONObject inscriptionClient) throws IOException {

        URL url = null;
        HttpURLConnection urlConn = null;
        DataOutputStream printout = null;
        DataInputStream input;

        url = new URL ("http://libretaxi-env.elasticbeanstalk.com/inscription");
        urlConn = (HttpURLConnection) url.openConnection();

        //urlConn.setDoInput (true);
        urlConn.setDoOutput (true);
        urlConn.setUseCaches(false);
        urlConn.setRequestProperty("Content-Type", "application/json");

        // Send POST output.
        printout = new DataOutputStream(urlConn.getOutputStream ());
        printout.writeUTF(inscriptionClient.toString());
        System.out.println(inscriptionChauffeur.toString());

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