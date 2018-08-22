package com.example.write_on_sd_card.osama.handelfileissue;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
int permission[]=new int[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get permition to write in storage
        permission[0] = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
if(permission[0] != PackageManager.PERMISSION_GRANTED){
    ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

}
    }

    public void directory(View view) {
        Intent intent =new Intent(this,DirectoryActivity.class);
        startActivity(intent);
    }

    public void requstFile(View view) {
        Intent intent =new Intent(this,RequstFileActivity.class);
        startActivity(intent);
    }
}
