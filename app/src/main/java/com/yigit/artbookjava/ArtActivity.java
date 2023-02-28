package com.yigit.artbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.yigit.artbookjava.databinding.ActivityArtBinding;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> goingToGalleryLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private ActivityArtBinding artBinding;
    private Bitmap targetImg ;

    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artBinding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = artBinding.getRoot();
        setContentView(view);
        launcherRegisters();
        database = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

                if (info.equals("new")){
                    //new art
                    artBinding.saveButton.setVisibility(View.VISIBLE);

                }
                else {
                    artBinding.saveButton.setVisibility(View.INVISIBLE);

                    int artId = intent.getIntExtra("artId", 1);

                    try {
                        Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?" , new String[] {String.valueOf(artId)});
                        int artNameIndex = cursor.getColumnIndex("artname");
                        int painterIndex = cursor.getColumnIndex("paintername");
                        int yearIndex = cursor.getColumnIndex("year");
                        int imageIx = cursor.getColumnIndex("image");

                        while (cursor.moveToNext()) {
                            artBinding.nameText.setText(cursor.getString(artNameIndex));
                            artBinding.artistText.setText(cursor.getString(painterIndex));
                            artBinding.yearText.setText(cursor.getString(yearIndex));

                            byte[] bytes = cursor.getBlob(imageIx);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                            artBinding.artImage.setImageBitmap(bitmap);

                        }
                        cursor.close();

                    }catch (Exception e){


                    }


                }

    }



    public void save(View view) {
        String artName = artBinding.nameText.getText().toString();
        String artistName = artBinding.artistText.getText().toString();
        String year = artBinding.yearText.getText().toString();

        Bitmap smallImage = makeSmallerImage(targetImg, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artname VARCHAR , paintername VARCHAR, year VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO arts (artname, paintername, year, image) VALUES (?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, artName);
            sqLiteStatement.bindString(2, artistName);
            sqLiteStatement.bindString(3, year);
            sqLiteStatement.bindBlob(4, byteArray);
            sqLiteStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ArtActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public Bitmap makeSmallerImage (Bitmap image , int maxSize){
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;

        if (bitmapRatio > 1) {
            //landspace image
            width = maxSize;
            height =  (int) (width / bitmapRatio);
        }
        else {
            //portrait image
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return image.createScaledBitmap(image,width,height,true);
    }
    public void selectImage(View view){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ANDROD 33+ - > READ_MEDIA_IMAGES

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    Snackbar.make(view, "Permission needed for gallery.", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);

                        }
                    }).show();
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);


                }

            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                goingToGalleryLauncher.launch(intent);

                // gallery
            }

        }
        else {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery.", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

                        }
                    }).show();
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);


                }

            } else {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                goingToGalleryLauncher.launch(intent);

                // gallery
            }
        }
    }

    void launcherRegisters(){

        goingToGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent intentFromGallery = result.getData();
            if (intentFromGallery != null) {
                Uri picturerURI =intentFromGallery.getData();
                try {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), picturerURI);
                     targetImg = ImageDecoder.decodeBitmap(source);
                    artBinding.artImage.setImageBitmap(targetImg);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
            }
        });


        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    goingToGalleryLauncher.launch(intent);
                }
                else {
                    Toast.makeText(ArtActivity.this, "Permission is needed.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}