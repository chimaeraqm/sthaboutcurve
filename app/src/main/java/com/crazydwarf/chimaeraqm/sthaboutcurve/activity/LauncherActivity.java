package com.crazydwarf.chimaeraqm.sthaboutcurve.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazydwarf.chimaeraqm.sthaboutcurve.R;

public class LauncherActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        TextView tv_GetIn = findViewById(R.id.tv_getin);
        tv_GetIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                Intent intent = new Intent(LauncherActivity.this,GeneticAlgorithmActivity.class);
                intent.putExtra("LOGIN",false);
                startActivity(intent);
                finish();
            }
        });

        ImageView im_GetIn = findViewById(R.id.im_getin);
        im_GetIn = findViewById(R.id.im_getin);
        im_GetIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                Intent intent = new Intent(LauncherActivity.this,GeneticAlgorithmActivity.class);
                intent.putExtra("LOGIN",false);
                startActivity(intent);
                finish();
            }
        });
    }
}
