package com.example.major;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.major.Retrofit.IUploadApi;
import com.example.major.Retrofit.RetrofitClient;
import com.example.major.Utils.Common;
import com.example.major.Utils.IUploadCallbacks;
import com.example.major.Utils.ProgressRequestBody;
import com.example.major.Retrofit.IUploadApi;
import com.example.major.Retrofit.RetrofitClient;
import com.example.major.Utils.Common;
import com.example.major.Utils.IUploadCallbacks;
import com.example.major.Utils.ProgressRequestBody;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class ScanActivity extends AppCompatActivity implements IUploadCallbacks {

    ImageView imageView,btnUpload,Save,Cancel;
    IUploadApi mService;
    Uri selectedFileUri;
    ProgressDialog dialog;

    //we need one string to save image name
    public String image_name="";

    //first we need bitmap and bitmapdrawable
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;


    private IUploadApi getApiUpload()
    {
        return RetrofitClient.getClient().create(IUploadApi.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mService = getApiUpload();

        imageView = (ImageView)findViewById(R.id.image_view);
        btnUpload = (ImageView)findViewById(R.id.button_upload);
        Save = (ImageView)findViewById(R.id.save);
        Cancel = (ImageView)findViewById(R.id.cancel);

        Uri image_uri = getIntent().getData();
        imageView.setImageURI(image_uri);
        selectedFileUri = image_uri;  //image_uri is uri for image that we take from camera

        //now i will write cancel and save function
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //on click cancel we will redirect to home activity without saving any image
                Intent i =new Intent(ScanActivity.this,CamActivity.class);
                startActivity(i);
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                FileOutputStream outputStream = null;

                //path to save file
                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsolutePath()+"/CamScannerCloneStorage");
                directory.mkdir();
                //make directory with the name CamScannerCloneStorage
                String fileName = image_name+".jpg";
                File outFile = new File(directory,fileName);

                try {
                    outputStream = new FileOutputStream(outFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    outputStream.flush();
                    outputStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });



    }

    private void uploadFile() {
        //now here we check we have uri of file or not
        if (selectedFileUri != null) {
            dialog = new ProgressDialog(ScanActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Uploading..");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();

            //now we load actual file from uri
            File file = null;
            try {
                file = new File(Common.getFilePath(this, selectedFileUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (file != null) {
                //if we get file and it is not empty then
                final ProgressRequestBody requestBody = new ProgressRequestBody(this, file);

                final MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mService.uploadFile(body)
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {

                                        //here we make image link that processed on server, so that we can update this processed image to show result in our app

                                        String image_processed_link = new StringBuilder("http://3.14.73.171/" +
                                                response.body().replace("\"", "")).toString();

                                        Toast.makeText(ScanActivity.this, "Please wait, Image is processing..", Toast.LENGTH_SHORT).show();

                                        //to load this image output from url we will use picasso
                                        Picasso.get()
                                                .load(image_processed_link)
                                                .fit().centerInside()
                                                .rotate(90)
                                                .into(imageView);       //so here we set image into our image view

                                        //so after loading image to image view what we have do is

                                        image_name += response.body().replace("\"","").split("/")[1];

                                        btnUpload.setVisibility(View.INVISIBLE);
                                        Save.setVisibility(View.VISIBLE);
                                        Cancel.setVisibility(View.VISIBLE);

                                        //so that we can save or cancel image
                                        //also we need image name so save image name from response.body

                                        dialog.dismiss();

                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Toast.makeText(ScanActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).start();

            }
        }
        else {
            Toast.makeText(this, "Cannot upload this file..", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onProgressUpdate(int percent) {

    }
}


