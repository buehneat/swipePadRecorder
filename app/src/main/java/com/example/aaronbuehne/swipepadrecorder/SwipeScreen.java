package com.example.aaronbuehne.swipepadrecorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import android.widget.TextView;

public class SwipeScreen extends AppCompatActivity implements SensorEventListener{
    Rect buttonBounds[] = new Rect[9];
    ImageView buttons[] = new ImageView[9];
    //Set pattern based on positions of buttons
    //Buttons numbered so top left is 0, to the right of it is 1, to the right of 1 is 2, and so on until bottom right is 8
    String pattern;
    String inputPattern = "";
    Boolean buttonsSet[] = new Boolean[9];
    ArrayList<String> SensorData = new ArrayList<>();
    Paint paint;
    Path path;
    Canvas canvas;
    boolean pathStarted = false;
    ImageView drawingImageView;
    TextView label;
    final String correct = "Pattern Correct";
    final String incorrect = "Please Enter Pattern";
    float originx;
    float originy;
    float fingerx;
    float fingery;
    boolean startGyro = false;
    int count = 4;
    ImageView bound1;
    ImageView bound2;
    ImageView bound3;
    ImageView bound4;
    int correctTimes;
    Button pbtn;
    TextView timesleft;
    boolean done;
    boolean pract;

    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    private int requestCode;
    private int grantResults[];

