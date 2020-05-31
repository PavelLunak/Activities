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

    EditText etNumberOne, etNumberTwo;  //Pole pro zadání čísel k odeslání
    Button btnSend;                     //Tlačítko pro odeslní čísel
    TextView labelResult;               //Label pro zobrazení vráceného součtu zadaných čísel

    int numberOne, numberTwo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_a);

        //Nastavení textu toolbaru
        setTitle(R.string.activity_a_title);

        //Reference na komponenty v XML návrhu
        etNumberOne = findViewById(R.id.etNumberOne);
        etNumberTwo = findViewById(R.id.etNumberTwo);
        labelResult = findViewById(R.id.labelResult);
        btnSend = findViewById(R.id.btnSend);

        //Odchycení události stisknutí tlačítka "Odeslat k výpočtu"
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    numberOne = Integer.parseInt(etNumberOne.getText().toString());
                    numberTwo = Integer.parseInt(etNumberTwo.getText().toString());
                    sendData();
                } catch (NumberFormatException e) {
                    //Chyba zadání - byl zadán znak, který není číslo
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

    //Metoda pro vytvoření intentu a pro otevření aktivity,
    //která sečte zadaná čísla a výsledek odšle zpět.
    private void sendData() {
        //Vytvoření explicitního intentu - má přesně určen cíl -> otevřít aktivitu ActivitySum.
        Intent sendIntent = new Intent(this, ActivitySum.class);

        //Přidání dat do intentu (dvě zadaná čísla). Díky klíčům "number_one" a "number_two" si
        //zadaná čísla vyzvedneme v aktivitě ActivitySum.
        sendIntent.putExtra("number_one", numberOne);
        sendIntent.putExtra("number_two", numberTwo);

        //Otevření aktivity ActivitySum. Podle zadaného requestCode (druhý parametr) v metodě
        //onActivityResult() budeme vědět, že jde o odpověď na z aktivity ActivitySum. Aktivita
        //ActivitySum odpověď odešle ve chvíli, kdy bude zavřena tlačítkem "Odeslat součet".
        startActivityForResult(sendIntent, 1);
    }

    //Metoda pro zobrazení zprávy s chybou.
    private void showErrorToast() {
        Toast.makeText(this, R.string.info_incorrect_entry, Toast.LENGTH_LONG).show();
    }

    //Metoda zpětného volání. Pokud bude jakákoliv další aktivita otevřena voláním
    //startActivityForResult() z této aktivity, bude po jejím ("správném") zavření volána tato metoda.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //Jde o odpověď z právě zavřené aktivity ActivitySum? (shodují se hodnoty proměnných
            //requestCode zde a v metodě sendData() voláním startActivityForResult() ).
            if (requestCode == 1) {
                //Kontrola příchozích dat z ukončené aktivity ActivitySum
                if (data != null) {
                    //Obsahuje odpověď data s klíčem "result_from_activity_sum"?
                    if (data.hasExtra("result_from_activity_sum")) {
                        labelResult.setText("" + data.getIntExtra("result_from_activity_sum", -1));
                    } else {
                        labelResult.setText(R.string.info_error_loading_result);
                    }
                }
            }
        } else if (resultCode == RESULT_CANCELED) {
            //Aktivita byla zavřena (například) tlačítkem ZPĚT
            labelResult.setText(R.string.info_no_result);
        }
    }
}
