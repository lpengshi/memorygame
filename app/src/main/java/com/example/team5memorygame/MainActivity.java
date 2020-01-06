package com.example.team5memorygame;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridView gridView = findViewById(R.id.gridView);
        ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();
        ImageAdapter imagesAdapter = new ImageAdapter(this, bitmapList);
        gridView.setAdapter(imagesAdapter);

        Button fetchBtn = findViewById(R.id.fetch);
        fetchBtn.setOnClickListener(this);

        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Image image = images[position];

                // This tells the GridView to redraw itself
                // in turn calling your BooksAdapter's getView method again for each cell
                imagesAdapter.notifyDataSetChanged();
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        EditText urlText = (EditText) findViewById(R.id.urlText);
        String url = urlText.getText().toString();
        new ImagesAsyncTask(this).execute(url);
    }

}