    public static String fileName = ParticipantInfo.age + ParticipantInfo.gender + ParticipantInfo.participantID + ".txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_screen);
        correctTimes = 0;
        timesleft = findViewById(R.id.timesLeft);
        timesleft.setText("10");
        done = false;
        if (ParticipantInfo.practice) {
            pract = true;
            timesleft.setVisibility(View.INVISIBLE);
        }
        else {
            pract = false;
            pbtn = findViewById(R.id.back_btn);
            pbtn.setClickable(false);
            pbtn.setEnabled(false);
            pbtn.setVisibility(View.INVISIBLE);
        }
        if (ParticipantInfo.firstTime) {
            pattern = Instructions.pattern1;
        }
        else {
            ImageView patternI = findViewById(R.id.patternView);
            patternI.setImageResource(R.drawable.pattern2);
            pattern = Instructions.pattern2;
        }
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        label = findViewById(R.id.Label);
        drawingImageView = this.findViewById(R.id.drawingImageView);
        drawingImageView.setTop(0);
        drawingImageView.setLeft(0);
        drawingImageView.setBottom(Resources.getSystem().getDisplayMetrics().heightPixels);
        drawingImageView.setRight(Resources.getSystem().getDisplayMetrics().widthPixels);
        Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bitmap);
        drawingImageView.setImageBitmap(bitmap);
        bound1 = findViewById(R.id.lineBound1);
        bound2 = findViewById(R.id.lineBound2);
        bound3 = findViewById(R.id.lineBound3);
        bound4 = findViewById(R.id.lineBound4);

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);


        //buttons in pattern code
        ImageView button1 = findViewById(R.id.button1);
        ImageView button2 = findViewById(R.id.button2);
        ImageView button3 = findViewById(R.id.button3);
        ImageView button4 = findViewById(R.id.button4);
        ImageView button5 = findViewById(R.id.button5);
        ImageView button6 = findViewById(R.id.button6);
        ImageView button7 = findViewById(R.id.button7);
        ImageView button8 = findViewById(R.id.button8);
        ImageView button9 = findViewById(R.id.button9);

        //buttons in array
        buttons[0] = button1;
        buttons[1] = button2;
        buttons[2] = button3;
        buttons[3] = button4;
        buttons[4] = button5;
        buttons[5] = button6;
        buttons[6] = button7;
        buttons[7] = button8;
        buttons[8] = button9;

        //booleans that tell whether or not each button was activated
        buttonsSet[0] = false;
        buttonsSet[1] = false;
        buttonsSet[2] = false;
        buttonsSet[3] = false;
        buttonsSet[4] = false;
        buttonsSet[5] = false;
        buttonsSet[6] = false;
        buttonsSet[7] = false;
        buttonsSet[8] = false;



        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setImageResource(R.drawable.pattern_button_untouched);
            buttonsSet[i] = false;
        }


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},requestCode);


        onRequestPermissionsResult(requestCode,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},grantResults);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {


                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission", "granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.uujm

                    //app cannot function without this permission for now so close it...
                    onDestroy();
                }
                return;
            }

            // other 'case' line to check fosr other
            // permissions this app might request
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {
        if (startGyro) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mGravity = event.values;
                if(!pract) {
                    SensorData.add("Accelerometer,x:" + event.values[0] + ",y:" + event.values[1] + ",z:" + event.values[2] + ",Time:" + timestamp + "\n");
                }
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values;
            if (mGravity != null && mGeomagnetic != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    if(!pract) {
                        SensorData.add("Azimut:" + orientation[0] + ",Pitch:" + orientation[1] + ",Roll:" + orientation[2] + ",Time:" + timestamp + "\n");
                    }
                }
            }
        }
    }

    //Measures where finger is
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        checkLines();

        path = new Path();
        int action = MotionEventCompat.getActionMasked(event);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        //bounds of each button
        for (int i = 0; i < buttons.length; i++) {

            int l[] = new int[2];
            buttons[i].getLocationOnScreen(l);
            Rect rect = new Rect();
            //makes button 20 smaller on each side so that there is room to go between the buttons
            rect.left = l[0] + 40;
            rect.top = l[1] + 40;
            rect.right = l[0] + buttons[i].getWidth() - 40;
            rect.bottom = l[1] + buttons[i].getHeight() - 40;


            buttonBounds[i] = rect;

        }

        switch (action) {
            //User touched screen
            case (MotionEvent.ACTION_DOWN) :
                startGyro = true;
                fingerx = event.getX();
                fingery = event.getY();
                if (!done) {
                    label.setText(incorrect);
                }
                if(!pract) {
                    SensorData.add("x:" + event.getX() + ",y:" + event.getY() + ",Time:" + timestamp + "\n");
                    SensorData.add("Pressure:" + event.getPressure() + ",Time:" + timestamp + "\n");
                    SensorData.add("Size:" + event.getSize() + ",Time:" + timestamp + "\n");
                }
                for(int i = 0; i < buttonBounds.length; i++) {
                    //User touched a button
                    if (buttonBounds[i].contains((int) event.getX(), (int) event.getY())) {
                        if (!buttonsSet[i]) {
                            if(!pract) {
                                SensorData.add("Button:" + i + ",x:" + buttonBounds[i].centerX()
                                        + ",y:" + buttonBounds[i].centerY() + ",Time:" + timestamp + "\n");
                            }
                            buttons[i].setImageResource(R.drawable.pattern_btn_touched);
                            inputPattern = inputPattern + i;
                            buttonsSet[i] = true;
                            if (!pathStarted) {
                                pathStarted = true;
                                originx = buttonBounds[i].centerX();
                                originy = buttonBounds[i].centerY();
                                //makes sure line is on center
                                if (i == 0 || i == 3 || i == 6) {
                                    originx = buttonBounds[i].centerX() - 45;
                                }
                                else if (i == 2 || i == 5 || i == 8) {
                                    originx = buttonBounds[i].centerX() + 45;
                                }
                                if (i == 0 || i == 1 || i == 2) {
                                    originy = buttonBounds[i].centerY() - 25;
                                }
                                else if (i == 6 || i == 7 || i == 8) {
                                    originy = buttonBounds[i].centerY() + 70;
                                }
                            }
                        }
                    }
                }
                return true;
            //User moved finger
            case (MotionEvent.ACTION_MOVE) :
                fingerx = event.getX();
                fingery = event.getY();
                if(!pract) {
                    SensorData.add("x:" + event.getX() + ",y:" + event.getY() + ",Time:" + timestamp + "\n");
                    SensorData.add("Pressure:" + event.getPressure() + ",Time:" + timestamp + "\n");
                    SensorData.add("Size:" + event.getSize() + ",Time:" + timestamp + "\n");
                }
                for(int i = 0; i < buttonBounds.length; i++) {
                    //User swiped across button
                    if (buttonBounds[i].contains((int) event.getX(), (int) event.getY())) {
                        if (!buttonsSet[i]) {
                            int middle = -1;
                            buttons[i].setImageResource(R.drawable.pattern_btn_touched);
                            if (inputPattern.length() > 0) {
                                middle = checkMiddle(Character.getNumericValue(inputPattern.charAt(inputPattern.length() - 1)), i);
                            }
                            if (middle != -1 && !buttonsSet[middle]) {
                                inputPattern = inputPattern + middle;
                                buttons[middle].setImageResource(R.drawable.pattern_btn_touched);
                                buttonsSet[middle] = true;
                                if(!pract) {
                                    SensorData.add("Button:" + middle + ",x:"
                                            + buttonBounds[middle].centerX() + ",y:" + buttonBounds[middle].centerY() + ",Time:" + timestamp + "\n");
                                }
                            }
                            if(!pract) {
                                SensorData.add("Button:" + i + ",x:" + buttonBounds[i].centerX()
                                        + ",y:" + buttonBounds[i].centerY() + ",Time:" + timestamp + "\n");
                            }
                            inputPattern = inputPattern + i;
                            buttonsSet[i] = true;
                            if (!pathStarted) {
                                pathStarted = true;
                                originx = buttonBounds[i].centerX();
                                originy = buttonBounds[i].centerY();
                                if (i == 0 || i == 3 || i == 6) {
                                    originx = buttonBounds[i].centerX() - 45;
                                }
                                else if (i == 2 || i == 5 || i == 8) {
                                    originx = buttonBounds[i].centerX() + 45;
                                }
                                if (i == 0 || i == 1 || i == 2) {
                                    originy = buttonBounds[i].centerY() - 25;
                                }
                                else if (i == 6 || i == 7 || i == 8) {
                                    originy = buttonBounds[i].centerY() + 70;
                                }

                            }
                        }
                    }
                }
                return true;
            //User lifted finger
            case (MotionEvent.ACTION_UP) :
                for (int i = 0; i < buttons.length; i++) {
                    buttons[i].setImageResource(R.drawable.pattern_button_untouched);
                    buttonsSet[i] = false;
                }
                if (!(inputPattern.equals(""))) {
                    //if the pattern is correct, then it will save the recorded data
                    if (inputPattern.equals(pattern) && correctTimes < 10) {
                        if(!pract) {
                            if (correctTimes == 0) {
                                if (ParticipantInfo.firstTime) {
                                    toBeWritten(("ID:" + ParticipantInfo.participantID +
                                            ",Age:" + ParticipantInfo.age + "," +
                                            "Gender:" + ParticipantInfo.gender + ",First\n"));
                                } else {
                                    toBeWritten(("ID:" + ParticipantInfo.participantID +
                                            ",Age:" + ParticipantInfo.age + "," +
                                            "Gender:" + ParticipantInfo.gender + ",Second\n"));
                                }
                            }
                            SensorData.add("FingerUp," + "Time:" + timestamp + "\n");
                            for (int i = 0; i < SensorData.size(); i++) {
                                toBeWritten(SensorData.get(i));
                                //System.out.println(SensorData.get(i));
                            }
                        }
                        label.setText(correct);
                        if(!pract) {
                            correctTimes++;
                        }
                        int togo = 10 - correctTimes;
                        String thing = Integer.toString(togo);
                        timesleft.setText(thing);
                        if(!pract) {
                            if (correctTimes == 10) {
                                toBeWritten("End\n");
                                done = true;
                                if (ParticipantInfo.firstTime) {
                                    goForTwo();
                                } else {
                                    Intent intent = new Intent(this, EndScreen.class);
                                    startActivity(intent);
                                    label.setText("Thank you. You may now exit the app");
                                }
                            }
                        }
                    }
                }
                System.out.println("Inputed: " + inputPattern);
                inputPattern = "";
                //Resets the recorded data
                SensorData = new ArrayList<>();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                path = new Path();
                path.reset();
                pathStarted = false;
                startGyro = false;
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    //writes the sensor data to file
    private void toBeWritten(String data) {

        if(!pract) {
            ParticipantInfo.writeFile.add(data);
        }

    }

    //checks if line passes through a middle button
    public int checkMiddle(int first, int second) {
        if (first == 0) {
            switch (second) {
                case 2:
                    return 1;
                case 6:
                    return 3;
                case 8:
                    return 4;
                default:
                    return -1;
            }
        }
        if (first == 1) {
            switch (second) {
                case 7:
                    return 4;
                default:
                    return -1;
            }
        }
        if (first == 2) {
            switch (second) {
                case 0:
                    return 1;
                case 8:
                    return 5;
                case 6:
                    return 4;
                default:
                    return -1;
            }
        }
        if (first == 3) {
            switch (second) {
                case 5:
                    return 4;
                default:
                    return -1;
            }
        }
        if (first == 5) {
            switch (second) {
                case 3:
                    return 4;
                default:
                    return -1;
            }
        }
        if (first == 6) {
            switch (second) {
                case 0:
                    return 3;
                case 2:
                    return 4;
                case 8:
                    return 7;
                default:
                    return -1;
            }
        }
        if (first == 7) {
            switch (second) {
                case 1:
                    return 4;
                default:
                    return -1;
            }
        }
        if (first == 8) {
            switch (second) {
                case 0:
                    return 4;
                case 2:
                    return 5;
                case 6:
                    return 7;
                default:
                    return -1;
            }
        }
        return -1;
    }

    //Draws the line that follows finger
    public void checkLines() {

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        path = new Path();
        path.reset();
        //For some reason, an image needs to be being updated on the phone for lines to work
        //As well as lines will only show up in that images immediate area
        if (count == 4) {
            bound1.setImageResource(R.drawable.pattern_button_gray);
            bound2.setImageResource(R.drawable.pattern_button_gray);
            bound3.setImageResource(R.drawable.pattern_button_gray);
            bound4.setImageResource(R.drawable.pattern_button_gray);
            count = 5;
        }
        else {
            bound1.setImageResource(R.drawable.pattern_button_untouched);
            bound2.setImageResource(R.drawable.pattern_button_untouched);
            bound3.setImageResource(R.drawable.pattern_button_untouched);
            bound4.setImageResource(R.drawable.pattern_button_untouched);
            count = 4;
        }
        if (pathStarted) {
            path.moveTo(originx, originy - 80);
            for (int i = 0; i < inputPattern.length(); i++) {
                if (inputPattern.length() > 0) {
                    int j = Character.getNumericValue(inputPattern.charAt(i));
                    float linex = buttonBounds[j].centerX();
                    float liney = buttonBounds[j].centerY();
                    if (j == 0 || j == 3 || j == 6) {
                        linex = buttonBounds[j].centerX() - 45;
                    }
                    else if (j == 2 || j == 5 || j == 8) {
                        linex = buttonBounds[j].centerX() + 45;
                    }
                    if (j == 0 || j == 1 || j == 2) {
                        liney = buttonBounds[j].centerY() - 25;
                    }
                    else if (j == 6 || j == 7 || j == 8) {
                        liney = buttonBounds[j].centerY() + 70;
                    }
                    path.lineTo(linex, liney - 80);
                }
            }
            path.lineTo(fingerx, fingery - 80);
            canvas.drawPath(path, paint);
        }
    }

    public void goForTwo() {
        ParticipantInfo.firstTime = false;
        Intent intent = new Intent(this, Instructions.class);
        startActivity(intent);
    }

    public void goBack(View view) {
        ParticipantInfo.practice = false;
        finish();
    }
}
