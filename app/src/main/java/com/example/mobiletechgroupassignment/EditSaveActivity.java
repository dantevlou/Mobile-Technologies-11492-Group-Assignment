package com.example.mobiletechgroupassignment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EditSaveActivity extends AppCompatActivity {

    private EditText editTextReader;
    private EditText editTextResult;
    private ImageView imageViewEditSave;
    private Uri imageUri;
    private boolean isNew;
    private String firebaseKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_save);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextReader = findViewById(R.id.editTextReader);
        editTextResult = findViewById(R.id.editTextResult);
        imageViewEditSave = findViewById(R.id.imageViewEditSave);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            editTextReader.setText(extras.getString("reader"));
            editTextResult.setText(extras.getString("result"));
            isNew = extras.getBoolean("isNew", true);
            firebaseKey = extras.getString("firebaseKey", "");

            String uriString = extras.getString("uri");
            if (uriString != null && !uriString.isEmpty()) {
                imageUri = Uri.parse(uriString);
                imageViewEditSave.setImageURI(imageUri);
            }
        }
    }

    public void saveData(View view) {
        String reader = editTextReader.getText().toString();
        String result = editTextResult.getText().toString();

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Storage");

        if (isNew) {
            String currentDateTime = LocalDateTime.now().toString();
            String filename = currentDateTime.replaceAll("\\D+", "");

            Bitmap bitmap = getBitmapFromUri(imageUri);
            if (bitmap != null) {
                saveImageToGallery(bitmap, filename, this);
            }

            String newKey = dbref.push().getKey();
            Map<String, String> data = new HashMap<>();
            data.put("reader", reader);
            data.put("text", result);
            data.put("filename", filename);
            dbref.child(newKey).setValue(data);
        } else {
            Map<String, Object> updates = new HashMap<>();
            updates.put("reader", reader);
            updates.put("text", result);
            dbref.child(firebaseKey).updateChildren(updates);
        }

        Intent intent = new Intent(EditSaveActivity.this, ListViewActivity.class);
        startActivity(intent);
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            ImageDecoder.Source source =
                    ImageDecoder.createSource(getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        } catch (IOException e) {
            Log.e("URI_TO_BITMAP", "Failed to load image", e);
            return null;
        }
    }

    private void saveImageToGallery(Bitmap bitmap, String fileName, Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        Uri uri = context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream outputStream =
                    context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("SAVE_GALLERY", "Image saved: " + uri.toString());
        } catch (IOException e) {
            Log.e("SAVE_GALLERY", "Error saving image", e);
        }
    }
}