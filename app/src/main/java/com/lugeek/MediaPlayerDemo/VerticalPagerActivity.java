package com.lugeek.MediaPlayerDemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.danikula.videocache.HttpProxyCacheServer;
import com.lugeek.texturelib.VideoTextureView;
import com.lugeek.texturelib.ViewPagerLayoutManager;

public class VerticalPagerActivity extends AppCompatActivity {

    private HttpProxyCacheServer proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_pager);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new ViewPagerLayoutManager(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new RecyclerView.Adapter<FullVideoViewHolder>() {
            @NonNull
            @Override
            public FullVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vertical_pager, parent, false);
                return new FullVideoViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull final FullVideoViewHolder holder, int position) {
                holder.videoView.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
                        holder.videoView.setFixedSize(width, height);
                        holder.videoView.invalidate();
                        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                holder.videoView.start();
                            }
                        });
                        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                holder.progressBar.setVisibility(View.GONE);
                                mediaPlayer.setVolume(0.0f, 0.0f);
                                holder.videoView.start();
                                mediaPlayer.setLooping(true);
                            }
                        });
                        HttpProxyCacheServer proxy = getProxy();
//                        String proxyUrl = proxy.getProxyUrl("https://file-examples-com.github.io/uploads/2017/04/file_example_MP4_640_3MG.mp4");
                        String proxyUrl = proxy.getProxyUrl("http://video.ltwebstatic.com/video/2020/10/12/16024833871113609231.mp4");
//                        holder.videoView.setVideoPath("http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8");
                        holder.videoView.setVideoPath(proxyUrl);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return 20;
            }

            @Override
            public void onViewAttachedToWindow(@NonNull FullVideoViewHolder holder) {
                super.onViewAttachedToWindow(holder);
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull FullVideoViewHolder holder) {
                super.onViewDetachedFromWindow(holder);
            }
        });
    }

    private HttpProxyCacheServer getProxy() {
        if (proxy == null) {
            proxy = new HttpProxyCacheServer(this.getApplicationContext());
        }
        return proxy;
    }

    public static class FullVideoViewHolder extends RecyclerView.ViewHolder {

        VideoTextureView videoView;
        ProgressBar progressBar;

        public FullVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

}