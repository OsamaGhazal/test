package com.example.write_on_sd_card.osama.handelfileissue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.security.PrivateKey;

public class DirectoryActivity extends AppCompatActivity {
    TextView myAppDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);
        myAppDirectory = findViewById(R.id.my_app_directory);

        // Set my app directory
        myAppDirectory.setText(getMyAppDirectory());

    }

    private String getMyAppDirectory() {
        return getFilesDir().getAbsolutePath();
    }
}
