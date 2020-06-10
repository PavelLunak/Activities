package cz.itnetwork.activities;

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

public class ActivityE extends AppCompatActivity {

    EditText etTextToShareText;     // Políčko pro zadání textu
    ImageView imgShare;             // Obrázek (tlačítko) pro sdílení zadaného textu

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_e);

        // Nastavení textu toolbaru
        setTitle(R.string.activity_e_title);

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

                Toast.makeText(ActivityE.this, R.string.info_no_text, Toast.LENGTH_LONG).show();
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
