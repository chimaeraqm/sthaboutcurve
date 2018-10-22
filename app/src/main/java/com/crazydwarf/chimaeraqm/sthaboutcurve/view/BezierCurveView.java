package com.crazydwarf.chimaeraqm.sthaboutcurve.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ComplexColorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;


import com.crazydwarf.chimaeraqm.sthaboutcurve.R;
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
     * @ param pickTag 用于标记将要移动点的序号
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

    private Paint mGridPaint;
    private float mGridGap_Width;
    private float mGridGap_Height;

    //记录action_down时操作点的位置(lastPointX,lastPointY)，以及action_move后点的位置(thisPointX,thisPointY)
    //并由两位置间移动的距离及时间判断是否为长按事件
    private boolean mIsLongPressed = false;
    private float lastPointX = 0;
    private float lastPointY = 0;
    private float thisPointX = 0;
    private float thisPointY = 0;
    private long lastDownTime = 0;
    private long thisEventTime = 0;

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
        {
            return;
        }

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

            mGridPaint = new Paint();
            mGridPaint.setColor(ContextCompat.getColor(mContext,R.color.colorTransGray));
            mGridPaint.setStrokeWidth(4);
            mGridPaint.setStyle(Paint.Style.STROKE);
            mGridPaint.setAntiAlias(true);
            mGridPaint.setStrokeCap(Paint.Cap.ROUND);
            mGridPaint.setPathEffect(new DashPathEffect(new float[]{10,5},0));
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
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mGridGap_Width = width/10.0f;
        mGridGap_Height = height/10.0f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float gridWidth_a = mGridGap_Width;
        float gridHeight_a = mGridGap_Height;
        Path gridPath = new Path();
        for(int i=0;i<9;i++)
        {
            //用纵横虚线划分绘图区域
            gridPath.moveTo(gridWidth_a,0);
            float height = getHeight();
            gridPath.lineTo(gridWidth_a,height);
            canvas.drawPath(gridPath,mGridPaint);
            gridPath.reset();

            gridPath.moveTo(0,gridHeight_a);
            float width = getWidth();
            gridPath.lineTo(width,gridHeight_a);
            canvas.drawPath(gridPath,mGridPaint);
            gridPath.reset();

            gridWidth_a += mGridGap_Width;
            gridHeight_a += mGridGap_Height;
        }
        //mControlPointPaint绘制控制点，mTextPaint绘制控制点文字
        for(PointF pointi : mPoints)
        {
            canvas.drawPoint(pointi.x,pointi.y,mControlPointPaint);
            int pos = mPoints.indexOf(pointi);
            String pointText = String.format(Locale.US,"P%d(%.2f,%.2f)",pos,pointi.x,pointi.y);
            //获取String边界，校正String显示位置
            Rect textRect = new Rect();
            mTextPaint.getTextBounds(pointText,0,pointText.length(),textRect);
            if((pointi.x + textRect.width()) > getWidth())
            {
                float startX =  getWidth() - textRect.width();
                canvas.drawText(pointText,startX,pointi.y,mTextPaint);
            }
            else
            {
                canvas.save();
                canvas.rotate(-45,pointi.x,pointi.y);
                canvas.drawText(pointText,pointi.x,pointi.y,mTextPaint);
                canvas.restore();
            }
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
                lastPointX = event.getX();
                lastPointY = event.getY();
                lastDownTime = event.getDownTime();
                for(int i=0;i<mPoints.size();i++)
                {
                    PointF pointF = mPoints.get(i);
                    if(i == 0)
                    {
                        distance = UserUtil.distanceBetween(pointF.x,pointF.y,lastPointX,lastPointY);
                        pickTag = 0;
                    }
                    else
                    {
                        float newdist = UserUtil.distanceBetween(pointF.x,pointF.y,lastPointX,lastPointY);
                        if(newdist < distance)
                        {
                            distance = newdist;
                            pickTag = i;
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                thisPointX = event.getX();
                thisPointY = event.getY();
                thisEventTime = event.getEventTime();
                //判断是否为长按事件
                if(!mIsLongPressed)
                {
                    mIsLongPressed = isLongPressed(lastPointX,lastPointY,thisPointX,thisPointY,lastDownTime,thisEventTime,500);
                }
                //长按事件弹出对应点坐标编辑对话框
                //非长按事件移动点至点击位置
                if(pickTag > -1)
                {
                    if(mIsLongPressed)
                    {

                    }
                    else
                    {
                        PointF point1 = mPoints.get(pickTag);
                        point1.x = thisPointX;
                        point1.y = thisPointY;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mIsLongPressed = false;
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

    //判断是否为长按时间
    static boolean isLongPressed(float lastX, float lastY, float thisX, float thisY, long lastDownTime, long thisEventTime,	long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
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
