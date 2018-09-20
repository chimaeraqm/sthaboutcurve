package com.crazydwarf.chimaeraqm.sthaboutcurve.fragment;

import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.crazydwarf.chimaeraqm.sthaboutcurve.R;
import com.crazydwarf.chimaeraqm.sthaboutcurve.view.BezierCurveView;

import java.util.ArrayList;
import java.util.List;

public class BezierCurveFragment extends Fragment
{
    private View view;
    private int level;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_bezier_curve,container,false);
        //BezierCurveView mBezierCurveView = new BezierCurveView(getActivity(),level);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        level = bundle.getInt("LEVEL",2);
        final BezierCurveView mBezierCurveView = view.findViewById(R.id.bezierCurveView);
        mBezierCurveView.setLevel(level);

        Button bnSimu = view.findViewById(R.id.bn_simu);
        bnSimu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBezierCurveView.startAnimator();
            }
        });
    }
}
