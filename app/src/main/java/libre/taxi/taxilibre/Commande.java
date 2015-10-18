package libre.taxi.taxilibre;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        bienvenue = (TextView) findViewById(R.id.bienvenue);
        commander = (Button) findViewById(R.id.commande);
        retour = (Button) findViewById(R.id.retourInsc);
        if (!Login.loginData.isEmpty() && !Login.loginData.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + Login.loginData.getString("nomUtilisateur"));
            Login.loginData.put("nomUtilisateur", "");
        }
        else if (!InscClients.inscriptionClient.isEmpty() && !InscClients.inscriptionClient.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + InscClients.inscriptionClient.getString("nomUtilisateur"));
            InscClients.inscriptionClient.put("nomUtilisateur", "");
        }
        final Context context = this;

        retour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, TaxiLibre.class);
                startActivity(intent);
            }
        });
    }
}