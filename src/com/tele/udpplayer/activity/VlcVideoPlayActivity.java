package com.tele.udpplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import com.tele.udpplayer.utils.ConnectIP;
import com.tele.udpplayer.utils.Constans;
import com.tele.udpplayer.utils.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import com.tele.udpplayer.R;

public class VlcVideoPlayActivity extends Activity implements IVLCVout.OnNewVideoLayoutListener, View.OnTouchListener, View.OnClickListener{

    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_SCREEN = 1;
    private static final int SURFACE_FILL = 2;
    private static final int SURFACE_16_9 = 3;
    private static final int SURFACE_4_3 = 4;
    private static final int SURFACE_ORIGINAL = 5;
    private static int CURRENT_SIZE = SURFACE_16_9;

    private FrameLayout mVideoSurfaceFrame = null;
    private SurfaceView mVideoSurface = null;

    private final Handler mHandler = new Handler();
    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private Media mMedia = null;

    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;
    private int mVideoSarNum = 0;
    private int mVideoSarDen = 0;

    private Socket mSocket = null;
    private OutputStream mWriter = null;

    final ArrayList<String> options = new ArrayList<>();
    private String mPath;//本地路径

    private ImageView iv_back;//返回键
    private TextView tv_content;//标题名称
    private RelativeLayout title_layout;//标题栏

//    private boolean isFullStatus = false;
    public static boolean isDisplay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localvideoplayphone);
        isDisplay = true;
        //Constans.isPlayStart = true;
        setVlcOptions();
        mLibVLC = new LibVLC(this, options);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mMediaPlayer.setEventListener(mMediaPlayerListener);

        initView();
    }

    private void initView() {

        Intent intent = getIntent();
        mPath = intent.getStringExtra("path");
        LogUtils.i("VlcVideoPlay mUrl: " + mPath);
        if (TextUtils.isEmpty(mPath)) {
            Toast.makeText(this, R.string.uri_invalid_msg, Toast.LENGTH_SHORT).show();
        }

        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(R.string.video_tab_title);
        title_layout = (RelativeLayout) findViewById(R.id.base_title3);
        title_layout.setVisibility(View.VISIBLE);
        mVideoSurfaceFrame = (FrameLayout) findViewById(R.id.viewBox);
        mVideoSurface = (SurfaceView) findViewById(R.id.videoView);

        //注册在设置或播放过程中发生错误时调用的回调函数。如果未指定回调函数，或回调函数返回false，VideoView 会通知用户发生了错误。
        mVideoSurfaceFrame.setOnTouchListener(this);
        mVideoSurfaceFrame.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        LogUtils.i("vlc play onStart");
        onDrawView();
        super.onStart();
    }

    @Override
    protected void onResume() {
    	LogUtils.i("onResume");
        super.onResume();
    }

    private void onDrawView() {
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mVideoSurface);
        vlcVout.attachViews(this);
        LogUtils.i("onDrawView mPath=="+mPath);
        if (mPath.contains("1234")){
        	LogUtils.i("url ==UPD");
        	mMedia = new Media(mLibVLC, Uri.parse(mPath));
        //}else if(mPath.contains("Download")){
         // mMedia = new Media(mLibVLC, mPath);
        }else{
        	LogUtils.i("url ==location");
            mMedia = new Media(mLibVLC, mPath);
        }

        mMediaPlayer.setMedia(mMedia);
        mMediaPlayer.play();

        if (mOnLayoutChangeListener == null) {
            mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                private final Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
                        updateVideoSurfaces();
                    }
                };

                @Override
                public void onLayoutChange(View v, int left, int top, int right,
                                           int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.post(mRunnable);
                    }
                }
            };
        }
        mVideoSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    @Override
    protected void onDestroy() {
        LogUtils.i("VlcVideoPlay onDestroy");
        //关闭资源
        CloseVideo();
        if (mLibVLC != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mLibVLC.release();
            mLibVLC = null;
        }

        if (mMedia != null) {
            mMedia.release();
            mMedia = null;
        }
        closeSocket();
        isDisplay = false;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        LogUtils.i("vlc play onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtils.i("VLC onStop");
        if (mLibVLC != null) {
            mMediaPlayer.pause();
            mMediaPlayer.getVLCVout().detachViews();
        }
        //Constans.isPlayStart = false;
        super.onStop();
    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mVideoSarNum = sarNum;
        mVideoSarDen = sarDen;
        LogUtils.i("vlc play onNewVideoLayout");
        updateVideoSurfaces();
    }

    private final MediaPlayer.EventListener mMediaPlayerListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {
            switch (event.type) {
                case MediaPlayer.Event.TimeChanged:
                    //Log.d(TAG, "MediaPlayer.Event.TimeChanged");
                    //Log.d(TAG, "mMediaPlayer.getTime(): " + mMediaPlayer.getTime());
                    break;
                case MediaPlayer.Event.MediaChanged:
                    //Log.d(TAG, "MediaPlayer.Event.MediaChanged");
                    break;
                case MediaPlayer.Event.Opening:
                    //Log.d(TAG, "MediaPlayer.Event.Opening");
                    break;
                case MediaPlayer.Event.Playing:
                    //Log.d(TAG, "MediaPlayer.Event.Playing");
                    break;
                case MediaPlayer.Event.Paused:
                    LogUtils.i("MediaPlayer.Event.Paused");
                    break;
                case MediaPlayer.Event.Stopped:
                    //Log.d(TAG, "MediaPlayer.Event.Stopped");
                    break;
                case MediaPlayer.Event.Buffering:
                    //Log.d(TAG, "MediaPlayer.Event.Buffering");
                    break;
                case MediaPlayer.Event.EncounteredError:
                    LogUtils.e("EncounteredError error");
                    //Toast.makeText(VlcVideoPlay.this, "视频无法播放", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //Log.d(TAG, "onTouch");
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }

    //横屏
    private void porraitToLandscape() {
        int i = getResources().getConfiguration().orientation;
        if (i == Configuration.ORIENTATION_PORTRAIT) {
            ((Activity) VlcVideoPlayActivity.this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mVideoSurface == null) {
            return;
        }
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {//横屏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().invalidate();

        } else {
            //LogUtils.i("111111111111111111111111111111111");
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        LogUtils.i("zdd onBackPressed");
        //CloseVideo();
        finish();
    }

    private void setVlcOptions() {
        options.add("-vvvv");
        options.add("--network-caching=15000"); //网络缓存
        options.add("--sout-mux-caching=15000");
        //options.add("--clock-synchro=1");
        //options.add("--clock-jitter=-2147483647");
    }

    private void changeMediaPlayerLayout(int displayW, int displayH) {
        /* Change the video placement using the MediaPlayer API */
        switch (CURRENT_SIZE) {
            case SURFACE_BEST_FIT:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_FIT_SCREEN:
            case SURFACE_FILL: {
                Media.VideoTrack vtrack = mMediaPlayer.getCurrentVideoTrack();
                if (vtrack == null)
                    return;
                final boolean videoSwapped = vtrack.orientation == Media.VideoTrack.Orientation.LeftBottom
                        || vtrack.orientation == Media.VideoTrack.Orientation.RightTop;
                if (CURRENT_SIZE == SURFACE_FIT_SCREEN) {
                    int videoW = vtrack.width;
                    int videoH = vtrack.height;

                    if (videoSwapped) {
                        int swap = videoW;
                        videoW = videoH;
                        videoH = swap;
                    }
                    if (vtrack.sarNum != vtrack.sarDen)
                        videoW = videoW * vtrack.sarNum / vtrack.sarDen;

                    float ar = videoW / (float) videoH;
                    float dar = displayW / (float) displayH;

                    float scale;
                    if (dar >= ar)
                        scale = displayW / (float) videoW; /* horizontal */
                    else
                        scale = displayH / (float) videoH; /* vertical */
                    mMediaPlayer.setScale(scale);
                    mMediaPlayer.setAspectRatio(null);
                } else {
                    mMediaPlayer.setScale(0);
                    mMediaPlayer.setAspectRatio(!videoSwapped ? "" + displayW + ":" + displayH
                            : "" + displayH + ":" + displayW);
                }
                break;
            }
            case SURFACE_16_9:
                mMediaPlayer.setAspectRatio("16:9");
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_4_3:
                mMediaPlayer.setAspectRatio("4:3");
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_ORIGINAL:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(1);
                break;
        }
    }

    private void updateVideoSurfaces() {

        final boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        LogUtils.i("zdd VlcVideoPlayPhone isPortrait=="+isPortrait);
        int sw ,sh;
        if (isPortrait){
             sw = getWindow().getDecorView().getWidth();
             sh = getWindow().getDecorView().getWidth()*9/16;
        }else{
             sw = getWindow().getDecorView().getWidth();
             sh = getWindow().getDecorView().getHeight();
        }
        //LogUtils.i("zdd VlcVideoPlayPhone==sw="+sw+"=sh="+sh);

        // sanity check
        if (sw * sh == 0) {
            //Log.e(TAG, "Invalid surface size");
            return;
        }

        mMediaPlayer.getVLCVout().setWindowSize(sw, sh);

        ViewGroup.LayoutParams lp = mVideoSurface.getLayoutParams();
        if (mVideoWidth * mVideoHeight == 0) {
            /* Case of OpenGL vouts: handles the placement of the video using MediaPlayer API */
            //lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            //lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (isPortrait){
                lp.width = sw;
                lp.height = sh;
            }else{
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;;
            }
            mVideoSurface.setLayoutParams(lp);
            lp = mVideoSurfaceFrame.getLayoutParams();
            if (isPortrait){
                lp.width = sw;
                lp.height = sh;
            }else{
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;;
            }
            //lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            //lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoSurfaceFrame.setLayoutParams(lp);
            changeMediaPlayerLayout(sw, sh);
            return;
        }

        if (lp.width == lp.height && lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            /* We handle the placement of the video using Android View LayoutParams */
            mMediaPlayer.setAspectRatio(null);
            mMediaPlayer.setScale(0);
        }

        double dw = sw, dh = sh;

        if (sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mVideoSarDen == mVideoSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double) mVideoSarNum / mVideoSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (CURRENT_SIZE) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_SCREEN:
                if (dar >= ar)
                    dh = dw / ar; /* horizontal */
                else
                    dw = dh * ar; /* vertical */
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }

        // set display size
        lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        mVideoSurface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = mVideoSurfaceFrame.getLayoutParams();
        lp.width = (int) Math.floor(dw);
        lp.height = (int) Math.floor(dh);
        mVideoSurfaceFrame.setLayoutParams(lp);

        mVideoSurface.invalidate();
    }

    private void CloseVideo(){

        if (mLibVLC != null) {
            mMediaPlayer.stop();
            mMediaPlayer.getVLCVout().detachViews();
            mMediaPlayer.release();
            mLibVLC.release();
            mLibVLC = null;
        }

        if(mMedia != null ){
            mMedia.release();
        }

        closeSocket();
        //Constans.isPlayStart = true;

    }

    private void closeSocket() {
        try {
            if (mWriter != null) {
                mWriter.close();
                mWriter = null;
            }

            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}