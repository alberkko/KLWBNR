package com.example.alber.klwbnr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button RegBtn;
    private Button LogBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        RegBtn = (Button) findViewById(R.id.start_reg_btn);
        LogBtn = (Button) findViewById(R.id.start_log_btn);
        RegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent =  new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
        LogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent =  new Intent(StartActivity.this, LoginActivity.class);
                startActivity(reg_intent);
            }
        });
    }
}
