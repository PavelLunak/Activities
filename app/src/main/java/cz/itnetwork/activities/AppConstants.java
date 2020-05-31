package cz.itnetwork.activities;

import android.os.Environment;

import java.io.File;

public interface AppConstants {

    String LOG_TAG = "Activities_log_tag";

    double LATITUDE_MIN = -90.0;
    double LATITUDE_MAX = 90.0;

    double LONGITUDE_MIN = -180.0;
    double LONGITUDE_MAX = 180.0;

    String PATH_PHOTO_DIR = Environment.getExternalStorageDirectory().toString() + File.separator + "ACTIVITIES";

    int REQUEST_PICK_IMAGE_GALLERY = 1;
    int REQUEST_PICK_IMAGE_CAMERA = 2;
}
