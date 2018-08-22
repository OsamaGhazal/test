package com.example.write_on_sd_card.osama.handelfileissue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RequstFileActivity extends AppCompatActivity {

    private final int SDRequestCode = 111;
    private final String MY_PREFS_NAME = "my_prefs_name";
    String TAG = "#RequstFileActivity#";
    EditText editText;
    TextView tvFileContent;
    DocumentFile selectedFile;
    private Intent mRequestFileIntent;

    private static String getRootOfInnerSdCardFolder(File file) {
        if (file == null)
            return null;
        final long totalSpace = file.getTotalSpace();
        while (true) {
            final File parentFile = file.getParentFile();
            if (parentFile == null || parentFile.getTotalSpace() != totalSpace)
                return file.getAbsolutePath();
            file = parentFile;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requst_file);
        editText = findViewById(R.id.new_file_name);
        tvFileContent = findViewById(R.id.tv_file_content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRequestFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            mRequestFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            mRequestFileIntent.setType("image/*");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SDRequestCode && data != null) {

            Uri treeUrii = data.getData();
            String path = FileUtil.getFullPathFromTreeUri(treeUrii, this);
            Uri treeUri = Uri.parse(path);
            selectedFile = DocumentFile.fromFile(new File(path));
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            grantUriPermission(getPackageName(), treeUri, takeFlags);



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                assert treeUri != null;
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            showFileContent(selectedFile);
            Log.e(TAG, "onActivityResult: file choicen name: " + selectedFile.getName() + " canW: " + selectedFile.canWrite());

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("sd_directory_uri", treeUri.toString());
            editor.apply();
        }
    }

    public void requastFile(View view) {
        startActivityForResult(mRequestFileIntent, SDRequestCode);
    }

    public void createFileInside(View view) {
        String enteredText = editText.getText().toString();
        Log.e(TAG, "createFileInside: canW " + selectedFile.canWrite() + " " + selectedFile.getName());
        if (enteredText.isEmpty()) {
            Toast.makeText(this, "Enter File Name", Toast.LENGTH_SHORT).show();
        } else {
            selectedFile.createDirectory(enteredText);
            showFileContent(selectedFile);
        }
    }

    public void selectFromPref(View view) {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("sd_directory_uri", "No name defined");//"No name defined" is the default value.
        Log.e(TAG, "the URIIIIIIIIII:" + name);
        Uri treeUri = Uri.parse(name);
        selectedFile = DocumentFile.fromTreeUri(this, treeUri);
        showFileContent(selectedFile);
    }

    public void selctInternal(View view) {
        File f = Environment.getExternalStorageDirectory();
        selectedFile = DocumentFile.fromFile(new File(f.getAbsolutePath()));
        Log.e(TAG, "selctInternal: file uri: " + selectedFile.getUri());

        showFileContent(selectedFile);

    }

    public void selectSD(View view) {
        selectedFile = DocumentFile.fromFile(new File(getSdcardPath()));
        Log.e(TAG, "selectSD: pathtttttttttt: " + selectedFile.getUri().toString());
        showFileContent(selectedFile);
    }

    void showFileContent(DocumentFile f) {
        tvFileContent.setText("file contain: can W?" + f.canWrite() + " isD?" + f.isDirectory());
        tvFileContent.append("\n" + f.getUri().getPath());
        for (DocumentFile cF : f.listFiles()) {
            tvFileContent.append("\n" + cF.getName());
        }
    }

    public String getSdcardPath() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String sdPath = getSdCardPath(false);
            if (sdPath != null) {
                return sdPath;
            } else {
                return "";

            }
        }
        return "/storage/extSdCard";
    }

    public String getSdCardPath(final boolean includePrimaryExternalStorage) {
        File[] externalCacheDirs = ContextCompat.getExternalCacheDirs(this);
        if (externalCacheDirs == null || externalCacheDirs.length == 0) {
            return null;
        }
        if (externalCacheDirs.length == 1) {
            if (externalCacheDirs[0] == null)
                return null;
            final String storageState = EnvironmentCompat.getStorageState(externalCacheDirs[0]);
            if (!Environment.MEDIA_MOUNTED.equals(storageState))
                return null;
            if (!includePrimaryExternalStorage && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Environment.isExternalStorageEmulated())
                return null;
        }
        final List<String> result = new ArrayList<>();
        if (includePrimaryExternalStorage || externalCacheDirs.length == 1)
            result.add(getRootOfInnerSdCardFolder(externalCacheDirs[0]));
        for (int i = 1; i < externalCacheDirs.length; ++i) {
            final File file = externalCacheDirs[i];
            if (file == null)
                continue;
            final String storageState = EnvironmentCompat.getStorageState(file);
            if (Environment.MEDIA_MOUNTED.equals(storageState))
                result.add(getRootOfInnerSdCardFolder(externalCacheDirs[i]));
        }
        if (result.isEmpty())
            return null;
        return result.get(0);
    }


}
