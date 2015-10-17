package libre.taxi.taxilibre;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Commande extends  Activity {

    TextView bienvenue = null;
    Button commander = null;
    Button retour = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commande);

        bienvenue = (TextView) findViewById(R.id.bienvenue);
        commander = (Button) findViewById(R.id.commande);
        retour = (Button) findViewById(R.id.retourInsc);
        if (!Login.loginData.getString("nomUtilisateur").equals(""))
            bienvenue.setText("Bienvenue " + Login.loginData.getString("nomUtilisateur"));
        else if (!InscClients.inscriptionClient.getString("nomUtilisateur").equals(""))
            bienvenue.setText("Bienvenue " + InscClients.inscriptionClient.getString("nomUtilisateur"));
        final Context context = this;

        retour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, Inscription.class);
                startActivity(intent);
            }
        });
    }
}