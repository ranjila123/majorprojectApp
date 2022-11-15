package com.example.major;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CamActivity extends AppCompatActivity {

    ImageView img;
    Button camera;
    Button extract;
    private final int Cam_Request_Code = 100;
//    StorageReference storageReference;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);

        img = findViewById(R.id.imageView);
        camera = findViewById(R.id.camera);
        extract = findViewById(R.id.extract);


        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CamActivity.this, "Extract text here", Toast.LENGTH_SHORT).show();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                    Intent icam = new Intent();
//                    icam.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(icam, Cam_Request_Code);
                dispatchTakePictureIntent();
            }
        });
    }


    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Cam_Request_Code) {
            if (resultCode == Activity.RESULT_OK) {

//                assert data != null;
//                Bitmap image = (Bitmap)data.getExtras().get("data");
//                img.setImageBitmap(image);

                File f = new File(currentPhotoPath);
                img.setImageURI(Uri.fromFile(f));
                Log.d("tag","Absolute URL of image is " + Uri.fromFile(f));
                //image to upload in gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//scan for new image file
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);//display in gallery
                this.sendBroadcast(mediaScanIntent); //inform mediaScanIntent that new file is created and that will be displayed in gallery
//               uploadFirebase(f.getName(),contentUri);
            }
        }

    }

//    private void uploadFirebase(String name, Uri contentUri) {
//        storageReference  = FirebaseStorage.getInstance().getReference().child("images/" + name );
//        storageReference.putFile(contentUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(CamActivity.this, "Images Uploaded", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//                        Toast.makeText(CamActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //for image to appear in gallery
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.major.android.file-provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Cam_Request_Code);
            }
        }
    }


}