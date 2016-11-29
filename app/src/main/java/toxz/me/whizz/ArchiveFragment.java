package toxz.me.whizz;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import de.timroes.android.listview.EnhancedListView;
import toxz.me.whizz.data.DatabaseHelper;

/**
 * Created by Carlos on 11/28/16.
 */

public class ArchiveFragment extends Fragment implements AdapterView.OnItemClickListener, View
        .OnLongClickListener {
    private EnhancedListView mListView;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(DatabaseHelper.ACTION_NOTES_CHANGED)) {
                refreshList();
            }
        }
    };

    @SuppressLint("InflateParams") @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pager_archive, container, false);

        mListView = (EnhancedListView) root.findViewById(R.id.main_list);
        mListView.setEmptyView(root.findViewById(R.id.no_note));
        mListView.setOnItemClickListener(this);
        mListView.setOnLongClickListener(this);
        refreshList();
        return root;
    }

    private void refreshList() {
        MyListAdapter listAdapter = new MyListAdapter(getLayoutInflater(null), true);
        mListView.setAdapter(listAdapter);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                            final long id) {

    }

    @Override public boolean onLongClick(final View v) {
        return false;
    }


    @Override public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter
                (DatabaseHelper.ACTION_NOTES_CHANGED));
    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
    }
}
