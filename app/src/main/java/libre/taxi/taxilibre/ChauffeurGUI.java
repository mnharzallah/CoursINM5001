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

public class ChauffeurGUI extends  Activity {

    TextView bienvenue = null;
    Button retour = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chauffeur);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        bienvenue = (TextView) findViewById(R.id.bienvenue);
        retour = (Button) findViewById(R.id.retourInsc);

        if (!Login.loginData.isEmpty() && !Login.loginData.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + Login.loginData.getString("nomUtilisateur"));
            Login.loginData.put("nomUtilisateur", "");
        }
        else if (!InscChauffeurs.inscriptionChauffeur.isEmpty() && !InscChauffeurs.inscriptionChauffeur.getString("nomUtilisateur").equals("")) {
            bienvenue.setText("Bienvenue " + InscChauffeurs.inscriptionChauffeur.getString("nomUtilisateur"));
            InscChauffeurs.inscriptionChauffeur.put("nomUtilisateur", "");
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