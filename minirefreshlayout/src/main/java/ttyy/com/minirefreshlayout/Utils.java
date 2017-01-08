package ttyy.com.minirefreshlayout;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;

/**
 * Author: Administrator
 * Date  : 2017/01/05 16:03
 * Name  : Utils
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/05    Administrator   1.0              1.0
 */
final class Utils {

    /**
     * 是否可以下拉
     * @return
     */
    static boolean canViewPullDown(View mChildView){
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mChildView, -1) || mChildView.getScrollY() > 0;
            }
        } else {
            // e1.getY - e2.getY
            // -1 下拉
            // 1  上拉
            // 此处判断是否可以在垂直方向上继续向下滑动
            // 所以实际上判断的是View是否可以继续下拉
            return ViewCompat.canScrollVertically(mChildView, -1);
        }
    }

    /**
     * 是否可以上拉
     * @return
     */
    static boolean canViewPullUp(View mChildView){
        if (mChildView == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 14) {
            if (mChildView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mChildView;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mChildView, 1) || mChildView.getScrollY() < 0;
            }
        } else {
            // 是否可以上拉
            return ViewCompat.canScrollVertically(mChildView, 1);
        }
    }

    static int dp2px(Context context, int value){
        int r = (int) (context.getResources().getDisplayMetrics().density * value);
        return r;
    }

}
