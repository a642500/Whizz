package toxz.me.whizz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.timroes.android.listview.EnhancedListView;

/**
 * Created by Carlos on 11/28/16.
 */

public class ArchiveFragment extends Fragment {

    @SuppressLint("InflateParams") @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pager_archive, container, false);

        EnhancedListView listView = (EnhancedListView) root.findViewById(R.id.main_list);
        listView.setEmptyView(inflater.inflate(R.layout.layout_empty, null));

        return root;
    }
}
