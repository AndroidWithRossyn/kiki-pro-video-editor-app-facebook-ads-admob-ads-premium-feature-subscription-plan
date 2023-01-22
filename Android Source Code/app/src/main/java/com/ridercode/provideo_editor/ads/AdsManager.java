package com.ridercode.provideo_editor.ads;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.ridercode.provideo_editor.R;
import com.ridercode.provideo_editor.Saved;
import com.ridercode.provideo_editor.SplashActivity;
import com.ridercode.provideo_editor.audioJoin.AudioJoinerActivity;
import com.ridercode.provideo_editor.audiocompress.AudioCompressorActivity;
import com.ridercode.provideo_editor.audiocutter.MP3CutterActivity;
import com.ridercode.provideo_editor.audiovideomixer.AudioVideoMixer;
import com.ridercode.provideo_editor.billing.Config;
import com.ridercode.provideo_editor.fastmotionvideo.FastMotionVideoActivity;
import com.ridercode.provideo_editor.phototovideo.MoiveMakerActivity;
import com.ridercode.provideo_editor.slowmotionvideo.SlowMotionVideoActivity;
import com.ridercode.provideo_editor.videocollage.VideoCollageMakerActivity;
import com.ridercode.provideo_editor.videocompress.VideoCompressor;
import com.ridercode.provideo_editor.videocrop.VideoCropActivity;
import com.ridercode.provideo_editor.videocutter.VideoCutter;
import com.ridercode.provideo_editor.videojoiner.AddAudioActivity;
import com.ridercode.provideo_editor.videojoiner.VideoJoinerActivity;
import com.ridercode.provideo_editor.videomirror.VideoMirrorActivity;
import com.ridercode.provideo_editor.videomute.VideoMuteActivity;
import com.ridercode.provideo_editor.videoreverse.VideoReverseActivity;
import com.ridercode.provideo_editor.videorotate.VideoRotateActivity;
import com.ridercode.provideo_editor.videotogif.VideoToGIFActivity;
import com.ridercode.provideo_editor.videotomp3.VideoToMP3ConverterActivity;
import com.ridercode.provideo_editor.videowatermark.VideoWatermarkActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AdsManager {

    private static String TAG = "AdsManager";

    private static UnifiedNativeAd nativeAd;
    private static com.google.android.gms.ads.InterstitialAd mInterstitialAd;
    private static InterstitialAd interstitialAd;
    private static com.facebook.ads.AdView fadView;
    private static NativeAd fnativeAd;

    public static void loadInterstitialAd(final Activity activity) {
        String type = AdsConstants.type;
        if (!isPremium(activity)) {
            if (type.equals("ad")) {
                loadAdmobInterstitialAd(activity);
            } else {
                AdSettings.addTestDevice("6179eaf0-d3e4-4aff-84ac-3f1f787774a2");
                loadFacebookInterstitialAd(activity);
            }
        }
    }

    public static void loadBannerAd(final Activity activity, final ViewGroup adContainer) {
        if (!isPremium(activity) && isOnline(activity)) {
            String type = AdsConstants.type;
            if (type.equals("ad")) {
                AdView adView = new AdView(activity);
                adView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
                adView.setAdUnitId(AdsConstants.admob_banner_id);
                adView.loadAd(new AdRequest.Builder().build());
                adContainer.addView(adView);
            } else {
                fadView = new com.facebook.ads.AdView(activity, AdsConstants.rider_fb_banner_code_id, AdSize.BANNER_HEIGHT_50);
                adContainer.addView(fadView);
                fadView.loadAd();
            }
        }
    }

    public static void loadNativeAd(final Activity activity, final ViewGroup viewGroup, NativeAdLayout nativeAdLayout) {
        if (!isPremium(activity) && isOnline(activity)) {
            String type = AdsConstants.type;
            if (type.equals("ad")) {
                loadAdmobNativeAd(activity, viewGroup);
            } else {
                loadFacebookNativeAd(activity, nativeAdLayout);
            }
        }
    }

    private static void loadFacebookNativeAd(final Activity activity, final NativeAdLayout nativeAdLayout) {
        fnativeAd = new NativeAd(activity, AdsConstants.rider_fb_native_code_id);

        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");

                inflateAd(activity, nativeAdLayout);
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(TAG, "Native ad impression logged!");
            }
        };

        fnativeAd.loadAd(
                fnativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }

    private static void inflateAd(final Activity activity, final NativeAdLayout nativeAdLayout) {

        fnativeAd.unregisterView();
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ads_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(activity, fnativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        nativeAdTitle.setText(fnativeAd.getAdvertiserName());
        nativeAdBody.setText(fnativeAd.getAdBodyText());
        nativeAdSocialContext.setText(fnativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(fnativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(fnativeAd.getAdCallToAction());
        sponsoredLabel.setText(fnativeAd.getSponsoredTranslation());

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        fnativeAd.registerViewForInteraction(
                adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    public static void loadAdmobNativeAd(final Activity activity, final ViewGroup view) {

        AdLoader.Builder builder = new AdLoader.Builder(activity, AdsConstants.admob_native_id);

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {

            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
                nativeAd = unifiedNativeAd;
                UnifiedNativeAdView adView = (UnifiedNativeAdView) activity.getLayoutInflater()
                        .inflate(R.layout.native_ads, null);
                populateUnifiedNativeAdView(unifiedNativeAd, adView);
                view.removeAllViews();
                view.addView(adView);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(false)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {

            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private static void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {


        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);


        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());


        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }


        adView.setNativeAd(nativeAd);

    }

    public static void loadAdmobInterstitialAd(final Context context) {

        TAG = "loadAdmobInterstitialAd";

        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "The interstitial onAdLoaded");
                mInterstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.d(TAG, "The interstitial onAdFailedToLoad");

                if (context instanceof SplashActivity) {
                    ((SplashActivity) context).finishActivity();
                }
            }

            @Override
            public void onAdOpened() {
            }

            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdLeftApplication() {
            }

            @Override
            public void onAdClosed() {
                if (context instanceof SplashActivity) {
                    ((SplashActivity) context).finishActivity();
                }

            }
        });

        mInterstitialAd.setAdUnitId(AdsConstants.admob_interstitial_id);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

    public static void loadFacebookInterstitialAd(final Context context) {

        TAG = "loadFacebookInterstitialAd";

        String className = context.getClass().getSimpleName();

        Log.d(TAG, className);

        com.facebook.ads.InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                if (context instanceof SplashActivity) {
                    ((SplashActivity) context).finishActivity();
                }
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                Log.d(TAG, "onError: " + adError.getErrorMessage());
                if (context instanceof SplashActivity) {
                    ((SplashActivity) context).finishActivity();
                }

            }

            @Override
            public void onAdLoaded(Ad ad) {
                Log.d(TAG, "onAdLoaded: ");
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
        interstitialAd = new com.facebook.ads.InterstitialAd(context, AdsConstants.rider_fb_interstitial_code_id);
        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).build());


    }

    public static void handleAds(Context context) {
        if (context instanceof SplashActivity) {
            ((SplashActivity) context).finishActivity();
        } else if (context instanceof VideoWatermarkActivity) {
            ((VideoWatermarkActivity) context).c();
        } else if (context instanceof AddAudioActivity) {
            ((AddAudioActivity) context).e();
        } else if (context instanceof VideoReverseActivity) {
            ((VideoReverseActivity) context).c();
        } else if (context instanceof SlowMotionVideoActivity) {
            ((SlowMotionVideoActivity) context).c();
        } else if (context instanceof VideoCutter) {
            ((VideoCutter) context).c();
        } else if (context instanceof VideoCropActivity) {
            ((VideoCropActivity) context).c();
        } else if (context instanceof FastMotionVideoActivity) {
            ((FastMotionVideoActivity) context).c();
        } else if (context instanceof VideoCollageMakerActivity) {
            ((VideoCollageMakerActivity) context).d();
        } else if (context instanceof MP3CutterActivity) {
            ((MP3CutterActivity) context).d();
        } else if (context instanceof VideoToMP3ConverterActivity) {
            ((VideoToMP3ConverterActivity) context).c();
        } else if (context instanceof AudioVideoMixer) {
            ((AudioVideoMixer) context).e();
        } else if (context instanceof VideoRotateActivity) {
            ((VideoRotateActivity) context).c();
        } else if (context instanceof AudioJoinerActivity) {
            ((AudioJoinerActivity) context).d();
        } else if (context instanceof AudioCompressorActivity) {
            ((AudioCompressorActivity) context).D();
        } else if (context instanceof VideoJoinerActivity) {
            ((VideoJoinerActivity) context).c();
        } else if (context instanceof VideoMuteActivity) {
            ((VideoMuteActivity) context).c();
        } else if (context instanceof VideoCompressor) {
            ((VideoCompressor) context).c();
        } else if (context instanceof MoiveMakerActivity) {
            ((MoiveMakerActivity) context).c();
        } else if (context instanceof VideoMirrorActivity) {
            ((VideoMirrorActivity) context).c();
        } else if (context instanceof VideoToGIFActivity) {
            ((VideoToGIFActivity) context).c();
        }
    }

    public static boolean isPremium(Context context) {
        return Saved.g_Boolean(context, Config.IsPurchased, false);
    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

}



