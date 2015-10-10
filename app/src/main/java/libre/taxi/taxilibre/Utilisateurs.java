/*
 * copyright Mohamad Yehia.
 */
package libre.taxi.taxilibre;

        /**
 *
 * @author Mohamad
 */
public class Utilisateurs  {

    String nom;
    String prenom;
    String telephone;
    String nomUtilisateur;
    String motDePasse;

    public Utilisateurs(String nom, String prenom, String telephone, String nomUtilisateur, String motDePasse) {
        if (telephone != null && telephone.length() == 10 && validerTelephone(telephone)) {
            this.telephone = telephone;
        } else {
            this.telephone = "";
        }

        if (nom != null) {
            this.nom = nom;
        } else {
            this.nom = "";
        }

        if (prenom != null) {
            this.prenom = prenom;
        } else {
            this.prenom = "";
        }

        if (nomUtilisateur != null) {
            this.nomUtilisateur = nomUtilisateur;
        } else {
            this.nomUtilisateur = "";
        }

        if (motDePasse != null) {
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
        return motDePasse.matches("[A-Za-z1-9]{4}|[A-Za-z1-9]{8}");
    }

    public static boolean estUnNumeroDeTelValide(String tel){
        return tel.matches("^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$");
    }

    public static boolean estUnCourrielValide(String tel){
        return tel.matches("^(.+)@(.+)$");
    }
}
