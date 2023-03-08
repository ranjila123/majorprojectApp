package com.example.major;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class DownloadActivity extends AppCompatActivity {

    Button download;
    EditText name;
    TextView date;
    private TextView dateTimeDisplay;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String dateToday;
    DownloadManager downloadManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        download = findViewById(R.id.button);
        name = findViewById(R.id.editText);
        date = findViewById(R.id.textView3);

        calendar = Calendar.getInstance();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        date.setText("Created on:"+timeStamp);
    }

    public void download(View v){
        try {
            String filename = name.getText().toString().trim()+".csv";
            downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("http://192.168.1.77:8000/download_csv");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, filename);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            Long reference = downloadManager.enqueue(request);
            Intent intent = new Intent(DownloadActivity.this,ImageActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Log.d("download", "downloadError: "+e.getLocalizedMessage());
            Intent intent = new Intent(DownloadActivity.this,ErrorActivity.class);
            startActivity(intent);
        }



//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://192.168.0.233:8000/download_csv")
//                .build();
//        Response response = client.newCall(request).execute();
//
//// Save the CSV file to disk
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "output.csv");
//        BufferedSink sink = Okio.buffer(Okio.sink(file));
//        sink.writeAll(response.body().source());
//        sink.close();
//
//        // Display a message to the user indicating that the file has been downloaded
//        Toast.makeText(DownloadActivity.this, "File downloaded to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();







    }
}