package ttyy.com.minirefreshlayout.exts;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ttyy.com.minirefreshlayout.MiniRefreshLayout;
import ttyy.com.minirefreshlayout.R;
import ttyy.com.minirefreshlayout.intfs.IStateView;

/**
 * Author: hjq
 * Date  : 2016/10/12
 * Class : SinaFooterRefreshView
 * Desc  : ...
 */
public class SinaLoadMoreView extends FrameLayout implements IStateView {

    private ImageView refreshArrow;
    private ImageView loadingView;
    private TextView refreshTextView;
    private View rootView;

    public SinaLoadMoreView(Context context) {
        this(context, null);
    }

    public SinaLoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SinaLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidgets();
    }

    protected void initWidgets(){
        if (rootView == null) {
            rootView = View.inflate(getContext(), R.layout.stateview_like_sina, null);
            refreshArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
            refreshTextView = (TextView) rootView.findViewById(R.id.tv);
            loadingView = (ImageView) rootView.findViewById(R.id.iv_loading);
            addView(rootView);
        }
    }

    public void setArrowResource( int resId) {
        refreshArrow.setImageResource(resId);
    }

    public void setPullUpStr(String pullDownStr1) {
        pullUpStr = pullDownStr1;
    }

    public void setReleaseRefreshStr(String releaseRefreshStr1) {
        releaseRefreshStr = releaseRefreshStr1;
    }

    public void setRefreshingStr(String refreshingStr1) {
        refreshingStr = refreshingStr1;
    }

    private String pullUpStr = "上拉加载更多";
    private String releaseRefreshStr = "释放加载更多";
    private String refreshingStr = "正在加载";

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void reset() {
    }

    @Override
    public LayoutParams getWrapperLayoutParams() {
        return null;
    }

    @Override
    public ZLayerMode getFillZLayerMode() {
        if(Build.VERSION.SDK_INT == 17){
            return ZLayerMode.Same;
        }

        return ZLayerMode.Top;
    }

    @Override
    public void onPullOffsetYChanged(float offsetY, float percent, MiniRefreshLayout.ContentProvider mProvider) {
        if(getFillZLayerMode() == ZLayerMode.Same){
            if(percent < 1){
                mProvider.getMiniRefreshLayoutParent().scrollTo(0, -(int) offsetY);
            }

            percent = percent < 1 ? percent : 1;
        }else {
            View mRefreshableView = mProvider.getRefreshableView();
            mRefreshableView.setTranslationY(offsetY);

            LayoutParams params = (LayoutParams) mProvider.getLoadMoreLayout().getLayoutParams();
            params.height = (int) -offsetY;
            mProvider.getLoadMoreLayout().requestLayout();

            int mMaxLoadHeight = mProvider.getMaxHeightForPullUp();
            int mStdLoadHeight = mProvider.getStdHeightForPullDown();
            percent = percent * mStdLoadHeight / mMaxLoadHeight;
        }

        refreshTextView.setText(pullUpStr);
        if(percent > 1){
            refreshTextView.setText(releaseRefreshStr);
        }

        refreshArrow.setRotation((1 - percent) * 180);
    }

    @Override
    public void onPullRelease(float offsetY, float percent, MiniRefreshLayout.ContentProvider mProvider) {
        if(getFillZLayerMode() == ZLayerMode.Same){
            if(percent < 1){
                mProvider.getMiniRefreshLayoutParent().scrollTo(0, -(int) offsetY);
            }

            percent = percent < 1 ? percent : 1;

        }else {
            View mRefreshableView = mProvider.getRefreshableView();
            mRefreshableView.setTranslationY(offsetY);

            LayoutParams params = (LayoutParams) mProvider.getLoadMoreLayout().getLayoutParams();
            params.height = (int) -offsetY;
            mProvider.getLoadMoreLayout().requestLayout();

            int mMaxLoadHeight = mProvider.getMaxHeightForPullUp();
            int mStdLoadHeight = mProvider.getStdHeightForPullDown();
            percent = percent * mStdLoadHeight / mMaxLoadHeight;
        }

        if(!mProvider.isLoading()){
            refreshTextView.setText(pullUpStr);

            refreshArrow.setRotation((1 - percent) * 180);
            if(!mProvider.isLoading()){
                AnimationDrawable ad = (AnimationDrawable) loadingView.getDrawable();
                ad.stop();
                loadingView.setVisibility(View.GONE);

                refreshArrow.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFloating(MiniRefreshLayout.ContentProvider mProvider) {
        refreshTextView.setText(refreshingStr);
        refreshArrow.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);

        AnimationDrawable ad = (AnimationDrawable) loadingView.getDrawable();
        ad.start();
    }
}
