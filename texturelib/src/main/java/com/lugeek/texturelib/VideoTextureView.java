package com.lugeek.texturelib;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.MediaController;

import java.io.IOException;
import java.util.Map;

public class VideoTextureView extends TextureView implements MediaController.MediaPlayerControl {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PREPARING = 1;
    /* access modifiers changed from: private */
    public String TAG;
    private int fixedHeight;
    private int fixedWidth;
    private int mAudioSession;
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener;
    /* access modifiers changed from: private */
    public boolean mCanPause;
    /* access modifiers changed from: private */
    public boolean mCanSeekBack;
    /* access modifiers changed from: private */
    public boolean mCanSeekForward;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    /* access modifiers changed from: private */
    public int mCurrentBufferPercentage;
    /* access modifiers changed from: private */
    public int mCurrentState;
    private MediaPlayer.OnErrorListener mErrorListener;
    private Map<String, String> mHeaders;
    private MediaPlayer.OnInfoListener mInfoListener;
    /* access modifiers changed from: private */
    public MediaController mMediaController;
    /* access modifiers changed from: private */
    public MediaPlayer mMediaPlayer;
    /* access modifiers changed from: private */
    public MediaPlayer.OnCompletionListener mOnCompletionListener;
    /* access modifiers changed from: private */
    public MediaPlayer.OnErrorListener mOnErrorListener;
    /* access modifiers changed from: private */
    public MediaPlayer.OnInfoListener mOnInfoListener;
    /* access modifiers changed from: private */
    public MediaPlayer.OnPreparedListener mOnPreparedListener;
    MediaPlayer.OnPreparedListener mPreparedListener;
    /* access modifiers changed from: private */
    public int mSeekWhenPrepared;
    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener;
    /* access modifiers changed from: private */
    public Surface mSurface;
    TextureView.SurfaceTextureListener mSurfaceTextureListener;
    /* access modifiers changed from: private */
    public int mTargetState;
    private Uri mUri;
    /* access modifiers changed from: private */
    public int mVideoHeight;
    /* access modifiers changed from: private */
    public int mVideoWidth;
    private Matrix matrix;
    private boolean requestAudioFocus;

