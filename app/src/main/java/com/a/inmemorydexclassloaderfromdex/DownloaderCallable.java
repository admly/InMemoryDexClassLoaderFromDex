package com.a.inmemorydexclassloaderfromdex;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

class DownloaderCallable implements Callable {
    public DownloaderCallable(Context applicationContext) {
    }

    @Override
    public Object call() throws Exception {


        URL url = new URL("http://10.0.2.2:8888/classes.dex");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();

        ByteArrayOutputStream bufferArray = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[2048];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            bufferArray.write(data, 0, nRead);
        }

        return bufferArray;
    }
}
