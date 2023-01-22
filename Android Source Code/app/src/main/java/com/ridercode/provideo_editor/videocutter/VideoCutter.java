package com.ridercode.provideo_editor.videocutter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.arthenica.mobileffmpeg.Config;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.ridercode.provideo_editor.R;
import com.ridercode.provideo_editor.VideoPlayer;
import com.ridercode.provideo_editor.VideoSliceSeekBar;
import com.ridercode.provideo_editor.VideoSliceSeekBar.SeekBarChangeListener;
import com.ridercode.provideo_editor.ads.AdsManager;
import com.ridercode.provideo_editor.listvideoandmyvideo.ListVideoAndMyAlbumActivity;
import com.ridercode.provideo_editor.videojoiner.VideoJoinerActivity;
import com.ridercode.provideo_editor.videojoiner.model.VideoPlayerState;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;

@SuppressLint({"WrongConstant"})
public class VideoCutter extends AppCompatActivity implements MediaScannerConnectionClient, OnClickListener {

    public static Context AppContext = null;
    static final boolean k = true;
    MediaScannerConnection a;
    int b = 0;
    int c = 0;
    TextView d;
    TextView e;
    TextView f;
    TextView g;
    ImageView h;
    VideoSliceSeekBar i;
    VideoView j;
    private String l = "";
    private String m;

    public String n;
    public FFmpeg fFmpeg;

    public VideoPlayerState o = new VideoPlayerState();
    private a p = new a();

    private class a extends Handler {
        private boolean b;
        private Runnable c;

        private a() {
            this.b = false;
            this.c = new Runnable() {
                public void run() {
                    a.this.a();
                }
            };
        }


        public void a() {
            if (!this.b) {
                this.b = VideoCutter.k;
                sendEmptyMessage(0);
            }
        }

