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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*
V této aktivitě uživatel nastaí zeměpisné souřadnice a tlačítkem "Zobrazit na mapě" odešle systému
požadavek na otevřené nějaké aplikace, která umí souřadnice zobrazit v mapě.
*/

/*
Tato aktivita v hlavičce implementuje rozhraní View.OnClickListener. Tímto se celý tento kontext
stává posluchačem události kliknutí - viz nastavování onClickListenerů dále... Na konci této třídy
je (povinné) překrytí metody onClick() listeneru View.OnClickListener.
*/
public class ActivityB extends AppCompatActivity implements View.OnClickListener {

    SeekBar seekBarLatitude;        // Posuvník pro nastavení zeměpisné šířky
    SeekBar seekBarLongitude;       // Posuvník pro nastavení zeměpisné výšky
    TextView labelLatitude;         // Label pro nastavenou šířku
    TextView labelLongitude;        // Label pro nastavení výšky
    ImageButton btnLatitudeMinus;   // Odečtení 0,1 od aktuálně nastavené šířky
    ImageButton btnLatitudePlus;    // Přidání 0,1 k aktuálně nastavené šířce
    ImageButton btnLongitudeMinus;  // Odečtení 0,1 od aktuálně nastavené výšky
    ImageButton btnLongitudePlus;   // Přidání 0,1 k aktuálně nastavené výšce
    Button btnSend;                 // Tlačítko pro odeslání požadavku na zobrazení mapy

    double latitude = 0;            // Nastavená zeměpisná šířka
    double longitude = 0;           // Nastavená zeměpisná výška


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_b);

        // Nastavení textu toolbaru
        setTitle(R.string.activity_b_title);

        // Reference na komponenty v XML návrhu
        seekBarLatitude = findViewById(R.id.seekBarLatitude);
        seekBarLongitude = findViewById(R.id.seekBarLongitude);
        labelLatitude = findViewById(R.id.labelLatitude);
        labelLongitude = findViewById(R.id.labelLongitude);
        btnLatitudeMinus = findViewById(R.id.btnLatitudeMinus);
        btnLatitudePlus = findViewById(R.id.btnLatitudePlus);
        btnLongitudeMinus = findViewById(R.id.btnLongitudeMinus);
        btnLongitudePlus = findViewById(R.id.btnLongitudePlus);
        btnSend = findViewById(R.id.btnSend);

        // Nastavení listenerů kliknutí komponentám
        btnLatitudeMinus.setOnClickListener(this);
        btnLatitudePlus.setOnClickListener(this);
        btnLongitudeMinus.setOnClickListener(this);
        btnLongitudePlus.setOnClickListener(this);
        btnSend.setOnClickListener(this);

        // Nastavení maximálních hodnot posuvníků
        seekBarLatitude.setMax(1800);
        seekBarLongitude.setMax(3600);

        // Nastavení výchozí hodnoty posuvníků
        seekBarLatitude.setProgress(900);
        seekBarLongitude.setProgress(1800);
        labelLatitude.setText("0");
        labelLongitude.setText("0");

        // Nastavení listenerů posuvníkům. Tyto listenery sledují změny hodnot těchto posuvníků.
        seekBarLatitude.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // Reakce na změnu hodnoty posuvníku
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Aktualizace labelu, který zobrazuje nastavenou hodnotu
                latitude = (double)(progress - 900)/10;
                labelLatitude.setText("" + latitude);

                // Posuvník má vždy výchozí hodnotu 0 a nelze ji nastavit zápornou. Protože ale jsou
                // zeměpisné souřadnice v rozsahu, který zasahuje i do záporných čísel, potřebujeme
                // tuto minimální hodnotu "uměle" nastavit na hodnotu menší než nula. Budeme-li se
                // bavit o zeměpisné šířce, která se pohybuje v rozsahu -90 až +90, nastavíme MAX
                // hodnotu příslušného posuvníku na dvojnásobek krajní kladné hodnoty a vždy od jeho
                // aktuální nastavené pozice odečteme hodnotu 90. Tím docílíme požadovaného rozsahu.

                // Maximální hodnoty jsou zároveň vynásobeny deseti proto, že chceme pracovat
                // s desetinnými čísly s přesností na jedno desetinné místo.
                // Desetinné číslo získáváme vydělením nastavené pozice desíti.
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarLongitude.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                longitude = (double)(progress - 1800)/10;
                labelLongitude.setText("" + longitude);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // Metoda pro odeslání požadavku systému - otevření mapy
    private void showMap(double latitude, double longitude) {
        // Příprava potřebných dat pro intent pro zobrazení souřadnic na mapě.
        // Formát dat pro mapy je pevně stanoven a další možnosti naleznete zde:
        // https://developers.google.com/maps/documentation/urls/android-intents
        Uri data = Uri.parse("geo:0,0?q=" + latitude + "," + longitude);

        // Vytvoření implicitního intentu pro zobrazení
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, data);

        // Pokud následující zakomentovaný řádek odkomentujete, nebude uživateli zobrazen seznam
        // aplikací, které umí mapu zobrazit, ale rovnou bude otevřena aplikace GoogleMaps, pokud je
        // v zařízení nainstalována.

        // mapIntent.setPackage("com.google.android.apps.maps");

        // Zeptáme se systému, jestli je v zařízení nainstalována nějaká aplikace, která umí daný
        // intent splnit. Pokud by zde tato podmínka nebyla a v zařízení nebyla žádná vhodn8 aplikace,
        // došlo by k pádu aplikace.
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    // Pokud třída implementuje rozhraní View.OnClickListener, je povinností tuto metodu přepsat.
    // Zde zjišťujeme, podle ID, která komponenta byla stisknuta. Referenci na stisknutou koponentu
    // získáme v parametru této metody.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLatitudeMinus: // Zmenšení hodnoty o 0,1
                if (latitude > AppConstants.LATITUDE_MIN) {
                    seekBarLatitude.setProgress(seekBarLatitude.getProgress() - 1);
                }
                break;
            case R.id.btnLatitudePlus:  // Zvýštšení hodnoty o 0,1
                if (latitude < AppConstants.LATITUDE_MAX) {
                    seekBarLatitude.setProgress(seekBarLatitude.getProgress() + 1);
                }
                break;
            case R.id.btnLongitudeMinus:    // Zmenšení hodnoty o 0,1
                if (longitude > AppConstants.LONGITUDE_MIN) {
                    seekBarLongitude.setProgress(seekBarLongitude.getProgress() - 1);
                }
                break;
            case R.id.btnLongitudePlus:     // Zvýštšení hodnoty o 0,1
                if (longitude < AppConstants.LONGITUDE_MAX) {
                    seekBarLongitude.setProgress(seekBarLongitude.getProgress() + 1);
                }
                break;
            case R.id.btnSend:              // Tlačítko pro zobrazení souřadnic v mapě
                showMap(latitude, longitude);
                break;
        }
    }
}
