package ttyy.com.minirefreshlayout;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import ttyy.com.minirefreshlayout.intfs.IStateView;
import ttyy.com.minirefreshlayout.intfs.PullListener;

/**
 * Author: Administrator
 * Date  : 2017/01/05 13:33
 * Name  : MiniPullEventProcessor
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
public class MiniPullEventProcessor {

    private MiniRefreshLayout.ContentProvider mContentProvider;

    private float mTouchY;
    private float mTouchX;

    /**
     * 子View添加手势操作
     */
    GestureDetector gestureDetector;
    Interpolator mInterpolator;

    /**
     * 最大可拉伸距离
     */
    int mMaxHeightForPullDown;
    int mStdHeightForPullDown;

    /**
     * 刷新/加载 高度
     */
    int mMaxHeightForPullUp;
    int mStdHeightForPullUp;

    int mTouchSlop;

    float mPullOffsetY;

    public MiniPullEventProcessor() {
        mInterpolator = new DecelerateInterpolator(10);
    }

    final void setContentProvider(MiniRefreshLayout.ContentProvider mProvider) {
        this.mContentProvider = mProvider;

        this.mMaxHeightForPullDown = mProvider.getMaxHeightForPullDown();
        this.mStdHeightForPullDown = mProvider.getStdHeightForPullDown();
        this.mMaxHeightForPullUp = mProvider.getMaxHeightForPullUp();
        this.mStdHeightForPullUp = mProvider.getStdHeightForPullUp();
        this.mTouchSlop = mContentProvider.getTouchSlop();

        gestureDetector = new GestureDetector(mContentProvider.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (mContentProvider.isRefreshing() && distanceY > mTouchSlop) {
                    // distanceY > 0 向上滑动
                    // 结束下拉刷新
                    mContentProvider.getRefreshCore().finishRefreshing();
                } else if (mContentProvider.isLoading() && distanceY < -mTouchSlop) {
                    // distanceY < 0 向下滑动
                    // 结束上拉加载
                    mContentProvider.getRefreshCore().finishLoading();
                }

                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        this.mContentProvider.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    /**
     * 是否拦截事件
     *
     * @return
     */
    protected boolean onInterceptTouchEvent(MotionEvent ev) {
        View mChildView = mContentProvider.getRefreshableView();
        if (mChildView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchY = ev.getY();
                    mTouchX = ev.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dy = ev.getY() - mTouchY;
                    float dx = ev.getX() - mTouchX;

                    if (Math.abs(dy) > Math.abs(dx)) {
                        // 确保是Y上的变动为主，滑动方向45°内
                        if (dy > 0 && !canChildScrollDown() && mContentProvider.isEnableRefresh()) {
                            // 触发下拉刷新
                            mContentProvider.getRefreshCore().setPullState(PullState.PULL_DOWN_REFRESH);
                            return true;
                        } else if (dy < 0 && !canChildScrollUp() && mContentProvider.isEnableLoadMore()) {
                            // 触发上拉刷新
                            mContentProvider.getRefreshCore().setPullState(PullState.PULL_UP_LOAD);
                            return true;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
        }
        return false;
    }

    /**
     * 消费事件
     *
     * @return
     */
    protected boolean onTouchEvent(MotionEvent ev) {
        View mChildView = mContentProvider.getRefreshableView();
        if (mChildView == null
                        || mContentProvider.isRefreshing() ||
                        mContentProvider.isLoading()) {
            // 控制避免同时可以上拉/下拉刷新
            return false;
        }

        PullState state = mContentProvider.getCurrPullState();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPullOffsetY = 0;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = ev.getY() - mTouchY;
                if (state == PullState.PULL_DOWN_REFRESH) {
                    // 下拉刷新
                    dy = Math.max(0, dy);
                    // 基点坐标矫正
                    if(dy > mMaxHeightForPullDown){
                        mTouchY += Math.abs(dy) - mMaxHeightForPullDown;
                    }else if(dy == 0){
                        mTouchY = ev.getY();
                    }
                    dy = Math.min(mMaxHeightForPullDown, dy);

                    IStateView mRefreshView = mContentProvider.getRefreshView();
                    PullListener mPullListener = mContentProvider.getPullListener();
                    float offsetY = mInterpolator.getInterpolation(dy / mMaxHeightForPullDown) * dy;
                    mPullOffsetY = offsetY;

                    float percent = offsetY / mStdHeightForPullDown;
                    if (mRefreshView != null) {
                        mRefreshView.onPullOffsetYChanged(offsetY, percent, mContentProvider);
                    }
                    if(mPullListener != null){
                        mPullListener.onOffsetYChanged(offsetY, percent, state);
                    }

                } else if (state == PullState.PULL_UP_LOAD) {
                    // 上拉加载
                    dy = Math.min(0, dy);
                    // 基点坐标矫正
                    if(Math.abs(dy) > mMaxHeightForPullUp){
                        mTouchY -= Math.abs(dy) - mMaxHeightForPullUp;
                    }else if(dy == 0){
                        mTouchY = ev.getY();
                    }
                    dy = Math.min(mMaxHeightForPullUp, Math.abs(dy));

                    IStateView mLoadMoreView = mContentProvider.getLoadMoreView();
                    PullListener mPullListener = mContentProvider.getPullListener();
                    float offsetY = -mInterpolator.getInterpolation(dy / mMaxHeightForPullUp) * dy;
                    float percent = -offsetY / mStdHeightForPullUp;
                    mPullOffsetY = offsetY;

                    if (mLoadMoreView != null) {
                        mLoadMoreView.onPullOffsetYChanged(offsetY, percent, mContentProvider);
                    }
                    if(mPullListener != null){
                        mPullListener.onOffsetYChanged(offsetY, percent, state);
                    }
                }

                return true;
            case MotionEvent.ACTION_UP:
                if (state == PullState.PULL_DOWN_REFRESH) {
                    // 下拉刷新
                    if (Math.abs(mPullOffsetY) > (mStdHeightForPullDown - mTouchSlop)) {
                        // 刷新
                        mContentProvider.getAnimProcessor().animateRefreshableView(mPullOffsetY, mStdHeightForPullDown);
                        mContentProvider.getRefreshCore().setIsRefreshing(true);

                        IStateView mRefreshView = mContentProvider.getRefreshView();
                        PullListener mPullListener = mContentProvider.getPullListener();
                        if (mRefreshView != null) {
                            mRefreshView.onFloating(mContentProvider);
                        }

                        if (mPullListener != null) {
                            mPullListener.onRefresh();
                        }

                    } else {
                        mContentProvider.getAnimProcessor().animateRefreshableView(mPullOffsetY, 0);
                    }
                } else if (state == PullState.PULL_UP_LOAD) {
                    // 上拉加载
                    if (Math.abs(mPullOffsetY) > (mStdHeightForPullUp - mTouchSlop)) {
                        // 刷新
                        mContentProvider.getAnimProcessor().animateRefreshableView(mPullOffsetY, -mStdHeightForPullUp);
                        mContentProvider.getRefreshCore().setIsLoadingMore(true);

                        IStateView mLoadMoreView = mContentProvider.getLoadMoreView();
                        PullListener mPullListener = mContentProvider.getPullListener();
                        if (mLoadMoreView != null) {
                            mLoadMoreView.onFloating(mContentProvider);
                        }

                        if (mPullListener != null) {
                            mPullListener.onLoadMore();
                        }

                    } else {
                        mContentProvider.getAnimProcessor().animateRefreshableView(mPullOffsetY, 0);
                    }
                }
                return true;
        }

        return false;
    }

    void setOffsetY(float value){
        this.mPullOffsetY = value;
    }

    float getOffsetY(){
        return mPullOffsetY;
    }

    boolean canChildScrollDown() {
        return Utils.canViewPullDown(mContentProvider.getRefreshableView());
    }

    boolean canChildScrollUp() {
        return Utils.canViewPullUp(mContentProvider.getRefreshableView());
    }

}
