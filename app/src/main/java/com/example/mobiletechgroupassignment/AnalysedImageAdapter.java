package com.example.mobiletechgroupassignment;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AnalysedImageAdapter extends ArrayAdapter<AnalysedImageItem> {

    private List<AnalysedImageItem> items;

    public AnalysedImageAdapter(@NonNull Context context, int resource,
                                @NonNull List<AnalysedImageItem> objects) {
        super(context, resource, objects);
        items = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, parent, false);
        }

        AnalysedImageItem item = items.get(position);

        ImageView imageView = convertView.findViewById(R.id.imageViewListItem);
        TextView textView = convertView.findViewById(R.id.textViewListItem);

        if (item.getImageUri() != null) {
            imageView.setImageURI(item.getImageUri());
        }

        textView.setText(item.getReader());

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.LTGRAY);
        }

        return convertView;
    }
}