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

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
Tato aktivita slouží jako ukázka výběru obrázku z galerie a jako příklad použití fotoaparátu.
Uživatel má možnost vybrat obrázek z galerie nebo pořídit fografii fotoaparátem, je-li jím zařízení
vybaveno. Výsledek bude zobrazen v této aktivitě, v umístěném ImageView. Je-li obrázek (fotografie)
zobrazen v aktivitě, lze použít tlačítko pro sdílení a tento obrázek (fotografii) odeslat emailem,
sdílet na sociálních sítích atd. Kliknutím na zobrazený obrázek požádáme systém, aby tento obrázek
otevřel v prohlížeči obrázku, je-li v zařízení nějaký nainstalován.
*/

public class PhotoActivity extends AppCompatActivity {
    ImageView image;                    // Zde bude zobrazen vybraný obrázek (pořízená fotografie)
    ImageView imgShare;                 // Tlačítko (obrázek) pro sdílení připraveného obrázku (fotografie)

    private Bitmap bitmap = null;       // Proměnná pro budoucí obrázek (fotografii)
    String currentPhotoPath;            // Pracovní proměnná pro cestu k souboru obrázku (fotografie)

    // Nový způsob zobrazování aktivit
    ActivityResultLauncher<Intent> pickGaleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();

                        try {
                            // Z dat příchozího intentu získáme cestu k vybranému obrázku
                            Uri selectedImage = data.getData();

                            // Vytvoření bitmapy z vybraného obrázku
                            if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                            } else {
                                ImageDecoder.Source source = ImageDecoder.createSource(getApplicationContext().getContentResolver(), selectedImage);
                                bitmap = ImageDecoder.decodeBitmap(source);
                            }

                            // Zmenšení obrázku
                            if (bitmap.getWidth() > 1024) {
                                bitmap = getResizedBitmap(bitmap, 1024);
                            }

