package com.thinksky.tox;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

public class ReviseActivity extends Activity {
    private RelativeLayout mBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_user_info);
        initview();
    }

    private void initview() {
        // TODO Auto-generated method stub
        mBack = (RelativeLayout) findViewById(R.id.back_button);
        MyOnClick my = new MyOnClick();
    }

    class MyOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int mId = v.getId();
            switch (mId) {
                case R.id.back_button:
                    finish();
                    break;
                default:
                    break;
            }
        }

    }
}
