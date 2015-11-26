package toxz.me.whizz;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import toxz.me.whizz.data.DatabaseHelper;
import toxz.me.whizz.data.MySQLiteOpenHelper;
import toxz.me.whizz.data.Note;

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
        Collections.sort(mNotes, new Comparator<Note>() {
            @Override
            public int compare(Note lhs, Note rhs) {
                int a = lhs.getImportance() == -1 ? 2 : lhs.getImportance();
                int b = rhs.getImportance() == -1 ? 2 : rhs.getImportance();
                return a - b;
            }
        });
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
        int size = note.getImagesUris().size();
        View view = null;
        if (size > 1) {
            view = mInflater.inflate(R.layout.two_image_layout, null);
            ImageView imageView1 = (ImageView) view.findViewById(R.id.image1);
            imageView1.setTag(note.getImagesUris().get(size - 1));
            imageView1.setClickable(false);
            Picasso.with(mInflater.getContext()).load(note.getImagesUris().get(size - 1)).error(R.drawable.load_image_error).into(imageView1);
            ImageView imageView2 = (ImageView) view.findViewById(R.id.image2);
            imageView2.setTag(note.getImagesUris().get(size - 2));
            imageView2.setClickable(false);
            Picasso.with(mInflater.getContext()).load(note.getImagesUris().get(size - 2)).error(R.drawable.load_image_error).into(imageView2);
        } else if (size == 1) {
            view = mInflater.inflate(R.layout.image_layout, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setTag(note.getImagesUris().get(size - 1));
            imageView.setClickable(false);
            Picasso.with(mInflater.getContext()).load(note.getImagesUris().get(size - 1)).error(R.drawable.load_image_error).into(imageView);
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