                            // Nastavení komponentě ImageView vybraný obrázek
                            image.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Vytvoření fotografie
                        File file = new File(currentPhotoPath);

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                            // Odstranění původní fotografie, která přišla z fotoaparátu - již ji nepotřebujeme
                            file.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (bitmap != null) {
                            // Zmenšení fotografie
                            if (bitmap.getWidth() > 1024) {
                                bitmap = getResizedBitmap(bitmap, 1024);
                            }

                            // Nastavení komponentě ImageView vytviřenou fotografii
                            image.setImageBitmap(bitmap);

                            // Vložení fotografie do galerie zařízení
                            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, bitmap.toString(), null);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.photo_activity);

        // Nastavení textu toolbaru
        setTitle(R.string.photo_activity_title);

        // Reference na komponenty v XML návrhu
        image = findViewById(R.id.image);
        imgShare = findViewById(R.id.imgShare);
    }

    // Click listener na ImageView s vybraným nebo vyfotografovaným obrázkem
    // v XML: android:onClick="imageClick"
    public void imageClick(View view) {

        if (bitmap == null) {
            // Není-li nastaven žádný soubor, metoda končí s upozorněním.
            Toast.makeText(this, R.string.info_no_image, Toast.LENGTH_LONG).show();
            return;
        }

        // Získání cesty k souboru
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, bitmap.toString(), null);
        Uri uri = Uri.parse(path);

        // Metoda pro odeslání souboru
        showPhoto(uri);
    }

    public void shareImage(View view) {
        if (bitmap == null) {
            Toast.makeText(this, R.string.info_no_image, Toast.LENGTH_LONG).show();
            return;
        }

        // Získání cesty k souboru ke sdílení
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, bitmap.toString(), null);
        Uri uri = Uri.parse(path);

        // Vytvoření implicitního intentu
        Intent intent = new Intent(Intent.ACTION_SEND);

        // Nastavení typu souboru, aby systém poznal, co chceme zobrazit
        intent.setType("image/jpeg");

        // Nastavení dat intentu s cestou k souboru
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // Otevření seznamu dostupných aplikací, kterými lze obrázek sdílet nebo odeslat. Uživatel
        // ze seznamu některou vybere a ta bude otevřena.
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    // Hlavní metoda pro volbu zda obrázek vybrat z galerie či vytvořit fotografii.
    // Nastavení listeneru kliknutí na TextView s textem "VYBRAT OBRÁZEK NEBO POŘÍDIT FOTOGRAFII"
    // je deklarováno v XML takto: android:onClick="selectImage". Pro takovéto použití musí
    // tato metoda přijímat parametr typu View.
    public void selectImage(View view) {
        try {

            // Pole Stringů s volbami pro následující systémové dialogové okno
            final CharSequence[] options = {
                    getString(R.string.label_photo_use_camera),
                    getString(R.string.label_photo_select_gallery),
                    getString(R.string.label_photo_select_cancel)};

            // Vytvoření systémového dialogového okna
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Nastavení voleb dialogovému oknu
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    // Kliknutí na volbu "Vyfotit"
                    if (options[item].equals(getString(R.string.label_photo_use_camera))) {
                        // Test, zda má aplikace udělena oprávnění k použití fofoaparátu a oprávnění
                        // k zápisu do úložiště zařízení. Pokud ano, tato metoda dále zajistí
                        // zprostředkování otevření fotoaparátu a zpracování pořízené fotografie.
                        checkPermissionsGranted();

                        // Zavření dialogového okna
                        dialog.dismiss();
                    }
                    // Kliknutí na volbu "Vybrat z galerie"
                    else if (options[item].equals(getString(R.string.label_photo_select_gallery))) {
                        // Zavření dialogového okna
                        dialog.dismiss();

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                        pickPhoto.setType("image/*");
                        String[] mimeTypes = {"image/jpeg", "image/png"};
                        pickPhoto.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);

                        /*
                        Otevření aktivity s POŽADAVKEM NA ODPOVĚĎ.
                        Druhým parametrem určujeme číselný "kód", podle kterého v odpovědi poznáme
                        zda je to odpověď na požadavek výběru obrázku z galerie. Odpověď později
                        získáme v překryté metodě onActivityResult()
                        */

                        //startActivityForResult(pickPhoto, AppConstants.REQUEST_PICK_IMAGE_GALLERY);

                        // Nový způsob zobrazování aktivit
                        pickGaleryLauncher.launch(pickPhoto);
                    }
                    // Kliknutí na volbu "Zrušit" - zavření dialogového okna
                    else if (options[item].equals(getString(R.string.label_photo_select_cancel))) {
                        dialog.dismiss();
                    }
                }
            });

            // Zobrazení vytvořeného a nastaveného dialogového okna.
            // NEZAPOMENOUT - bez tohoto volání je dialogové okno sice vytvořené, ale nezobrazí se!
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metoda pro odeslání požadavku vytvoření fotografie
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Test, zda je zařízení schopno pořídit fotografii
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Nejprve je nutné vytvořit soubor, do kterého bude budoucí fotografie uložena
            File photoFile = null;
            photoFile = createImageFile();
            Uri photoURI;

            // Pokud je soubor úspěšně vytvořen, pokračujeme dalé...
            if (photoFile != null) {
                // DO verze API 24 použijeme standardní postup
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    photoURI = Uri.fromFile(photoFile);
                }
                // OD verze API 24 nutno použít třídu FileProvider
                else {
                    photoURI = FileProvider.getUriForFile(
                            this,
                            "cz.itnetwork.activities.fileprovider",
                            photoFile);
                }

                grantUriPermission("cz.itnetwork.activities.fileprovider", photoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                grantUriPermission("cz.itnetwork.activities.fileprovider", photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                //Zastaralý způsob
                //startActivityForResult(takePictureIntent, AppConstants.REQUEST_PICK_IMAGE_CAMERA);

                // Nový způsob zobrazování aktivit
                takePhotoLauncher.launch(takePictureIntent);
            }
        }
    }

    // Metoda pro vytvoření souboru, do kterého bude fotografie uložena
    private File createImageFile() {
        // Vytvoření názvu souboru obrázku
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;

        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Uložení cesty k vytvořenému souboru pro budoucí fotografii
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Metoda pro zmenšení fotografie (obrázku)
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth) {
        // Aktální rozměry obrázku
        int width = bm.getWidth();
        int height = bm.getHeight();

        // Poměr nové velikosti ke staré
        float scale = ((float) newWidth) / width;

        // Nové rozměry obrázku
        int nWidth = newWidth;
        int nHeight = (int) (height * scale);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, nWidth, nHeight, true);
        bm.recycle();

        return resizedBitmap;
    }

    //Zde zjistíme, zda má aplikace oprávnění používat kameru (fotoaparát) a zda má oprávnění
    //k zápisu do úložiště zařízení. Pokud oprávnění uděleno není, bude uživatel požádan o jeho udělení.
    //Tato metoda je volána při požadavku uživatele na vytvoření fotografie. Pokud jsou potřebná
    //oprávnění již udělena nebo aplikace běží na API menší bež 23 (oprávnění se za běhu nezjišťují
    //ani nezískávají), pokračujeme voláním metody dispatchTakePictureIntent(), která zahajuje
    //práci s fotoaparátem.
    private void checkPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Odpověď na dotaz na oprávnění k použití fotoaparátu
            int camera = checkSelfPermission(Manifest.permission.CAMERA);

            // Odpověď na dotaz na oprávnění k zápisu do úložiště zařízení
            int storage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            // Seznam pro oprávnění, která aplikace nemá udělena.
            ArrayList<String> requests = new ArrayList<>();

            // Pokud nemáme oprávnění ke kameře, přidáme ho do seznamu
            if (camera != PackageManager.PERMISSION_GRANTED) {
                requests.add(Manifest.permission.CAMERA);
            }

            // Pokud nemáme oprávnění k zápisu, přidáme ho do seznamu
            if (storage != PackageManager.PERMISSION_GRANTED) {
                requests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            // Pokud seznam není prázdný, musíme uživatele požádat o tato chybějící oprávnění
            if (!requests.isEmpty()) {
                ActivityCompat.requestPermissions(
                        PhotoActivity.this,
                        requests.toArray(new String[requests.size()]),
                        1);
            } else {
                // Žádná oprávnění nechybí, můžeme pokračovat ve vytváření fotografie
                dispatchTakePictureIntent();
            }
        } else {
            // Aplikace běží na API menší než 23, proto oprávnění neřešíme a jdeme na vytvoření fotografie
            dispatchTakePictureIntent();
        }
    }

    // Odeslání obrázku systému pro jeho zobrazení v prohlížeči obrázků
    private void showPhoto(Uri photoUri) {

        // Vytvoření implicitního intentu
        Intent intent = new Intent();

        // Nastavení akce intentu
        intent.setAction(Intent.ACTION_VIEW);

        // Nastavení dat intentu
        intent.setDataAndType(photoUri, "image/*");

        // Test zda je v zařízení nainstalovaná aplikace, která je schopná intent dokončit
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Spuštění aktivity.
            // Zároveň je po systému požadováno otevřít okno se seznamem aplikací, které jsou schopné
            // tento intent splnit. Uživatel dostane na výběr, ve které aplikaci obrázek otevřít
            startActivity(Intent.createChooser(intent, getString(R.string.show)));
        }
    }

    // Tato metoda je volána po tom, co byla uživateli zobrazena žádost o udělení oprávnění.
    // Zde zjistíme, jak se uživatel rozhodl a můžeme na to zde reagovat.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i ++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // Uživatel oprávnění neudělil - nelze pokračovat spuštěním fotoaparátu.
                    Toast.makeText(PhotoActivity.this, R.string.info_missing_permissions, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Oprávnění udělena, pokračujeme k fotoaparátu...
            dispatchTakePictureIntent();
        }
    }

    /*
    // Metoda zpětného volání. Tato metoda bude volána po výběru obrázku nebo po vytvoření fotografie.
    // To, která ze zmíněných možností to bude, poznáme podle proměnné requestCode.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // V zavřené aktivitě byl result nastaven na RESULT_OK
            if (requestCode == AppConstants.REQUEST_PICK_IMAGE_GALLERY) {
                // Výběr obrázku z galerie
                try {
                    // Z dat příchozího intentu získáme cestu k vybranému obrázku
                    Uri selectedImage = data.getData();

                    // Vytvoření bitmapy z vybraného obrázku
                    if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } else {
                        ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImage);
                        bitmap = ImageDecoder.decodeBitmap(source);
                    }

                    // Zmenšení obrázku
                    if (bitmap.getWidth() > 1024) {
                        bitmap = getResizedBitmap(bitmap, 1024);
                    }

                    // Nastavení komponentě ImageView vybraný obrázek
                    image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == AppConstants.REQUEST_PICK_IMAGE_CAMERA) {
                // Vytvoření fotografie
                File file = new File(currentPhotoPath);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    // Odstranění původní fotografie, která přišla z fotoaparátu - již ji nepotřebujeme
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap != null) {
                    // Zmenšení fotografie
                    if (bitmap.getWidth() > 1024) {
                        bitmap = getResizedBitmap(bitmap, 1024);
                    }

                    // Nastavení komponentě ImageView vytviřenou fotografii
                    image.setImageBitmap(bitmap);
                }
            }
        } else {
            // Před zavřením aktivity nebyl result nastaven na RESULT_OK (například zavřením
            // tlačítkem ZPĚT) nebo byl nastaven na RESULT_CANCELED
        }
    }
    */
}
