package cz.itnetwork.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
Tato aktivita slouží jako ukázka výběru obrázku z galerie a také jako příklad použití fotoaparátu.
Uživatel má možnost vybrat obrázek z galerie nebo pořídit fografii fotoaparátem, je-li jím zařízení
vybaveno. Výsledek bude zobrazen v této aktivitě, v umístěném ImageView. Je-li obrázek (fotografie)
zobrazen v aktivitě, lze použít tlačítko pro sdílení a tento obrázek (fotografii) odeslat emailem,
sdílet na sociálních sítích atd. Kliknutím na zobrazený obrázek požádáme systém, aby tento obrázek
otevřel v prohlížeči obrázku, je-li v zařízení nějaký nainstalován.
*/

public class ActivityD extends AppCompatActivity {

    ImageView image;        //Zde bude zobrazen vybraný obrázek (pořízená fotografie)
    ImageView imgShare;     //Tlačítko (obrázek) pro sdílení připraveného obrázku (fotografie)

    private Bitmap bitmap = null;       //Proměnná pro budoucí obrázek (fotografii)
    private File destination = null;    //Umístění souboru vytvořené fotografie

    String currentPhotoPath;            //Pracovní proměnná pro soubor obrázku (fotografie)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Nastavení připraveného XML návrhu grafického uživatelského rozhraní této aktivitě
        setContentView(R.layout.activity_d);

        //Nastavení textu toolbaru
        setTitle(R.string.activity_d_title);

