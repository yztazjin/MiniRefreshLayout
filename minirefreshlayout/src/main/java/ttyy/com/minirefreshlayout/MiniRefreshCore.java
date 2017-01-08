package ttyy.com.minirefreshlayout;

import android.view.MotionEvent;

import ttyy.com.minirefreshlayout.intfs.IStateView;
import ttyy.com.minirefreshlayout.intfs.PullListener;

/**
 * Author: Administrator
 * Date  : 2017/01/05 14:06
 * Name  : MiniRefreshCore
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
final class MiniRefreshCore {

    MiniRefreshLayout.ContentProvider mProvider;

    /**
     * 是否允许下拉刷新
     */
    boolean isEnableRefresh = true;
    /**
     * 是否允许上拉加载
     */
    boolean isEnableLoadMore = true;

    /**
     * 正在刷新
     */
    boolean isRefreshing;
    /**
     * 正在加载
     */
    boolean isLoading;

    PullState mCurrState = PullState.None;

    private MiniRefreshCore(MiniRefreshLayout.ContentProvider mProvider){
        this.mProvider = mProvider;
    }

    protected static MiniRefreshCore from(MiniRefreshLayout.ContentProvider mProvider){
        return new MiniRefreshCore(mProvider);
    }

    final boolean onInterceptTouchEvent(MotionEvent event){
        return mProvider.getEventProcessor().onInterceptTouchEvent(event);
    }

    final boolean onTouchEvent(MotionEvent event){
        return mProvider.getEventProcessor().onTouchEvent(event);
    }

    final void setPullState(PullState state){
        mCurrState = state;
    }

    void setEnableRefresh(boolean value){
        this.isEnableRefresh = value;
    }

    void setEnableLoadMore(boolean value){
        this.isEnableLoadMore = value;
    }

    void setIsRefreshing(boolean value){
        this.isRefreshing = value;
    }

    void setIsLoadingMore(boolean value){
        this.isLoading = value;
    }

    void finishAll(){
        if(isRefreshing){
            finishRefreshing();
        }else if(isLoading){
            finishLoading();
        }
    }

    void finishRefreshing(){
        isRefreshing = false;
        mProvider.getAnimProcessor().animateRefreshableView(mProvider.getEventProcessor().getOffsetY(), 0);
    }

    void finishLoading(){
        isLoading = false;
        mProvider.getAnimProcessor().animateRefreshableView(mProvider.getEventProcessor().getOffsetY(), 0);

    }

    void setRefreshing(){
        if(isLoading || isRefreshing || mProvider.getRefreshableView() == null)
            return;

        if (!mProvider.getEventProcessor().canChildScrollDown()) {
            // 触发下拉刷新
            mCurrState = PullState.PULL_DOWN_REFRESH;
            isRefreshing = true;

            mProvider.getAnimProcessor().animateRefreshableView(0, mProvider.getStdHeightForPullDown());
            IStateView mRefreshView = mProvider.getRefreshView();
            PullListener mPullListener = mProvider.getPullListener();
            if(mRefreshView != null){
                mRefreshView.onFloating(mProvider);
            }
            if(mPullListener != null){
                mPullListener.onRefresh();
            }
        }
    }

    void setLoadingMore(){
        if(isLoading || isRefreshing || mProvider.getRefreshableView() == null)
            return;

        if (!mProvider.getEventProcessor().canChildScrollUp()) {
            // 触发上拉刷新
            mCurrState = PullState.PULL_UP_LOAD;
            isLoading = true;

            mProvider.getAnimProcessor().animateRefreshableView(0, -mProvider.getStdHeightForPullUp());
            IStateView mLoadView = mProvider.getLoadMoreView();
            PullListener mPullListener = mProvider.getPullListener();
            if(mLoadView != null){
                mLoadView.onFloating(mProvider);
            }
            if(mPullListener != null){
                mPullListener.onLoadMore();
            }
        }
    }
}
