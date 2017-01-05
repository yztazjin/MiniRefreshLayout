package ttyy.com.minirefreshlayout.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ttyy.com.minirefreshlayout.MiniRefreshLayout;
import ttyy.com.minirefreshlayout.intfs.PullListenerAdapter;

public class MainActivity extends AppCompatActivity {

    MiniRefreshLayout mini_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mini_refresh = (MiniRefreshLayout) findViewById(R.id.mini_refresh);
        mini_refresh.setPullListener(new PullListenerAdapter(){
            @Override
            public void onRefresh() {
                super.onRefresh();
                Log.d("Hjq","onRefresh");
            }

            @Override
            public void onLoadMore() {
                super.onLoadMore();
                Log.d("Hjq","onLoadMore");
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.tv_refresh:
                mini_refresh.setRefreshing();
                break;
            case R.id.tv_load:
                mini_refresh.setLoadingMore();
                break;
            case R.id.tv_stop:
                mini_refresh.finishAll();
                break;
        }
    }
}
