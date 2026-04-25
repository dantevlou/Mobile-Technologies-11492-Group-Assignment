package com.example.mobiletechgroupassignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TextReader extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_text_reader);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        Button openCameraButton = findViewById(R.id.textOpenCameraButton);
        Button loadImageButton = findViewById(R.id.barcodeLoadImageButton);

        openCameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Camera.class);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        });

        loadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Gallary.class);
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("bitmap");
            imageView.setImageBitmap(photo);
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = Uri.parse(data.getStringExtra("imageUri"));
            imageView.setImageURI(imageUri);
        }
    }
}