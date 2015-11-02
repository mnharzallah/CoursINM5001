/*
 * copyright Mohamad Yehia.
 */
package libre.taxi.taxilibre;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;

import android.app.Application;
import android.net.ConnectivityManager;
import android.content.Context;
import android.net.NetworkInfo;
/**
 *
 * @author Mohamad
 */
public class Utilisateurs  {

    String nom;
    String prenom;
    String telephone;
    String motDePasse;

    public Utilisateurs(String nom, String prenom, String telephone, String motDePasse) {
        if (telephone != null && telephone.length() == 10 && validerTelephone(telephone)) {
            this.telephone = telephone;
        } else {
            this.telephone = "";
        }
        if (nom != null && Utilisateurs.estUneEntreeValide(nom)) {
            this.nom = nom;
        } else {
            this.nom = "";
        }
        if (prenom != null && Utilisateurs.estUneEntreeValide(prenom)) {
            this.prenom = prenom;
        } else {
            this.prenom = "";
        }
        if (motDePasse != null && Utilisateurs.estUnMotDePasse(motDePasse)) {
            this.motDePasse = motDePasse;
        } else {
            this.motDePasse = "";
        }
    }

    public static boolean validerTelephone(String telephone) {
        int compteur = 0;
        boolean estValide = true;

        while (compteur < telephone.length() && estValide) {
            if (telephone.charAt(compteur) < '0' && telephone.charAt(compteur) > '9') {
                estValide = false;
            }
            compteur++;
        }
        return estValide;
    }

    public static boolean estUneEntreeValide(String s){
        return s.matches("[A-Za-z1-9]{2,8}");
    }

    public static boolean estUnMotDePasse(String motDePasse){
        return motDePasse.matches("[A-Za-z1-9]{4,8}");
    }

    public static boolean estUnNumeroDeTelValide(String tel){
        return tel.matches("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$");
    }

    public static boolean estUnCourrielValide(String tel){
        return tel.matches("^(.+)@(.+)$");
    }

    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}