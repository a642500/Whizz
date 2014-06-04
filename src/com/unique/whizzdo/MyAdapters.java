package com.unique.whizzdo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;
import com.unique.whizzdo.data.DatabaseHelper;
import com.unique.whizzdo.data.MySQLiteOpenHelper;
import com.unique.whizzdo.data.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 5/28/14.
 */
class MyPagerAdapter extends PagerAdapter {
    private List<View> mViews;

    public MyPagerAdapter(List<View> mViews) {
        this.mViews = mViews;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        ((ViewPager) arg0).addView(mViews.get(arg1), 0);
        return mViews.get(arg1);
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(mViews.get(arg1));
    }
}

class MyListAdapter extends BaseAdapter {
    private ArrayList<Note> mNotes;
    private Context mContext;
    private LayoutInflater mInflater;

    public MyListAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mNotes = DatabaseHelper.getDatabaseHelper(mContext.getApplicationContext()).getNotes(false, MySQLiteOpenHelper.COLUMN_CREATED_TIME, DatabaseHelper.DESC);
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return mNotes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mNotes.get(position).getID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_layout, parent, false);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.item_text);
        Note note = ((Note) getItem(position));
        int size = note.getImagesPaths().size();
        View view = null;
        if (size > 1) {
            view = mInflater.inflate(R.layout.two_image_layout, null);
            Picasso.with(mInflater.getContext()).load("file:" + note.getImagesPaths().get(size - 1)).error(R.drawable.load_image_error).into((ImageView) view.findViewById(R.id.image1));
            Picasso.with(mInflater.getContext()).load("file:" + note.getImagesPaths().get(size - 2)).error(R.drawable.load_image_error).into((ImageView) view.findViewById(R.id.image2));
        } else if (size == 1) {
            view = mInflater.inflate(R.layout.image_layout, null);
            Picasso.with(mInflater.getContext()).load("file:" + note.getImagesPaths().get(size - 1)).error(R.drawable.load_image_error).into((ImageView) view.findViewById(R.id.image));
        }

        if (view != null) {
            LinearLayout container = ((LinearLayout) convertView.findViewById(R.id.item_image_container));
            container.removeAllViews();
            container.setVisibility(View.VISIBLE);
            container.addView(view);
        }


        convertView.setTag(note);

        String content = note.getContent();
        if (content.trim().length() == 0) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setText(content);
        }
        View line = convertView.findViewById(R.id.item_importance_indicator);
        switch (note.getImportance()) {
            case Note.HIGH_IMPORTANCE:
                line.setBackgroundResource(R.color.line_urgency);
                break;
            case Note.NORMAL_IMPORTANCE:
                line.setBackgroundResource(R.color.line_normal);
                break;
            default:
                line.setBackgroundResource(R.color.line_ease);
                break;
        }

        //TODO 设置item view


        return convertView;
    }


}


