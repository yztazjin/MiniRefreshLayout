package ttyy.com.minirefreshlayout.exts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * Author: hjq
 * Date  : 2016/12/04 20:22
 * Name  : WaveHeaderView
 * Intro : 仿照波浪起伏的View
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/04    hjq   1.0              1.0
 */
public class WaveView extends View {

    Paint mAboveWavePaint;
    Paint mBelowWavePaint;

    Path mAboveWavePath;// 上面(近处)的浪 正余弦曲线路径图
    Path mBelowWavePath;// 下面(远处)的浪 正余弦曲线路径图

    double Ω;// 正余弦 360°曲线图 转换倍数前缀
    float β;// 正余弦曲线 x轴 变化因子
    float mAboveWaveHeight;
    float mBelowWaveHeight;

    int mWaveColor = Color.parseColor("#3366FF");
    int gravityFactor = Gravity.TOP;

    Rect mWaveFrame;

    OnAnimationChangedListener listener;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    void init(AttributeSet attrs){
        mWaveFrame = new Rect();

        mAboveWavePaint = new Paint();
        mAboveWavePaint.setStyle(Paint.Style.FILL);
        mAboveWavePaint.setAntiAlias(true);
        mAboveWavePaint.setColor(mWaveColor);

        mBelowWavePaint = new Paint();
        mBelowWavePaint.setStyle(Paint.Style.FILL);
        mBelowWavePaint.setAntiAlias(true);
        mBelowWavePaint.setColor(mWaveColor);
        mBelowWavePaint.setAlpha(85);

        mAboveWavePath = new Path();
        mBelowWavePath = new Path();

        mAboveWaveHeight = 7 * getResources().getDisplayMetrics().density;
        mBelowWaveHeight = mAboveWaveHeight * 0.618f;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Ω = 1.8 * Math.PI / getWidth();

        mWaveFrame.left = getPaddingLeft();
        mWaveFrame.right = getMeasuredWidth() - getPaddingRight();
        mWaveFrame.top = getPaddingTop();
        mWaveFrame.bottom = getMeasuredHeight() - getPaddingBottom();
    }

    public void setWaveAnimationListener(OnAnimationChangedListener listener){
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        β -= 0.1f;

        switch (gravityFactor){
            case Gravity.TOP:
                drawWaveAtTop(canvas);
                break;
            case Gravity.BOTTOM:
                drawWaveAtBottom(canvas);
                break;
            default:
                drawWaveAtTop(canvas);
                break;
        }

        if(enableWaving){
            postInvalidateDelayed(20);
        }
    }

    boolean enableWaving = false;
    public void startWaving(){
        enableWaving = true;
        postInvalidate();
    }

    public void stopWaving(){
        enableWaving = false;
    }

    private void drawWaveAtBottom(Canvas canvas){
        float y0;
        float y1;

        mAboveWavePath.reset();
        mBelowWavePath.reset();
        mAboveWavePath.moveTo(mWaveFrame.left, mWaveFrame.top);
        mBelowWavePath.moveTo(mWaveFrame.left, mWaveFrame.top);
        for(int x = mWaveFrame.left; x < mWaveFrame.right + 20; x += 20){

            if(x > mWaveFrame.right){
                x = mWaveFrame.right;
            }

            y0 = (float)(mWaveFrame.bottom - ((mAboveWaveHeight * Math.sin(Ω * x + β) + mAboveWaveHeight)));
            y1 = (float)(mWaveFrame.bottom - ((mBelowWaveHeight * Math.cos(Ω * x + β) + mBelowWaveHeight)));

            mAboveWavePath.lineTo(x, y0);
            mBelowWavePath.lineTo(x, y1);

            if(listener != null){
                listener.onWaveAnimation(y0);
            }
        }
        mAboveWavePath.lineTo(mWaveFrame.right, mWaveFrame.top);
        mBelowWavePath.lineTo(mWaveFrame.right, mWaveFrame.top);

        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
        canvas.drawPath(mBelowWavePath, mBelowWavePaint);
    }

    private void drawWaveAtTop(Canvas canvas){
        float y0;
        float y1;

        mAboveWavePath.reset();
        mBelowWavePath.reset();
        mAboveWavePath.moveTo(mWaveFrame.left, mWaveFrame.bottom);
        mBelowWavePath.moveTo(mWaveFrame.left, mWaveFrame.bottom);
        for(int x = mWaveFrame.left; x < mWaveFrame.right + 20; x += 20){

            if(x > mWaveFrame.right){
                x = mWaveFrame.right;
            }

            y0 = (float)((mAboveWaveHeight * Math.sin(Ω * x + β) + mAboveWaveHeight));;
            y1 = (float)((mBelowWaveHeight * Math.cos(Ω * x + β) + mBelowWaveHeight));

            mAboveWavePath.lineTo(x, y0);
            mBelowWavePath.lineTo(x, y1);

            if(listener != null){
                listener.onWaveAnimation(y0);
            }
        }
        mAboveWavePath.lineTo(mWaveFrame.right, mWaveFrame.bottom);
        mBelowWavePath.lineTo(mWaveFrame.right, mWaveFrame.bottom);

        canvas.drawPath(mAboveWavePath, mAboveWavePaint);
        canvas.drawPath(mBelowWavePath, mBelowWavePaint);
    }

    /**
     * 设置波浪的显示位置
     * 顶部 Gravity.TOP
     * 底部 Gravity.BOTTOM
     * @param gravity
     */
    public void setWaveGravity(int gravity){
        gravityFactor = gravity;
    }

    /**
     * 设置波浪的颜色
     * @param color
     */
    public void setWaveColor(int color){
        mWaveColor = color;
        mAboveWavePaint.setColor(color);
        mBelowWavePaint.setColor(color);
    }

    /**
     * 动画监听器
     */
    public interface OnAnimationChangedListener{

        /**
         * 波浪的中间的y值得起伏变化
         * @param y
         */
        void onWaveAnimation(float y);

    }

}
