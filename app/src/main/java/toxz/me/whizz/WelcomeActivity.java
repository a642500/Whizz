package toxz.me.whizz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import toxz.me.whizz.application.MyApplication;


/**
 * Created by Carlos on 4/16/2014.
 */
public class WelcomeActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
    }


    public void enterMain(View v) {
        assert getApplicationContext() != null;
        ((MyApplication) getApplicationContext()).newAccount(MyApplication.LOCAL_ACCOUNT);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

}
