package com.example.major;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class DownloadActivity extends AppCompatActivity {

    Button download;
    TextView name;
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
        name = findViewById(R.id.textView2);
        date = findViewById(R.id.textView3);
        name.setText("Name");

        calendar = Calendar.getInstance();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        date.setText("Created on:"+timeStamp);
    }

    public void download(View v){
        downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse("http://192.168.1.77:8000/MajorProject-master/src/output.csv");
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "output.csv");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        Long reference = downloadManager.enqueue(request);


//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url("https://www.africau.edu/images/default/sample.pdf").build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    throw new IOException("Unexpected code " + response);
//                }
//                String responseData = response.body().string();
//                // Write responseData to a local file in the app's internal storage
//                String filename = "sample.pdf";
//                File file = new File(getFilesDir(), filename);
//                FileOutputStream outputStream;
//                try {
//                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
//                    outputStream.write(responseData.getBytes());
//                    outputStream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });




    }
}