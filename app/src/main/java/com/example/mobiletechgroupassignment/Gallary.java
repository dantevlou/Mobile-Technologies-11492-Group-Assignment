package com.example.mobiletechgroupassignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Gallary extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Intent resultIntent = new Intent();
            resultIntent.putExtra("imageUri", imageUri.toString());
            setResult(RESULT_OK, resultIntent);
        }
        finish();
    }
}