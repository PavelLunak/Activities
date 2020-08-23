/*  _____ _______         _                      _
 * |_   _|__   __|       | |                    | |
 *   | |    | |_ __   ___| |___      _____  _ __| | __  ___ ____
 *   | |    | | '_ \ / _ \ __\ \ /\ / / _ \| '__| |/ / / __|_  /
 *  _| |_   | | | | |  __/ |_ \ V  V / (_) | |  |   < | (__ / /
 * |_____|  |_|_| |_|\___|\__| \_/\_/ \___/|_|  |_|\_(_)___/___|
 *                                _
 *              ___ ___ ___ _____|_|_ _ _____
 *             | . |  _| -_|     | | | |     |  LICENCE
 *             |  _|_| |___|_|_|_|_|___|_|_|_|
 *             |_|
 *
 *   PROGRAMOVÁNÍ  <>  DESIGN  <>  PRÁCE/PODNIKÁNÍ  <>  HW A SW
 *
 * Tento zdrojový kód je součástí výukových seriálů na
 * IT sociální síti WWW.ITNETWORK.CZ
 *
 * Kód spadá pod licenci prémiového obsahu a vznikl díky podpoře
 * našich členů. Je určen pouze pro osobní užití a nesmí být šířen.
 * Více informací na http://www.itnetwork.cz/licence
 */

package cz.itnetwork.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/*
V této aktivitě uživatel do textového pole vyplní telefonní číslo, na které následně může poslat SMS
nebo zahájit tel. hovor. V obou případech je od systému požadováno otevření příslušné systémové
aktivity - pro psaní SMS nebo pro správu tel. hovorů.
*/

public class ActivityC extends AppCompatActivity {

    EditText etPhoneNumber;     // Políčko pro zadání tel. čísla
    EditText etSmsText;         // Políčko pro zadání textu SMS
    Button btnDial;             // Tlačítko pro vytočení tel. čísla
    Button btnSms;              // Tlačítko pro odeslání SMS

    String phoneNumber;         // Proměnná pro uložení zadaného tel. čísla


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_c);

        // Nastavení textu toolbaru
        setTitle(R.string.activity_c_title);

        // Reference na komponenty v XML návrhu
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etSmsText = findViewById(R.id.etSmsText);
        btnDial = findViewById(R.id.btnDial);
        btnSms = findViewById(R.id.btnSms);

        if (savedInstanceState == null) {
            btnDial.setEnabled(false);
            btnSms.setEnabled(false);
        } else {
            btnDial.setEnabled(validatePhoneNumber(etPhoneNumber.getText()));
            btnSms.setEnabled(validatePhoneNumber(etPhoneNumber.getText()));
        }

        // EditTextům nastavíme listener, který bude sledovat změny zadaného textu. Zde budeme
        // v průběhu zadávání čísla kontrolovat jeho správný formát
        etPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (validatePhoneNumber(s)) {
                    btnDial.setEnabled(true);
                    btnSms.setEnabled(true);
                } else {
                    btnDial.setEnabled(false);
                    btnSms.setEnabled(false);
                }
            }
        });
    }

    // Metoda pro obsluhu události kliknutí na tlačítko "Zavolat"
    public void dial(View view) {
        // Vytvoření implicitního intentu
        Intent callIntent = new Intent(Intent.ACTION_DIAL);

        // Přidání dat do intentu pro zobrazení systémové aktivity ke správě tel. hovorů
        callIntent.setData(Uri.parse("tel:" + etPhoneNumber.getText().toString().trim()));

        if (callIntent.resolveActivity(getPackageManager()) != null) {
            // Otevření požadované aktivity
            startActivity(callIntent);
        }
    }

    // Metoda pro obsluhu události kliknutí na tlačítko "Poslat SMS"
    public void sendSms(View view) {
        String smsText = "";

        try {
            smsText = etSmsText.getText().toString();
        } catch (NullPointerException e) {
            Log.d(AppConstants.LOG_TAG, "SMS text null");
        }

        // Vytvoření implicitního intentu
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);

        // Přidání dat do intentu pro zobrazení systémové aktivity pro psaní SMS
        smsIntent.setData(Uri.parse("smsto:" + phoneNumber));
        smsIntent.putExtra("sms_body", smsText);

        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            // Otevření požadované aktivity
            startActivity(smsIntent);
        }
    }

    // Metoda pro kontolu správného formátu zadaného tel. čísla
    private boolean validatePhoneNumber(Editable input) {
        if (input == null) {
            phoneNumber = "";
            return false;
        }

        if (input.toString().isEmpty()) {
            phoneNumber = "";
            return false;
        }

        String inputText = input.toString().trim();

        /*
        ((\+|00){1}\d{3})? Jednou "+" nebo "00" a k tomu tři čísla. A celé tam být může a nemusí - ()?
        ( |-)? může a nemusí být mezera nebo pomlčka
        [1-9][0-9]{2} jedno číslo 1-9 a k tomu dvě čísla 0-9
        ( |-)? může a nemusí být mezera nebo pomlčka
        [0-9]{3} tři čísla 0-9
        ( |-)? může a nemusí být mezera nebo pomlčka
        [0-9]{3} tři čísla 0-9
        $ na konci řetězce

        PŘÍKLADY SPRÁVNÝCH FORMÁTŮ:
        123456789
        123 456 789, 123456 789, 123 456789
        123-456-789, 123-456789, 123456-789, 123-456 789, 123 456-789
        +420123456789
        +420-123-456-789, +420 123 456 789, +420-123-456 789
        00420123456789, 00420-123-456-789, 00420 123 456 789, 00420-123 456-789
        */

        // Regulární výraz jako vzor správného formátu tel. čísla
        String pattern = "^((\\+|00){1}\\d{3})?( |-)?[1-9][0-9]{2}( |-)?[0-9]{3}( |-)?[0-9]{3}$";

        if (inputText.matches(pattern)) {
            phoneNumber = inputText;
            return true;
        } else {
            phoneNumber = "";
            return false;
        }
    }
}
