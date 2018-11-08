package com.crazydwarf.chimaeraqm.sthaboutcurve.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
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

        final ImageView imLaunchLogo = findViewById(R.id.im_launchLogo);
        imLaunchLogo.setAlpha(0f);
        final ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
        animator.setDuration(1000);
        animator.setStartDelay(300);
        animator.setRepeatCount(0);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float currentValue = (Float) animator.getAnimatedValue();
                imLaunchLogo.setAlpha(currentValue);
                imLaunchLogo.requestLayout();
            }
        });

        final TextView tvLaunchTitle = findViewById(R.id.tv_launchtitle);
        float curTranslationX = tvLaunchTitle.getTranslationX();
        tvLaunchTitle.setTranslationX(1000f);
        final ValueAnimator tvValueAnimator = ValueAnimator.ofFloat(1000f,curTranslationX);
        tvValueAnimator.setDuration(1500);
        tvValueAnimator.setStartDelay(300);
        tvValueAnimator.setRepeatMode(ValueAnimator.RESTART);
        tvValueAnimator.setRepeatCount(0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                int a = 0;
                tvValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        Float currentValue = (Float) tvValueAnimator.getAnimatedValue();
                        tvLaunchTitle.setTranslationX(currentValue);
                        tvLaunchTitle.requestLayout();
                    }
                });
                tvValueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animLator) {
                        Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                tvValueAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }
}
