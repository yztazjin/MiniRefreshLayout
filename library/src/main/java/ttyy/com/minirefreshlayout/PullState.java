package ttyy.com.minirefreshlayout;

/**
 * Author: Administrator
 * Date  : 2017/01/05 17:47
 * Name  : PullState
 * Intro : 滑动状态
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
public enum PullState{
    /**
     * 下拉刷新
     */
    PULL_DOWN_REFRESH,
    /**
     * 上拉加载
     */
    PULL_UP_LOAD,
    None;

}
