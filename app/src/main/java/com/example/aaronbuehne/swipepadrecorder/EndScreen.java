package com.example.aaronbuehne.swipepadrecorder;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.*;

public class EndScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);

        waiter();

    }

    private void writeToFile(String data, Context context) {
        try {

            if (isWritable()) {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File (root.getAbsolutePath() + "/download");
                dir.mkdirs();
                File file = new File(dir, SwipeScreen.fileName);
                FileOutputStream f = new FileOutputStream(file, true);
                PrintWriter pw = new PrintWriter(f);
                pw.println(data);
                pw.flush();
                pw.close();
                f.close();
            }
            else {
                System.out.println("Not writable");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isWritable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        else {
            return false;
        }
    }

    public void waiter(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startWriting();
            }
        }, 3000);


    }

    //Writes data to download directory of phone
    public void startWriting(){
        for (int i = 0; i < ParticipantInfo.writeFile.size(); i++) {
            writeToFile(ParticipantInfo.writeFile.get(i), this.getApplicationContext());
        }

        TextView endLabel = findViewById(R.id.endLabel);
        endLabel.setText("Thank you. You may now exit the app");
    }
}
