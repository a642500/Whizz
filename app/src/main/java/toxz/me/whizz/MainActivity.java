package toxz.me.whizz;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
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


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    public ActionBarDrawerToggle mDrawerToggle;
    private List<ActivityCallback> mCallbacks = new ArrayList<>();

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

         /* initViewPager */
        //TODO set fragment
        ViewPagerFragment fragment = (ViewPagerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setIcon(R.drawable.logo_whizzdo);
        actionBar.setTitle(null);

        initDrawer(toolbar);
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
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.logo_whizzdo);

        drawerLayout.addDrawerListener(mDrawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.action_bar));
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

    public interface ActivityCallback {
        boolean onBackPressed();

        boolean onViewClick(final View view);
    }
}
