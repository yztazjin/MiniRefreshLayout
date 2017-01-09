package ttyy.com.minirefreshlayout.exts;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
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
 * Class : SinaHeaderRefreshView
 * Desc  : ...
 */
public class SinaRefreshView extends FrameLayout implements IStateView {

    private ImageView refreshArrow;
    private ImageView loadingView;
    private TextView refreshTextView;
    private View rootView;

    public SinaRefreshView(Context context) {
        this(context, null);
    }

    public SinaRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SinaRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWidgets();
    }

    protected void initWidgets(){
        if (rootView == null) {
            rootView = View.inflate(getContext(), R.layout.stateview_like_sina, this);
            refreshArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
            refreshTextView = (TextView) rootView.findViewById(R.id.tv);
            loadingView = (ImageView) rootView.findViewById(R.id.iv_loading);
        }
    }

    private String pullDownStr = "下拉刷新";
    private String releaseRefreshStr = "释放刷新";
    private String refreshingStr = "正在刷新";

    @Override
    public ZLayerMode getFillZLayerMode() {
        return ZLayerMode.Top;
    }

    @Override
    public void onPullOffsetYChanged(float offsetY, float percent, MiniRefreshLayout.ContentProvider mProvider) {
        View mRefreshableView = mProvider.getRefreshableView();
        mRefreshableView.setTranslationY(offsetY);

        LayoutParams params = (LayoutParams) mProvider.getRefreshLayout().getLayoutParams();
        params.height = (int) offsetY;
        mProvider.getRefreshLayout().requestLayout();

        refreshTextView.setText(pullDownStr);
        if(percent > 1){
            refreshTextView.setText(releaseRefreshStr);
        }

        int mMaxRefreshHeight = mProvider.getMaxHeightForPullDown();
        int mStdRefreshHeight = mProvider.getStdHeightForPullDown();
        percent = percent * mStdRefreshHeight / mMaxRefreshHeight;
        refreshArrow.setRotation(percent * 180);
    }

    @Override
    public void onPullRelease(float offsetY, float percent, MiniRefreshLayout.ContentProvider mProvider) {
        View mRefreshableView = mProvider.getRefreshableView();
        mRefreshableView.setTranslationY(offsetY);

        LayoutParams params = (LayoutParams) mProvider.getRefreshLayout().getLayoutParams();
        params.height = (int) offsetY;
        mProvider.getRefreshLayout().requestLayout();

        if(!mProvider.isRefreshing()){
            refreshTextView.setText(pullDownStr);

            int mMaxRefreshHeight = mProvider.getMaxHeightForPullDown();
            int mStdRefreshHeight = mProvider.getStdHeightForPullDown();
            percent = percent * mStdRefreshHeight / mMaxRefreshHeight;
            refreshArrow.setRotation(percent * 180);
            if (!mProvider.isRefreshing()) {
                AnimationDrawable ad = (AnimationDrawable) loadingView.getDrawable();
                ad.stop();

                loadingView.setVisibility(GONE);
                refreshArrow.setVisibility(VISIBLE);

            }
        }
    }

    @Override
    public void onFloating(MiniRefreshLayout.ContentProvider mProvider) {
        refreshTextView.setText(refreshingStr);
        refreshArrow.setVisibility(GONE);
        loadingView.setVisibility(VISIBLE);

        AnimationDrawable ad = (AnimationDrawable) loadingView.getDrawable();
        ad.start();
    }

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

}