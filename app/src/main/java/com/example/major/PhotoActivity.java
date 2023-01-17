package com.example.major;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PhotoActivity extends AppCompatActivity implements RecyclerAdapter.CountOfImagesWhenRemoved{
   Button extract;
   TextView count;
   RecyclerView recyclerView;
   ImageView gallery;
   ImageView camera;
    RecyclerAdapter adapter;

    ArrayList<Uri> list;
    int Read_Permission = 0;
    int PERMISSION_CODE = 1;
    Uri imageUri;
//    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        extract = findViewById(R.id.extract);
        count = findViewById(R.id.count);
        recyclerView = findViewById(R.id.recycler);
        gallery = findViewById(R.id.gallery);
        camera = findViewById(R.id.camera);


        handlePermission();

        list = new ArrayList<>();

        adapter = new RecyclerAdapter(list, PhotoActivity.this, this);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i  = new Intent(PhotoActivity.this,CamActivity.class);
                startActivity(i);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), 10);
            }
        });


        extract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PhotoActivity.this, "Extract text here", Toast.LENGTH_SHORT).show();
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

        if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {  //when multiple images are selected
                    //limiting no of images picked from gallery
                    for (int i = 0; i < clipData.getItemCount(); i++) {

                        if(list.size()<10){
                            imageUri = clipData.getItemAt(i).getUri();
                            list.add(imageUri);
                            //image will be uploaded to firebase after being picked from gallery
//                        uploadToFirebase();
                            setImage();
                        }else{
                            Toast.makeText(PhotoActivity.this, "Not allowed to pick more than 10 images.", Toast.LENGTH_SHORT).show();
                        }


                    }

                    adapter.notifyDataSetChanged();
                    count.setText("Photos:(" + list.size() + ")");

                } else {
                    //limiting no of images picked from gallery
                    if(list.size()<10){
                        //this case is used if single image is picked
                        Uri uri = data.getData();
                        list.add(uri);
//                    uploadToFirebase();
                        setImage();
                        adapter.notifyDataSetChanged();
                        count.setText("Photos:(" + list.size() + ")");
                    }else {
                        Toast.makeText(PhotoActivity.this, "Not allowed to pick more than 10 images.", Toast.LENGTH_SHORT).show();
                    }

                }




            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }



    }




    private void setImage() {
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PhotoActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        if(adapter.getItemCount()>0){
            extract.setVisibility(View.VISIBLE);
        }
//        else{
//            extract.setVisibility(View.INVISIBLE);
//        }

    }



//    private void uploadToFirebase() {
//        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());          //in firebase images will be stored in images folder with name of any date and time
//        String imageFileName = "PNG_" + timeStamp + "_";
//        storageReference  = FirebaseStorage.getInstance().getReference().child("images/"+imageFileName);
//        storageReference.putFile(imageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(PhotoActivity.this, "Images Uploaded", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnCanceledListener(new OnCanceledListener() {
//                    @Override
//                    public void onCanceled() {
//                        Toast.makeText(PhotoActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }

    @Override
    public void clicked(int getSize)
    {
        count.setText("Photos:(" + list.size() + ")");
        if(list.size()==0){
            extract.setVisibility(View.INVISIBLE);
        }
    }

}