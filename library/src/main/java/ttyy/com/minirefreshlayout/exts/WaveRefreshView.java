package ttyy.com.minirefreshlayout.exts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import ttyy.com.minirefreshlayout.MiniRefreshLayout;
import ttyy.com.minirefreshlayout.R;
import ttyy.com.minirefreshlayout.intfs.IStateView;

/**
 * Author: Administrator
 * Date  : 2017/01/06 13:53
 * Name  : WaveRefreshView
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/06    Administrator   1.0              1.0
 */
public class WaveRefreshView extends FrameLayout implements IStateView {

    WaveView wave_view;

    public WaveRefreshView(Context context) {
        this(context, null);
    }

    public WaveRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.stateview_refresh_wave, this);
        wave_view = (WaveView) findViewById(R.id.wave_view);
    }

    @Override
    public ZLayerMode getFillZLayerMode() {
        return ZLayerMode.Bottom;
    }

    @Override
    public void onPullOffsetYChanged(float offsetY, float percent, MiniRefreshLayout.ContentProvider mProvider) {
        View mRefreshableView = mProvider.getRefreshableView();
        mRefreshableView.setTranslationY(offsetY);

        FrameLayout.LayoutParams params = (LayoutParams) wave_view.getLayoutParams();

        int max = mProvider.getMaxHeightForPullDown();
        int std = mProvider.getStdHeightForPullDown();
        percent = percent * std / max / 3;

        params.topMargin = (int) (percent * max);
        if(params.topMargin > std / 3){
            params.topMargin = std / 3;
        }
        wave_view.setLayoutParams(params);
    }

    @Override
    public void onPullRelease(float offsetY, float percent, MiniRefreshLayout.ContentProvider mProvider) {
        View mRefreshableView = mProvider.getRefreshableView();
        mRefreshableView.setTranslationY(offsetY);

        FrameLayout.LayoutParams params = (LayoutParams) wave_view.getLayoutParams();

        int max = mProvider.getMaxHeightForPullDown();
        int std = mProvider.getStdHeightForPullDown();
        percent = percent * std / max / 3;

        params.topMargin = (int) (percent * max);
        if(params.topMargin > std / 3){
            params.topMargin = std / 3;
        }
        wave_view.setLayoutParams(params);

        if(!mProvider.isRefreshing()){
            wave_view.stopWaving();
        }
    }

    @Override
    public void onFloating(MiniRefreshLayout.ContentProvider mProvider) {
        wave_view.startWaving();
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
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.bottomMargin = (int) (getResources().getDisplayMetrics().density * 90);
        return params;
    }
}
