/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.examples.revolvegesture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * RevolveGesture
 * <p>
 * 旋转进度
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 8, 2013
 */
public class RevolveGestureView extends View {

    private static final String LOG_TAG = RevolveGestureView.class.getSimpleName();

    private Bitmap mBitmap;

    private int mMaxWidth = 0;

    private float mCenterX = 0;

    private float mCenterY = 0;

    private int width;

    private int height;

    private float mDetaDegree;

    // 默认起始角度
    private float mDefaultDegree = 225f;

    /**
     * 最大角度，与最小角度,以下两个数值是在正常坐标系的角度
     * 需要考虑这是倒立的平面直角坐标系，这样就可以控制旋转的范围在正常的坐标系的[225°,-45°]之间
     */
    private float mMaxDegree = 45f;

    private float mMinDegree = -225f;

    private RectF mOvalRectF = new RectF();

    private Paint mMainPaint;

    private Paint mFirstPaint;

    private int mMaxProgress;

    private int mCurrentProgress;

    private ArrayList<PointF> mPointFs;

    private static final int STEP = 10;

    private Drawable mDrawable;

    public RevolveGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Drawable bgDrawable = getBackground();
        // if (bgDrawable != null && bgDrawable instanceof BitmapDrawable) {
        // mBitmap = ((BitmapDrawable) bgDrawable).getBitmap();
        // }
        initSize();
        mDetaDegree = -mDefaultDegree;
        mMainPaint = new Paint();
        mMainPaint.setAntiAlias(true);
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setColor(0xFFFF0000);
        mMainPaint.setStrokeWidth(8);

        mFirstPaint = new Paint();
        mFirstPaint.setAntiAlias(true);
        mFirstPaint.setStyle(Paint.Style.STROKE);
        mFirstPaint.setColor(0xFF00FF00);
        mFirstPaint.setStrokeWidth(5);

