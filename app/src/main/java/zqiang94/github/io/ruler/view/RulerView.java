package zqiang94.github.io.ruler.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Date;

import zqiang94.github.io.ruler.R;

public class RulerView extends View implements GestureDetector.OnGestureListener {

    private final static String TAG = "RulerView";

    private long downTime;

    //最小值
    private int minValue;
    //最大值
    private int maxValue;
    //每刻度 值
    private float spacingValue;
    //控件宽度
    private float mWidth;
    //控件高度
    private float mHeight;
    //刻度间隔距离
    private float perWidth;
    //短刻度高度
    private float shortHeight;
    //长刻度高度
    private float longHeight;
    //中线高度
    private float middleHeight;
    //刻度线宽
    private float lineWidth;
    //中刻度线宽
    private float middleLineWidth;
    //文字大小
    private float textSize;
    //刻度线颜色
    private int lineColor;
    //中线颜色
    private int middleLineColor;
    //文字颜色
    private int textColor;
    //大刻度间距
    private int longSpacingValue;
    //文字和长刻度距离
    private float textMargin;
    //刻度线画笔
    private Paint linePaint;
    //中线画笔
    private Paint middlePaint;
    //刻度文字画笔
    private Paint mTextPaint;
    //开始横坐标
    private float startX;
    //路径
    private Path mPath;

    //当前值
    private int mCurrentValue;

    //是否显示底线
    private boolean showBaseLine;

    public boolean isShowBaseLine() {
        return showBaseLine;
    }

    public void setShowBaseLine(boolean showBaseLine) {
        this.showBaseLine = showBaseLine;
        invalidate();
    }

    //值变化监听
    private OnValueChangedListener mOnValueChangedListener;
    private ValueAnimator valueAnimator;

    public void setOnValueChangedListener(OnValueChangedListener mOnValueChangedListener) {
        this.mOnValueChangedListener = mOnValueChangedListener;
    }

    /**
     * 获取当前刻度值
     *
     * @return 当前刻度值
     */
    public int getCurrentValue() {
        return mCurrentValue;
    }

    GestureDetector mGestureDetector;
    private float totalWidth;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        perWidth = a.getDimension(R.styleable.RulerView_perWidth, SizeUtils.dp2px(context, 5));
        shortHeight = a.getDimension(R.styleable.RulerView_shortHeight, SizeUtils.dp2px(context, 5));
        longHeight = a.getDimension(R.styleable.RulerView_longHeight, SizeUtils.dp2px(context, 10));
        middleHeight = a.getDimension(R.styleable.RulerView_middleHeight, SizeUtils.dp2px(context, 15));
        lineWidth = a.getDimension(R.styleable.RulerView_lineWidth, SizeUtils.dp2px(context, 1));
        textSize = a.getDimension(R.styleable.RulerView_textSize, SizeUtils.sp2px(context, 12));
        middleLineWidth = a.getDimension(R.styleable.RulerView_middleLineWidth, SizeUtils.dp2px(context, 1));
        textMargin = a.getDimension(R.styleable.RulerView_textMargin, SizeUtils.dp2px(context, 10));


        lineColor = a.getColor(R.styleable.RulerView_lineColor, Color.GRAY);
        middleLineColor = a.getColor(R.styleable.RulerView_middleLineColor, Color.RED);
        textColor = a.getColor(R.styleable.RulerView_textColor, Color.GRAY);

        minValue = a.getInt(R.styleable.RulerView_minValue, 0);
        maxValue = a.getInt(R.styleable.RulerView_maxValue, 100);
        spacingValue = a.getInt(R.styleable.RulerView_spacingValue, 1);
        longSpacingValue = a.getInt(R.styleable.RulerView_longSpacingValue, 10);

        showBaseLine = a.getBoolean(R.styleable.RulerView_showBaseLine, false);

