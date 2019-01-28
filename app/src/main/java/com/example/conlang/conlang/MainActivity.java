package com.example.conlang.conlang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {


    Button admin,txt_customer,txt_translato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("continue as");


        admin = (Button) findViewById(R.id.admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, loginActivity.class);
                startActivity(i);
            }
        });


        txt_customer= (Button) findViewById(R.id.txt_customer);
        txt_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, loginActivity.class);
                startActivity(i);
            }
        });




        txt_translato= (Button) findViewById(R.id.txt_translator);
        txt_translato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, loginActivity.class);
                startActivity(i);
            }
        });

    }
}






