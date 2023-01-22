package com.ridercode.provideo_editor.billing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.ridercode.provideo_editor.R;
import com.ridercode.provideo_editor.Saved;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnlockAllActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final String TAG = "UnlockAllActivity";

    private BillingProcessor bp;

    private MutableLiveData<Integer> all_check = new MutableLiveData<>();
    @BindView(R.id.one_month)
    RadioButton oneMonth;
    @BindView(R.id.three_month)
    RadioButton threeMonth;
    @BindView(R.id.six_month)
    RadioButton sixMonth;
    @BindView(R.id.one_year)
    RadioButton oneYear;
    @BindView(R.id.monthlyCard)
    CardView monthlyCard;
    @BindView(R.id.threeMonthCard)
    CardView threeMonthCard;
    @BindView(R.id.sixMonthCard)
    CardView sixMonthCard;
    @BindView(R.id.yearlyCard)
    CardView yearlyCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscription_activity);
        ButterKnife.bind(this);
        all_check.setValue(-1);
        all_check.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 0:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 1:
                        oneMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 2:
                        threeMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 3:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        break;

                }
            }
        });
        bp = new BillingProcessor(this, Config.IAP_LISENCE_KEY, this);
        bp.initialize();

        oneMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(0);
            }
        });
        threeMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(1);
            }
        });
        sixMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(2);
            }
        });
        oneYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) all_check.postValue(3);
            }
        });

    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.d(TAG, "purchased successfully " + details.purchaseInfo.purchaseData);
        Config.all_subscription = true;
        Saved.s_Boolean(this, Config.IsPurchased, true);
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
        }
        else {
            Saved.s_Boolean(this, Config.IsPurchased, false);
        }
    }


    private void unlock_all(int i) {
        switch (i) {
            case 0:
                bp.subscribe(this, Config.all_month_id);
                break;
            case 1:
                bp.subscribe(this, Config.all_threemonths_id);
                break;
            case 2:
                bp.subscribe(this, Config.all_sixmonths_id);
                break;
            case 3:
                bp.subscribe(this, Config.all_yearly_id);
                break;
        }
    }

    @OnClick(R.id.all_pur)
    void unlockAll() {
        if (all_check.getValue() != null) unlock_all(all_check.getValue());
        else Toast.makeText(this, "dd", Toast.LENGTH_SHORT).show();
    }

    public void UnlockClick(View view) {
        switch (view.getId()) {
            case R.id.monthlyCard:
                oneMonth.setChecked(true);
                break;
            case R.id.threeMonthCard:
                threeMonth.setChecked(true);
                break;
            case R.id.sixMonthCard:
                sixMonth.setChecked(true);
                break;
            case R.id.yearlyCard:
                oneYear.setChecked(true);
                break;
        }
    }
}
