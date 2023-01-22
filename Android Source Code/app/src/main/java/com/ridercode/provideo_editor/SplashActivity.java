package com.ridercode.provideo_editor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ridercode.provideo_editor.ads.AdsConstants;
import com.ridercode.provideo_editor.ads.AdsManager;
import com.ridercode.provideo_editor.billing.Config;


public class SplashActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private final String TAG = "Splash";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!AdsManager.isPremium(SplashActivity.this)) {
                    AdsManager.loadInterstitialAd(SplashActivity.this);
                } else {
                    finishActivity();
                }
            }
        }, 3000);


        DatabaseReference typeRef = database.getReference("type");
        DatabaseReference rider_ad_banner_code = database.getReference("rider_ad_banner_code");
        DatabaseReference rider_ad_native_code = database.getReference("rider_ad_native_code");
        DatabaseReference rider_ad_interstitial_code = database.getReference("rider_ad_interstitial_code");
        DatabaseReference rider_fb_banner_code = database.getReference("rider_fb_banner_code");
        DatabaseReference rider_fb_interstitial_code = database.getReference("rider_fb_interstitial_code");
        DatabaseReference rider_fb_native_code_id = database.getReference("rider_fb_native_code");

        typeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                AdsConstants.type = value;
                Log.d(TAG, "Type" + value);
                Log.d(TAG, "Type" + AdsConstants.type);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        rider_ad_native_code.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                AdsConstants.admob_native_id = value;
                Log.d(TAG, "Native" + value);
                Log.d(TAG, "Native" + AdsConstants.admob_native_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        rider_ad_banner_code.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                AdsConstants.admob_banner_id = value;
                Log.d(TAG, "Admob Banner" + value);
                Log.d(TAG, "Admob Banner" + AdsConstants.admob_banner_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        rider_ad_interstitial_code.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                AdsConstants.admob_interstitial_id = value;
                Log.d(TAG, "Admob interstitial" + value);
                Log.d(TAG, "Admob interstitial" + AdsConstants.admob_interstitial_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        rider_fb_banner_code.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                AdsConstants.rider_fb_banner_code_id = value;
                Log.d(TAG, "rider_fb_banner_code" + value);
                Log.d(TAG, "rider_fb_banner_code" + AdsConstants.rider_fb_banner_code_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        rider_fb_interstitial_code.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                AdsConstants.rider_fb_interstitial_code_id = value;
                Log.d(TAG, "rider_fb_interstitial_code" + value);
                Log.d(TAG, "rider_fb_interstitial_code" + AdsConstants.rider_fb_interstitial_code_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        rider_fb_native_code_id.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                AdsConstants.rider_fb_native_code_id = value;
                Log.d(TAG, "rider_fb_banner_code" + value);
                Log.d(TAG, "rider_fb_banner_code" + AdsConstants.rider_fb_native_code_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        bp = new BillingProcessor(this, Config.IAP_LISENCE_KEY, this);
        bp.initialize();

    }

    public void finishActivity() {
        startActivity(new Intent(SplashActivity.this, StartActivity.class));
        finish();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
    }

    @Override
    public void onBillingInitialized() {
        Log.d(TAG, "onBillingInitialized");

        if (bp.isSubscribed(Config.all_month_id) ||
                bp.isSubscribed(Config.all_threemonths_id) ||
                bp.isSubscribed(Config.all_sixmonths_id) ||
                bp.isSubscribed(Config.all_yearly_id)) {

            Config.all_subscription = true;
            Saved.s_Boolean(this, Config.IsPurchased, true);
        } else {
            Saved.s_Boolean(this, Config.IsPurchased, false);
        }
    }
}