        mMaxProgress = (int) (mMaxDegree - mMinDegree);
        mCurrentProgress = (int) Math.abs(mMinDegree - mDetaDegree);

    }

    public void setRevolveDrawableResource(int id) {
        mDrawable = getContext().getResources().getDrawable(id);
        mDrawable.setCallback(this);
        if (mDrawable != null && mDrawable.isStateful()) {
            mDrawable.setState(getDrawableState());
        }
        initSize();
        int left = (int) ((mMaxWidth - width) * 0.5);
        int top = (int) ((mMaxWidth - height) * 0.5);
        mDrawable.setBounds(left, top, width + left, height + top);
        postInvalidate();
    }

    @Override
    protected void drawableStateChanged() {
        Drawable d = mDrawable;
        if (d != null && d.isStateful()) {
            int[] state = getDrawableState();
            d.setState(state);
        }
        super.drawableStateChanged();
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return who == mDrawable || super.verifyDrawable(who);
    }

    private void initSize() {
        if (mDrawable == null) {
            return;
        }
        width = mDrawable.getIntrinsicWidth();
        height = mDrawable.getIntrinsicHeight();
        mMaxWidth = (int) Math.sqrt(width * width + height * height);
        mCenterX = mCenterY = mMaxWidth * 0.5f;

        // 初始化点
        int pointCount = mMaxProgress / STEP + 1;
        mPointFs = new ArrayList<PointF>(pointCount);
        float radius = (width * 0.5f) + 5.0f;
        for (int i = (int) mMinDegree; i <= mMaxDegree; i = i + STEP) {
            mPointFs.add(getPointF(mCenterX, mCenterY, i, radius));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMaxWidth, mMaxWidth);
        float r = (width * 0.5f);
        mOvalRectF.top = mCenterY - r;
        mOvalRectF.left = mCenterX - r;
        mOvalRectF.right = mCenterX + r;
        mOvalRectF.bottom = mCenterY + r;
        Log.i(LOG_TAG, "Oval RectF: " + mOvalRectF.toString());
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int saveCount = canvas.save();
        canvas.rotate(mDetaDegree, mMaxWidth * 0.5f, mMaxWidth * 0.5f);
        mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(LOG_TAG, "当前角度: " + mDetaDegree);
        // Matrix matrix = new Matrix();
        // // 设置转轴位置
        // matrix.setTranslate((float) width / 2, (float) height / 2);
        // // 开始转,正--逆时针；负--顺时针
        // matrix.preRotate(mDetaDegree);
        // // 转轴还原
        // matrix.preTranslate(-(float) width / 2, -(float) height / 2);
        // // 将位置送到view的中心
        // matrix.postTranslate((float) (mMaxWidth - width) / 2, (float)
        // (mMaxWidth - height) / 2);
        // canvas.drawBitmap(mBitmap, matrix, null);
        //

        // canvas.drawArc(mOvalRectF, mMinDegree, mMaxProgress, false,
        // mMainPaint);
        // canvas.drawArc(mOvalRectF, mMinDegree, mCurrentProgress, false,
        // mFirstPaint);
        if (mPointFs != null && mPointFs.size() > 0) {
            for (PointF p : mPointFs) {
                // Log.i(LOG_TAG, "Point: " + p.toString());
                canvas.drawCircle(p.x, p.y, 3, mMainPaint);
            }
        }
        float radius = (width * 0.5f) + 5.0f;
        for (int i = (int) mMinDegree; i <= mDetaDegree; i = i + STEP) {
            PointF pointF = getPointF(mCenterX, mCenterY, i, radius);
            canvas.drawCircle(pointF.x, pointF.y, 2, mFirstPaint);
        }

    }

    private float mCurrentDegree = 0f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBitmap == null) {

        }
        float lastX;
        float lastY;
        float currentX;
        float currentY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                setPressed(true);
                lastX = event.getX();
                lastY = event.getY();
                mCurrentDegree = detaDegree(mCenterX, mCenterY, lastX, lastY);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                lastX = currentX = event.getX();
                lastY = currentY = event.getY();
                float degree = detaDegree(mCenterX, mCenterY, currentX, currentY);
                // 滑过的弧度增量
                float dete = degree - mCurrentDegree;
                // 如果小于-90度说明 它跨周了，需要特殊处理350->17,
                if (dete < -270) {
                    dete = dete + 360;
                    // 如果大于90度说明 它跨周了，需要特殊处理-350->-17,
                } else if (dete > 270) {
                    dete = dete - 360;
                }
                addDegree(dete);
                mCurrentDegree = degree;
                mCurrentProgress = (int) Math.abs(mMinDegree - mDetaDegree);
                invalidate();
                return true;
            }
            case MotionEvent.ACTION_CANCEL:
                setPressed(false);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据原点，角度，和半径，计算坐标
     * 
     * @param originalX 原点x
     * @param originalY 原点y
     * @param degree 角度
     * @param radius 半径
     * @return {@link PointF}
     */
    private PointF getPointF(float originalX, float originalY, float degree, float radius) {
        float x = 0f;
        float y = 0f;
        // 角度转弧度
        float radian = (float) ((degree * Math.PI) / 180);
        x = (float) Math.sqrt((radius * radius) / (Math.tan(radian) * Math.tan(radian) + 1));
        y = (float) (Math.tan(radian) * x);
        float positiveDegree = degree;
        if (degree < 0) {
            positiveDegree = 360 + degree;
        }
        if (positiveDegree > 0 && positiveDegree < 90) {
            // 第一象限
            x = Math.abs(x);
            y = Math.abs(y);
        } else if (positiveDegree > 90 && positiveDegree < 180) {
            // 第二象限
            x = -Math.abs(x);
            y = Math.abs(y);
        } else if (positiveDegree > 180 && positiveDegree < 270) {
            // 第三象限
            x = -Math.abs(x);
            y = -Math.abs(y);
        } else if (positiveDegree > 270 && positiveDegree < 360) {
            // 第四象限
            x = Math.abs(x);
            y = -Math.abs(y);
        }
        Log.i(LOG_TAG, "degree: " + degree + " x: " + x + " y: " + y);
        return new PointF(x + originalX, y + originalY);
    }

    /**
     * 计算以(originalX,originalY)为坐标圆点，建立直角体系，求出(targetX,targetY)坐标与x轴的夹角
     * 主要是利用反正切函数的知识求出夹角
     * <p>
     * 第一象限：θ=arctan|b|/|a|， <br>
     * 第二象限：θ=180°-arctan|b|/|a|，<br>
     * 第三象限：θ=180°+arctan|b|/|a|，<br>
     * 第四象限：θ=360°-arctan|b|/|a|，
     * </p>
     * <p>
     * 角度弧度转换:
     * </p>
     * <p>
     * 弧度 角度 <br>
     * ---- = ---- <br>
     * π 180°
     * </p>
     * 
     * @param originalX
     * @param originalY
     * @param targetX
     * @param targetY
     * @return
     */
    private float detaDegree(float originalX, float originalY, float targetX, float targetY) {
        float detaX = targetX - originalX;
        float detaY = targetY - originalY;
        // 弧度
        double d;
        // 坐标在四个象限里
        if (detaX != 0) {
            float tan = Math.abs(detaY / detaX);
            if (detaX > 0) {
                // 第一象限
                if (detaY >= 0) {
                    d = Math.atan(tan);
                } else {
                    // 第四象限
                    d = 2 * Math.PI - Math.atan(tan);
                }
            } else {
                if (detaY >= 0) {
                    // 第二象限
                    d = Math.PI - Math.atan(tan);
                } else {
                    // 第三象限
                    d = Math.PI + Math.atan(tan);
                }
            }
        } else {
            // 坐标在y轴上
            if (detaY > 0) {
                // 坐标在y>0上
                d = Math.PI / 2;
            } else {
                // 坐标在y<0上
                d = -Math.PI / 2;
            }
        }
        return (float) ((d * 180) / Math.PI);
    }

    private void addDegree(float degree) {
        mDetaDegree += degree;
        if (mDetaDegree > 360 || mDetaDegree < -360) {
            mDetaDegree = mDetaDegree % 360;
        }
        if (mDetaDegree > mMaxDegree) {
            mDetaDegree = mMaxDegree;
        } else if (mDetaDegree < mMinDegree) {
            mDetaDegree = mMinDegree;
        }
    }

    static class SavedState extends BaseSavedState {

        float detaDegree;

        int currentProgress;

        /**
         * Constructor called from
         * {@link RevolveGestureView#onSaveInstanceState()}
         */
        SavedState(Parcelable superState) {
            super(superState);
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            detaDegree = in.readFloat();
            currentProgress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeFloat(detaDegree);
            out.writeInt(currentProgress);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    @Override
    public Parcelable onSaveInstanceState() {
        // Force our ancestor class to save its state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.detaDegree = mDetaDegree;
        ss.currentProgress = mCurrentProgress;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mCurrentProgress = ss.currentProgress;
        setDetaDegree(ss.detaDegree);
    }

    private void setDetaDegree(float degree) {
        mDetaDegree = degree;
        postInvalidate();
    }
}