    public VideoTextureView(Context context) {
        super(context);
        this.TAG = "VideoTextureView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurface = null;
        this.mMediaPlayer = null;
        this.requestAudioFocus = true;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
                int unused = VideoTextureView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                int unused2 = VideoTextureView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                if (VideoTextureView.this.mVideoWidth != 0 && VideoTextureView.this.mVideoHeight != 0) {
                    VideoTextureView.this.getSurfaceTexture().setDefaultBufferSize(VideoTextureView.this.mVideoWidth, VideoTextureView.this.mVideoHeight);
                    VideoTextureView.this.requestLayout();
                    VideoTextureView fixedTextureVideoView = VideoTextureView.this;
                    fixedTextureVideoView.transformVideo(fixedTextureVideoView.mVideoWidth, VideoTextureView.this.mVideoHeight);
                    Log.d(VideoTextureView.this.TAG, String.format("OnVideoSizeChangedListener, mVideoWidth=%d,mVideoHeight=%d", new Object[]{Integer.valueOf(VideoTextureView.this.mVideoWidth), Integer.valueOf(VideoTextureView.this.mVideoHeight)}));
                }
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                int unused = VideoTextureView.this.mCurrentState = 2;
                VideoTextureView fixedTextureVideoView = VideoTextureView.this;
                boolean unused2 = fixedTextureVideoView.mCanPause = fixedTextureVideoView.mCanSeekBack = fixedTextureVideoView.mCanSeekForward = true;
                if (VideoTextureView.this.mOnPreparedListener != null) {
                    VideoTextureView.this.mOnPreparedListener.onPrepared(VideoTextureView.this.mMediaPlayer);
                }
                if (VideoTextureView.this.mMediaController != null) {
                    VideoTextureView.this.mMediaController.setEnabled(true);
                }
                int unused3 = VideoTextureView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                int unused4 = VideoTextureView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                int access$1100 = VideoTextureView.this.mSeekWhenPrepared;
                if (access$1100 != 0) {
                    VideoTextureView.this.seekTo(access$1100);
                }
                if (VideoTextureView.this.mVideoWidth != 0 && VideoTextureView.this.mVideoHeight != 0) {
                    VideoTextureView.this.getSurfaceTexture().setDefaultBufferSize(VideoTextureView.this.mVideoWidth, VideoTextureView.this.mVideoHeight);
                    if (VideoTextureView.this.mTargetState == 3) {
                        VideoTextureView.this.start();
                        if (VideoTextureView.this.mMediaController != null) {
                            VideoTextureView.this.mMediaController.show();
                        }
                    } else if (VideoTextureView.this.isPlaying()) {
                    } else {
                        if ((access$1100 != 0 || VideoTextureView.this.getCurrentPosition() > 0) && VideoTextureView.this.mMediaController != null) {
                            VideoTextureView.this.mMediaController.show(0);
                        }
                    }
                } else if (VideoTextureView.this.mTargetState == 3) {
                    VideoTextureView.this.start();
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                int unused = VideoTextureView.this.mCurrentState = 5;
                int unused2 = VideoTextureView.this.mTargetState = 5;
                if (VideoTextureView.this.mMediaController != null) {
                    VideoTextureView.this.mMediaController.hide();
                }
                if (VideoTextureView.this.mOnCompletionListener != null) {
                    VideoTextureView.this.mOnCompletionListener.onCompletion(VideoTextureView.this.mMediaPlayer);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() {
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                if (VideoTextureView.this.mOnInfoListener == null) {
                    return true;
                }
                VideoTextureView.this.mOnInfoListener.onInfo(mediaPlayer, i, i2);
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                try {
                    String access$300 = VideoTextureView.this.TAG;
                    Log.d(access$300, "Error: " + i + "," + i2);
                    int unused = VideoTextureView.this.mCurrentState = -1;
                    int unused2 = VideoTextureView.this.mTargetState = -1;
                    if (VideoTextureView.this.mMediaController != null) {
                        VideoTextureView.this.mMediaController.hide();
                    }
                    if ((VideoTextureView.this.mOnErrorListener == null || !VideoTextureView.this.mOnErrorListener.onError(VideoTextureView.this.mMediaPlayer, i, i2)) && VideoTextureView.this.getWindowToken() != null) {
                        VideoTextureView.this.getContext().getResources();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                int unused = VideoTextureView.this.mCurrentBufferPercentage = i;
            }
        };
        this.mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                boolean z = true;
                boolean z2 = VideoTextureView.this.mTargetState == 3;
                if (i <= 0 || i2 <= 0) {
                    z = false;
                }
                if (VideoTextureView.this.mMediaPlayer != null && z2 && z) {
                    if (VideoTextureView.this.mSeekWhenPrepared != 0) {
                        VideoTextureView fixedTextureVideoView = VideoTextureView.this;
                        fixedTextureVideoView.seekTo(fixedTextureVideoView.mSeekWhenPrepared);
                    }
                    VideoTextureView.this.start();
                }
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                Surface unused = VideoTextureView.this.mSurface = new Surface(surfaceTexture);
                VideoTextureView.this.openVideo();
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (VideoTextureView.this.mSurface != null) {
                    VideoTextureView.this.mSurface.release();
                    Surface unused = VideoTextureView.this.mSurface = null;
                }
                if (VideoTextureView.this.mMediaController != null) {
                    VideoTextureView.this.mMediaController.hide();
                }
                VideoTextureView.this.release(true);
                return true;
            }
        };
        initVideoView();
    }

    public VideoTextureView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
        initVideoView();
    }

