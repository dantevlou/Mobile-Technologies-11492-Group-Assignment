package com.example.mobiletechgroupassignment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DisplayItemActivity extends AppCompatActivity {

    private TextView textViewDisplayReader;
    private TextView textViewDisplayResult;
    private ImageView imageViewDisplay;
    private String key;
    private String reader;
    private String text;
    private String filename;
    private String uriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewDisplayReader = findViewById(R.id.textViewDisplayReader);
        textViewDisplayResult = findViewById(R.id.textViewDisplayResult);
        imageViewDisplay = findViewById(R.id.imageViewDisplay);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            key = extras.getString("key");
            reader = extras.getString("reader");
            text = extras.getString("text");
            filename = extras.getString("filename");
            uriString = extras.getString("uri");

            textViewDisplayReader.setText(reader);
            textViewDisplayResult.setText(text);

            if (uriString != null && !uriString.isEmpty()) {
                Uri imageUri = Uri.parse(uriString);
                imageViewDisplay.setImageURI(imageUri);
            }
        }
    }

    public void editItem(View view) {
        Intent intent = new Intent(DisplayItemActivity.this, EditSaveActivity.class);
        intent.putExtra("reader", reader);
        intent.putExtra("result", text);
        intent.putExtra("uri", uriString);
        intent.putExtra("isNew", false);
        intent.putExtra("firebaseKey", key);
        startActivity(intent);
    }

    public void deleteItem(View view) {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Storage");
        dbref.child(key).removeValue();
        Intent intent = new Intent(DisplayItemActivity.this, ListViewActivity.class);
        startActivity(intent);
    }

    public void cancelItem(View view) {
        Intent intent = new Intent(DisplayItemActivity.this, ListViewActivity.class);
        startActivity(intent);
    }
}