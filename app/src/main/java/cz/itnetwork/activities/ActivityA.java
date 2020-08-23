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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*
Tato aktivita od uživatele získá dvě čísla, která budou odeslána aktivitě ActivitySum.
Aktivita ActivitySum čísla sečte a výsledek vrátí zpět do této aktivity.
Výsledek obdržíme v přepsané metodě onActivityResult().
*/

public class ActivityA extends AppCompatActivity {

    EditText etNumber1, etNumber2;      // Políčko pro zadání čísel k součtu
    Button btnSend;                     // Tlačítko pro odeslní čísel do ActivitySum
    TextView labelResult;               // Label pro zobrazení vráceného součtu zadaných čísel

    int number1, number2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_a);

        // Nastavení textu toolbaru
        setTitle(R.string.activity_a_title);

        // Reference na komponenty v XML návrhu
        etNumber1 = findViewById(R.id.etNumber1);
        etNumber2 = findViewById(R.id.etNumber2);
        labelResult = findViewById(R.id.labelResult);
        btnSend = findViewById(R.id.btnSend);

        // Odchycení události stisknutí tlačítka "Odeslat k výpočtu"
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    number1 = Integer.parseInt(etNumber1.getText().toString());
                    number2 = Integer.parseInt(etNumber2.getText().toString());
                    sendData();
                } catch (NumberFormatException e) {
                    // Chyba zadání - byl zadán znak, který není číslice
                    Log.d(AppConstants.LOG_TAG, "NumberFormatException");
                    Log.d(AppConstants.LOG_TAG, e.getMessage());
                    showErrorToast();
                } catch (NullPointerException e) {
                    Log.d(AppConstants.LOG_TAG, "NullPointerException");
                    Log.d(AppConstants.LOG_TAG, e.getMessage());
                    showErrorToast();
                }
            }
        });
    }

    // Metoda pro vytvoření intentu a pro otevření aktivity ActivitySum,
    // která sečte zadaná čísla a výsledek odšle zpět.
    private void sendData() {
        // Vytvoření explicitního intentu - má přesně určen cíl -> otevřít aktivitu ActivitySum.
        Intent sendIntent = new Intent(this, ActivitySum.class);

        // Přidání dat do intentu (dvě zadaná čísla). Díky klíčům "number_1" a "number_2" si
        // zadaná čísla vyzvedneme v aktivitě ActivitySum.
        sendIntent.putExtra("number_1", number1);
        sendIntent.putExtra("number_2", number2);

        // Otevření aktivity ActivitySum. Podle zadaného requestCode (druhý parametr) v metodě
        // onActivityResult() budeme vědět, že jde o odpověď z aktivity ActivitySum. Aktivita
        // ActivitySum odpověď odešle ve chvíli, kdy bude zavřena tlačítkem "Odeslat součet".
        startActivityForResult(sendIntent, 1);
    }

    // Metoda pro zobrazení zprávy s chybou.
    private void showErrorToast() {
        Toast.makeText(this, R.string.info_incorrect_entry, Toast.LENGTH_LONG).show();
    }

    // Metoda zpětného volání. Pokud bude jakákoliv další aktivita otevřena voláním
    // startActivityForResult() z této aktivity, bude po jejím zavření volána tato metoda.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // Jde o odpověď z právě zavřené aktivity ActivitySum? (shodují se hodnoty proměnných
            // requestCode zde a v metodě sendData() voláním startActivityForResult()).
            if (requestCode == 1) {
                // Kontrola příchozích dat z ukončené aktivity ActivitySum
                if (data != null) {
                    // Obsahuje odpověď data s klíčem "result_from_activity_sum"?
                    if (data.hasExtra("result_from_activity_sum")) {
                        labelResult.setText("" + data.getIntExtra("result_from_activity_sum", -1));
                    } else {
                        labelResult.setText(R.string.info_error_loading_result);
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            // Aktivita byla zavřena (například) tlačítkem ZPĚT
            labelResult.setText(R.string.info_no_result);
        }
    }
}
