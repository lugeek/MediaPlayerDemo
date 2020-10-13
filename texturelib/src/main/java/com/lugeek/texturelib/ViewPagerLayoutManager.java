package com.lugeek.texturelib;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ViewPagerLayoutManager extends LinearLayoutManager {
    private static final String TAG = "ViewPagerLayoutM anager";

    public interface ViewPagerListener {
        void onInitComplete();

        void onPageRelease(boolean z, int i);

        void onPageSelected(int i, boolean z);
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        public void onChildViewAttachedToWindow(View view) {
            if (ViewPagerLayoutManager.this.mOnViewPagerListener != null && ViewPagerLayoutManager.this.getChildCount() == 1) {
                ViewPagerLayoutManager.this.mOnViewPagerListener.onInitComplete();
            }
        }

        public void onChildViewDetachedFromWindow(View view) {
            if (ViewPagerLayoutManager.this.mDrift >= 0) {
                if (ViewPagerLayoutManager.this.mOnViewPagerListener != null) {
                    ViewPagerLayoutManager.this.mOnViewPagerListener.onPageRelease(true, ViewPagerLayoutManager.this.getPosition(view));
                }
            } else if (ViewPagerLayoutManager.this.mOnViewPagerListener != null) {
                ViewPagerLayoutManager.this.mOnViewPagerListener.onPageRelease(false, ViewPagerLayoutManager.this.getPosition(view));
            }
        }
    };
    /* access modifiers changed from: private */
    public int mDrift;
    /* access modifiers changed from: private */
    public ViewPagerListener mOnViewPagerListener;
    private PagerSnapHelper mPagerSnapHelper;
    private RecyclerView mRecyclerView;

    public ViewPagerLayoutManager(Context context, int i) {
        super(context, i, false);
        init();
    }

    public ViewPagerLayoutManager(Context context, int i, boolean z) {
        super(context, i, z);
        init();
    }

    private void init() {
        this.mPagerSnapHelper = new PagerSnapHelper();
    }

    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        this.mPagerSnapHelper.attachToRecyclerView(recyclerView);
        this.mRecyclerView = recyclerView;
        this.mRecyclerView.addOnChildAttachStateChangeListener(this.mChildAttachStateChangeListener);
        this.mRecyclerView.setOverScrollMode(0);
    }

    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
    }

    public void onScrollStateChanged(int i) {
        boolean z = true;
        if (i == 0) {
            View findSnapView = this.mPagerSnapHelper.findSnapView(this);
            if (findSnapView != null) {
                int position = getPosition(findSnapView);
                if (this.mOnViewPagerListener != null && getChildCount() == 1) {
                    ViewPagerListener viewPagerListener = this.mOnViewPagerListener;
                    if (position != getItemCount() - 1) {
                        z = false;
                    }
                    viewPagerListener.onPageSelected(position, z);
                }
            }
        } else if (i == 1) {
            getPosition(this.mPagerSnapHelper.findSnapView(this));
        } else if (i == 2) {
            getPosition(this.mPagerSnapHelper.findSnapView(this));
        }
    }

    public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = i;
        return super.scrollVerticallyBy(i, recycler, state);
    }

    public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = i;
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    public void setOnViewPagerListener(ViewPagerListener viewPagerListener) {
        this.mOnViewPagerListener = viewPagerListener;
    }
}