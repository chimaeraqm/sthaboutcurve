package com.crazydwarf.chimaeraqm.sthaboutcurve.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crazydwarf.chimaeraqm.sthaboutcurve.R;

import java.util.List;

public class PointDetailDialog extends Dialog
{
    private Context mContext;
    public PointDetailDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_point_detail);

        EditText edValueX = findViewById(R.id.et_valuex);
        edValueX.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });

        EditText edValueY = findViewById(R.id.et_valuey);
        edValueY.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return false;
            }
        });

        ImageView imUse = findViewById(R.id.im_use);
        imUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
