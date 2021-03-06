package com.a.inmemorydexclassloaderfromdex;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import dalvik.system.InMemoryDexClassLoader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button button1;
    TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button);
        button1.setOnClickListener(this);
        textView1 = findViewById(R.id.textView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            performAction();
        }
    }

    private void performAction() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<String> future = pool.submit(new LoaderCallable(this.getApplicationContext()));
        try {
            String string = future.get();
            textView1.setText(string);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class LoaderCallable implements Callable<String> {

    private Context context;

    LoaderCallable(Context applicationContext) {
        this.context = applicationContext;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public String call() throws Exception {

        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future<ByteArrayOutputStream> future = pool.submit(new DownloaderCallable(context));

        ByteBuffer byteBuffer = ByteBuffer.wrap(future.get().toByteArray());

        Log.i("MainActivity", "DOWNLOADING FINISHED!!!");

        InMemoryDexClassLoader inMemoryDexClassLoader = new InMemoryDexClassLoader(byteBuffer, context.getClassLoader());

        Class<?> dynamicClass = inMemoryDexClassLoader.loadClass("ClassToLoadWithClassloader");
        Constructor<?> ctor = dynamicClass.getConstructor();
        Object clazz = ctor.newInstance();
        Method method = dynamicClass.getMethod("sayHello");
        return (String) method.invoke(clazz);
    }
}