        @Override
        public void handleMessage(Message message) {
            this.b = false;
            VideoCutter.this.i.videoPlayingProgress(VideoCutter.this.j.getCurrentPosition());
            if (!VideoCutter.this.j.isPlaying() || VideoCutter.this.j.getCurrentPosition() >= VideoCutter.this.i.getRightProgress()) {
                if (VideoCutter.this.j.isPlaying()) {
                    VideoCutter.this.j.pause();
                    VideoCutter.this.h.setBackgroundResource(R.drawable.play2);
                }
                VideoCutter.this.i.setSliceBlocked(false);
                VideoCutter.this.i.removeVideoStatusThumb();
                return;
            }
            postDelayed(this.c, 50);
        }
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.video_cutter_activity);
        fFmpeg = FFmpeg.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText("Video Cutter");
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (k || supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(k);
            supportActionBar.setDisplayShowTitleEnabled(false);
            AppContext = this;
            this.h = (ImageView) findViewById(R.id.buttonply1);
            this.i = (VideoSliceSeekBar) findViewById(R.id.seek_bar1);
            this.f = (TextView) findViewById(R.id.Filename);
            this.d = (TextView) findViewById(R.id.left_pointer);
            this.e = (TextView) findViewById(R.id.right_pointer);
            this.g = (TextView) findViewById(R.id.dur);
            this.j = (VideoView) findViewById(R.id.videoView1);
            this.h.setOnClickListener(this);
            this.m = getIntent().getStringExtra("path");
            if (this.m == null) {
                finish();
            }
            g();
            this.f.setText(new File(this.m).getName());
            this.j.setVideoPath(this.m);
            this.j.seekTo(100);
            e();
            this.j.setOnCompletionListener(mediaPlayer -> VideoCutter.this.h.setBackgroundResource(R.drawable.play2));
            AdsManager.loadInterstitialAd(this);
            return;
        }
        throw new AssertionError();
    }


    public void c() {
        Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
        intent.setFlags(67108864);
        intent.putExtra("song", this.n);
        startActivity(intent);
        finish();
    }

    private void d() {
        String valueOf = String.valueOf(this.c);
        String.valueOf(this.b);
        String valueOf2 = String.valueOf(this.b - this.c);
        String format = new SimpleDateFormat("_HHmmss", Locale.US).format(new Date());

        String sb2 = getExternalMediaDirs()[0].toString() +
                "/" +
                getResources().getString(R.string.MainFolderName) +
                "/" +
                getResources().getString(R.string.VideoCutter) ;

        File file = new File(sb2);
        if (!file.exists()) {
            file.mkdirs();
        }


        this.n = sb2 +
                "/videocutter" +
                format +
                ".mp4";
        String[] cmd = new String[]{"-ss", valueOf, "-y", "-i", this.m, "-t", valueOf2, "-vcodec", "mpeg4", "-b:v", "2097152", "-b:a", "48000", "-ac", "2", "-ar", "22050", this.n};

        a(cmd, this.n);
    }

    private void a(String[] strArr, final String str) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            a_api_24_plus(strArr,str);
        } else{
            a_api_24_lower(strArr,str);
        }
    }


    private void a_api_24_plus(String[] strArr, final String str) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        //getContentResolver().openOutputStream(Uri.parse(str))


        com.arthenica.mobileffmpeg.FFmpeg.executeAsync(strArr, (executionId1, returnCode) -> {
            if (returnCode == RETURN_CODE_SUCCESS) {
                Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                intent.setData(Uri.fromFile(new File(str)));
                VideoCutter.this.sendBroadcast(intent);
                Log.i(Config.TAG, "Async command execution completed successfully.");
            } else if (returnCode == RETURN_CODE_CANCEL) {
                try {
                    new File(str).delete();
                    VideoCutter.this.deleteFromGallery(str);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                Log.i(Config.TAG, "Async command execution cancelled by user.");
            } else {
                try {
                    new File(str).delete();
                    VideoCutter.this.deleteFromGallery(str);
                } catch (Throwable th) {
                    th.printStackTrace();
                }
                Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
            }
            runOnUiThread(progressDialog::dismiss);
            VideoCutter.this.refreshGallery(str);
        });

        Config.enableStatisticsCallback(statistics -> {
            String sb = "progress : " +
                    statistics.toString();
            runOnUiThread(() -> progressDialog.setMessage(sb));
        });



    }

    private void a_api_24_lower(String[] strArr, final String str){
        Log.d("cmd", Arrays.toString(strArr));
        try {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.show();
            this.fFmpeg.execute(strArr, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String str) {
                    Log.d("ffmpegfailure", str);
                    try {
                        new File(str).delete();
                        VideoCutter.this.deleteFromGallery(str);
                        Toast.makeText(VideoCutter.this, "Error Creating Video", 0).show();
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(String str) {
                    progressDialog.dismiss();
                    Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
                    intent.setData(Uri.fromFile(new File(VideoCutter.this.n)));
                    VideoCutter.this.sendBroadcast(intent);

                }

                @Override
                public void onProgress(String str) {
                    Log.d("ffmpegResponse", str);
                    StringBuilder sb = new StringBuilder();
                    sb.append("progress : ");
                    sb.append(str);
                    progressDialog.setMessage(sb.toString());
                }

                @Override
                public void onStart() {
                    progressDialog.setMessage("Processing...");
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
                    VideoCutter.this.refreshGallery(str);
                }
            });
            getWindow().clearFlags(16);
        } catch (FFmpegCommandAlreadyRunningException unused) {
        }
    }


    private void g() {
        try {
            this.fFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    VideoCutter.this.h();
                    Log.d("ffmpeg loading failed! ", "");
                }

                @Override
                public void onFinish() {
                    Log.d("ffmpeg loading finish! ", "");
                }

                @Override
                public void onStart() {
                    Log.d("ffmpeg loading started!", "");
                }

                @Override
                public void onSuccess() {
                    Log.d("ffmpeg loading success!", "");
                }
            });
        } catch (FFmpegNotSupportedException unused) {
            h();
        }
    }

    private void e() {
        this.j.setOnPreparedListener(mediaPlayer -> {
            VideoCutter.this.i.setSeekBarChangeListener(new SeekBarChangeListener() {
                public void SeekBarValueChanged(int i, int i2) {
                    if (VideoCutter.this.i.getSelectedThumb() == 1) {
                        VideoCutter.this.j.seekTo(VideoCutter.this.i.getLeftProgress());
                    }
                    VideoCutter.this.d.setText(VideoJoinerActivity.formatTimeUnit((long) i));
                    VideoCutter.this.e.setText(VideoJoinerActivity.formatTimeUnit((long) i2));
                    VideoCutter.this.o.setStart(i);
                    VideoCutter.this.o.setStop(i2);
                    VideoCutter.this.c = i / 1000;
                    VideoCutter.this.b = i2 / 1000;
                    TextView textView = VideoCutter.this.g;
                    StringBuilder sb = new StringBuilder();
                    sb.append("duration : ");
                    sb.append(String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf((VideoCutter.this.b - VideoCutter.this.c) / 3600), Integer.valueOf(((VideoCutter.this.b - VideoCutter.this.c) % 3600) / 60), Integer.valueOf((VideoCutter.this.b - VideoCutter.this.c) % 60)}));
                    textView.setText(sb.toString());
                }
            });
            VideoCutter.this.i.setMaxValue(mediaPlayer.getDuration());
            VideoCutter.this.i.setLeftProgress(0);
            VideoCutter.this.i.setRightProgress(mediaPlayer.getDuration());
            VideoCutter.this.i.setProgressMinDiff(0);
        });
    }

    private void f() {
        if (this.j.isPlaying()) {
            this.j.pause();
            this.i.setSliceBlocked(false);
            this.h.setBackgroundResource(R.drawable.play2);
            this.i.removeVideoStatusThumb();
            return;
        }
        this.j.seekTo(this.i.getLeftProgress());
        this.j.start();
        this.i.videoPlayingProgress(this.i.getLeftProgress());
        this.h.setBackgroundResource(R.drawable.pause2);
        this.p.a();
    }

    @Override
    public void onClick(View view) {
        if (view == this.h) {
            f();
        }
    }

    public void onMediaScannerConnected() {
        this.a.scanFile(this.l, "video/*");
    }

    public void onScanCompleted(String str, Uri uri) {
        this.a.disconnect();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    public void h() {
        new AlertDialog.Builder(this).setTitle("Device not supported").setMessage("FFmpeg is not supported on your device").setCancelable(false).setPositiveButton(R.string.alert_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                VideoCutter.this.finish();
            }
        }).create().show();
    }

    public void deleteFromGallery(String str) {
        String[] strArr = {"_id"};
        String[] strArr2 = {str};
        Uri uri = Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor query = contentResolver.query(uri, strArr, "_data = ?", strArr2, null);
        if (query.moveToFirst()) {
            try {
                contentResolver.delete(ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, query.getLong(query.getColumnIndexOrThrow("_id"))), null, null);
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
            }
        } else {
            try {
                new File(str).delete();
                refreshGallery(str);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
        query.close();
    }

    public void refreshGallery(String str) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        intent.setData(Uri.fromFile(new File(str)));
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ListVideoAndMyAlbumActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_picker, menu);
        return k;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            onBackPressed();
            return k;
        }
        if (menuItem.getItemId() == R.id.Done) {
            if (this.j.isPlaying()) {
                this.j.pause();
                this.h.setBackgroundResource(R.drawable.play2);
            }
            d();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
