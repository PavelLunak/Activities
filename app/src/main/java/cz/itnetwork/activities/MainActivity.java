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

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    // Čas posledního stisknutí tlačítka ZPĚT v milisekundách
    long backPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_main);

        // Nastavení textu toolbaru
        setTitle(R.string.activity_main_title);
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btnA:
                Intent intentA = new Intent(MainActivity.this, ActivityA.class);
                startActivity(intentA);
                break;
            case R.id.btnB:
                Intent intentB = new Intent(MainActivity.this, ActivityB.class);
                startActivity(intentB);
                break;
            case R.id.btnC:
                Intent intentC = new Intent(MainActivity.this, ActivityC.class);
                startActivity(intentC);
                break;
            case R.id.btnD:
                Intent intentD = new Intent(MainActivity.this, ActivityD.class);
                startActivity(intentD);
                break;
            case R.id.btnE:
                Intent intentE = new Intent(MainActivity.this, ActivityE.class);
                startActivity(intentE);
                break;
            case R.id.btnITnetwork:
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse("https://www.itnetwork.cz/"));

                // Test, jestli je v zařízení nainstalovaná nějaká aplikace,
                // která je schopná splnit záměr (intent) webIntent
                if (webIntent.resolveActivity(getPackageManager()) != null) {
                    // Otevření aktivity
                    startActivity(webIntent);
                }
                break;
        }
    }

    // Tato metoda je volána při stisknutí tlačítka ZPĚT na zařízení. Po stisknutí tlačítka ZPĚT
    // uživateli zobrazíme zprávu, že pro ukončení aplikace musí toto tlačítko stisknout znovu.
    // Pokud tak učiní do 3 sekund, bude aplikace ukončena.
    @Override
    public void onBackPressed() {
        long actualTime = new Date().getTime();         // Aktuální čas v milisekundách
        long difference = actualTime - backPressedTime;

        // Je časový úsek mezi dvěma stisknutími tl. ZPĚT menší než 3 sekundy...?
        if (difference < 3000) {
            super.onBackPressed();  // Ukončení aplikace stiskem tl. ZPĚT
        } else {
            backPressedTime = new Date().getTime();
            Toast.makeText(MainActivity.this, R.string.info_finish_app, Toast.LENGTH_SHORT).show();
        }
    }
}
