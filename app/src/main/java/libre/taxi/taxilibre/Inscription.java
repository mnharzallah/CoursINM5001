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

/**
 *
 * @author Mohamad
 */
public class Inscription extends Activity {

    Button InscClient = null;
    Button InscChauffeur = null;
    Button retour = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inscription);

        InscClient = (Button) findViewById(R.id.inscr_client);
        InscChauffeur = (Button) findViewById(R.id.inscr_chauffeur);
        retour = (Button) findViewById(R.id.retourMain);

        final Context context = this;

        InscClient.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, InscClients.class);
                startActivity(intent);
            }
        });

        InscChauffeur.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, InscChauffeurs.class);
                startActivity(intent);
            }
        });
        
        retour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(context, TaxiLibre.class);
                startActivity(intent);
            }
        });
    }
}
