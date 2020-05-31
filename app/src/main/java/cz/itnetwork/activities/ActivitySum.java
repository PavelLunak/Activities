package cz.itnetwork.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*
Tato aktivita bude otevírána aktivitou ActivityA, od které bude přijímat dvě čísla. Úkolem této
aktivity bude tato dvě čísla sečíst a zobrazit výsledek. Stisknutím tlačítka "Odeslat součet" bude
tento výsledek odeslán zpět do aktivity ActivityA.
*/
public class ActivitySum extends AppCompatActivity {

    TextView labelNumberOne;    //Zobrazení prvního příchozího čísla
    TextView labelNumberTwo;    //Zobrazení druhého příchozího čísla
    TextView labelResult;       //Zobrazení výsledku součtu příchozích čísel
    Button btnSend;             //Tlačítko pro odeslání odpovědi do aktivity ActivityA

    int numberOne = 0;
    int numberTwo = 0;

    //Deklarace listeneru pro odchycení události stisku tlačítka pro odeslání.
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Podle id stisknuté komponenty zjistíme co bylo stisknuto. Referenci na stisknutou
            //komponentu získáme v parametru metody onClick(View v).
            if (v.getId() == R.id.btnSend) {
                //Vytvoření intentu, který zde slouží pouze jako kontejner primitivních dat
                Intent resultIntent = new Intent();

                //Přidání dat do intentu -> výsledek součtu přijatých čísel. Je použit klíč
                //"result_from_activity_sum", díky kterému si tato data vyzvedneme v aktivitě ActivityA
                resultIntent.putExtra("result_from_activity_sum", numberOne + numberTwo);

                //Nastavení výsledku uzavření této aktivity pro aktivitu, ze které byla tato
                //aktivita otevřena. Typ tohoto výsledku rozlišujeme v aktivitě ActivityA
                //v metodě onActivityResult().
                setResult(RESULT_OK, resultIntent);

                //Zavření této aktivity a návrat do předchozí aktivity ActivityA
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_sum);

        //Nastavení textu toolbaru
        setTitle(R.string.activity_sum_title);

        //Reference na komponenty v XML návrhu
        labelNumberOne = findViewById(R.id.labelNumberOne);
        labelNumberTwo = findViewById(R.id.labelNumberTwo);
        labelResult = findViewById(R.id.labelResult);
        btnSend = findViewById(R.id.btnSend);

        //Tlačítku pro odeslání odpovědi nastavíme dříve deklarovaný listener onClick()
        btnSend.setOnClickListener(clickListener);

        try {
            //Získání příchozího intentu, kterým byla tato aktivita otevřena.
            Intent incomingIntent = getIntent();

            numberOne = incomingIntent.getIntExtra("number_one", 0);
            numberTwo = incomingIntent.getIntExtra("number_two", 0);

            labelNumberOne.setText("" + numberOne);
            labelNumberTwo.setText("" + numberTwo);
            labelResult.setText("" + (numberOne + numberTwo));
        } catch (NullPointerException e) {
            //Ošetření výjimky na NullPointerException je zde proto, že incomingIntent může být NULL.
            //Pokud by byl incomingIntent NULL, došlo by k pádu aplikace za běhu.
            labelNumberOne.setText("?");
            labelNumberTwo.setText("?");

            Toast.makeText(this, R.string.incoming_intent_data_error, Toast.LENGTH_LONG).show();
        }
    }
}
