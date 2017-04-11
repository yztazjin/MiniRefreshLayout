package ttyy.com.minirefreshlayout.intfs;

import android.view.View;
import android.widget.FrameLayout;

import ttyy.com.minirefreshlayout.MiniRefreshLayout;

/**
 * Author: Administrator
 * Date  : 2017/01/05 11:11
 * Name  : IStateView
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
public interface IStateView {

    ZLayerMode getFillZLayerMode();

    /**
     * 触发上拉加载/下拉刷新临界后
     * 下拉过程中 or 上拉过程中 Y的变化值
     * @param offsetY
     * @param percent
     * @param mProvider
     */
    void onPullOffsetYChanged(float offsetY, float percent, MiniRefreshLayout.ContentProvider mProvider);

    /**
     * 松开下拉/上拉
     * @param offsetY
     * @param percent
     * @param mProvider
     */
    void onPullRelease(float offsetY, float percent,  MiniRefreshLayout.ContentProvider mProvider);

    /**
     * 悬浮
     * 下拉刷新 刷新状态 or 上拉加载 加载状态
     */
    void onFloating(MiniRefreshLayout.ContentProvider mProvider);

    View getView();

    void reset();

    FrameLayout.LayoutParams getWrapperLayoutParams();

    enum ZLayerMode{
        Top,
        Bottom,
        Same;
    }
}