        //Reference na komponenty v XML návrhu
        image = findViewById(R.id.image);
        imgShare = findViewById(R.id.imgShare);
    }

    //Tato překrytá metoda je volána při ukončení a "zničení" této aktivity. Využijeme ji pro
    //odstranění všech souborů v úložišti zařízení, které tato aktivita vytvořila.
    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Vytvoření instance umístění pracovní složky této aplikace v umístění, které máme
        //deklarováno v konstantách.
        File directory = new File(AppConstants.PATH_PHOTO_DIR);
        deleteDir(directory);
    }

    //Odstranění složky a veškerého jejího obsahu. Protože nelze odstranit složku, která není prázdná,
    //musíme jí procházet a postupně vymazat celý její obsah. Tato metoda je rekurzivní - volá smam sebe.
    public boolean deleteDir(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            //Jde o složku - musíme jí projít a vymazat její obsah.
            //Získání seznamu s jejím obsahem
            String[] children = fileOrDir.list();

            for (int i = 0; i < children.length; i ++) {
                //Rekurze - metoda volá sama sebe...
                boolean success = deleteDir(new File(fileOrDir, children[i]));

                if (!success) {
                    return false;
                }
            }
        }

        //Nyní je složka prázdná a lze jí odstranit.
        return fileOrDir.delete();
    }

    //Click listener na ImageView s vybraným nebo vyfotografovaným obrázkem
    //v XML: android:onClick="imageClick"
    public void imageClick(View view) {

        if (bitmap == null) {
            //Není-li nastaven žádný soubor, metoda končí s upozorněním.
            Toast.makeText(this, R.string.info_no_image, Toast.LENGTH_LONG).show();
            return;
        }

        //Získání cesty k souboru
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);
        Uri uri = Uri.parse(path);

        //Metoda pro odeslání souboru
        showPhoto(uri);
    }

    public void shareImage(View view) {
        if (bitmap == null) {
            Toast.makeText(this, R.string.info_no_image, Toast.LENGTH_LONG).show();
            return;
        }

        //Získání cesty k souboru ke sdílení
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Image Description", null);
        Uri uri = Uri.parse(path);

        //Vytvoření implicitního intentu
        Intent intent = new Intent(Intent.ACTION_SEND);

        //Nastavení typu souboru, aby systém poznal, co chceme zobrazit
        intent.setType("image/jpeg");

        //Nastavení dat intentu s cestou k souboru
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        //Otevření seznamu dostupných aplikací, kterými lze obrázek sdílet nebo odeslat. Uživatel
        //ze seznamu některou vybere a ta bude otevřena.
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

    //Hlavní metoda pro volbu zda obrázek vybrat z galerie či vytvořit fotografii
    //Nastavení listeneru kliknutí na TextView s textem "VYBRAT OBRÁZEK NEBO POŘÍDIT FOTOGRAFII"
    //je deklarováno v XML takto: android:onClick="selectImage". Pro takovéto použití musí mít
    //taková metoda v parametru parametr typu View.
    public void selectImage(View view) {
        try {

            //Pole Stringů, které obsahuje volby pro následující systémové dialogové okno
            final CharSequence[] options = {
                    getString(R.string.label_photo_use_camera),
                    getString(R.string.label_photo_select_gallery),
                    getString(R.string.label_photo_select_cancel)};

            //Vytvoření systémového dialogového okna
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            //Nastavení voleb dialogovému oknu
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    //Kliknutí na volbu "Vyfotit"
                    if (options[item].equals(getString(R.string.label_photo_use_camera))) {
                        //Test, zda má aplikace udělena oprávnění k použití fofoaparátu a oprávnění
                        //k zápisu do úložiště zařízení. Pokud ano, tato metoda dále zajistí
                        //zprostředkování otevření fotoaparátu a zpracování pořízené fotografie.
                        checkPermissionsGranted();

                        //Zavření dialogového okna
                        dialog.dismiss();
                    }
                    //Kliknutí na volbu "Vybrat z galerie"
                    else if (options[item].equals(getString(R.string.label_photo_select_gallery))) {
                        //Zavření dialogového okna
                        dialog.dismiss();

                        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                        pickPhoto.setType("image/*");
                        String[] mimeTypes = {"image/jpeg", "image/png"};
                        pickPhoto.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);

                        /*
                        //Vytvoření intentu pro výběr obrázku
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        //Otevření aktivity s POŽADAVKEM NA ODPOVĚĎ
                        //Druhým parametrem určujeme číselný "kód", podle kterého v odpovědi poznáme
                        //zda je to odpověď na požadavek výběru obrázku z galerie. Odpověď později
                        //získáme v překryté metodě onActivityResult()
                        */
                        startActivityForResult(pickPhoto, AppConstants.REQUEST_PICK_IMAGE_GALLERY);
                    }
                    //Kliknutí na volbu "Zrušit" - zavření dialogového okna
                    else if (options[item].equals(getString(R.string.label_photo_select_cancel))) {
                        //Zavření dialogového okna bez žádné akce
                        dialog.dismiss();
                    }
                }
            });

            //Zobrazení vytvořeného a nastaveného dialogového okna.
            //NEZAPOMENOUT - bez tohoto volání je dialogové okno sice vytvořené, ale nezobrazí se!
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Metoda pro odeslání požadavku vytvoření fotografie
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Test, zda je zařízení schopno pořídit fotografii
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Napřed je nutné vytvořit soubor, do kterého bude fotografie uložena
            File photoFile = null;
            photoFile = createImageFile();
            Uri photoURI;

            //Pokud je soubor úspěšně vytvořen, pokračujeme dalé...
            if (photoFile != null) {
                //Do verze API 24 použijeme standardní postup
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    photoURI = Uri.fromFile(photoFile);
                }
                //OD verze API 24 nutno použít file providera
                else {
                    photoURI = FileProvider.getUriForFile(
                            this,
                            "cz.itnetwork.activities.fileprovider"  /*nebo: BuildConfig.APPLICATION_ID + ".fileprovider"*/,
                            photoFile);

                    //Předchozí řádek vygeneruje URI: content://cz.itnetwork.activities.fileprovider/my_images/název_souboru
                }

                grantUriPermission("cz.itnetwork.activities.fileprovider", photoURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                grantUriPermission("cz.itnetwork.activities.fileprovider", photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, AppConstants.REQUEST_PICK_IMAGE_CAMERA);
            }
            /*
            //Pokud je soubor úspěšně vytvořen, pokračujeme dalé...
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                        this,
                        "cz.itnetwork.activities.fileprovider",
                        photoFile);

                //Předchozí řádek vygeneruje URI: content://cz.itnetwork.activities.fileprovider/my_images/název_souboru

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, AppConstants.REQUEST_PICK_IMAGE_CAMERA);
            }
            */
        }
    }

    //Metoda pro vytvoření souboru, do kterého bude fotografie uložena
    private File createImageFile() {
        String fileName = "activities_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg";
        File directory = new File(AppConstants.PATH_PHOTO_DIR);

        if (directory == null) {
            return null;
        }

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File image = new File(directory, fileName);

        //Uložení cesty k vytvořenému souboru pro budoucí fotografii
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Uložení vytvořené fotografie
    private void saveBitmap(Bitmap bitmap) {
        String directoryPath = AppConstants.PATH_PHOTO_DIR;
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

        try {
            destination = new File(directory, "" + new Date().getTime() + ".jpg");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Metoda pro zmenšení fotografie (obrázku)
    private Bitmap getResizedBitmap(Bitmap bm, int newWidth) {
        //Aktální rozměry obrázku
        int width = bm.getWidth();
        int height = bm.getHeight();

        //Poměr nové velikosti ke staré
        float scale = ((float) newWidth) / width;

        //Nové rozměry obrázku
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
    //ani nezískávají), pokračujeme voláním metody dispatchTakePictureIntent(), která zajišťuje
    //práci s fotoaparátem.
    private void checkPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Odpověď na dotaz na oprávnění k použití fotoaparátu
            int camera = checkSelfPermission(Manifest.permission.CAMERA);

            //Odpověď na dotaz na oprávnění k zápisu do úložiště zařízení
            int storage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            //Seznam pro oprávnění, která aplikace nemá.
            ArrayList<String> requests = new ArrayList<>();

            //Pokud nemáme oprávnění ke kameře, přidáme ho do seznamu
            if (camera != PackageManager.PERMISSION_GRANTED) {
                requests.add(Manifest.permission.CAMERA);
            }

            //Pokud nemáme oprávnění k zápisu, přidáme ho do seznamu
            if (storage != PackageManager.PERMISSION_GRANTED) {
                requests.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            //Pokud seznam není prázdný, musíme uživatele požádat o chybějící oprávnění (v seznamu)
            if (!requests.isEmpty()) {
                ActivityCompat.requestPermissions(
                        ActivityD.this,
                        requests.toArray(new String[requests.size()]),
                        1);
            } else {
                //Žádná oprávnění nechybí, můžeme pokračovat ve vytváření fotografie
                dispatchTakePictureIntent();
            }
        } else {
            //Aplikace běží na API menší než 23, proto oprávnění neřešíme a jdeme na vytvoření fotografie
            dispatchTakePictureIntent();
        }
    }

    //Odeslání obrázku systému pro jeho zobrazení v prohlížeči obrázků
    private void showPhoto(Uri photoUri) {
        //Vytvoření implicitního intentu
        Intent intent = new Intent();

        //Nastavení akce intentu
        intent.setAction(Intent.ACTION_VIEW);

        //Nastavení dat intentu
        intent.setDataAndType(photoUri, "image/*");

        //Test zda je v zařízení nainstalovaná nějaká aplikace, která je schopná intent splnit
        if (intent.resolveActivity(getPackageManager()) != null) {
            //Spuštění aktivity
            //Zároveň je po systému požadováno otevřít okno se seznamem aplikací, které jsou schopné
            //tento intent splnit. Uživatel dostane na výběr, ve které aplikaci obrázek otevřít
            startActivity(Intent.createChooser(intent, getString(R.string.show)));
        }
    }

    //Tato metoda je volána po tom, co byla uživateli zobrazena žádost o udělení oprávnění.
    //Zde zjistíme, jak se uživatel rozhodl a můžeme na to zde reagovat.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i ++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //Uživatel oprávnění neudělil - nelze pokračovat spuštěním fotoaparátu.
                    Toast.makeText(ActivityD.this, R.string.info_missing_permissions, Toast.LENGTH_LONG).show();
                    return;
                }
            }

            //Oprávnění udělena, pokračujeme k fotoaparátu...
            dispatchTakePictureIntent();
        }
    }

    //Metoda zpětného volání. Tato metoda bude volána po výběru obrázku nebo po vytvoření fotografie.
    //To, která ze zmíněných možností to bude, poznáme podle proměnné requestCode.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            //V zavřené aktivitě byl result nastaven na RESULT_OK
            if (requestCode == AppConstants.REQUEST_PICK_IMAGE_GALLERY) {
                //Jde o zpětné volání po požadavku na výběr obrázku z galerie
                try {
                    //Z dat příchozího intentu získáme cestu k vybranému obrázku
                    Uri selectedImage = data.getData();

                    //Vytvoření bitmapy z vybraného obrázku
                    if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } else {
                         ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImage);
                         bitmap = ImageDecoder.decodeBitmap(source);
                    }

                    //Zmenšení obrázku
                    if (bitmap.getWidth() > 1024) {
                        bitmap = getResizedBitmap(bitmap, 1024);
                    }

                    //Nastavení komponentě ImageView vybraný obrázek
                    image.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(AppConstants.LOG_TAG, "ActivityNewPerson -> gallery image processing error");
                    Log.d(AppConstants.LOG_TAG, e.getMessage());
                }
            } else if (requestCode == AppConstants.REQUEST_PICK_IMAGE_CAMERA) {
                //Jde o zpětné volání po požadavku na vytvoření fotografie
                File file = new File(currentPhotoPath);

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    //Odstranění původní fotografie, která přišla z fotoaparátu, již ji nepotřebujeme
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(AppConstants.LOG_TAG, e.getMessage());
                }

                if (bitmap != null) {
                    //Zmenšení fotografie
                    if (bitmap.getWidth() > 1024) {
                        bitmap = getResizedBitmap(bitmap, 1024);
                    }

                    //Uložení fotografie do úložiště
                    saveBitmap(bitmap);

                    //Nastavení komponentě ImageView vybraný obrázek
                    image.setImageBitmap(bitmap);
                }
            }
        } else {
            //Před zavřením aktivity nebyl result nastaven na RESULT_OK (například zavřením
            //tlačítkem ZPĚT) nebo byl nastaven na RESULT_CANCELED
        }
    }
}
