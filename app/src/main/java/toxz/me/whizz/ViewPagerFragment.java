package toxz.me.whizz;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.timroes.android.listview.EnhancedListView;
import toxz.me.whizz.application.ShortCutCreator;
import toxz.me.whizz.data.DatabaseHelper;
import toxz.me.whizz.data.Note;
import toxz.me.whizz.dateparser.ParsedDate;
import toxz.me.whizz.dateparser.ParserUtil;
import toxz.me.whizz.view.ProgressionDateSpinner;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


/**
 * Created by Carlos on 11/28/16.
 */
public class ViewPagerFragment extends Fragment implements ActionMode.Callback, ViewPager
        .OnPageChangeListener, MainActivity.ActivityCallback {

    public static final int REQUEST_CODE_IMAGE_PICK = 1023;
    public static final int SCROLL_FLAG_FROM_ONE_TO_TWO = 0;
    public static final int SCROLL_FLAG_FROM_TWO_TO_ONE = 1;
    private static final String TAG = "ViewPagerFragment";
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm 创建",
            Locale.getDefault());
    private View mNoNote;
    private ViewPager mViewPager;
    private EnhancedListView mMainList;
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(DatabaseHelper.ACTION_NOTES_CHANGED)) {
                refreshList();
            }
        }
    };
    private ArrayList<Note> mCheckedItem = new ArrayList<>();
    private boolean isEdit = false;
    private String mTempText = "";
    private EditText mNewNoteEditText;
    private LinearLayout mImageContainer;
    private View mNewItemPager;
    private TextView mCreatedTimeText;
    /**
     * Note mCurrent is used to contain temp data, null when create a new note.
     */
    private Note mCurrentNote = null;
    private ActionMode mActionMode = null;
    private int mImageContainerWidth = 0, mImageHeight = 0, mCurrentPage = 0;
    private TextView mDateText;
    private ProgressionDateSpinner mDateSpinner;

    EditText getNewNoteEditText() {
        return mNewNoteEditText;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        mViewPager = new ViewPager(inflater.getContext());

        List<View> mPagers = new ArrayList<>();
        mPagers.add(initMainPager(inflater));
        mPagers.add(initAddNotePager());

        mViewPager.setAdapter(new MyPagerAdapter(mPagers));

        return mViewPager;
    }

    @Override public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, new IntentFilter
                (DatabaseHelper.ACTION_NOTES_CHANGED));
        ((MainActivity) getActivity()).registerActivityCallback(this);

        final ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (null == actionBar.getCustomView()) {
            View customActionBarView = getLayoutInflater(savedInstanceState)
                    .inflate(R.layout.custom_action_bar, null);
            UnderlinePageIndicator pagerIndicator = (UnderlinePageIndicator) customActionBarView
                    .findViewById(R.id.pageIndicator);
            pagerIndicator.setSelectedColor(getResources().getColor(R.color.item_background));
            pagerIndicator.setFades(false);
            pagerIndicator.setViewPager(mViewPager);
            pagerIndicator.setOnPageChangeListener(this);
            actionBar.setCustomView(customActionBarView);
        }
        actionBar.setDisplayShowCustomEnabled(true);

    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
        ((MainActivity) getActivity()).unregisterActivityCallback(this);

        final ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(false);

    }

    @Override public void onStart() {
        super.onStart();
        int launchFrom = getActivity().getIntent()
                .getIntExtra(ShortCutCreator.EXTRA_LAUNCH_METHOD, -1);
        switch (launchFrom) {
            case ShortCutCreator.NEW_TEXT_NOTE_SHORTCUT:
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(1);
                    isEdit = true;
                }
                break;
            default:
                break;
        }
    }

    private View initMainPager(LayoutInflater inflater) {
        @SuppressLint("InflateParams")
        View itemListPager = inflater.inflate(R.layout.item_list_pager, null);
        assert itemListPager != null;

        mMainList = (EnhancedListView) itemListPager.findViewById(R.id.main_list);
        mNoNote = itemListPager.findViewById(R.id.no_note);
        mMainList.setDivider(getResources().getDrawable(R.drawable.line_divider));

        AdapterView.OnItemClickListener listItemClickListener = new AdapterView
                .OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode == null) {
                    mCurrentNote = (Note) view.getTag();
                    mViewPager.setCurrentItem(1, true);
                    onScrollPage(SCROLL_FLAG_FROM_ONE_TO_TWO);
                } else {
                    final Note note = (Note) view.getTag();
                    if (mCheckedItem.contains(note)) {
                        view.setBackgroundResource(R.color.main_background);
                        Log.i("onItemClick()", "item " + position + " is removed from ArrayList");
                        mCheckedItem.remove(note);

                    } else {
                        view.setBackgroundResource(R.color.selected_background);
                        Log.i("onItemClick()", "item " + position + " is added to ArrayList");
                        mCheckedItem.add((Note) view.getTag());
                    }
                }

            }
        };

        mMainList.setOnItemClickListener(listItemClickListener);
        AdapterView.OnItemLongClickListener listItemLongClickListener = new AdapterView
                .OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long
                    id) {
                if (mActionMode != null) { return true; }
                ((AppCompatActivity) getActivity()).startSupportActionMode(ViewPagerFragment.this);
                view.setBackgroundResource(R.color.selected_background);
                Log.i("onItemLongClick()", "play actionMode, item " + position + " is added to " +
                        "ArrayList");
                mCheckedItem.add((Note) view.getTag());
                return true;
            }
        };
        mMainList.setOnItemLongClickListener(listItemLongClickListener);
        mMainList.setSwipeDirection(EnhancedListView.SwipeDirection.END).setDismissCallback(
                new EnhancedListView.OnDismissCallback() {
                    @Override
                    public EnhancedListView.Undoable onDismiss(EnhancedListView listView, int
                            position) {
                        if (position < listView.getAdapter().getCount()) {
                            final Note note = (Note) listView.getAdapter().getItem(position);
                            note.delete(DatabaseHelper.getDatabaseHelper(mMainList.getContext()));
                            return new EnhancedListView.Undoable() {
                                @Override
                                public void undo() {
                                    note.resetID().commit(DatabaseHelper.getDatabaseHelper
                                            (mMainList.getContext()));
                                }
                            };
                        } else {
                            Log.e("onDismiss()", "return null");
                            return null;
                        }
                    }
                }).setRequireTouchBeforeDismiss(false).setUndoHideDelay(3000).setUndoStyle
                (EnhancedListView.UndoStyle.MULTILEVEL_POPUP).enableSwipeToDismiss();
        refreshList();
        return itemListPager;
    }

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu
            menu) {

        mode.getMenuInflater().inflate(R.menu.action_mode, menu);
        mActionMode = mode;
        return true;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu
            menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, final
    MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionMode_finish:
                Note note;
                for (int i = 0; i < mCheckedItem.size(); i++) {
                    note = mCheckedItem.get(i);
                    note.setFinished(true);
                    Log.i("onActionItemClicked()", "note " + note.getContent() + " , id " + note
                            .getID() + " is finished.");
                    note.commit(DatabaseHelper.getDatabaseHelper(getContext()));
                }
                mode.finish();
                break;
            case R.id.actionMode_delete:
                Note note2;
                for (int i = 0; i < mCheckedItem.size(); i++) {
                    note2 = mCheckedItem.get(i);
                    note2.delete(DatabaseHelper.getDatabaseHelper(getContext()));
                }
                mode.finish();
                break;
            case R.id.actionMode_select_all:
                break;
        }
        return false;
    }

    @Override public void onDestroyActionMode(final ActionMode mode) {
        if (mCheckedItem.size() != 0) { refreshList(); }
        mCheckedItem.clear();
        mActionMode = null;
    }

    //TODO IMPROVE: when scroll between two page, disable the relayout of the first pager
    //TODO pictures break down after restart app, maybe because permission, I believe copying a
    // picture into our zoom can fix it.
    private void refreshNotePager() {
        mImageContainer.removeAllViews();
        /* what need to be refresh: image list , alarm color , time pick */
        if (mCurrentNote == null) {
            mNewNoteEditText.setText("");
            mCreatedTimeText.setText(format.format(System.currentTimeMillis()));
            mDateSpinner.setCalendar(null);
            mDateText.setVisibility(View.INVISIBLE);
        } else {
            if (mCurrentNote.getImagesPath().size() > 0) {
                LinearLayout linearLayout;
                Log.i("refreshNotePager()", "image path sizes: " + mCurrentNote.getImagesPath()
                        .size());
                for (int i = 0; i < mCurrentNote.getImagesPath().size(); i += 2) {
                    Log.i("load images", "Path is " + mCurrentNote.getImagesPath().get(i));
                    Log.i("refreshNotePager()", "add pic  i = " + i);
                    if (i == mCurrentNote.getImagesPath().size() - 1) {
                        Log.i("refreshNotePager()", "kind 1 was created ! ");
                        linearLayout = (LinearLayout) getLayoutInflater(null).inflate(R.layout
                                        .image_layout,
                                null);
                        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.image);
                        imageView.setTag(mCurrentNote.getImagesPath().get(i));
                        //                        Bitmap bitmap = BitmapFactory.decodeFile
                        // (mCurrentNote.getImagesPath().get(i));
                        //                        imageView.setImageBitmap(bitmap);
                        Picasso.with(getContext()).load(("file://" + mCurrentNote
                                .getImagesPath().get(i))).error(R.drawable.ic_launcher)
                                .into(imageView);
                    } else {
                        Log.i("refreshNotePager()", "kind 2 was created ! i=" + i);
                        linearLayout = (LinearLayout) getLayoutInflater(null).inflate(R.layout
                                .two_image_layout, null);
                        ImageView imageView1 = (ImageView) linearLayout.findViewById(R.id.image1);
                        imageView1.setTag(mCurrentNote.getImagesPath().get(i));

                        //                        Bitmap bitmap1 = BitmapFactory.decodeFile
                        // (mCurrentNote.getImagesPath().get(i));
                        //                        imageView1.setImageBitmap(bitmap1);
                        Picasso.with(getContext())
                                .load("file://" + mCurrentNote.getImagesPath().get(i))
                                .error(R.drawable.ic_launcher)
                                .into(imageView1);

                        Log.i("refreshNotePager()", "kind 2 was created ! i=" + (i + 1));
                        ImageView imageView2 = (ImageView) linearLayout.findViewById(R.id.image2);
                        imageView2.setTag(mCurrentNote.getImagesPath().get(i));

                        //                        Bitmap bitmap2 = BitmapFactory.decodeFile
                        // (mCurrentNote.getImagesPath().get(i + 1));
                        //                        imageView2.setImageBitmap(bitmap2);
                        Picasso.with(getContext())
                                .load("file://" + mCurrentNote.getImagesPath().get(i + 1))
                                .error(R.drawable.ic_launcher)
                                .into(imageView2);
                    }
                    mImageContainer.addView(linearLayout);
                }
            }
            mNewNoteEditText.setText(mCurrentNote.getContent());
            mCreatedTimeText.setText(format.format(mCurrentNote.getCreatedTime()));
        }
    }

    private void onScrollPage(int flag) {
        if (mActionMode != null) {
            mActionMode.finish();
        }
        switch (flag) {
            case SCROLL_FLAG_FROM_ONE_TO_TWO:
                Log.i("onScrollPage()", "SCROLL_FLAG_FROM_ONE_TO_TWO");
                refreshNotePager();
                if (mNewNoteEditText != null) {
                    mNewNoteEditText.requestFocus();
                    KeyboardUtil.showKeyboard(getContext(), mNewNoteEditText);
                }
                break;
            case SCROLL_FLAG_FROM_TWO_TO_ONE:
                Log.i("onScrollPage()", "SCROLL_FLAG_FROM_TWO_TO_ONE");
                mNewNoteEditText.clearFocus();
                mMainList.requestFocus();
                KeyboardUtil.hideKeyboard(getContext(), mMainList.getWindowToken());

                String note = String.valueOf(mNewNoteEditText.getText());

                boolean nullNote = mCurrentNote == null;

                boolean noPic = nullNote || mCurrentNote.getImagesPath() == null
                        || mCurrentNote.getImagesPath().size() == 0;
                boolean noSavedText = nullNote || mCurrentNote.getContent() == null
                        || mCurrentNote.getContent().trim().length() == 0;
                boolean noPendingText = note == null || note.trim().length() == 0;

                boolean noText = noSavedText && noPendingText;
                if (noPic && noText) {
                    Toast.makeText(getContext(), "空白记事，已舍弃", Toast.LENGTH_SHORT).show();
                    mCurrentNote = null;
                } else {
                    if (mCurrentNote == null) {
                        mCurrentNote = new Note.Builder()
                                .setNotice(true)
                                .setContent(note)
                                .setCreatedTime(System.currentTimeMillis())
                                .create();
                    } else {
                        if (mCurrentNote.getID() == -1) {
                            mCurrentNote.setCreatedTime(System.currentTimeMillis());
                        }
                        mCurrentNote.setContent(note);
                        mCurrentNote.setNotice(true);
                    }
                    mCurrentNote.commit(DatabaseHelper.getDatabaseHelper(getContext()));
                    Toast.makeText(getContext(), "已保存", Toast.LENGTH_SHORT).show();
                    mCurrentNote = null;
                    refreshList();
                }

                refreshNotePager();
                break;
            default:
        }

    }

    /**
     * refresh the list, data will refreshed.
     */
    public void refreshList() {
        //every item show up to 80 Words.Set by xml.
        Log.i("refreshList()", "!!!");
        if (!DatabaseHelper.getDatabaseHelper(getContext().getApplicationContext()).hasNotes()) {
            mNoNote.setVisibility(View.VISIBLE);
        }
        //if contain no items，not display list, and the notice for no item will displayed.
        else { mNoNote.setVisibility(View.GONE); }
        mMainList.setAdapter(new MyListAdapter(getLayoutInflater(null), false));
    }

    /* call by init() */
    private View initAddNotePager() {
        mNewItemPager = getLayoutInflater(null).inflate(R.layout.new_item_pager, null);
        assert mNewItemPager != null;
        mNewNoteEditText = (EditText) mNewItemPager.findViewById(R.id.et_input_note);
        // final RectAnimationLinearLayout dateGroup = (RectAnimationLinearLayout) mNewItemPager
        //         .findViewById(R.id.dateGroup);

        mNewNoteEditText.addTextChangedListener(new TextWatcher() {
            private ParsedDate lastDate;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                Log.i(TAG, String.format("parse play, %d, str: %s", SystemClock.uptimeMillis(),
                        str));
                ParsedDate parsedDate = ParserUtil.parseDeadline(str);
                if (parsedDate != null && mDateSpinner != null) {
                    mDateSpinner.setCalendar(parsedDate.date);
                    // dateGroup.play();

                    if (!parsedDate.equals(lastDate)) {
                        lastDate = parsedDate;
                        YoYo.with(Techniques.Shake).playOn(mDateSpinner);
                    }

                }
                Log.i(TAG, String.format("parse end, %d, date: %s", SystemClock.uptimeMillis(),
                        parsedDate == null ? "null" : parsedDate.toString()));
            }
        });

        mImageContainer = (LinearLayout) mNewItemPager.findViewById(R.id.image_container);
        mCreatedTimeText = (TextView) mNewItemPager.findViewById(R.id.tv_create_time);

        mDateText = (TextView) mNewItemPager.findViewById(R.id.dateText);
        mDateSpinner = (ProgressionDateSpinner) (mNewItemPager.findViewById(R.id
                .progressionDateSpinner));
        mDateSpinner.setSupportFragmentManager(getFragmentManager());
        // mDateSpinner.setOnSelectedListener(new ProgressionDateSpinner.OnSelectedListener() {
        //     @Override
        //     public void onSelected(Level level, Calendar cl) {
        //
        //     }
        //
        //     @Override
        //     public void onCancel() {
        //     }
        // });
        mDateSpinner.setOnCalendarChangedListener(new ProgressionDateSpinner
                .OnCalendarChangedListener() {
            @Override
            public void onChanged(Calendar before, Calendar after,
                                  ProgressionDateSpinner.ProgressionAdapter.Level beforeLevel,
                                  ProgressionDateSpinner.ProgressionAdapter.Level afterLevel) {

                if (mCurrentNote == null) {
                    mCurrentNote = new Note();
                }
                switch (afterLevel) {
                    case HIGH:
                        mCurrentNote.setImportance(Note.HIGH_IMPORTANCE);
                        break;
                    case MEDIUM:
                        mCurrentNote.setImportance(Note.NORMAL_IMPORTANCE);
                        break;
                    case LOW:
                        mCurrentNote.setImportance(Note.LOW_IMPORTANCE);
                        break;
                    default:
                        mCurrentNote.setImportance(Note.NO_IMPORTANCE);
                        break;
                }


                if (after != null) {
                    mCurrentNote.setDeadline(after.getTimeInMillis());

                    mDateText.setVisibility(View.VISIBLE);
                    String pattern;

                    final Calendar today = Calendar.getInstance();
                    //                    int delta = cl.get(Calendar.DAY_OF_YEAR) - today.get
                    // (Calendar.DAY_OF_YEAR);
                    if (after.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {
                        pattern = "YYYY年MM月dd日";
                    } else {
                        pattern = "MM月dd日";
                    }

                    mDateText.setText(new SimpleDateFormat(pattern,
                            Locale.getDefault()).format(after.getTime()));
                } else {
                    mDateText.setVisibility(View.INVISIBLE);
                }
            }
        });

        refreshNotePager();
        return mNewItemPager;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        //
        //        Log.i("vital", "Viewpager onPageScrolled ! position is :" + position + ",
        // position offset is " + positionOffset + ", pixels is " + positionOffsetPixels);
        //        Log.i("vital", "isEdit is  " + isEdit);
        //TODO 仿x-plore的滑动


        /*当偏移小于一定值时绘制指示图标，当大于这一值时，触发onScrollPage*/
    }

    @Override
    public void onPageSelected(int position) {
        //        Log.i("vital", "onPageSelected : position is " + position);
        //        Log.i("vital", "isEdit is  " + isEdit);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // 如果本来是在编辑状态，则不变；如果不在编辑状态，则判断是否进入编辑状态
        boolean sign = isEdit;
        isEdit = isEdit || (state == 0 && mCurrentPage == 1);
        if (!sign && isEdit) {
            onScrollPage(SCROLL_FLAG_FROM_ONE_TO_TWO);
        }

        // 如果不是在编辑状态，则不变；如果是在编辑状态，判断是否需要保存，若需要，则保存，然后退出编辑状态

        if (isEdit && (state == 0 && mCurrentPage == 0)) {
            onScrollPage(SCROLL_FLAG_FROM_TWO_TO_ONE);
            isEdit = false;
        }
        //        Log.i("onPageScrollStateChanged()", "isEdit  is " + isEdit + " ,  mCurrentPage
        // is " + mCurrentPage);
        //                Log.i("vital", "isEdit is  " + isEdit);

    }


    private void onReceiveImage(Uri uri) {
        if (uri != null) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                inputStream = getContext().getContentResolver().openInputStream(uri);
                if (inputStream == null) { throw new IOException("unable open image"); }

                File dir = new File(getContext().getFilesDir(), "images");
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new IOException("unable create image dir");
                }

                File file = new File(dir, System.currentTimeMillis() + ".jpg");
                Log.i(TAG, String.format("onReceiveImage: file %s", file));

                if (file.exists() && !file.delete()) { throw new IOException(); } else {
                    outputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];

                    int length;
                    //copy the file content in bytes
                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    inputStream.close();
                    outputStream.close();
                    outputStream.flush();

                    if (mCurrentNote == null) {
                        mCurrentNote = new Note();
                    }

                    List<String> images = mCurrentNote.getImagesPath();
                    images = new ArrayList<>(images);
                    images.add(file.toString());
                    mCurrentNote.setImagesPath(images);
                    mCurrentNote.setContent(mTempText);
                    refreshNotePager();
                    mTempText = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }


        }
    }


    @Override public boolean onBackPressed() {
        if (mCurrentPage == 1) {
            isEdit = false;
            mCurrentPage = 0;
            onScrollPage(SCROLL_FLAG_FROM_TWO_TO_ONE);
            return true;
        }
        return false;
    }

    @Override public boolean onViewClick(final View view) {
        switch (view.getId()) {
            default:
                return false;
            case R.id.bottom_bar_audio:
                //TODO audio
                break;
            case R.id.bottom_bar_image:
                mTempText = mNewNoteEditText.getText().toString();
                if (mTempText == null) { mTempText = ""; }
                startActivityForResult(new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI),
                        REQUEST_CODE_IMAGE_PICK);
                break;
            case R.id.action_bar_item_add_note:
                mViewPager.setCurrentItem(1, true);
                break;
            case R.id.action_bar_item_list:
                mViewPager.setCurrentItem(0, true);
                break;
            case R.id.no_note:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.image:
            case R.id.image1:
            case R.id.image2:
                String path = (String) view.getTag();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Uri uri = FileProvider.getUriForFile(getContext(), getContext()
                        .getApplicationContext().getPackageName() + ".provider", new File(path));

                intent.setDataAndType(uri, "image/*");
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_IMAGE_PICK) {
            Log.i(TAG, String.format("resultCode %d, data %s", requestCode, data));
            if (resultCode == RESULT_OK && data != null) {
                Uri image = data.getData();
                onReceiveImage(image);
            }
        }
    }
}
