# MiniRefreshLayout
简约下拉刷新上拉加载处理外层包裹Layout
* 被包裹的可刷新View必须为Layout的第一个子View
* 被包裹的可刷新View需要支持滑动

### 代码示例
```xml
<ttyy.com.minirefreshlayout.MiniRefreshLayout
        android:id="@+id/mini_refresh"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent">
            
        </ScrollView>
</ttyy.com.minirefreshlayout.MiniRefreshLayout>
```

```Java
 mini_refresh.setPullListener(new PullListenerAdapter(){
            @Override
            public void onRefresh() {
                super.onRefresh();
                // 下拉刷新
            }

            @Override
            public void onLoadMore() {
                super.onLoadMore();
                // 上拉加载
            }
        });
        
 // refreshableView处于滑动顶部时，setRefreshing，调起下拉刷新       
 mini_refresh.setRefreshing();
 // refreshableView处于滑底部时，setRefreshing，调起下拉刷新       
 mini_refresh.setLoadingMore();
 // 结束刷新动画
 mini_refresh.finishAll();
```

### 定制
* MiniRefreshLayout.setRefreshView(IStateView view);
* MiniRefreshLayout.setLoadMoreView(IStateView view) 
> 定制化自定义状态刷新View

* 收起，弹出动画暂不支持定制

### IStateView
```Java
public interface IStateView {
    
    /**
     * 状态View填充方式
     */
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
    
    /*
     * 刷新状态View，自定义LayoutParams
     * return null 默认填充方式高度0
     */
    FrameLayout.LayoutParams getWrapperLayoutParams();

    enum ZLayerMode{
        /**
         * Z轴方向，在可刷新View上层
         */
        Top,
        /**
         * Z轴方向，在可刷新View下层
         */
        Bottom;
    }
}
```

### 已有刷新状态样式View
* SinaLoadMoreView
> 底部加载更多，仿新浪样式

* SinaRefreshView
> 顶部刷新，仿新浪样式

* WaveRefreshView
> 顶部刷新， 波浪样式
