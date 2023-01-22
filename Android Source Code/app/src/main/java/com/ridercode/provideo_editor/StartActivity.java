package com.ridercode.provideo_editor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.NativeAdLayout;
import com.ridercode.provideo_editor.ads.AdsManager;
import com.ridercode.provideo_editor.billing.UnlockAllActivity;
import com.ridercode.provideo_editor.fragment.CenteredFragment;
import com.ridercode.provideo_editor.menu.DrawerAdapter;
import com.ridercode.provideo_editor.menu.DrawerItem;
import com.ridercode.provideo_editor.menu.SimpleItem;
import com.ridercode.provideo_editor.menu.SpaceItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;



public class StartActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    private static final int POS_BUG = 0;
    private static final int POS_RATE = 1;
    private static final int POS_SHARE = 2;
    private static final int POS_PRIVACY = 3;
    private static final int POS_VERSION = 5;
    private String TAG = "StartActivity2";

    private String[] screenTitles;
    private Drawable[] screenIcons;
    Dialog closeAppDialog;
    ImageView premiumBtn;

    private SlidingRootNav slidingRootNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        premiumBtn = findViewById(R.id.premiumBtn);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_layout)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_BUG),
                createItemFor(POS_RATE),
                createItemFor(POS_SHARE),
                createItemFor(POS_PRIVACY),
                new SpaceItem(48),
                createItemFor(POS_VERSION)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_VERSION);

        initCloseAppDialog();

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 101);
        }

        premiumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, UnlockAllActivity.class));
            }
        });
    }

    @Override
    public void onItemSelected(int position) {
        switch (position) {
            case POS_BUG:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://www.google.com"));
                startActivity(i);
                break;
            case POS_RATE:
                ratingDialog(this);
                break;
            case POS_SHARE:
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                }
                break;
            case POS_PRIVACY:
                Intent ii = new Intent(Intent.ACTION_VIEW);
                ii.setData(Uri.parse("https://www.google.com"));
                startActivity(ii);
                break;
            case POS_VERSION:

                break;
        }
        slidingRootNav.closeMenu();
        Fragment selectedScreen = CenteredFragment.createFor(screenTitles[position]);
        showFragment(selectedScreen);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        if (position == POS_VERSION)
            return new SimpleItem(screenIcons[position], "Version " + BuildConfig.VERSION_NAME)
                    .withSelectedTextTint(color(R.color.black));
        else return new SimpleItem(screenIcons[position], screenTitles[position])
                .withTextTint(color(R.color.black))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    public void initCloseAppDialog() {
        closeAppDialog = new Dialog(this);
        closeAppDialog.requestWindowFeature(1);
        closeAppDialog.setContentView(R.layout.dialog_back);
        FrameLayout frameLayout =
                closeAppDialog.findViewById(R.id.fl_adplaceholder);

        NativeAdLayout nativeAdLayout = closeAppDialog.findViewById(R.id.native_ad_container);

        AdsManager.loadNativeAd(this, frameLayout, nativeAdLayout);

        ((TextView) closeAppDialog.findViewById(R.id.tv_dialog_text)).setText(getString(R.string.sure_close_app));
        Button button = (Button) closeAppDialog.findViewById(R.id.bt_cancel);
        button.setText(getString(R.string.cancel));
        button.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                closeAppDialog.dismiss();
            }
        });
        Button button2 = (Button) closeAppDialog.findViewById(R.id.bt_yes);
        button2.setText(getString(R.string.close));
        button2.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                closeAppDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        closeAppDialog.show();
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    public static void ratingDialog(Activity activity) {

        Intent i3 = new Intent(Intent.ACTION_VIEW, Uri
                .parse("market://details?id=" + activity.getPackageName()));
        activity.startActivity(i3);
    }
}
