package ttyy.com.minirefreshlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import ttyy.com.minirefreshlayout.exts.SinaLoadMoreView;
import ttyy.com.minirefreshlayout.exts.SinaRefreshView;
import ttyy.com.minirefreshlayout.intfs.IStateView;
import ttyy.com.minirefreshlayout.intfs.PullListener;

/**
 * Author: Administrator
 * Date  : 2017/01/05 11:11
 * Name  : MiniRefreshLayout
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
public class MiniRefreshLayout extends FrameLayout{

    private View mRefreshableView;
    private MiniRefreshCore mRefreshCore;

    private FrameLayout mRefreshLayout;
    private FrameLayout mLoadMoreLayout;
    private IStateView mRefreshView;
    private IStateView mLoadMoreView;

    private PullListener mPullListener;

    private Scroller mScroller;

    public MiniRefreshLayout(Context context) {
        this(context, null);
    }

    public MiniRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attrs){
        mScroller = new Scroller(getContext());
        mRefreshCore = MiniRefreshCore.from(provider);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(mRefreshView.getFillZLayerMode() == IStateView.ZLayerMode.Same){
            mRefreshLayout.layout(0,
                    -provider.getStdHeightForPullDown(),
                    getMeasuredWidth(),
                    0);
        }

        if(mLoadMoreView.getFillZLayerMode() == IStateView.ZLayerMode.Same){
            mLoadMoreLayout.layout(0,
                    getMeasuredHeight(),
                    getMeasuredWidth(),
                    getMeasuredHeight() + provider.getStdHeightForPullUp());
        }
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected final void onFinishInflate() {
        super.onFinishInflate();
        if (mRefreshableView == null) {
            mRefreshableView = getChildAt(0);
        }

        if (mRefreshableView == null) {
            return;
        }

        setRefreshView((IStateView) createRefreshView());
        setLoadMoreView((IStateView) createLoadMoreView());

        provider.init();
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev) || mRefreshCore.onInterceptTouchEvent(ev);
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || mRefreshCore.onTouchEvent(event);
    }

    public final void setEnableRefresh(boolean value){
        provider.getRefreshCore().setEnableRefresh(value);
    }

    public final void setEnableLoadMore(boolean value){
        provider.getRefreshCore().setEnableLoadMore(value);
    }

    public MiniRefreshLayout setRefreshView(IStateView view){
        if(mRefreshView != null){
            if(mRefreshLayout != null){
                removeView(mRefreshLayout);
                mRefreshLayout = null;
            }
        }
        mRefreshView = view;
        mRefreshLayout = new FrameLayout(getContext());
        if(mRefreshView != null){
            LayoutParams params = mRefreshView.getWrapperLayoutParams();
            if(params == null){
                params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            }
            params.gravity = Gravity.TOP;
            if(mRefreshView.getFillZLayerMode() == IStateView.ZLayerMode.Top){
                addView(mRefreshLayout, params);
            }else if(mRefreshView.getFillZLayerMode() == IStateView.ZLayerMode.Same){
                params.height = provider.getStdHeightForPullDown();
                addView(mRefreshLayout, 0, params);
            }else{
                addView(mRefreshLayout, 0, params);
            }

            mRefreshView.reset();
            mRefreshLayout.addView((View) mRefreshView);
        }

        return this;
    }

    public MiniRefreshLayout setLoadMoreView(IStateView view){
        if(mLoadMoreView != null){
            if(mLoadMoreLayout != null){
                removeView(mLoadMoreLayout);
                mLoadMoreLayout = null;
            }
        }
        mLoadMoreView = view;
        mLoadMoreLayout = new FrameLayout(getContext());
        if(mLoadMoreView != null){

            LayoutParams params = mLoadMoreView.getWrapperLayoutParams();
            if(params == null){
                params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            }
            params.gravity = Gravity.BOTTOM;
            if(mLoadMoreView.getFillZLayerMode() == IStateView.ZLayerMode.Top){
                addView(mLoadMoreLayout, params);
            }else if(mLoadMoreView.getFillZLayerMode() == IStateView.ZLayerMode.Same){
                params.height = provider.getStdHeightForPullUp();
                addView(mLoadMoreLayout, 0, params);
            }else{
                addView(mLoadMoreLayout, 0, params);
            }

            mLoadMoreView.reset();
            mLoadMoreLayout.addView((View) mLoadMoreView);
        }
        return this;
    }

    protected View createRefreshView(){
        SinaRefreshView refreshView = new SinaRefreshView(getContext());
        return refreshView;
    }

    protected View createLoadMoreView(){
        SinaLoadMoreView loadView = new SinaLoadMoreView(getContext());
        return loadView;
    }

    public void setPullListener(PullListener listener){
        this.mPullListener = listener;
    }

    public void finishAll(){
        provider.getRefreshCore().finishAll();
    }

    public void setRefreshing(){
        provider.getRefreshCore().setRefreshing();
    }

    public void setLoadingMore(){
        provider.getRefreshCore().setLoadingMore();
    }

    /**
     * 内容提供者
     */
    private ContentProvider provider = new ContentProvider();
    /**
     * 内容提供者
     */
    public final class ContentProvider{

        private MiniPullEventProcessor mEventProcessor;
        private MiniAnimProcessor mAnimProcessor;
        private int mRefreshViewRefreshingHeight;
        private int mLoadMoreViewLoadingHeight;
        /**
         * 滑动最小判断距离
         */
        private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        ContentProvider(){
            mAnimProcessor = new MiniAnimProcessor();
            mEventProcessor = new MiniPullEventProcessor();

            mRefreshViewRefreshingHeight = Utils.dp2px(getContext(), 80);
            mLoadMoreViewLoadingHeight = Utils.dp2px(getContext(), 80);
        }

        void init(){
            mAnimProcessor.setContentProvider(this);
            mEventProcessor.setContentProvider(this);
        }

        public MiniPullEventProcessor getEventProcessor(){
            return mEventProcessor;
        }

        public MiniAnimProcessor getAnimProcessor(){
            return mAnimProcessor;
        }

        public View getRefreshableView(){
            return mRefreshableView;
        }

        public MiniRefreshCore getRefreshCore(){
            return mRefreshCore;
        }

        public int getMaxHeightForPullUp(){
            return (int) (mLoadMoreViewLoadingHeight * 1.5);
        }

        public int getStdHeightForPullUp(){
            return mLoadMoreViewLoadingHeight;
        }

        public int getMaxHeightForPullDown(){
            return (int) (mRefreshViewRefreshingHeight * 1.5);
        }

        public int getStdHeightForPullDown(){
            return mRefreshViewRefreshingHeight;
        }

        public IStateView getRefreshView(){
            return mRefreshView;
        }

        public IStateView getLoadMoreView(){
            return mLoadMoreView;
        }

        public FrameLayout getRefreshLayout(){
            return mRefreshLayout;
        }

        public FrameLayout getLoadMoreLayout(){
            return mLoadMoreLayout;
        }

        public PullListener getPullListener(){
            return mPullListener;
        }

        public int getTouchSlop(){
            return mTouchSlop;
        }

        public boolean isEnableRefresh(){
            return mRefreshCore.isEnableRefresh && mRefreshView != null;
        }

        public boolean isEnableLoadMore(){
            return mRefreshCore.isEnableLoadMore && mLoadMoreView != null;
        }

        public boolean isRefreshing(){
            return mRefreshCore.isRefreshing;
        }

        public boolean isLoading(){
            return mRefreshCore.isLoading;
        }
        
        public boolean isAnimating(){
            return mAnimProcessor.isAnimating;
        }

        public PullState getCurrPullState(){
            return mRefreshCore.mCurrState;
        }

        public Context getContext(){
            return MiniRefreshLayout.this.getContext();
        }

        public Scroller getScroller(){
            return MiniRefreshLayout.this.mScroller;
        }

        public MiniRefreshLayout getMiniRefreshLayoutParent(){
            return MiniRefreshLayout.this;
        }
    }
}
