package libre.taxi.taxilibre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InscClients extends Activity implements TextWatcher{

    private EditText textNomClient;
    private EditText textPrenomClient;
    private EditText textUtilClient;
    private EditText textPasseClient;
    private EditText textTelClient;
    private EditText textCourriel;

    String nomClient;
    String prenomClient;
    String telClient;
    String utilClient;
    String passeClient;
    String courriel;
    Button retourInsc = null;
    Context context = this;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insc_client);

        final Button button = (Button) findViewById(R.id.button_send);

        textNomClient = (EditText) findViewById(R.id.nomClient);
        textNomClient.addTextChangedListener(this);

        textPrenomClient = (EditText) findViewById(R.id.prenomClient);
        textPrenomClient.addTextChangedListener(this);

        textTelClient = (EditText) findViewById(R.id.telClient);
        textTelClient.addTextChangedListener(this);

        textCourriel = (EditText) findViewById(R.id.courriel);
        textCourriel.addTextChangedListener(this);

        textUtilClient = (EditText) findViewById(R.id.utilClient);
        textUtilClient.addTextChangedListener(this);

        textPasseClient = (EditText) findViewById(R.id.passeClient);
        textPasseClient.addTextChangedListener(this);

        final TextView resulEnreg = (TextView) findViewById(R.id.resultat);
        retourInsc = (Button) findViewById(R.id.retourInsc);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                java.sql.Connection conn = null;
                java.sql.PreparedStatement stmt = null;

                nomClient = textNomClient.getText().toString();
                prenomClient = textPrenomClient.getText().toString();
                telClient = textTelClient.getText().toString();
                courriel = textCourriel.getText().toString();
                utilClient = textUtilClient.getText().toString();
                passeClient = textPasseClient.getText().toString();

                Clients client = new Clients(nomClient, prenomClient, telClient, utilClient, passeClient, courriel);

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
        textNomClient.setError(null);
        textPrenomClient.setError(null);
        textTelClient.setError(null);
        textCourriel.setError(null);
        textUtilClient.setError(null);
        textPasseClient.setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!Utilisateurs.estUneEntreeValide(textNomClient.getText().toString()) && s == textNomClient.getEditableText()) {

            textNomClient.setError("SVP entrez votre nom correcte 2 caracter au minimum");

        } else if (!Utilisateurs.estUneEntreeValide(textPrenomClient.getText().toString()) && s == textPrenomClient.getEditableText()) {

            textPrenomClient.setError("SVP entrez votre nom correcte 2 caracter au minimum");

        } else if (!Utilisateurs.estUnNumeroDeTelValide(textTelClient.getText().toString()) && s == textTelClient.getEditableText()) {

            textTelClient.setError("SVP entrez un numero de telephone valide");

        } else if (!Utilisateurs.estUnCourrielValide(textCourriel.getText().toString()) && s == textCourriel.getEditableText()) {

            textCourriel.setError("SVP entrez une adresse courriel valide");

        } else if (!Utilisateurs.estUneEntreeValide(textUtilClient.getText().toString()) && s == textUtilClient.getEditableText()) {

            textUtilClient.setError("SVP entrez votre nom correcte 2 caracter au minimum");

        } else if (!Utilisateurs.estUnMotDePasse(textPasseClient.getText().toString()) && s == textPasseClient.getEditableText()) {

            textPasseClient.setError("SVP entrez un mot de passe entre 4 et 8 charactere");
        }
    }

    @Override
    public void afterTextChanged(Editable s){

    }
}
