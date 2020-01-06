package com.example.team5memorygame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImagesAsyncTask extends AsyncTask<String, Integer, List<Bitmap>> {

    private AppCompatActivity caller;

    public ImagesAsyncTask(AppCompatActivity caller){
        this.caller = caller;
    }

    @Override
    protected List<Bitmap> doInBackground(String... params) {

        List<String> imagesURL = getImagesURL(params[0]);
        List<String> fPaths = new ArrayList<>();

        String fPath = "";
        for(int i=0; i<20; i++){
            fPath = caller.getFilesDir() + "/image" + i + ".jpg";
            fPaths.add(fPath);
        }

        long imageLen = 0;
        long totalSoFar = 0;
        int readLen = 0;
        List<Bitmap> bitmaps = new ArrayList<>();
        Bitmap bitmap = null;

        for (int i=0; i<imagesURL.size(); i++) {
            try {
                URL url = new URL(imagesURL.get(i));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.addRequestProperty("User-Agent", "Mozilla/4.76");
                conn.connect();

                imageLen = conn.getContentLength();
                byte[] data = new byte[1024];

                InputStream in = url.openStream();
                BufferedInputStream bufIn = new BufferedInputStream(in, 2048);
                OutputStream out = new FileOutputStream(fPaths.get(i));

                while ((readLen = bufIn.read(data)) != -1) {
                    totalSoFar += readLen;
                    out.write(data, 0, readLen);
                }
                publishProgress((int)((i + 1) / 20 * 100));
                File file = new File(fPaths.get(i));
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                bitmaps.add(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bitmaps;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        AppCompatActivity activity = caller;
        ProgressBar bar = activity.findViewById(R.id.progressBar);
        bar.setProgress(Math.round(values[0]));
        //set text also?
    }

    @Override
    protected void onPostExecute(List<Bitmap> bitmaps) {
        AppCompatActivity activity = caller;
        GridView gridView = caller.findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(caller, bitmaps));
        ProgressBar bar = activity.findViewById(R.id.progressBar);
        bar.setVisibility(View.GONE);
    }

    public List<String> getImagesURL(String website) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        List<String> imagesURL = new ArrayList<>();


        try {
            url = new URL(website);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/4.76");
            conn.connect();
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                if(line.contains("https://cdn.stocksnap.io/img-thumbs/280h")){
                    line = line.substring(10, 65);
                    System.out.println(line);
                    imagesURL.add(line);
                    if(imagesURL.size() == 20)
                        break;
                }

            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
            }
        }

        return imagesURL;
    }
}
