package com.crazydwarf.chimaeraqm.sthaboutcurve.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ComplexColorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;


import com.crazydwarf.chimaeraqm.sthaboutcurve.R;
import com.crazydwarf.chimaeraqm.sthaboutcurve.dialog.PointDetailDialog;
import com.crazydwarf.chimaeraqm.sthaboutcurve.util.UserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BezierCurveView extends View
{
    private Context mContext;
    private float centerX = 0;
    private float centerY = 0;
    //用于fragment初次建立时，centerX和centerY初始化后控制点的重置
    private boolean initCheck = false;
    /**
     * @param mPoints 保存控制点
     * @param drawPoints 所需绘制的点
     *
     */
    private List<PointF> mPoints = new ArrayList<PointF>();
    private List<PointF> drawPoints = new ArrayList<PointF>();

    //t:曲线绘制的控制节点
    private float t = 0;

    //根据杨辉三角设置的曲线计算常数
    private int[] constValue = null;

    //bezier曲线的阶数
    private int level;

    /**
     * @param pickTag 用于标记将要移动点的序号
     */
    int pickTag = -1;

    /**
     * 界面上绘制的图形包括
     * 1 控制点连线 mControlLinePaint
     * 2 控制点 mControlPointPaint
     * 3 bezier曲线 mBezierPaint
     * 4 bezier曲线端点 mBezierPaint
     * 4 文字 mTextPaint
     * 5 辅助线
     * 6 辅助线端点（与控制点连线的交点）
     */
    //绘制各线的paint和path
    private Paint mControlLinePaint;
    private Paint mControlPointPaint;

    private Paint mBezierPaint;
    private Paint mBezierPointPaint;

    private Paint mTextPaint;

    private List<Paint> adsLinePaints = new ArrayList<Paint>();
    private List<Paint> adsPointPaints = new ArrayList<Paint>();

    private Path mControlPath;
    private Path mBezierPath;
    private Path mAdsLinePath;

    public BezierCurveView(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public BezierCurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    public BezierCurveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    void initView()
    {
        if(level < 2)
            return;

        if(mPoints.size() != level+1)
        {

            int[] colorList = {ContextCompat.getColor(mContext,R.color.colorBezier2),
                    ContextCompat.getColor(mContext,R.color.colorBezier3),
                    ContextCompat.getColor(mContext,R.color.colorBezier4),
                    ContextCompat.getColor(mContext,R.color.colorBezier5),
                    ContextCompat.getColor(mContext,R.color.colorBezier6),
                    ContextCompat.getColor(mContext,R.color.colorBezier7),
                    ContextCompat.getColor(mContext,R.color.colorBezier8),
                    ContextCompat.getColor(mContext,R.color.colorBezier9),
                    ContextCompat.getColor(mContext,R.color.colorBezier10),
            };

            mControlLinePaint = new Paint();
            mControlLinePaint.setColor(Color.GRAY);
            mControlLinePaint.setStrokeWidth(8);
            mControlLinePaint.setStyle(Paint.Style.STROKE);
            mControlLinePaint.setAntiAlias(true);
            mControlLinePaint.setStrokeCap(Paint.Cap.ROUND);

            mControlPointPaint  = new Paint();
            mControlPointPaint.setColor(Color.BLACK);
            mControlPointPaint.setStrokeWidth(4);
            mControlPointPaint.setStyle(Paint.Style.STROKE);
            mControlPointPaint.setAntiAlias(true);
            mControlPointPaint.setStrokeCap(Paint.Cap.ROUND);

            mBezierPaint = new Paint();
            mBezierPaint.setColor(Color.RED);
            mBezierPaint.setStrokeWidth(8);
            mBezierPaint.setStyle(Paint.Style.STROKE);
            mBezierPaint.setAntiAlias(true);
            mBezierPaint.setStrokeCap(Paint.Cap.ROUND);

            mBezierPointPaint  = new Paint();
            mBezierPointPaint.setColor(Color.BLACK);
            mBezierPointPaint.setStrokeWidth(10);
            mBezierPointPaint.setStyle(Paint.Style.FILL);
            mBezierPointPaint.setAntiAlias(true);

            mTextPaint = new Paint();
            int textStrokeWidth = UserUtil.px2sp(mContext,10);
            mTextPaint.setTextSize(40);
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setStrokeWidth(textStrokeWidth);
            mTextPaint.setStyle(Paint.Style.FILL);
            for(int i=0;i<level-1;i++)
            {
                Paint adsLinePaint1 = new Paint();
                adsLinePaint1.setColor(colorList[i]);
                adsLinePaint1.setStrokeWidth(8);
                adsLinePaint1.setStyle(Paint.Style.STROKE);
                adsLinePaint1.setAntiAlias(true);
                adsLinePaint1.setStrokeCap(Paint.Cap.ROUND);
                adsLinePaints.add(adsLinePaint1);

                Paint adsPointPaint1 = new Paint();
                adsPointPaint1.setColor(colorList[i]);
                adsPointPaint1.setStrokeWidth(10);
                adsPointPaint1.setStyle(Paint.Style.FILL);
                adsPointPaint1.setAntiAlias(true);
                adsPointPaints.add(adsPointPaint1);
            }

            mControlPath = new Path();
            mBezierPath = new Path();
            mAdsLinePath = new Path();


            constValue = null;
            if(level == 2)
            {
                constValue = new int[]{1,2,1};
            }
            else if(level == 3)
            {
                constValue = new int[]{1,3,3,1};
            }
            else if(level == 4)
            {
                constValue = new int[]{1,4,6,4,1};
            }
            else if(level == 5)
            {
                constValue = new int[]{1,5,10,10,5,1};
            }
            else if(level == 6)
            {
                constValue = new int[]{1,6,15,20,15,6,1};
            }
            else if(level == 7)
            {
                constValue = new int[]{1,7,21,35,35,21,7,1};
            }
            else if(level == 8)
            {
                constValue = new int[]{1,8,28,56,70,56,28,8,1};
            }
            else if(level == 9)
            {
                constValue = new int[]{1,9,36,84,126,126,84,36,9,1};
            }
            else if(level == 10)
            {
                constValue = new int[]{1,10,45,120,200,252,200,120,45,10,1};
            }
        }

        if(mPoints.size() != level+1 || initCheck == true)
        {
            mPoints.clear();
            //初始化各点位置
            float gap = 1000f/level;
            for(int i = 0;i<=level;i++)
            {
                float xpos = centerX-500+gap*i;
                mPoints.add(new PointF(xpos,centerY));
            }
        }
        drawPoints.clear();
        drawPoints.add(mPoints.get(0));
        invalidate();
    }

    void cleanSetup()
    {
        mControlPath.reset();
        mBezierPath.reset();
        mAdsLinePath.reset();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //mControlPointPaint绘制控制点，mTextPaint绘制控制点文字
        for(PointF pointi : mPoints)
        {
            canvas.drawPoint(pointi.x,pointi.y,mControlPointPaint);
            int pos = mPoints.indexOf(pointi);
            String pointText = String.format(Locale.US,"P%d(%.2f,%.2f)",pos,pointi.x,pointi.y);
            canvas.drawText(pointText,pointi.x,pointi.y,mTextPaint);
        }

        //mControlLinePaint沿mControlPath绘制辅助线
        mControlPath.reset();
        PointF pointHead = mPoints.get(0);
        mControlPath.moveTo(pointHead.x,pointHead.y);
        List<PointF> adsPoints = new ArrayList<PointF>();
        for(int i=0;i<mPoints.size();i++)
        {
            PointF pointi = mPoints.get(i);
            mControlPath.lineTo(pointi.x,pointi.y);
            adsPoints.add(pointi);
        }
        canvas.drawPath(mControlPath,mControlLinePaint);

        //adsLinePaints绘制辅助线，adsPointPaints绘制辅助线端点
        List<PointF> backupAdsPoints = new ArrayList<PointF>();
        for(int i=0;i<level-1;i++)
        {
            Paint adsPointPaint1 = adsPointPaints.get(i);
            Paint adsLinePaint1 = adsLinePaints.get(i);
            mAdsLinePath.reset();
            for(int m=1;m < adsPoints.size();m++)
            {
                PointF point0 = adsPoints.get(m-1);
                PointF pointi = adsPoints.get(m);
                PointF process = new PointF();
                process.x = (1 - t) * point0.x + t * pointi.x;
                process.y = (1 - t) * point0.y + t * pointi.y;
                backupAdsPoints.add(process);
                if(m == 1)
                {
                    mAdsLinePath.moveTo(process.x,process.y);
                }
                else
                {
                    mAdsLinePath.lineTo(process.x,process.y);
                }
                canvas.drawPoint(process.x,process.y,adsPointPaint1);
            }
            canvas.drawPath(mAdsLinePath,adsLinePaint1);
            adsPoints.clear();
            for(int m=0;m<backupAdsPoints.size();m++)
            {
                adsPoints.add(backupAdsPoints.get(m));
            }
            backupAdsPoints.clear();

        }


        //mBezierPaint沿mBezierPath绘制bezier曲线,mBezierPointPaint绘制bezier曲线尾点
        mBezierPath.reset();
        if(drawPoints.size() > 1)
        {
            mBezierPath.moveTo(pointHead.x,pointHead.y);
            for(int i=1;i<drawPoints.size();i++)
            {
                PointF pointi = drawPoints.get(i);
                mBezierPath.lineTo(pointi.x,pointi.y);
            }
            canvas.drawPath(mBezierPath,mBezierPaint);
            PointF tailPoint = drawPoints.get(drawPoints.size()-1);
            canvas.drawPoint(tailPoint.x,tailPoint.y,mBezierPointPaint);
            String bezierPointCoor = String.format(Locale.US,"(%.2f,%.2f)",tailPoint.x,tailPoint.y);
            canvas.drawText(bezierPointCoor,tailPoint.x,tailPoint.y,mTextPaint);
        }
    }

    public void startAnimator()
    {
        //开始动画之前重置相关绘制设置
        initView();
        //将绘制过程细分为100，绘制时间5s;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(10 * 1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                cleanSetup();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                t = (float) (animation.getAnimatedValue());
                float finalX = 0;
                float finalY = 0;
                //level 临时计算bezier曲线的阶数
                for(int i=0;i<mPoints.size();i++)
                {
                    PointF pointi = mPoints.get(i);
                    float pointix = pointi.x;
                    float pointiy = pointi.y;
                    int param = constValue[i];
                    double x = param * pointix * Math.pow(1-t,level-i) * Math.pow(t,i);
                    double y = param * pointiy * Math.pow(1-t,level-i) * Math.pow(t,i);
                    finalX += x;
                    finalY += y;
                }
                PointF nextPoint = new PointF(finalX,finalY);
                drawPoints.add(nextPoint);
                invalidate();
            }
        });
        valueAnimator.start();

    }

    /**
     * @param eventx,eventy 记录鼠标点击下去的坐标信息
     */
    private float eventx;
    private float eventy;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        /**
         * @param distance 用于记录鼠标点击点和各控制点的距离，距离最近的点即为将要移动的点
         */
        float distance = 0;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                for(int i=0;i<mPoints.size();i++)
                {
                    PointF pointF = mPoints.get(i);
                    eventx = event.getX();
                    eventy = event.getY();
                    if(i == 0)
                    {
                        distance = UserUtil.distanceBetween(pointF.x,pointF.y,eventx,eventy);
                        pickTag = 0;
                    }
                    else
                    {
                        float newdist = UserUtil.distanceBetween(pointF.x,pointF.y,eventx,eventy);
                        if(newdist < distance)
                        {
                            distance = newdist;
                            pickTag = i;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(pickTag > -1)
                {
                    PointF point1 = mPoints.get(pickTag);
                    point1.x = event.getX();
                    point1.y = event.getY();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                long downTime = event.getDownTime();
                long eventTime = event.getEventTime();
                long interval = eventTime-downTime;
                if(interval > 300)
                {
                    float upx = event.getX();
                    float upy = event.getY();
                    float moveDist = UserUtil.distanceBetween(eventx,eventy,upx,upy);
                    if(moveDist < 15)
                    {
                        PointDetailDialog pointDetailDialog = new PointDetailDialog(mContext);
                        pointDetailDialog.show();
                    }
                }
                break;

                default:
                    break;

        }
        return true;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w/2;
        centerY = h/2;
        initCheck = true;
        initView();
        initCheck = false;
    }

    public void setLevel(int level) {
        this.level = level;
        initView();
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public int[] getConstValue() {
        return constValue;
    }

    public List<PointF> getmPoints() {
        return mPoints;
    }

    public List<PointF> getDrawPoints() {
        return drawPoints;
    }

    public void setDrawPoints(List<PointF> drawPoints) {
        this.drawPoints = drawPoints;
        invalidate();
    }
}
