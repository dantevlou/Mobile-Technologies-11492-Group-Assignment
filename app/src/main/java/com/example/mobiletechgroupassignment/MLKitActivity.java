package com.example.mobiletechgroupassignment;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.pm.PackageManager;
import android.util.Log;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.List;

public class MLKitActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 3000;
    private Uri imageFileUri;
    private ImageView imageView;
    private TextView textViewOutput;
    private TextView textViewTitle;
    private String reader;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null &&
                                result.getData().getData() != null)
                            imageFileUri = result.getData().getData();
                        imageView.setImageURI(imageFileUri);

                        textViewOutput.setText("");
                        InputImage image = null;
                        try {
                            image = InputImage.fromFilePath(getBaseContext(), imageFileUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (image != null) {
                            if (reader != null && reader.equals("Barcode Reader")) {
                                processImageFromBarcodeReader(image);
                            } else if (reader != null && reader.equals("Content Reader")) {
                                processImageFromContentReader(image);
                            } else if (reader != null && reader.equals("Text Reader")) {
                                processImageFromTextReader(image);
                            }
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mlkit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageViewMLKit);
        textViewOutput = findViewById(R.id.textViewMLKit);
        textViewTitle = findViewById(R.id.textViewMLKitTitle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            reader = extras.getString("reader");
            textViewTitle.setText(reader);

            if (reader.equals("Barcode Reader")) {
                imageView.setImageResource(R.drawable.barcode);
            } else if (reader.equals("Content Reader")) {
                imageView.setImageResource(R.drawable.content);
            } else if (reader.equals("Text Reader")) {
                imageView.setImageResource(R.drawable.text);
            }
        }

        Bundle resultExtras = getIntent().getExtras();
        if (resultExtras != null && resultExtras.getString("result") != null) {
            String result = resultExtras.getString("result");
            textViewOutput.setText(result);
            Uri uri = Uri.parse(resultExtras.getString("uri"));
            imageView.setImageURI(uri);
        }
    }

    private boolean checkPermission() {
        String permission = android.Manifest.permission.CAMERA;
        boolean grantCamera = ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
        if (!grantCamera) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION);
        }
        return grantCamera;
    }

    public void openCamera(View view) {
        if (!checkPermission()) return;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        activityResultLauncher.launch(takePhotoIntent);
    }

    public void loadImage(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(galleryIntent);
    }

    public void openEditResults(View view) {
        Intent intent = new Intent(MLKitActivity.this, EditSaveActivity.class);
        intent.putExtra("reader", textViewTitle.getText().toString());
        intent.putExtra("result", textViewOutput.getText().toString());
        intent.putExtra("uri", imageFileUri != null ? imageFileUri.toString() : "");
        intent.putExtra("isNew", true);
        startActivity(intent);
    }

    public void processImageFromBarcodeReader(InputImage image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build();
        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    textViewTitle.setText("Barcode Reader");
                    textViewOutput.setText("Detected barcode:\n");
                    String result = "";
                    for (Barcode barcode : barcodes) {
                        result = barcode.getRawValue();
                        textViewOutput.append(result + "\n");
                    }
                    if (result.length() < 2) {
                        textViewOutput.append("Barcode not found.\n");
                    }
                    showEditButton();
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void processImageFromContentReader(InputImage image) {
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    textViewTitle.setText("Content Reader");
                    if (labels.size() == 0) {
                        textViewOutput.setText("Nothing found in the image\n");
                        showEditButton();
                        return;
                    }
                    textViewOutput.setText("Recognised image content:\n");
                    int counter = 1;
                    for (ImageLabel label : labels) {
                        String result = label.getText();
                        float confidence = label.getConfidence();
                        textViewOutput.append(counter + ". " + result +
                                " (" + String.format("%.2f", confidence * 100.0f) + "% confidence)\n");
                        counter++;
                    }
                    showEditButton();
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    public void processImageFromTextReader(InputImage image) {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    textViewTitle.setText("Text Reader");
                    textViewOutput.setText("Extracted text:\n");
                    String result = visionText.getText();
                    if (result.length() > 1)
                        textViewOutput.append(result + "\n");
                    else
                        textViewOutput.append("No text found.\n");
                    showEditButton();
                })
                .addOnFailureListener(e -> textViewOutput.setText("Failed"));
    }

    private void showEditButton() {
        findViewById(R.id.buttonEditResult).setVisibility(View.VISIBLE);
    }
}