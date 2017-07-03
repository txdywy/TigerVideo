package cn.ittiger.video.activity;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.video.R;
import cn.ittiger.video.app.TigerApplication;
import cn.ittiger.video.fragment.BaseFragment;
import cn.ittiger.video.factory.FragmentFactory;
import cn.ittiger.video.fragment.NameFragment;
import cn.ittiger.video.http.DataType;
import cn.ittiger.video.player.VideoPlayerHelper;
import cn.ittiger.video.util.ShareHelper;
import cn.ittiger.video.util.UIUtil;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    SparseArray<Fragment> mFragmentSparseArray = new SparseArray<>();
    private InterstitialAd mInterstitialAd;
    private long AdInterTs;
    private long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setTranslucentStatus(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);



        VideoPlayerHelper.init(this);
        init();

        this.AdInterTs = System.currentTimeMillis();
        this.count = 0;
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9974885785906256/2421647124");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                Log.d("hahaha", "ad closed");
            }
        });
        requestNewInterstitial();

        showInterAd();

    }


    private void requestNewInterstitial() {
        //Log.d("hahaha", AdRequest.DEVICE_ID_EMULATOR);
        AdRequest adRequest = new AdRequest.Builder().build();

        /*
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        */
        mInterstitialAd.loadAd(adRequest);
    }


    public void showInterAd() {
        long ts = System.currentTimeMillis();
        long elapsed = ts - this.AdInterTs;
        this.count++;
        if (elapsed > 3 * 60 * 1000 || this.count % 5 == 3){
            this.AdInterTs = ts;
        }
        else{
            Log.d("hahaha", "time too short " + elapsed/1000 + " count:" + this.count);
            return;
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.d("hahaha", "ad 111 ready");
        }
        else{
            Log.d("hahaha", "ad 222 not ready");
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.colorPrimary));
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(false);
    }

    private void init() {

        BaseFragment fragment = FragmentFactory.createMainFragment(DataType.NET_EASY);
        switchFragment(fragment);
        mNavigationView.setCheckedItem(R.id.nav_net_easy);
        mFragmentSparseArray.put(R.id.nav_net_easy, fragment);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        if(item.isChecked()) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        int id = item.getItemId();
        Fragment fragment = mFragmentSparseArray.get(id);
        if(fragment == null) {
            switch (id) {
                case R.id.nav_net_easy:
                    if(fragment == null) {
                        fragment = FragmentFactory.createMainFragment(DataType.NET_EASY);
                    }
                    break;
                case R.id.nav_ttkb:
                    fragment = FragmentFactory.createMainFragment(DataType.TTKB);
                    break;
                case R.id.nav_ifeng:
                    fragment = FragmentFactory.createMainFragment(DataType.IFENG);
                    break;
                case R.id.nav_share://分享
                    ShareHelper.shareApp(this);
                    break;
                case R.id.nav_about:
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    fragment = null;
                    break;
            }
        }
        if(fragment != null) {
            mFragmentSparseArray.put(id, fragment);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if(fragment != null) {
            switchFragment(fragment);
        }

        return true;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        ((TigerApplication)getApplication()).onDestroy();
        VideoPlayerHelper.getInstance().stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoPlayerHelper.getInstance().pause();

    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if(System.currentTimeMillis() - exitTime > 2000) {
            UIUtil.showToast(this, R.string.two_click_exit_app);
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 切换界面
     * @param fragment
     */
    private void switchFragment(Fragment fragment) {

        setTitle(((NameFragment)fragment).getName());

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.content_main, fragment);

        fragmentTransaction.commit();
    }
}
