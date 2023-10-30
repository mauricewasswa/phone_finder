package com.rengcorp.phone_finder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Name extends AppCompatActivity {

    EditText namespace;
    Button showmapbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        showmapbtn = findViewById(R.id.showmap);
        namespace = findViewById(R.id.name);

        String enteredName = namespace.getText().toString();


        showmapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Name.this, MainActivity.class);
                intent.putExtra("user_name", enteredName);
                startActivity(intent);
            }
        });
    }
}