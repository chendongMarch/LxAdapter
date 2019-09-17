package com.zfy.adapter.helper;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zfy.adapter.LxAdapter;
import com.zfy.adapter.LxList;
import com.zfy.adapter.R;
import com.zfy.adapter.data.LxModel;

import java.util.List;

/**
 * CreateAt : 2019-09-17
 * Describe :
 * <p>
 * 嵌套滑动专用，记住上次滑动的位置，以便恢复状态
 * <p>
 * only support LinearLayoutManager
 *
 * @author chendong
 */
public class LxNesting {

    private static final String KEY_POS    = "KEY_POS";
    private static final String KEY_OFFSET = "KEY_OFFSET";

    public interface OnNoAdapterCallback {
        void set(RecyclerView view, LxList list);
    }

    public static void setup(RecyclerView view, LxModel model, List<LxModel> datas, OnNoAdapterCallback factory) {
        RecyclerView.Adapter adapter = view.getAdapter();
        LxNesting.backup(view, model);
        LxList data;
        if (adapter != null) {
            data = ((LxAdapter) adapter).getData();
        } else {
            data = new LxList();
            factory.set(view, data);
        }
        data.update(datas);
    }

    public static void backup(RecyclerView view, LxModel model) {
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return;
        }
        RecyclerView.OnScrollListener listener = (RecyclerView.OnScrollListener) view.getTag(R.id.tag_listener);
        if (listener != null) {
            view.removeOnScrollListener(listener);
        }
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    getPositionAndOffset(recyclerView, model);
                }
            }
        });
        scrollToPosition(view, model);
    }

    private static void getPositionAndOffset(RecyclerView view, LxModel model) {
        RecyclerView.LayoutManager layoutManager = view.getLayoutManager();
        // 获取可视的第一个view
        View topView = layoutManager.getChildAt(0);
        if (topView != null) {
            // 获取与该view的顶部的偏移量
            int lastOffset = LxUtil.getRecyclerViewOrientation(view) == RecyclerView.VERTICAL ? topView.getTop() : topView.getLeft();
            // 得到该View的数组位置
            int lastPosition = layoutManager.getPosition(topView);
            model.getExtra().putInt(KEY_OFFSET, lastOffset);
            model.getExtra().putInt(KEY_POS, lastPosition);
        }
    }

    private static void scrollToPosition(RecyclerView view, LxModel model) {
        LinearLayoutManager manager = (LinearLayoutManager) view.getLayoutManager();
        int offset = model.getExtra().getInt(KEY_OFFSET, 0);
        int pos = model.getExtra().getInt(KEY_POS, 0);
        manager.scrollToPositionWithOffset(pos, offset);
    }

}
