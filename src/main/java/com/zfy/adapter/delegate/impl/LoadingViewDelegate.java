package com.zfy.adapter.delegate.impl;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.zfy.adapter.LightHolder;
import com.zfy.adapter.common.LightUtils;
import com.zfy.adapter.common.LightValues;
import com.zfy.adapter.listener.BindCallback;
import com.zfy.adapter.model.LightView;
import com.zfy.adapter.model.LoadingState;

import java.util.ArrayList;
import java.util.List;

/**
 * CreateAt : 2018/11/5
 * Describe :
 *
 * @author chendong
 */
public class LoadingViewDelegate extends BaseDelegate {

    private LoadingState mLoadingState; // 加载状态
    private ViewGroup mLoadingView; // 容器
    private LightHolder mLightHolder; // 当前 holder
    private BindCallback<LoadingState> mBindCallback; // 绑定回调
    private boolean mLoadingEnable; // 是否支持 loadingView

    private List<Runnable> mPendingRunnableList = new ArrayList<>();

    @Override
    public int getKey() {
        return LOADING;
    }

    @Override
    public LightHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == LightValues.TYPE_LOADING) {
            mLightHolder = new LightHolder(mAdapter, viewType, mLoadingView);
            return mLightHolder;
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public boolean onBindViewHolder(LightHolder holder, int position) {
        if (mAdapter.getItemViewType(position) == LightValues.TYPE_LOADING) {
            return true;
        }
        return super.onBindViewHolder(holder, position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (!mPendingRunnableList.isEmpty()) {
            for (Runnable pendingRunnable : mPendingRunnableList) {
                pendingRunnable.run();
            }
            mPendingRunnableList.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int aboveItemCount = mAdapter.getDelegateRegistry().getAboveItemCount(LightValues.FLOW_LEVEL_LOADING);
        if (isLoadingEnable() && position == aboveItemCount) {
            return LightValues.TYPE_LOADING;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return isLoadingEnable() ? 1 : 0;
    }

    @Override
    public int getAboveItemCount(int level) {
        if (isLoadingEnable() && level > LightValues.FLOW_LEVEL_LOADING) {
            return 1;
        }
        return super.getAboveItemCount(level);
    }


    /**
     * 设置 LoadingView
     *
     * @param lightView LightView
     * @param callback  绑定回调
     */
    public void setLoadingView(LightView lightView, BindCallback<LoadingState> callback) {
        mBindCallback = callback;
        mLoadingState = LoadingState.from(LoadingState.INIT);
        Runnable runnable = () -> {
            lightView.inflate(mAdapter.getContext());
            boolean isNewLoading = false;
            if (mLoadingView == null) {
                mLoadingView = LightUtils.createWrapContentLinearContainerView(mAdapter.getContext(), mView);
                isNewLoading = true;
            }
            mLoadingView.addView(lightView.view);
            mLoadingEnable = true;
            if (isNewLoading && mLoadingView.getChildCount() == 1) {
                mAdapter.notifyItemInserted(mAdapter.getDelegateRegistry().getAboveItemCount(LightValues.FLOW_LEVEL_LOADING));
            }
            setLoadingState(LoadingState.INIT);
        };
        if (isAttached() && mView.getLayoutManager() != null) {
            runnable.run();
        } else {
            mPendingRunnableList.add(runnable);
        }
    }

    public void setLoadingEnable(boolean loadingEnable) {
        if (mLoadingView == null) {
            return;
        }
        if (mLoadingEnable == loadingEnable) {
            return;
        }
        mLoadingEnable = loadingEnable;
        if (mLoadingEnable) {
            mAdapter.notifyItem().insert(mAdapter.getDelegateRegistry().getAboveItemCount(LightValues.FLOW_LEVEL_LOADING));
        } else {
            mAdapter.notifyItem().remove(mAdapter.getDelegateRegistry().getAboveItemCount(LightValues.FLOW_LEVEL_LOADING));
        }
    }

    /**
     * @return loadingView 功能是否可用
     */
    public boolean isLoadingEnable() {
        return mLoadingEnable && mLoadingView != null;
    }

    /**
     * 设置 Loading 状态
     *
     * @param state 状态
     * @see LoadingState#FINISH
     * @see LoadingState#INIT
     * @see LoadingState#LOADING
     * @see LoadingState#NO_DATA
     */
    public void setLoadingState(int state) {
        if (mLoadingState == null) {
            return;
        }
        mLoadingState.state = state;
        if (mBindCallback != null && mLightHolder != null) {
            mBindCallback.bind(mLightHolder, LightValues.NONE, mLoadingState);
        }
    }
}
