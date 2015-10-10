/*
 * copyright Mohamad Yehia.
 */
package libre.taxi.taxilibre;

/**
 *
 * @author Mohamad
 */
public class Chauffeurs extends Utilisateurs {

    String matricule;

    public Chauffeurs(String nom, String prenom, String telephone, String nomUtilisateur, String motDePasse, String matricule) {
        super(nom, prenom, telephone, nomUtilisateur, motDePasse);
        if (matricule.length() > 0 && validerMatricule(matricule))
            this.matricule = matricule;
        else
            this.matricule = "";
    }

    public static boolean validerMatricule(String matricule) {
        int compteur = 0;
        boolean estValide = true;
        matricule = matricule.substring(0, matricule.length() - 1);
        if ((matricule.charAt(0) == 'T' || matricule.charAt(0) == 't') && matricule.length() == 6) {
            while (compteur < matricule.length() && estValide) {
                if (matricule.charAt(compteur) < '0' && matricule.charAt(compteur) > '9') {
                    estValide = false;
                }
                compteur++;
            }
        }else
            estValide = false;
        return estValide;
    }

}
