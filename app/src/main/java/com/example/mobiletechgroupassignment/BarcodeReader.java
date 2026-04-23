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

public class BarcodeReader extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private ImageView imageView;
    private Bitmap currentBitmap;
    private Uri currentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_barcode_reader);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView = findViewById(R.id.imageView);
        Button openCameraButton = findViewById(R.id.textOpenCameraButton);
        Button loadImageButton = findViewById(R.id.barcodeLoadImageButton);
        Button saveButton = findViewById(R.id.saveButton);

        openCameraButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Camera.class);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        });

        loadImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Gallary.class);
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        });

        saveButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditAndSave.class);
            intent.putExtra("readerType", "Barcode Reader");
            intent.putExtra("results", "Detected barcode results here");
            if (currentUri != null) {
                intent.putExtra("imageUri", currentUri.toString());
            } else if (currentBitmap != null) {
                intent.putExtra("bitmap", currentBitmap);
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            currentBitmap = (Bitmap) data.getExtras().get("bitmap");
            currentUri = null;
            imageView.setImageBitmap(currentBitmap);
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            currentUri = Uri.parse(data.getStringExtra("imageUri"));
            currentBitmap = null;
            imageView.setImageURI(currentUri);
        }
    }
}