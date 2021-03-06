package cz.itnetwork.activities;
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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*
Aktivita uživateli umožňuje do políčka vložit jakýkoliv text, který dále může sdílet do jiných aplikací
*/

public class ShareActivity extends AppCompatActivity {
    EditText etTextToShareText;     // Políčko pro zadání textu
    ImageView imgShare;             // Obrázek (tlačítko) pro sdílení zadaného textu

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.share_activity);

        // Nastavení textu toolbaru
        setTitle(R.string.share_activity_title);

        // Reference na komponenty v XML návrhu
        etTextToShareText = findViewById(R.id.etTextToShareText);
        imgShare = findViewById(R.id.imgShare);

        // Odchycení události stisknutí tlačítka pro sdílení
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etTextToShareText.getText() != null) {
                    if (!etTextToShareText.getText().toString().trim().equals("")) {
                        shareText(etTextToShareText.getText().toString().trim());
                        return;
                    }
                }

                Toast.makeText(ShareActivity.this, R.string.info_no_text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void shareText(String text) {
        // Vytvoření implicitního intentu
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // Určení typu odesílaných dat
        shareIntent.setType("text/plain");

        // Přidání dat do intentu (uživatelem vložený text)
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        // Test, zda je v zařízení nainstalovaná nějaká aplikace, schopná vytvořený intent splnit
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            // Otevření okna s výběrem aplikací (je-li jich více), schopných intent zpracovat.
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        }
    }
}
