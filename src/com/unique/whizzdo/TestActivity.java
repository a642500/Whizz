package com.unique.whizzdo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.unique.whizzdo.application.MyApplication;

/**
 * Created by carlos on 5/31/14.
 */
public class TestActivity extends Activity implements View.OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
    }

    @Override
    public void onClick(final View v) {
        final String text = ((EditText) findViewById(R.id.editText)).getText().toString();
        final int time = Integer.parseInt(((EditText) findViewById(R.id.editText_time)).getText().toString());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String name = ((Button) v).getText().toString();
                if (name.equals("特殊")) {
                    ((MyApplication) getApplication()).getNoticeMonitorService().postSpecial(text, 3000);
                } else
                    ((MyApplication) getApplication()).getNoticeMonitorService().post(text, 3000);
            }
        }, time);


        this.finish();

    }
}
