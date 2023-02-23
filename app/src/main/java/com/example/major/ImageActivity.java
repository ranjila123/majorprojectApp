package com.example.major;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ImageActivity extends AppCompatActivity {

    ImageView camera;
    ImageView gallery;
    ImageView image;
    private final int Cam_Request_Code = 100;
    int Read_Permission = 0;
    int PERMISSION_CODE = 1;
//    TextView textView;
    Uri uri;

    String currentPhotoPath;

    boolean imagesSelected = false; // Whether the user selected at least an image or not.
    boolean dataExtracted = false;
    String selectedImagePath;

    private String postUrl="http://192.168.1.77:8000/";//****Put your  URL here******
    private String POST="POST";
    private String GET="GET";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        camera = findViewById(R.id.camera);
        gallery = findViewById(R.id.gallery);
        image = findViewById(R.id.imageView2);
//        textView = findViewById(R.id.textView2);

        handlePermission();

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), 10);
            }
        });

    }

    private void handlePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }

        if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
        }

    }


    public void connectServer(View v){
        if (imagesSelected == false) { // This means no image is selected and thus nothing to upload.
            Toast.makeText(this, "No Image Selected to Upload. Select Image and Try Again.", Toast.LENGTH_SHORT).show();
//            textView.setText("No Image Selected to Upload. Select Image and Try Again.");
            return;
        }
        Toast.makeText(this, "Extracting the data. Please Wait ...", Toast.LENGTH_SHORT).show();
//        textView.setText("Extracting the data. Please Wait ...");

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        try {
            Log.d("testlink", "connectServer: "+selectedImagePath);
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
            Log.d("check","medd" + bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            multipartBodyBuilder.addFormDataPart("image" , "Android_Flask_"+".jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));
            RequestBody postBodyImage = multipartBodyBuilder.build();
            postRequest(postUrl, postBodyImage);
        }catch (Exception e){
            Log.d("testimage", "connectServer: "+e.getLocalizedMessage());
            Toast.makeText(this, "Please Make Sure the Selected File is an Image.\"+e", Toast.LENGTH_SHORT).show();
            //textView.setText("Please Make Sure the Selected File is an Image."+e);
            return;
        }
        dataExtracted = true;
    }


    public void finish(View v){
        if(dataExtracted==false){
            Toast.makeText(this, "There was no data extraction. Please begin by extracting data.", Toast.LENGTH_SHORT).show();
//            textView.setText("No Image Selected to Upload. Select Image and Try Again.");
            return;
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ImageActivity.this);
        alertDialog.setTitle("Confirm");
        alertDialog.setIcon(R.drawable.ic_baseline_question_mark_24);
        alertDialog.setMessage("Are you sure you have finnished selecting the images?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ImageActivity.this, "Getting ready for download...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ImageActivity.this,ProgressbarActivity.class);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ImageActivity.this, "Please select other images...", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.show();

    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("return", "connectServer: "+e.getLocalizedMessage());
                // Cancel the post on failure.
                call.cancel();

                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ImageActivity.this, "Failed to Connect to Server", Toast.LENGTH_SHORT).show();
                        //textView.setText("Failed to Connect to Server");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("return", "onResponse");
                            Toast.makeText(ImageActivity.this, response.body().string(), Toast.LENGTH_SHORT).show();
                            //textView.setText(response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
                image.setImageURI(Uri.fromFile(f));
                uri = Uri.fromFile(f);
                selectedImagePath = getPath(getApplicationContext(), uri);
                imagesSelected = true;

                Log.d("tag","Absolute URL of image is " + Uri.fromFile(f));

                //image to upload in gallery
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//scan for new image file
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);//display in gallery
                this.sendBroadcast(mediaScanIntent); //inform mediaScanIntent that new file is created and that will be displayed in gallery
//               uploadFirebase(f.getName(),contentUri);
            }
        }
        else {
            if (requestCode == 10) {
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    image.setImageURI(uri);
                    selectedImagePath = getPath(getApplicationContext(), uri);
                    imagesSelected = true;

                }
            }

        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); //for image to appear in gallery
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
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
                photoFile = createImageFile(); //return image with filename
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.major.fileProvider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Cam_Request_Code);
            }
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}