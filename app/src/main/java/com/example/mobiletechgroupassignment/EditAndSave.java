package com.example.mobiletechgroupassignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditAndSave extends AppCompatActivity {

    private EditText editTextReader;
    private EditText editTextResults;
    private ImageView imageView;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_and_save);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance().getReference("images");

        editTextReader = findViewById(R.id.editTextReader);
        editTextResults = findViewById(R.id.editTextResults);
        imageView = findViewById(R.id.imageView);
        Button saveButton = findViewById(R.id.saveButton);

        String readerType = getIntent().getStringExtra("readerType");
        String results = getIntent().getStringExtra("results");
        String uriString = getIntent().getStringExtra("imageUri");
        Bitmap bitmap = getIntent().getParcelableExtra("bitmap");

        editTextReader.setText(readerType);
        editTextResults.setText(results);

        if (uriString != null) {
            imageView.setImageURI(Uri.parse(uriString));
        } else if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }

        saveButton.setOnClickListener(v -> {
            String reader = editTextReader.getText().toString();
            String resultText = editTextResults.getText().toString();
            String key = database.push().getKey();
            database.child(key).child("reader").setValue(reader);
            database.child(key).child("results").setValue(resultText);
            Intent intent = new Intent(this, AnalysedImages.class);
            startActivity(intent);
            finish();
        });
    }
}