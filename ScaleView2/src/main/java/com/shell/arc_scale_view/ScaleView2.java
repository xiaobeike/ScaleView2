package com.shell.arc_scale_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author ：xiaobeike
 * @time ：2021/3/31
 * 作用 ：移动刻度尺
 */
public class ScaleView2 extends View {
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private int radius;
    private int padding;
    private float currentAngle;
    private double initAngle;
    private Path mArcPath;
    private Paint mArcPaint;
    private Paint mScaleLinePaint;
    private TextPaint mScaleTextPaint;
    private TextPaint mSelectedTextPaint;
    private Paint mCurrentSelectValuePaint;
    private Paint mIndicatorPaint;
    private Paint mMaskPaint;
    private SelectScaleListener selectScaleListener;
    private String mScaleUnit;
    private float mEvenyScaleValue;
    private int mScaleNumber;
    private float mScaleSpace;
    private int mScaleMin;
    private int mDrawLineSpace;
    private int mDrawTextSpace;
    private int mArcLineColor;
    private int mScaleLineColor;
    private int mIndicatorColor;
    private int mScaleTextColor;
    private int mSelectTextColor;
    private int mScaleMaxLength;
    private int eachScalePix;
    private int mSlidingMoveX;
    private int totalX;
    private float mDownX;
    private float mDownY;
    private boolean isArc;
    private int currentValue;

    private String tag = "view";

    public ScaleView2(Context context) {
        this(context, (AttributeSet) null);
    }

