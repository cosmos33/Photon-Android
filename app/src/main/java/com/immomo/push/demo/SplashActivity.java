package com.immomo.push.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            if (getIntent().getStringExtra("goto") != null) {
                Log.v("SplashActivity", getIntent().getStringExtra("push_to"));
            }
        }
        if (!this.isTaskRoot()) { // 当前类不是该Task的根部，那么之前启动
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) { // 当前类是从桌面启动的
                    finish(); // finish掉该类，直接打开该Task中现存的Activity
                    return;
                }
            }
        }
        setContentView(R.layout.activity_splash);
        findViewById(R.id.tvSplash).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 2000);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v("SplashActivity", "newIntent");
    }
}
