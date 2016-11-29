package toxz.me.whizz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import toxz.me.whizz.application.SettingsHelper;
import toxz.me.whizz.data.Note;
import toxz.me.whizz.notification.NotificationUtil;

/**
 * Created by carlos on 5/31/14.
 */
public class TestActivity extends Activity implements View.OnClickListener {

    private EditText mMainEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        CheckBox checkBox = (CheckBox) findViewById(R.id.flash_light_checkBox);
        checkBox.setChecked(SettingsHelper.isSnapFlash(this));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsHelper.setSnapFlash(isChecked, TestActivity.this);
            }
        });

        mMainEditText = (EditText) findViewById(R.id.editText);
    }

    @Override
    public void onClick(final View v) {
        Note note = new Note();
        switch (v.getId()) {
            case R.id.testToastBtn:
                note.setContent(mMainEditText.getText().toString());
                NotificationUtil.toastNote(this, note);
                break;
            case R.id.testDialogBtn:
                note.setContent(mMainEditText.getText().toString());
                note.setDeadline(System.currentTimeMillis());
                NotificationUtil.dialogNote(this, note);
                break;
        }
        // final String text = ((EditText) findViewById(R.id.editText)).getText().toString();
        // final int time = Integer.parseInt(((EditText) findViewById(R.id.editText_time)).getText()
        //         .toString());
        // new Handler().postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        //         String name = ((Button) v).getText().toString();
        //         if (name.equals("特殊")) {
        //             ((MyApplication) getApplication()).getNoticeMonitorService().post(text, 3000,
        //                     MonitorService.TEXT_WHITH_BUTTON_TOAST);
        //         } else {
        //             ((MyApplication) getApplication()).getNoticeMonitorService().post(text,
        // 3000);
        //         }
        //     }
        // }, time);
    }
}
