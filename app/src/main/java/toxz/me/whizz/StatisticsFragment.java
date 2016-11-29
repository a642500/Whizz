package toxz.me.whizz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Carlos on 11/29/16.
 */

public class StatisticsFragment extends Fragment {

    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_statistics, container, false);
    }

    @Override public void onStart() {
        super.onStart();
        Toast.makeText(getActivity(), "还没有生成统计数据，等会再来看看吧", Toast.LENGTH_LONG).show();
    }
}
