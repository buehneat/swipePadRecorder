package com.example.aaronbuehne.swipepadrecorder;

import android.content.Intent;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.view.View;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ParticipantInfo extends AppCompatActivity {

    public static ArrayList<String> writeFile = new ArrayList<>();
    public static String participantID = "";
    public static String age = "";
    public static String gender = "";
    public static boolean firstTime;
    public static boolean practice;
    EditText idEdit;
    EditText ageEdit;
    TextView label;
    RadioGroup radioSexGroup;
    RadioButton radioSexButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_info);

        idEdit = findViewById(R.id.idNumInput);
        ageEdit = findViewById(R.id.ageInput);
        radioSexGroup = findViewById(R.id.radioSex);
        label = findViewById(R.id.InfoLabel);

        ParticipantInfo.firstTime = true;

    }

    public void goInstructions(View view) {
        if (idEdit.getText().toString().equals("") || ageEdit.getText().toString().equals("")) {
            label.setText("ID and Age must be filled");
        }
        else {
            int radioSelect = radioSexGroup.getCheckedRadioButtonId();
            radioSexButton = findViewById(radioSelect);
            gender = radioSexButton.getText().toString();
            participantID = (idEdit.getText().toString());
            age = (ageEdit.getText().toString());

            Intent intent = new Intent(this, Instructions.class);
            startActivity(intent);
        }
    }
}
