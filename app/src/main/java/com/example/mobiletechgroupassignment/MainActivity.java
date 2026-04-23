package com.example.mobiletechgroupassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton buttonBarcodeReader = findViewById(R.id.barcodeReaderButton);
        buttonBarcodeReader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BarcodeReader.class);
            startActivity(intent);
        });

        ImageButton buttonContentReader = findViewById(R.id.contentReaderButton);
        buttonContentReader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ContentReader.class);
            startActivity(intent);
        });

        ImageButton buttonTextReader = findViewById(R.id.textReaderButton);
        buttonTextReader.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TextReader.class);
            startActivity(intent);
        });

        Button buttonAnalysedList = findViewById(R.id.analysedListButton);
        buttonAnalysedList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AnalysedImages.class);
            startActivity(intent);
        });

    }
}