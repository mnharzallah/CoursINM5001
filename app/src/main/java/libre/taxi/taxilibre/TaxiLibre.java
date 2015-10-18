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
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 *
 * @author Mohamad
 */
public class TaxiLibre extends Activity {

    Button inscription = null;
    Button login = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi_libre);
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        inscription = (Button) findViewById(R.id.inscription);
        login = (Button) findViewById(R.id.login);
        final Context context = this;

        inscription.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
                Intent intent = new Intent(context, Inscription.class);
                startActivity(intent);
            }
        });
        
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                
                Intent intent = new Intent(context, Login.class);
                startActivity(intent);
            }
        });
    }
}