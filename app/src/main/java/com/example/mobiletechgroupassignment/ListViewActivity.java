package com.example.mobiletechgroupassignment;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private ListView listView;
    private List<AnalysedImageItem> items = new ArrayList<>();
    private AnalysedImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        adapter = new AnalysedImageAdapter(this, R.layout.list_item, items);
        listView.setAdapter(adapter);

        loadFromFirebase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AnalysedImageItem item = items.get(position);
                Intent intent = new Intent(ListViewActivity.this, DisplayItemActivity.class);
                intent.putExtra("key", item.getKey());
                intent.putExtra("reader", item.getReader());
                intent.putExtra("text", item.getText());
                intent.putExtra("filename", item.getFilename());
                intent.putExtra("uri", item.getImageUri() != null ?
                        item.getImageUri().toString() : "");
                startActivity(intent);
            }
        });
    }

    private void loadFromFirebase() {
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Storage");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();
                    String reader = child.child("reader").getValue(String.class);
                    String text = child.child("text").getValue(String.class);
                    String filename = child.child("filename").getValue(String.class);
                    Uri imageUri = loadImageFromGallery(filename + ".png");
                    items.add(new AnalysedImageItem(key, reader, text, filename, imageUri));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private Uri loadImageFromGallery(String filename) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DISPLAY_NAME + "=?";
        String[] selectionArgs = {filename};
        try (Cursor cursor = getContentResolver().query(uri, projection,
                selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                long imageId = cursor.getLong(idColumn);
                return ContentUris.withAppendedId(uri, imageId);
            }
        }
        return null;
    }

    public void openMainActivity(View view) {
        Intent intent = new Intent(ListViewActivity.this, MainActivity.class);
        startActivity(intent);
    }
}