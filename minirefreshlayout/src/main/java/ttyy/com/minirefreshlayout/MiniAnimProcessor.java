package ttyy.com.minirefreshlayout;

import android.animation.ValueAnimator;
import android.view.animation.DecelerateInterpolator;

import ttyy.com.minirefreshlayout.intfs.IStateView;
import ttyy.com.minirefreshlayout.intfs.PullListener;

/**
 * Author: Administrator
 * Date  : 2017/01/05 14:04
 * Name  : MiniAnimProcessor
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
public class MiniAnimProcessor {

    protected MiniRefreshLayout.ContentProvider mContentProvider;
    protected int mStdHeightForPullDown;
    protected int mStdHeightForPullUp;
    protected boolean isAnimating = false;
    
    final void setContentProvider(MiniRefreshLayout.ContentProvider mContentProvider){
        this.mContentProvider = mContentProvider;
        mStdHeightForPullDown = mContentProvider.getStdHeightForPullDown();
        mStdHeightForPullUp = mContentProvider.getStdHeightForPullUp();
    }

    protected void animateRefreshableView(float start, float end){
        animateRefreshableView(start, end, 300);
    }

    protected void animateRefreshableView(float start, float end, long duration){
        ValueAnimator va = ValueAnimator.ofFloat(start, end);
        va.setDuration(duration);
        va.setInterpolator(new DecelerateInterpolator());
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
            }
        });
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setPullOffsetY(value);

                PullState state = mContentProvider.getCurrPullState();
                PullListener mPullListener = mContentProvider.getPullListener();
                if (state == PullState.PULL_DOWN_REFRESH) {

                    IStateView mRefreshView = mContentProvider.getRefreshView();
                    float percent = Math.abs(value) / mStdHeightForPullDown;
                    if(mRefreshView != null){
                        mRefreshView.onPullRelease(value, percent, mContentProvider);
                    }
                    if(mPullListener != null){
                        mPullListener.onOffsetYChanged(value, percent, state);
                    }

                } else if (state == PullState.PULL_UP_LOAD) {

                    IStateView mLoadMoreView = mContentProvider.getLoadMoreView();
                    float percent = Math.abs(value) / mStdHeightForPullUp;
                    if(mLoadMoreView != null){
                        mLoadMoreView.onPullRelease(value, percent, mContentProvider);
                    }
                    if(mPullListener != null){
                        mPullListener.onOffsetYChanged(value, percent, state);
                    }

                }
            }
        });
        va.start();
    }

    protected final void setPullOffsetY(float value){
        mContentProvider.getEventProcessor().setOffsetY(value);
    }
}
