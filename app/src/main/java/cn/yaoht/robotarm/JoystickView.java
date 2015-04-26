package cn.yaoht.robotarm;

/**
 * Created by yht on 2015/3/28.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class JoystickView extends SurfaceView implements SurfaceHolder.Callback {
    private Paint mFillPaint;
    private Paint mDisabledFillPaint;
    private Paint mStrokePaint;
    private Paint mKnobPaint;
    private int mRadius;
    private int mWidth;
    private int mHeight;
    private float mJoystickX;
    private float mJoystickY;

    private UpdateListener mListener;

    public JoystickView(Context context) {
        super(context);

        setZOrderOnTop(true);

        Resources r = getResources();
        mKnobPaint = new Paint();
        mKnobPaint.setColor(Color.BLACK);
        mKnobPaint.setStyle(Paint.Style.FILL);
        mFillPaint = new Paint();
        mFillPaint.setColor(Color.WHITE);
        mFillPaint.setStyle(Paint.Style.FILL);
        mDisabledFillPaint = new Paint();
        mDisabledFillPaint.setColor(Color.GRAY);
        mDisabledFillPaint.setStyle(Paint.Style.FILL);
        mStrokePaint = new Paint();
        mStrokePaint.setColor(Color.BLACK);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics()));

        getHolder().addCallback(this);
    }

    public float getX() {
        return mJoystickX / mRadius;
    }

    public float getY() {
        return mJoystickY / mRadius;
    }

    public void setUpdateListener(UpdateListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    mJoystickX = event.getX() - mWidth / 2;
                    mJoystickY = event.getY() - mHeight / 2;
                    double dist = Math.sqrt(mJoystickX * mJoystickX + mJoystickY * mJoystickY);
                    if (dist > mRadius) {
                        double ratio = mRadius / dist;
                        mJoystickX = (int) (mJoystickX * ratio);
                        mJoystickY = (int) (mJoystickY * ratio);
                    }
                    invalidate();
                    if (mListener != null) {
                        mListener.onUpdate(getX(), getY());
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    mJoystickX = 0;
                    mJoystickY = 0;
                    invalidate();
                    if (mListener != null) {
                        mListener.onUpdate(getX(), getY());
                    }
                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            mJoystickX = 0;
            mJoystickY = 0;
        }
        super.setEnabled(enabled);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, isEnabled() ? mFillPaint : mDisabledFillPaint);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mRadius, mStrokePaint);
        canvas.drawCircle(mJoystickX + mWidth / 2, mJoystickY + mHeight / 2, 20, mKnobPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int min = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(min, min);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRadius = Math.min(w, h) / 2 - 5;
        mWidth = w;
        mHeight = h;
        mJoystickX = 0;
        mJoystickY = 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
        holder.setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public interface UpdateListener {
        public void onUpdate(float x, float y);
    }
}