    public VideoTextureView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.TAG = "TextureVideoView";
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurface = null;
        this.mMediaPlayer = null;
        this.requestAudioFocus = true;
        this.mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
                int unused = VideoTextureView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                int unused2 = VideoTextureView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                if (VideoTextureView.this.mVideoWidth != 0 && VideoTextureView.this.mVideoHeight != 0) {
                    VideoTextureView.this.getSurfaceTexture().setDefaultBufferSize(VideoTextureView.this.mVideoWidth, VideoTextureView.this.mVideoHeight);
                    VideoTextureView.this.requestLayout();
                    VideoTextureView fixedTextureVideoView = VideoTextureView.this;
                    fixedTextureVideoView.transformVideo(fixedTextureVideoView.mVideoWidth, VideoTextureView.this.mVideoHeight);
                    Log.d(VideoTextureView.this.TAG, String.format("OnVideoSizeChangedListener, mVideoWidth=%d,mVideoHeight=%d", new Object[]{Integer.valueOf(VideoTextureView.this.mVideoWidth), Integer.valueOf(VideoTextureView.this.mVideoHeight)}));
                }
            }
        };
        this.mPreparedListener = new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
                int unused = VideoTextureView.this.mCurrentState = 2;
                VideoTextureView fixedTextureVideoView = VideoTextureView.this;
                boolean unused2 = fixedTextureVideoView.mCanPause = fixedTextureVideoView.mCanSeekBack = fixedTextureVideoView.mCanSeekForward = true;
                if (VideoTextureView.this.mOnPreparedListener != null) {
                    VideoTextureView.this.mOnPreparedListener.onPrepared(VideoTextureView.this.mMediaPlayer);
                }
                if (VideoTextureView.this.mMediaController != null) {
                    VideoTextureView.this.mMediaController.setEnabled(true);
                }
                int unused3 = VideoTextureView.this.mVideoWidth = mediaPlayer.getVideoWidth();
                int unused4 = VideoTextureView.this.mVideoHeight = mediaPlayer.getVideoHeight();
                int access$1100 = VideoTextureView.this.mSeekWhenPrepared;
                if (access$1100 != 0) {
                    VideoTextureView.this.seekTo(access$1100);
                }
                if (VideoTextureView.this.mVideoWidth != 0 && VideoTextureView.this.mVideoHeight != 0) {
                    VideoTextureView.this.getSurfaceTexture().setDefaultBufferSize(VideoTextureView.this.mVideoWidth, VideoTextureView.this.mVideoHeight);
                    if (VideoTextureView.this.mTargetState == 3) {
                        VideoTextureView.this.start();
                        if (VideoTextureView.this.mMediaController != null) {
                            VideoTextureView.this.mMediaController.show();
                        }
                    } else if (VideoTextureView.this.isPlaying()) {
                    } else {
                        if ((access$1100 != 0 || VideoTextureView.this.getCurrentPosition() > 0) && VideoTextureView.this.mMediaController != null) {
                            VideoTextureView.this.mMediaController.show(0);
                        }
                    }
                } else if (VideoTextureView.this.mTargetState == 3) {
                    VideoTextureView.this.start();
                }
            }
        };
        this.mCompletionListener = new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mediaPlayer) {
                int unused = VideoTextureView.this.mCurrentState = 5;
                int unused2 = VideoTextureView.this.mTargetState = 5;
                if (VideoTextureView.this.mMediaController != null) {
                    VideoTextureView.this.mMediaController.hide();
                }
                if (VideoTextureView.this.mOnCompletionListener != null) {
                    VideoTextureView.this.mOnCompletionListener.onCompletion(VideoTextureView.this.mMediaPlayer);
                }
            }
        };
        this.mInfoListener = new MediaPlayer.OnInfoListener() {
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
                if (VideoTextureView.this.mOnInfoListener == null) {
                    return true;
                }
                VideoTextureView.this.mOnInfoListener.onInfo(mediaPlayer, i, i2);
                return true;
            }
        };
        this.mErrorListener = new MediaPlayer.OnErrorListener() {
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                try {
                    String access$300 = VideoTextureView.this.TAG;
                    Log.d(access$300, "Error: " + i + "," + i2);
                    int unused = VideoTextureView.this.mCurrentState = -1;
                    int unused2 = VideoTextureView.this.mTargetState = -1;
                    if (VideoTextureView.this.mMediaController != null) {
                        VideoTextureView.this.mMediaController.hide();
                    }
                    if ((VideoTextureView.this.mOnErrorListener == null || !VideoTextureView.this.mOnErrorListener.onError(VideoTextureView.this.mMediaPlayer, i, i2)) && VideoTextureView.this.getWindowToken() != null) {
                        VideoTextureView.this.getContext().getResources();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                int unused = VideoTextureView.this.mCurrentBufferPercentage = i;
            }
        };
        this.mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                boolean z = true;
                boolean z2 = VideoTextureView.this.mTargetState == 3;
                if (i <= 0 || i2 <= 0) {
                    z = false;
                }
                if (VideoTextureView.this.mMediaPlayer != null && z2 && z) {
                    if (VideoTextureView.this.mSeekWhenPrepared != 0) {
                        VideoTextureView fixedTextureVideoView = VideoTextureView.this;
                        fixedTextureVideoView.seekTo(fixedTextureVideoView.mSeekWhenPrepared);
                    }
                    VideoTextureView.this.start();
                }
            }

            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                Surface unused = VideoTextureView.this.mSurface = new Surface(surfaceTexture);
                VideoTextureView.this.openVideo();
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (VideoTextureView.this.mSurface != null) {
                    VideoTextureView.this.mSurface.release();
                    Surface unused = VideoTextureView.this.mSurface = null;
                }
                if (VideoTextureView.this.mMediaController != null) {
                    VideoTextureView.this.mMediaController.hide();
                }
                VideoTextureView.this.release(true);
                return true;
            }
        };
        initVideoView();
    }

    public void setFixedSize(int i, int i2) {
        this.fixedHeight = i2;
        this.fixedWidth = i;
        String str = this.TAG;
        Log.d(str, "setFixedSize,width=" + i + "height=" + i2);
        requestLayout();
    }

    public int getVideoHeight() {
        return this.mVideoHeight;
    }

    public int getVideoWidth() {
        return this.mVideoWidth;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int i4 = this.fixedWidth;
        if (i4 == 0 || (i3 = this.fixedHeight) == 0) {
            defaultMeasure(i, i2);
        } else {
            setMeasuredDimension(i4, i3);
        }
        Log.d(this.TAG, String.format("onMeasure, fixedWidth=%d,fixedHeight=%d", new Object[]{Integer.valueOf(this.fixedWidth), Integer.valueOf(this.fixedHeight)}));
    }

    /* access modifiers changed from: protected */
    public void defaultMeasure(int i, int i2) {
        int i3;
        int i4 = 0;
        int defaultSize = getDefaultSize(this.mVideoWidth, i);
        int defaultSize2 = getDefaultSize(this.mVideoHeight, i2);
        if (this.mVideoWidth > 0 && this.mVideoHeight > 0) {
            int mode = View.MeasureSpec.getMode(i);
            i3 = View.MeasureSpec.getSize(i);
            int mode2 = View.MeasureSpec.getMode(i2);
            int size = View.MeasureSpec.getSize(i2);
            if (mode == 1073741824 && mode2 == 1073741824) {
                int i5 = this.mVideoWidth;
                int i6 = i5 * size;
                int i7 = this.mVideoHeight;
                if (i6 < i3 * i7) {
                    defaultSize = (i5 * size) / i7;
                    defaultSize2 = size;
                } else if (i5 * size > i3 * i7) {
                    i4 = (i7 * i3) / i5;
                    setMeasuredDimension(i3, i4);
                }
            } else if (mode == 1073741824) {
                int i8 = (this.mVideoHeight * i3) / this.mVideoWidth;
                if (mode2 != Integer.MIN_VALUE || i8 <= size) {
                    i4 = i8;
                    setMeasuredDimension(i3, i4);
                }
            } else if (mode2 == 1073741824) {
                int i9 = (this.mVideoWidth * size) / this.mVideoHeight;
                if (mode != Integer.MIN_VALUE || i9 <= i3) {
                    i3 = i9;
                }
            } else {
                int i10 = this.mVideoWidth;
                int i11 = this.mVideoHeight;
                if (mode2 != Integer.MIN_VALUE || i11 <= size) {
                    i4 = i11;
                } else {
                    i10 = (i10 * size) / i11;
                    i4 = size;
                }
                if (mode != Integer.MIN_VALUE || i10 <= i3) {
                    i3 = i10;
                } else {
                    i4 = (this.mVideoHeight * i3) / this.mVideoWidth;
                }
                setMeasuredDimension(i3, i4);
            }
            i4 = size;
            setMeasuredDimension(i3, i4);
        }
        i3 = defaultSize;
        setMeasuredDimension(i3, i4);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName(VideoTextureView.class.getName());
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(VideoTextureView.class.getName());
    }

    public int resolveAdjustedSize(int i, int i2) {
        return getDefaultSize(i, i2);
    }

    private void initVideoView() {
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        setSurfaceTextureListener(this.mSurfaceTextureListener);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        this.mCurrentState = 0;
        this.mTargetState = 0;
    }

    public void setVideoPath(String str) {
        setVideoURI(Uri.parse(str));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, (Map<String, String>) null);
    }

    public void setVideoURI(Uri uri, Map<String, String> map) {
        this.mUri = uri;
        this.mHeaders = map;
        this.mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            this.mTargetState = 0;
            AudioManager audioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener) null);
            }
        }
    }

    public void isRequestAudioFocus(boolean z) {
        this.requestAudioFocus = z;
    }

    /* access modifiers changed from: private */
    public void openVideo() {
        AudioManager audioManager;
        if (this.mUri != null && this.mSurface != null) {
            release(false);
            if (this.requestAudioFocus && (audioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE)) != null) {
                audioManager.requestAudioFocus((AudioManager.OnAudioFocusChangeListener) null, 3, 1);
            }
            try {
                this.mMediaPlayer = new MediaPlayer();
                if (this.mAudioSession != 0) {
                    this.mMediaPlayer.setAudioSessionId(this.mAudioSession);
                } else {
                    this.mAudioSession = this.mMediaPlayer.getAudioSessionId();
                }
                this.mMediaPlayer.setOnPreparedListener(this.mPreparedListener);
                this.mMediaPlayer.setOnVideoSizeChangedListener(this.mSizeChangedListener);
                this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
                this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
                this.mMediaPlayer.setOnInfoListener(this.mInfoListener);
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
                this.mCurrentBufferPercentage = 0;
                this.mMediaPlayer.setDataSource(getContext().getApplicationContext(), this.mUri, this.mHeaders);
                this.mMediaPlayer.setSurface(this.mSurface);
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.setScreenOnWhilePlaying(true);
                this.mMediaPlayer.prepareAsync();
                this.mCurrentState = 1;
                attachMediaController();
            } catch (IOException e) {
                String str = this.TAG;
                Log.w(str, "Unable to open content: " + this.mUri, e);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            } catch (IllegalArgumentException e2) {
                String str2 = this.TAG;
                Log.w(str2, "Unable to open content: " + this.mUri, e2);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            } catch (IllegalStateException e3) {
                e3.printStackTrace();
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        }
    }

    public void setMediaController(MediaController mediaController) {
        MediaController mediaController2 = this.mMediaController;
        if (mediaController2 != null) {
            mediaController2.hide();
        }
        this.mMediaController = mediaController;
        attachMediaController();
    }

    private void attachMediaController() {
        MediaController mediaController;
        if (this.mMediaPlayer != null && (mediaController = this.mMediaController) != null) {
            mediaController.setMediaPlayer(this);
            this.mMediaController.setAnchorView(getParent() instanceof View ? (View) getParent() : this);
            this.mMediaController.setEnabled(isInPlaybackState());
        }
    }

    /* access modifiers changed from: private */
    public void transformVideo(int i, int i2) {
        if (getResizedHeight() == 0 || getResizedWidth() == 0) {
            String str = this.TAG;
            Log.d(str, "transformVideo, getResizedHeight=" + getResizedHeight() + ",getResizedWidth=" + getResizedWidth());
            return;
        }
        float f = (float) i;
        float resizedWidth = ((float) getResizedWidth()) / f;
        float f2 = (float) i2;
        float resizedHeight = ((float) getResizedHeight()) / f2;
        String str2 = this.TAG;
        Log.d(str2, "transformVideo, sx=" + resizedWidth);
        String str3 = this.TAG;
        Log.d(str3, "transformVideo, sy=" + resizedHeight);
        float max = Math.max(resizedWidth, resizedHeight);
        Matrix matrix2 = this.matrix;
        if (matrix2 == null) {
            this.matrix = new Matrix();
        } else {
            matrix2.reset();
        }
        this.matrix.preTranslate((float) ((getResizedWidth() - i) / 2), (float) ((getResizedHeight() - i2) / 2));
        this.matrix.preScale(f / ((float) getResizedWidth()), f2 / ((float) getResizedHeight()));
        this.matrix.postScale(max, max, (float) (getResizedWidth() / 2), (float) (getResizedHeight() / 2));
        String str4 = this.TAG;
        Log.d(str4, "transformVideo, maxScale=" + max);
        setTransform(this.matrix);
        postInvalidate();
        String str5 = this.TAG;
        Log.d(str5, "transformVideo, videoWidth=" + i + ",videoHeight=" + i2);
    }

    public int getResizedWidth() {
        int i = this.fixedWidth;
        return i == 0 ? getWidth() : i;
    }

    public int getResizedHeight() {
        int i = this.fixedHeight;
        return i == 0 ? getHeight() : i;
    }

    public void setOnPreparedListener(MediaPlayer.OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.mOnCompletionListener = onCompletionListener;
    }

    public void setOnErrorListener(MediaPlayer.OnErrorListener onErrorListener) {
        this.mOnErrorListener = onErrorListener;
    }

    public void setOnInfoListener(MediaPlayer.OnInfoListener onInfoListener) {
        this.mOnInfoListener = onInfoListener;
    }

    /* access modifiers changed from: private */
    public void release(boolean z) {
        MediaPlayer mediaPlayer = this.mMediaPlayer;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                this.mMediaPlayer.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            if (z) {
                this.mTargetState = 0;
            }
            AudioManager audioManager = (AudioManager) getContext().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                audioManager.abandonAudioFocus((AudioManager.OnAudioFocusChangeListener) null);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return super.onTouchEvent(motionEvent);
    }

    public boolean onTrackballEvent(MotionEvent motionEvent) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return super.onTrackballEvent(motionEvent);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        boolean z = (i == 4 || i == 24 || i == 25 || i == 164 || i == 82 || i == 5 || i == 6) ? false : true;
        if (isInPlaybackState() && z && this.mMediaController != null) {
            if (i == 79 || i == 85) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                } else {
                    start();
                    this.mMediaController.hide();
                }
                return true;
            } else if (i == 126) {
                if (!this.mMediaPlayer.isPlaying()) {
                    start();
                    this.mMediaController.hide();
                }
                return true;
            } else if (i == 86 || i == 127) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                }
                return true;
            } else {
                toggleMediaControlsVisiblity();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    private void toggleMediaControlsVisiblity() {
        if (this.mMediaController.isShowing()) {
            this.mMediaController.hide();
        } else {
            this.mMediaController.show();
        }
    }

    public void start() {
        if (isInPlaybackState()) {
            this.mMediaPlayer.start();
            this.mCurrentState = 3;
        }
        this.mTargetState = 3;
    }

    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            this.mCurrentState = 4;
        }
        this.mTargetState = 4;
    }

    public void suspend() {
        release(false);
    }

    public void resume() {
        openVideo();
    }

    public int getDuration() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getDuration();
        }
        return -1;
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int i) {
        if (isInPlaybackState()) {
            this.mMediaPlayer.seekTo(i);
            this.mSeekWhenPrepared = 0;
            return;
        }
        this.mSeekWhenPrepared = i;
    }

    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        if (this.mMediaPlayer != null) {
            return this.mCurrentBufferPercentage;
        }
        return 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0005, code lost:
        r0 = r3.mCurrentState;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isInPlaybackState() {
        /*
            r3 = this;
            android.media.MediaPlayer r0 = r3.mMediaPlayer
            r1 = 1
            if (r0 == 0) goto L_0x000f
            int r0 = r3.mCurrentState
            r2 = -1
            if (r0 == r2) goto L_0x000f
            if (r0 == 0) goto L_0x000f
            if (r0 == r1) goto L_0x000f
            goto L_0x0010
        L_0x000f:
            r1 = 0
        L_0x0010:
            return r1
        */
        boolean state = true;
        if (mMediaPlayer == null) {
            return false;
        }
        if (mCurrentState == 0 || mCurrentState == -1 || mCurrentState == 1) {
            state = false;
        }
        return state;
    }

    public boolean canPause() {
        return this.mCanPause;
    }

    public boolean canSeekBackward() {
        return this.mCanSeekBack;
    }

    public boolean canSeekForward() {
        return this.mCanSeekForward;
    }

    public int getAudioSessionId() {
        if (this.mAudioSession == 0) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            this.mAudioSession = mediaPlayer.getAudioSessionId();
            mediaPlayer.release();
        }
        return this.mAudioSession;
    }
}