        mGestureDetector = new GestureDetector(context, this);
        mTextPaint = new TextPaint();
        middlePaint = new Paint();
        linePaint = new Paint();
        mPath = new Path();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);
        middlePaint.setAntiAlias(true);
        middlePaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        initData();
        a.recycle();
    }


    private void initData() {
        totalWidth = (maxValue - minValue) * perWidth / spacingValue;
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);
        middlePaint.setColor(middleLineColor);
        middlePaint.setStrokeWidth(middleLineWidth);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * 延迟滑动
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(final Message msg) {
            scrollTo((int) msg.obj);
            return false;
        }
    });

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Message msg = new Message();
                msg.obj = getCurrentValue();
                handler.sendMessageDelayed(msg, 200);
                long upTime = new Date().getTime();
                if (upTime - downTime <= 500) {
                    performClick();
                }
                break;
        }

        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (startX == 0) {
            startX = mWidth / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        if (showBaseLine) {
            mPath.moveTo(startX, mHeight);
            mPath.lineTo(startX + totalWidth, mHeight);
        }
        for (float i = minValue; i <= maxValue; i = i + spacingValue) {
            mPath.moveTo(startX + (i - minValue) * perWidth / spacingValue, mHeight);
            if (i % (longSpacingValue) == 0) {
                mPath.lineTo(startX + (i - minValue) * perWidth / spacingValue, mHeight - longHeight);
                canvas.drawText(String.valueOf((int) i), startX + (i - minValue) * perWidth / spacingValue,
                        mHeight - longHeight - textMargin, mTextPaint);
            } else {
                mPath.lineTo(startX + (i - minValue) * perWidth / spacingValue, mHeight - shortHeight);
            }
        }
        canvas.drawPath(mPath, linePaint);
        canvas.drawLine(mWidth / 2, mHeight, mWidth / 2, mHeight - middleHeight, middlePaint);
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
        totalWidth = (maxValue - minValue) * perWidth / spacingValue;
        invalidate();
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        totalWidth = (maxValue - minValue) * perWidth / spacingValue;
        invalidate();
    }

    public float getSpacingValue() {
        return spacingValue;
    }

    public void setSpacingValue(float spacingValue) {
        this.spacingValue = spacingValue;
        totalWidth = (maxValue - minValue) * perWidth / spacingValue;
        invalidate();
    }

    public float getPerWidth() {
        return perWidth;
    }

    public void setPerWidth(float perWidth) {
        this.perWidth = perWidth;
        totalWidth = (maxValue - minValue) * perWidth / spacingValue;
        invalidate();
    }

    public float getShortHeight() {
        return shortHeight;
    }

    public void setShortHeight(float shortHeight) {
        this.shortHeight = shortHeight;
        initData();
        invalidate();
    }

    public float getLongHeight() {
        return longHeight;
    }

    public void setLongHeight(float longHeight) {
        this.longHeight = longHeight;
        initData();
        invalidate();
    }

    public float getMiddleHeight() {
        return middleHeight;
    }

    public void setMiddleHeight(float middleHeight) {
        this.middleHeight = middleHeight;
        initData();
        invalidate();
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
        linePaint.setStrokeWidth(lineWidth);
        invalidate();
    }

    public float getMiddleLineWidth() {
        return middleLineWidth;
    }

    public void setMiddleLineWidth(float middleLineWidth) {
        this.middleLineWidth = middleLineWidth;
        middlePaint.setStrokeWidth(middleLineWidth);
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        mTextPaint.setTextSize(textSize);
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        linePaint.setColor(lineColor);
        invalidate();
    }

    public int getMiddleLineColor() {
        return middleLineColor;
    }

    public void setMiddleLineColor(int middleLineColor) {
        this.middleLineColor = middleLineColor;
        middlePaint.setColor(middleLineColor);
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        mTextPaint.setColor(textColor);
        invalidate();
    }

    public int getLongSpacingValue() {
        return longSpacingValue;
    }

    public void setLongSpacingValue(int longSpacingValue) {
        this.longSpacingValue = longSpacingValue;
        initData();
        invalidate();
    }

    public float getTextMargin() {
        return textMargin;
    }

    public void setTextMargin(float textMargin) {
        this.textMargin = textMargin;
        initData();
        invalidate();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        downTime = new Date().getTime();
        return true;
    }


    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (valueAnimator != null) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
                valueAnimator = null;
            }
        }
        startX = startX - distanceX;
        if (startX >= mWidth / 2) {
            startX = mWidth / 2;
        }
        if (startX <= mWidth / 2 - totalWidth) {
            startX = mWidth / 2 - totalWidth;
        }
        computeCurrentValue();
        if (mOnValueChangedListener != null) {
            mOnValueChangedListener.onValueChanged(mCurrentValue);
        }
        invalidate();
        return true;
    }

    private void computeCurrentValue() {
        float a = (minValue + ((mWidth / 2 - startX) / perWidth) * spacingValue);
        int b = (int) (a / spacingValue);
        int c = b * (int) spacingValue;
        if (a - c >= spacingValue / 2) {
            mCurrentValue = c + (int) spacingValue;
        } else {
            mCurrentValue = c;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    private void scrollTo(int value) {
        Log.d(TAG, "scrollTo " + value);
        if (value < minValue || value > maxValue) {
            return;
        }
        float x = startX + ((value - minValue) / spacingValue) * perWidth;
        valueAnimator = null;
        valueAnimator = ValueAnimator.ofFloat(startX, startX - (x - mWidth / 2));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startX = (float) animation.getAnimatedValue();
                computeCurrentValue();
                if (mOnValueChangedListener != null) {
                    mOnValueChangedListener.onValueChanged(mCurrentValue);
                }
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void scrollToValue(final int value) {
        if (mWidth == 0) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollToValue(value);
                }
            }, 500);
        } else {
            scrollTo(value);
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.currentValue = mCurrentValue;
        Log.d(TAG, "onSaveInstanceState: mCurrentValue: " + mCurrentValue);
        return ss;
    }

    static class SavedState extends BaseSavedState {
        int currentValue;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentValue = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentValue);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentValue = savedState.currentValue;
        Log.d(TAG, "onRestoreInstanceState: mCurrentValue: " + mCurrentValue);
        scrollToValue(mCurrentValue);
    }


    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        super.dispatchSaveInstanceState(container);
    }

    private Handler mHandler = new Handler();

    public interface OnValueChangedListener {
        void onValueChanged(int value);
    }

}
