/*
 * copyright Mohamad Yehia.
 */
package libre.taxi.taxilibre;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mohamad
 */
public class Clients extends Utilisateurs {

    String courriel;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE);

    public Clients(String nom, String prenom, String telephone, String motDePasse, String courriel) {
        super(nom, prenom, telephone, motDePasse);
        if (courriel != null && validerCourriel(courriel)) {
            this.courriel = courriel;
        } else {
            this.courriel = "";
        }
    }

    public static boolean validerCourriel(String courriel) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(courriel);
        return matcher.find();
    }
}