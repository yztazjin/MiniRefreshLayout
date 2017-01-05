package ttyy.com.minirefreshlayout.intfs;

import ttyy.com.minirefreshlayout.PullState;

/**
 * Author: Administrator
 * Date  : 2017/01/05 17:44
 * Name  : PullListener
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
public interface PullListener {

    void onOffsetYChanged(float offsetY, float percent, PullState state);

    void onRefresh();

    void onLoadMore();
}
