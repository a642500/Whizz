package toxz.me.whizz;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.unique.whizzdo.application.MyApplication;
import com.unique.whizzdo.application.ShortCutCreator;

/**
 * Created by carlos on 5/31/14.
 */
public class ShortCreateActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shortcut);

        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"新建文字事项"}));

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        switch (position) {
            case 0:
                ShortCutCreator.addShortcut(this, ShortCutCreator.NEW_TEXT_NOTE_SHORTCUT);
                ((MyApplication) getApplication()).getNoticeMonitorService().post("快捷方式已创建", 3000);
                this.finish();
                break;
            default:
                break;
        }
    }
}