    public ScaleView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.padding = 10;
        this.currentAngle = 0f;//修改默认是0度 -15  20.0F
        this.mScaleUnit = "°";
        this.mEvenyScaleValue = 1.0F;
        this.mScaleNumber = 30;
        this.mScaleSpace = 1;
        this.mScaleMin = 0;
        this.mDrawLineSpace = 1;
        this.mDrawTextSpace = 5;
        this.mArcLineColor = -65536;
        this.mScaleLineColor = -16776961;
        this.mIndicatorColor = -16711936;
        this.mScaleTextColor = -16777216;
        this.mSelectTextColor = -16777216;
        this.mScaleMaxLength = 100;
        this.eachScalePix = 15;
        this.mSlidingMoveX = 0;
        this.totalX = 0;
        this.isArc = false;
        this.currentValue = 0;
        this.setClickable(true);
        this.initAttr(context, attrs);
        this.initPath();
        this.initPaint();

    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleView);
        int shape = typedArray.getInt(R.styleable.ScaleView_shape, 0);
        if (shape == 0) {
            this.isArc = true;
        } else {
            this.isArc = false;
        }

        this.mScaleUnit = typedArray.getString(R.styleable.ScaleView_scaleUnit);
        if (TextUtils.isEmpty(this.mScaleUnit)) {
            this.mScaleUnit = "";
        }

        this.mEvenyScaleValue = typedArray.getFloat(R.styleable.ScaleView_everyScaleValue, 1.0F);
        this.mScaleNumber = typedArray.getInt(R.styleable.ScaleView_scaleNum, 30);
        this.mScaleSpace = typedArray.getFloat(R.styleable.ScaleView_scaleSpace, 1);
        this.mScaleMin = typedArray.getInt(R.styleable.ScaleView_scaleMin, 30);
        this.mDrawLineSpace = typedArray.getInt(R.styleable.ScaleView_drawLineSpace, 1);
        this.mDrawTextSpace = typedArray.getInt(R.styleable.ScaleView_drawTextSpace, 5);
        this.mArcLineColor = typedArray.getColor(R.styleable.ScaleView_arcLineColor, -65536);
        this.mScaleLineColor = typedArray.getColor(R.styleable.ScaleView_scaleLineColor, -65536);
        this.mIndicatorColor = typedArray.getColor(R.styleable.ScaleView_indicatorColor, -16711936);
        this.mScaleTextColor = typedArray.getColor(R.styleable.ScaleView_scaleTextColor, -16777216);
        this.mSelectTextColor = typedArray.getColor(R.styleable.ScaleView_selectTextColor, -16777216);
        this.mScaleLineColor = typedArray.getColor(R.styleable.ScaleView_scaleLineColor, Color.parseColor("#666666"));
        this.mScaleTextColor = typedArray.getColor(R.styleable.ScaleView_scaleTextColor, Color.parseColor("#666666"));
        this.mIndicatorColor = typedArray.getColor(R.styleable.ScaleView_indicatorColor, Color.parseColor("#ff9933"));
        this.mScaleMaxLength = typedArray.getInt(R.styleable.ScaleView_scaleMaxLength, 100);

    }

    private void initPaint() {
        this.mArcPaint = new Paint(1);
        this.mArcPaint.setColor(this.mArcLineColor);
        this.mArcPaint.setStyle(Paint.Style.STROKE);
        this.mArcPaint.setStrokeWidth(18.0F);
        this.mScaleLinePaint = new Paint(1);
        this.mScaleLinePaint.setColor(this.mScaleLineColor);
        this.mScaleLinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.mScaleLinePaint.setStyle(Paint.Style.STROKE);
        this.mScaleTextPaint = new TextPaint(1);
        this.mScaleTextPaint.setColor(this.mScaleTextColor);
        this.mScaleTextPaint.setTypeface(Typeface.SANS_SERIF);
        this.mScaleTextPaint.setTextSize(36.0F);
        this.mSelectedTextPaint = new TextPaint(1);
        this.mSelectedTextPaint.setTypeface(Typeface.SERIF);
        this.mSelectedTextPaint.setColor(this.mSelectTextColor);
        this.mSelectedTextPaint.setTextSize(50.0F);
        this.mIndicatorPaint = new Paint();
        this.mIndicatorPaint.setFlags(1);
        this.mIndicatorPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mIndicatorPaint.setColor(this.mIndicatorColor);
        this.mIndicatorPaint.setStrokeWidth(18.0F);
        this.mMaskPaint = new Paint();
        this.mMaskPaint.setFlags(1);
        this.mScaleLinePaint = new Paint();
        this.mScaleLinePaint.setAntiAlias(true);
        this.mScaleLinePaint.setStyle(Paint.Style.STROKE);
        this.mScaleLinePaint.setStrokeWidth(2.0F);
        this.mScaleLinePaint.setColor(this.mScaleLineColor);
        this.mCurrentSelectValuePaint = new Paint();
        this.mCurrentSelectValuePaint.setAntiAlias(true);
        this.mCurrentSelectValuePaint.setTextSize(30.0F);
        this.mCurrentSelectValuePaint.setColor(this.mSelectTextColor);
        this.mCurrentSelectValuePaint.setTextAlign(Paint.Align.CENTER);
        this.mIndicatorPaint = new Paint();
        this.mIndicatorPaint.setAntiAlias(true);
        this.mIndicatorPaint.setColor(this.mIndicatorColor);

    }

    private void initPath() {
        this.mArcPath = new Path();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.mWidth = (int) getMeasuredWidth();
        this.mHeight = (int) getMeasuredHeight();
        if (this.isArc) {
            this.mCenterX = this.mWidth;
            this.mCenterY = this.mHeight / 2 + this.padding;

            this.radius = Math.max(this.mWidth / 2 - this.padding,
                    this.mHeight / 2 - this.padding);

        } else {
            this.mCenterY = this.getMeasuredHeight() / 2;
            this.mCenterX = this.getMeasuredWidth() / 2;
        }

    }

    //arc为弧形，line为直尺形
    protected void onDraw(Canvas canvas) {
        if (this.isArc) {
            this.drawArc(canvas);//圆弧
            this.drawMask(canvas);//遮罩
            this.drawScale(canvas);//刻度
            this.drawSelectedScale(canvas);//选择的中间数字显示
            this.drawIndicator(canvas);//选择指示器
        } else {
            this.drawCurrentScale(canvas);
            this.drawNum(canvas);
            this.drawMask(canvas);
        }

    }

    private void drawMask(Canvas canvas) {
        LinearGradient mLeftLinearGradient;
        LinearGradient rightLinearGradient;
        if (this.isArc) {
            mLeftLinearGradient = new LinearGradient(0.0F, 0.0F, (float) (this.mCenterX - 100), 150.0F, Color.BLUE, Color.BLUE, Shader.TileMode.CLAMP);
            this.mMaskPaint.setShader(mLeftLinearGradient);
            canvas.drawPath(this.mArcPath, this.mMaskPaint);
            rightLinearGradient = new LinearGradient((float) this.mWidth, 0.0F, (float) (this.mCenterX + 100), 150.0F, Color.BLUE, Color.BLUE, Shader.TileMode.CLAMP);
            this.mMaskPaint.setShader(rightLinearGradient);
            canvas.drawPath(this.mArcPath, this.mMaskPaint);
            canvas.drawPath(this.mArcPath, this.mArcPaint);
        } else {
            this.mMaskPaint.setStrokeWidth((float) this.mHeight);
            mLeftLinearGradient = new LinearGradient(0.0F, (float) this.mCenterY, 100.0F, (float) this.mCenterY, Color.parseColor("#01000000"), Color.parseColor("#11000000"), Shader.TileMode.CLAMP);
            this.mMaskPaint.setShader(mLeftLinearGradient);
            canvas.drawLine(0.0F, (float) this.mCenterY, (float) this.mCenterX, (float) this.mCenterY, this.mMaskPaint);
            rightLinearGradient = new LinearGradient((float) (this.mWidth - 100), (float) this.mCenterY, (float) this.mWidth, (float) this.mCenterY, Color.parseColor("#11000000"), Color.parseColor("#01000000"), Shader.TileMode.CLAMP);
            this.mMaskPaint.setShader(rightLinearGradient);
            canvas.drawLine((float) this.mCenterX, (float) this.mCenterY, (float) this.mWidth, (float) this.mCenterY, this.mMaskPaint);
        }

    }

    //指示器
    private void drawIndicator(Canvas canvas) {

        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(this.mArcPath, false);
        float[] tan = new float[2];
        float[] pos = new float[2];
        mPathMeasure.getPosTan(mPathMeasure.getLength() * 0.5F, pos, tan);
        canvas.save();
        double angle = this.calcArcAngle(Math.atan2((double) tan[1], (double) tan[0])) + 90.0D;
        canvas.rotate((float) angle, pos[0], pos[1]);
        canvas.drawLine(pos[0], pos[1], pos[0] + 20.0F, pos[1], this.mIndicatorPaint);

        Path linePath = new Path();
        linePath.moveTo(pos[0], pos[1] - 8.0F);
        linePath.lineTo(pos[0] + 30.0f, pos[1]);
        linePath.lineTo(pos[0], pos[1] + 8.0F);
        canvas.drawPath(linePath, this.mIndicatorPaint);
        canvas.restore();
    }

    //选中的文字
    private void drawSelectedScale(Canvas canvas) {

        int selectedScale = Math.round(this.currentAngle +
                (float) this.mScaleMin +
                (float) (this.mScaleNumber / 2 * this.mScaleSpace));

        selectedScale = selectedScale - 120;
        selectedScale = selectedScale % 360;
        if (selectedScale > 180) {
            selectedScale = selectedScale - 360;
        } else if (selectedScale < -180) {
            selectedScale = selectedScale + 360;
        }

        if (this.selectScaleListener != null) {
            this.selectScaleListener.selectScale(selectedScale);
        }
        //需要画的的文字
        String mScaleText = selectedScale + this.mScaleUnit;
        float scaleTextLength = this.mScaleTextPaint
                .measureText(mScaleText, 0, mScaleText.length());

        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(this.mArcPath, false);
        float[] tan = new float[2];
        float[] pos = new float[2];
        mPathMeasure.getPosTan(mPathMeasure.getLength() * 0.5F, pos, tan);
        canvas.save();
        double angle = this.calcArcAngle(Math.atan2((double) tan[1], (double) tan[0])) + 90.0D;
        canvas.rotate((float) angle + 90, pos[0], pos[1]);

        //画矩形
        this.mScaleTextPaint.setColor(Color.BLUE);
        canvas.drawRect(0, pos[1] - 70 - scaleTextLength, pos[0] + 30,
                pos[1] - 60.0f, mScaleTextPaint);
        canvas.restore();

        //画文字
        this.mScaleTextPaint.setColor(Color.BLACK);
        canvas.drawText(mScaleText + "°", pos[0] + 60.0f,
                pos[1] + padding,
                this.mScaleTextPaint);

    }

    //刻度
    private void drawScale(Canvas canvas) {
        PathMeasure mPathMeasure = new PathMeasure();
        mPathMeasure.setPath(this.mArcPath, false);
        float[] pos = new float[2];
        float[] tan = new float[2];

        //mScaleNumber为刻度点的数量，for循环遍历刻度点
        for (int i = 1; i <= this.mScaleNumber; ++i) {

            float percentage = (float) i / (float) this.mScaleNumber;
            mPathMeasure.getPosTan(mPathMeasure.getLength() * percentage, pos, tan);
            double atan2 = Math.atan2((double) tan[1], (double) tan[0]);
            double angle = this.calcArcAngle(atan2) + 90.0D;

            int scale = Math.round(this.currentAngle
                    + (float) this.mScaleMin
                    + (float) (i * this.mScaleSpace));

            scale = scale - 120;

            scale = scale % 360;
            if (scale > 180) {
                scale = scale - 360;
            } else if (scale < -180) {
                scale = scale + 360;
            }

            //scale >= this.mScaleMin &&
            if (scale % this.mDrawLineSpace == 0) {
                float startX = pos[0];
                float startY = pos[1];
                float endX = 0.0F;
                float endY = pos[1];
                if (scale % this.mDrawTextSpace == 0) {
                    //跟着字画长线
                    endX = pos[0] + 26.0F;
                    this.mScaleLinePaint.setStrokeWidth(3);
                    this.mScaleLinePaint.setColor(this.mScaleLineColor);
                    canvas.save();
                    canvas.rotate((float) (angle), pos[0], pos[1]);

                    //画字
                    String mScaleText = scale + this.mScaleUnit;
                    this.mScaleTextPaint.setColor(Color.parseColor("#DEFFFFFF"));
                    float scaleTextLength = this.mScaleTextPaint
                            .measureText(mScaleText, 0, mScaleText.length());
                    canvas.drawText(mScaleText, pos[0] + 60.0f,
                            pos[1] + padding,
                            this.mScaleTextPaint);

                    canvas.restore();

                } else if (scale % this.mDrawLineSpace == 0) {
                    //画短线
                    this.mScaleLinePaint.setColor(Color.parseColor("#b3ffffff"));
                    this.mScaleLinePaint.setStrokeWidth(2);
                    endX = pos[0] + 18.0F;
                }

                canvas.save();
                canvas.rotate((float) (angle), pos[0], pos[1]);
                canvas.drawLine(startX, startY, endX, endY, this.mScaleLinePaint);
                canvas.restore();
            }
        }

    }

    /**
     * dip转为 px
     */
    public int dip2px(float dipValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * convert px to its equivalent dp
     * 将px转换为与之相等的dp
     */
    public int px2dp2(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void drawArc(Canvas canvas) {

        int top = padding;
        int bottom = this.mHeight / 2 + this.radius - padding;
        int left = this.mCenterX - this.radius + dip2px(100);
        int right = this.mCenterX + this.radius;

        this.mArcPath.reset();
        this.mArcPath.addArc(new RectF((float) left, (float) top, (float) right, (float) bottom),
                90F, 180.0F);
        canvas.drawPath(this.mArcPath, this.mArcPaint);
    }

    private void drawNum(Canvas canvas) {
        this.mScaleTextPaint.setStrokeWidth(2.0F);
        this.mScaleTextPaint.setColor(this.mScaleTextColor);
        this.mScaleTextPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < this.mWidth; ++i) {
            int top = this.mCenterY + 10;
            if ((-this.totalX + i) % (this.eachScalePix * this.mDrawTextSpace) == 0) {
                top += 30;
                if (-this.totalX + i >= 0 && -this.totalX + i <= this.mScaleMaxLength * this.eachScalePix) {
                    canvas.drawText((-this.totalX + i) / this.eachScalePix + this.mScaleMin + "",
                            (float) i, (float) (top + 20), this.mScaleTextPaint);
                }
            }

            if ((-this.totalX + i) % this.eachScalePix == 0 && -this.totalX + i >= 0 && -this.totalX + i <= this.mScaleMaxLength * this.eachScalePix) {
                canvas.drawLine((float) i, (float) this.mCenterY, (float) i, (float) top, this.mScaleLinePaint);
            }
        }

    }

    private void drawCurrentScale(Canvas canvas) {
        this.currentValue = (-this.totalX + this.mCenterX) / this.eachScalePix + this.mScaleMin;
        if (this.selectScaleListener != null) {
            this.selectScaleListener.selectScale(this.currentValue);
        }

        RectF roundRectF = new RectF();
        roundRectF.left = (float) (this.mCenterX - 3);
        roundRectF.right = (float) (this.mCenterX + 3);
        roundRectF.top = (float) this.mCenterY;
        roundRectF.bottom = (float) (this.mCenterY + 50);
        canvas.drawRoundRect(roundRectF, 6.0F, 6.0F, this.mIndicatorPaint);
        String currentScaleText = this.currentValue + "";
        canvas.drawText(currentScaleText + this.mScaleUnit,
                (float) this.mCenterX, (float) (this.mCenterY - 10),
                this.mCurrentSelectValuePaint);

    }

    public boolean onTouchEvent(MotionEvent event) {
        float mTouchX;
        float mTouchY;
        double arcLength;//弧长
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (this.isArc) {
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    //初始度数
                    this.initAngle = this.computeAngle(mTouchX, mTouchY);
                    //初始Y值
                    this.mDownY = (int) event.getY();
                    this.mDownX = (int) event.getX();
                } else {
                    this.mDownX = (int) event.getX();
                }
            case MotionEvent.ACTION_UP:
            default:
                break;
            case MotionEvent.ACTION_MOVE:
                if (this.isArc) {

                    //利用角度移动
//                    mTouchX = event.getX();
//                    mTouchY = event.getY();
//                    //移动的角度
//                    double moveAngle = this.computeAngle(mTouchX, mTouchY);
//                    // 变化的角度
//                    double tempAngle = moveAngle - this.initAngle;
//                    // 取整数 四舍五入
//                    double addValue = tempAngle * (double) this.mEvenyScaleValue;
//                    this.currentAngle += (float) addValue;
//                    this.invalidate();
//                    this.initAngle = moveAngle;

                    //利用Y轴移动
                    if (event.getX() > 0) {
                        arcLength = (event.getY() - this.mDownY) /
                                Math.abs(radius - event.getX());
                    } else {
                        arcLength = (event.getY() - this.mDownY) /
                                Math.abs(event.getX() - radius);
                    }
                    double step = arcLength * mScaleNumber / 3.14;
                    this.currentAngle += step;
                    this.mDownY = event.getY();
                    this.invalidate();

                    return true;

                } else {
                    this.mSlidingMoveX = (int) (event.getX() - (float) this.mDownX);
                    this.totalX += this.mSlidingMoveX;
                    if (this.mSlidingMoveX < 0) {
                        if (-this.totalX + this.mCenterX > this.mScaleMaxLength * this.eachScalePix) {
                            this.totalX -= this.mSlidingMoveX;
                            return true;
                        }
                        this.invalidate();
                    } else {
                        if (this.totalX - this.mCenterX > 0) {
                            this.totalX -= this.mSlidingMoveX;
                            return true;
                        }

                        this.invalidate();
                    }

                    this.mDownX = (int) event.getX();
                }
        }

        return super.onTouchEvent(event);
    }

    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    private double computeAngle(float touchX, float touchY) {
        double atan2 = Math.atan2((double) touchY, (double) touchX);
        double taperedEdge = Math.sqrt(
                Math.pow((double) (touchX - (float) this.mCenterX), 2.0D)
                        + Math.pow((double) (touchY - (float) this.mCenterY), 2.0D));
        double sin = (double) (touchY - (float) this.mCenterY) / taperedEdge;
        //返回x的反正弦弧度值
        double asin = Math.asin(sin);

        double calcArcAngle = this.calcArcAngle(asin);
        return calcArcAngle;
    }

    //以弧度为单位的角 转换成 以度为单位的角
    private double calcArcAngle(double arc) {


        double angle = arc * 180.0D / 3.141592653589793D;
        return angle;
    }

    //区域外是否能滑动 Math.pow()返回第一个函授的第二个函数的次方
    private boolean isTouch(float touchX, float touchY) {
        float x = touchX - (float) this.mCenterX;
        float y = touchY - (float) this.mCenterY;
        return Math.pow((double) x, 2.0D) +
                Math.pow((double) y, 2.0D) < Math.pow((double) this.radius, 2.0D);
    }

    public void setSelectScaleListener(SelectScaleListener listener) {
        this.selectScaleListener = listener;
    }

    public void setScaleUnit(String scaleUnit) {
        this.mScaleUnit = scaleUnit;
    }

    public void setEvenyScaleValue(float everyScaleValue) {
        this.mEvenyScaleValue = everyScaleValue;
    }

    public void setScaleNum(int scaleNum) {
        this.mScaleNumber = scaleNum;
    }

    public void setScaleMin(int mScaleMin) {
        this.mScaleMin = mScaleMin;
    }

    public void setDrawLineSpace(int drawLineSpace) {
        this.mScaleNumber = drawLineSpace;
        this.invalidate();
    }

    public void setDrawTextSpace(int drawTextSpace) {
        this.mScaleNumber = drawTextSpace;
        this.invalidate();
    }

    private void setArcLineColor(int color) {
        this.mArcLineColor = color;
        this.invalidate();
    }

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
        this.invalidate();
    }

    private void setScaleLineColor(int color) {
        this.mScaleLineColor = color;
        this.invalidate();
    }

    private void setIndicatorColor(int color) {
        this.mIndicatorColor = color;
        this.invalidate();
    }

    private void setScaleTextColor(int color) {
        this.mScaleTextColor = color;
        this.invalidate();
    }

    private void setSelectTextColor(int color) {
        this.mSelectTextColor = color;
        this.invalidate();
    }

    public void setmScaleMaxLength(int mScaleMaxLength) {
        this.mScaleMaxLength = mScaleMaxLength;
        this.invalidate();
    }

    public void setShape2Arc(boolean isArc) {
        this.isArc = isArc;
        this.invalidate();
    }

    public interface SelectScaleListener {
        void selectScale(int var1);
    }
}
