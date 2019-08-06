package com.miaozi.gesturelockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class GestureLockView extends View {
    private int mRadius = 60;
    private Paint mLinePaint;
    private Paint mOuterPaint;
    private Paint mInnerPaint;
    private int mNormalColor = 0xff979696;
    private int mPressedColor = 0xffE95C5C;
    private int mErrorColor = 0xff7E0202;
    private int mCorrectColor = 0xff1129C2;
    private boolean mFistInit;
    private List<Point> mPointList = new ArrayList<>();
    private List<Point> mPressedPointList = new ArrayList<>();
    private float mMoveX;
    private float mMoveY;
    private static final int STATUS_DEFAULT = 0;
    private static final int STATUS_CORRECT = 1;
    private static final int STATUS_ERROR = 2;
    private static final int STATUS_PRESSED = 3;
    //设置默认初始密码
    private String mNormalPassword = "012";
    //九个空格代表的密码
    private String[] mLockNormalValue = {"0","1","2","3","4","5","6","7","8"};
    public GestureLockView(Context context) {
        this(context,null);
    }

    public GestureLockView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GestureLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.GestureLockView);
        mNormalColor = typedArray.getColor(R.styleable.GestureLockView_defaultColor,mNormalColor);
        mPressedColor = typedArray.getColor(R.styleable.GestureLockView_pressedColor,mPressedColor);
        mErrorColor = typedArray.getColor(R.styleable.GestureLockView_errorColor,mErrorColor);
        mCorrectColor = typedArray.getColor(R.styleable.GestureLockView_correctColor,mCorrectColor);
        mRadius = (int) typedArray.getDimension(R.styleable.GestureLockView_radius,mRadius);
        typedArray.recycle();
    }


    private void initPoint() {

        mPointList.clear();

        int width = getWidth();
        int height = getHeight();

        float offsetY = 0.0f;
        float offsetX = 0.0f;

        float circleWidth;

        //兼容横竖屏
        if(width > height){
            offsetX = ( width - height ) / 2;
            circleWidth = height / 3 ;
        }else {
            offsetY = ( height - width ) / 2;
            circleWidth = width / 3 ;
        }

        mPointList.add(new Point(offsetX + circleWidth / 2 ,  offsetY + circleWidth / 2 ,mLockNormalValue[0]));
        mPointList.add(new Point(offsetX + 3 * circleWidth / 2 , offsetY + circleWidth / 2 ,mLockNormalValue[1]));
        mPointList.add(new Point(offsetX + 5 * circleWidth / 2 , offsetY + circleWidth / 2 ,mLockNormalValue[2]));

        mPointList.add(new Point(offsetX + circleWidth / 2 , offsetY + 3 * circleWidth / 2 ,mLockNormalValue[3]));
        mPointList.add(new Point(offsetX + 3 * circleWidth / 2 , offsetY + 3 * circleWidth / 2 ,mLockNormalValue[4]));
        mPointList.add(new Point(offsetX + 5 * circleWidth / 2 , offsetY + 3 * circleWidth / 2 ,mLockNormalValue[5]));

        mPointList.add(new Point(offsetX + circleWidth / 2 ,  offsetY + 5 * circleWidth / 2 ,mLockNormalValue[6]));
        mPointList.add(new Point(offsetX + 3 * circleWidth / 2 , offsetY + 5 * circleWidth / 2 ,mLockNormalValue[7]));
        mPointList.add(new Point(offsetX + 5 * circleWidth / 2 , offsetY + 5 * circleWidth / 2 ,mLockNormalValue[8]));

    }

    private void initPaint() {

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(mPressedColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(6);

        mOuterPaint = new Paint();
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setColor(mNormalColor);
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setStrokeWidth(6);

        mInnerPaint = new Paint();
        mInnerPaint.setAntiAlias(true);
        mInnerPaint.setColor(mNormalColor);
        mInnerPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!mFistInit){
            mFistInit = true;
            initPaint();
            initPoint();
        }

        for(Point point : mPointList){
            //根据不同的状态 设置不同的画笔颜色
            if(point.status == STATUS_PRESSED){
                mOuterPaint.setColor(mPressedColor);
                mInnerPaint.setColor(mPressedColor);
            }else if(point.status == STATUS_DEFAULT){
                mOuterPaint.setColor(mNormalColor);
                mInnerPaint.setColor(mNormalColor);
            }else if(point.status == STATUS_ERROR){
                mOuterPaint.setColor(mErrorColor);
                mInnerPaint.setColor(mErrorColor);
            }else if(point.status == STATUS_CORRECT){
                mOuterPaint.setColor(mCorrectColor);
                mInnerPaint.setColor(mCorrectColor);
            }
            //画外圆
            canvas.drawCircle(point.centerX, point.centerY, mRadius, mOuterPaint);
            //画内圆
            canvas.drawCircle(point.centerX, point.centerY, mRadius / 6, mInnerPaint);
        }


        //画线
        if(mPressedPointList.size() > 0){
            Point point = mPressedPointList.get(mPressedPointList.size() - 1);
            //抬起的时候不需要再画多出来的直线
            if( mMoveX != 0.0f && mMoveY != 0.0f) {
                canvas.drawLine(point.centerX, point.centerY,
                        mMoveX, mMoveY, mLinePaint);
            }
            for(int i = 0 ; i < mPressedPointList.size()-1 ; i ++ ){
                canvas.drawLine(mPressedPointList.get(i).centerX,mPressedPointList.get(i).centerY,
                        mPressedPointList.get(i+1).centerX,mPressedPointList.get(i+1).centerY,mLinePaint);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = null;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mMoveX = event.getX();
                mMoveY = event.getY();
                //按下的点和圆心的距离是否大于半径
                point = judgeInCircle(mMoveX, mMoveY);
                if(point!=null){
                    mPressedPointList.add(point);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = event.getX();
                mMoveY = event.getY();
                //按下的点和圆心的距离是否大于半径
                point = judgeInCircle(mMoveX,mMoveY);
                if(point!=null) {
                    Point lastPoint = mPressedPointList.get(mPressedPointList.size() - 1);
                    float centerX1 = Math.abs(lastPoint.centerX + point.centerX) / 2;
                    float centerY1 = Math.abs(lastPoint.centerY + point.centerY) / 2;
                    Point centerPoint = judgeInCircle(centerX1, centerY1);
                    if(centerPoint!=null) {
                        mPressedPointList.add(centerPoint);
                    }
                    mPressedPointList.add(point);
                }
                break;
            case MotionEvent.ACTION_UP:
                mMoveX = 0.0f;
                mMoveY = 0.0f;
                showResult();
                break;
        }
        invalidate();
        return true;
    }

    /**
     * 恢复数据
     */
    private void recoverStatus(){
        mPressedPointList.clear();
        for(Point point : mPointList){
            point.status = STATUS_DEFAULT;
        }
        mLinePaint.setColor(mPressedColor);
        invalidate();
    }

    /**
     * 显示结果
     */
    private void showResult(){
        int status;
        String result = "";
        for(Point point : mPressedPointList){
            result += point.psw;
        }
        if(result.equals(mNormalPassword)){
            status = 1;
            mLinePaint.setColor(mCorrectColor);
        }else {
            status = 2;
            mLinePaint.setColor(mErrorColor);
        }
        if(this.lockValueCallBack!=null){
            this.lockValueCallBack.valueCallBack(result);
        }
        for(Point point : mPointList){
            if(point.status == STATUS_PRESSED) {
                if (status == 1) {
                    point.status = STATUS_CORRECT;
                } else if (status == 2) {
                    point.status = STATUS_ERROR;
                }
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recoverStatus();
            }
        },500);
    }

    /**
     * 判断划过的点是否是圆点
     */
    private Point judgeInCircle(float centerX,float centerY){
        for(Point point : mPointList){
            //勾股定理 Math.abs()取绝对值 Math.pow(a,b)取a的b次方 Math.sqrt()开平方
            double x2 = Math.pow(Math.abs(centerX - point.centerX), 2);
            double y2 = Math.pow(Math.abs(centerY - point.centerY), 2);
            double r = Math.sqrt( x2 + y2);
            if(r < mRadius){
                if(!mPressedPointList.contains(point)) {
                    point.status = STATUS_PRESSED;
                    return point;
                }
            }
        }
        return null;
    }

    /**
     * 设置默认数组值
     */
    public void setLockValueArray(String[] normalValues){
        this.mLockNormalValue = null;
        this.mLockNormalValue = normalValues;
        invalidate();
    }

    /**
     * 设置正确密码
     * @param correctValue
     */
    public void setCorrectValue(String correctValue){
        this.mNormalPassword = correctValue;
    }
    private LockValueCallBack lockValueCallBack;
    protected void setLockValueCallBack(LockValueCallBack lockValueCallBack){
        this.lockValueCallBack = lockValueCallBack;
    }
    protected interface LockValueCallBack{
        void valueCallBack(String value);
    }
    static class Point{
        private float centerX;
        private float centerY;
        private String psw;
        private int status;

        public Point(float centerX, float centerY, String psw){
            this.centerX = centerX;
            this.centerY = centerY;
            this.psw = psw;
            this.status = STATUS_DEFAULT;
        }
        public String toString(){
            return "centerX="+centerX+" centerY="+centerY + " psw="+psw +" status="+ this.status;
        }
    }
}
