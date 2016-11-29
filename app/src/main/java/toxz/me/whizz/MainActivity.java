package toxz.me.whizz;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import toxz.me.whizz.application.MyApplication;
import toxz.me.whizz.monitor.MonitorService;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    public ActionBarDrawerToggle mDrawerToggle;
    private List<ActivityCallback> mCallbacks = new ArrayList<>();
    private ViewPagerFragment mInboxFragment;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if account is not initialized ,launch welcome activity.
        if (MyApplication.AccountInfo.getAccount() == null) {
            Log.i("vital", "account == null ");
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            init();
        }
    }

    /**
     * init and display.
     */
    private void init() {
        setContentView(R.layout.main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setIcon(R.drawable.logo_whizzdo);
        actionBar.setTitle(null);

        initDrawer(toolbar);

        mInboxFragment = (ViewPagerFragment) Fragment
                .instantiate(this, "toxz.me.whizz.ViewPagerFragment");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, mInboxFragment, "Inbox")
                .commitAllowingStateLoss();
    }

    @Override protected void onPostCreate(@Nullable final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override public boolean onOptionsItemSelected(final MenuItem item) {
        mDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initDrawer(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navigationView);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.logo_whizzdo);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.action_bar));

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override public void onDrawerSlide(final View drawerView, final float slideOffset) {
                KeyboardUtil.hideKeyboard(MainActivity.this, drawerView.getWindowToken());
            }

            @Override public void onDrawerOpened(final View drawerView) {

            }

            @Override public void onDrawerClosed(final View drawerView) {
                if (mInboxFragment.isAdded()) {
                    if (mInboxFragment.getViewPager().getCurrentItem() == 1) {
                        KeyboardUtil.showKeyboard(MainActivity.this,
                                mInboxFragment.getNewNoteEditText());
                    }
                }
            }

            @Override
            public void onDrawerStateChanged(final int newState) {

            }
        });
    }

    private void initDao() {
        //        helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        //        db = helper.getWritableDatabase();
        //        daoMaster = new DaoMaster(db);
        //        daoSession = daoMaster.newSession();
        // // do this in your activities/fragments to get hold of a DAO
        //        noteDao = daoSession.getNoteDao();
    }

    @Override
    public void onBackPressed() {
        for (ActivityCallback callback : mCallbacks) {
            if (callback.onBackPressed()) {
                return;
            }
        }

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }


        if (!mInboxFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fragment_fade_in, R.anim.fragment_fade_out)
                    .replace(R.id.fragmentContainer, mInboxFragment).commitAllowingStateLoss();
            mNavigationView.setCheckedItem(R.id.navigation_item_inbox);
            return;
        }


        sendBroadcast(new Intent(MonitorService.MY_ACTION_MAIN_ACTIVITY_EXIT));
        super.onBackPressed();
    }

    @Override public void onClick(final View v) {
        for (ActivityCallback callback : mCallbacks) {
            if (callback.onViewClick(v)) {
                return;
            }
        }
    }

    public void registerActivityCallback(ActivityCallback callback) {
        if (!mCallbacks.contains(callback)) { mCallbacks.add(callback); }
    }

    public void unregisterActivityCallback(ActivityCallback callback) {
        mCallbacks.remove(callback);
    }

    @Override public boolean onNavigationItemSelected(@NonNull final MenuItem menuItem) {
        mDrawerLayout.closeDrawers();

        if (menuItem.isChecked()) {
            return true;
        }
        final int menuId = menuItem.getItemId();


        if (menuId == R.id.navigation_item_settings) {
            mHandler.postDelayed(new Runnable() {
                @Override public void run() {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            }, 300);
            return false;
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override public void run() {
                    switch (menuId) {
                        case R.id.navigation_item_inbox:
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fragment_fade_in,
                                            R.anim.fragment_fade_out)
                                    .replace(R.id.fragmentContainer, mInboxFragment)
                                    .commitAllowingStateLoss();
                            break;
                        case R.id.navigation_item_archive:
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fragment_fade_in,
                                            R.anim.fragment_fade_out)
                                    .replace(R.id.fragmentContainer, Fragment.instantiate
                                            (MainActivity.this,
                                                    "toxz.me.whizz.ArchiveFragment"))
                                    .commitAllowingStateLoss();
                            break;
                        case R.id.navigation_item_2:
                            //TODO add this
                            break;
                    }
                }
            }, 300);
            return true;
        }
    }

    public interface ActivityCallback {
        boolean onBackPressed();

        boolean onViewClick(final View view);
    }
}
