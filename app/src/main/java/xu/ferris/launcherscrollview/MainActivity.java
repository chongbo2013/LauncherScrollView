package xu.ferris.launcherscrollview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private OverScrollView mOverScrollView;
    private Handler mHandler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mOverScrollView=(OverScrollView)findViewById(R.id.pullView);
        mOverScrollView.setOnPullListem(new OverScrollView.OnPullListem() {
            @Override
            public void refresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mOverScrollView.completePull();
                        Toast.makeText(getApplicationContext(),"刷新完毕",Toast.LENGTH_SHORT).show();
                    }
                },1500);
            }
        });
    }
}
