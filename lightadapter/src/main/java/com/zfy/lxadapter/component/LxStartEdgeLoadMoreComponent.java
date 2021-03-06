package com.zfy.lxadapter.component;

import com.zfy.lxadapter.Lx;
import com.zfy.lxadapter.LxAdapter;
import com.zfy.lxadapter.LxList;
import com.zfy.lxadapter.listener.OnLoadMoreListener;

/**
 * CreateAt : 2019-09-01
 * Describe :
 *
 * @author chendong
 */
public class LxStartEdgeLoadMoreComponent extends LxLoadMoreComponent {

    public LxStartEdgeLoadMoreComponent(int startLoadMoreCount, OnLoadMoreListener loadMoreListener) {
        super(Lx.LoadMoreEdge.START, startLoadMoreCount, loadMoreListener);
    }

    public LxStartEdgeLoadMoreComponent(OnLoadMoreListener loadMoreListener) {
        super(Lx.LoadMoreEdge.START, DEFAULT_START_LOAD_COUNT, loadMoreListener);
    }


    @Override
    public void onAttachedToAdapter(LxAdapter lxAdapter) {
        super.onAttachedToAdapter(lxAdapter);
        LxList data = lxAdapter.getData();
        data.subscribe(Lx.Event.FINISH_START_EDGE_LOAD_MORE, (event, adapter, extra) -> {
            LxStartEdgeLoadMoreComponent startEdgeLoadMoreComponent = adapter.getComponent(LxStartEdgeLoadMoreComponent.class);
            if (startEdgeLoadMoreComponent != null) {
                startEdgeLoadMoreComponent.finishLoadMore();
            }
        });
        data.subscribe(Lx.Event.START_EDGE_LOAD_MORE_ENABLE, (event, adapter, extra) -> {
            if (!(extra instanceof Boolean)) {
                return;
            }
            LxStartEdgeLoadMoreComponent startEdgeLoadMoreComponent = adapter.getComponent(LxStartEdgeLoadMoreComponent.class);
            if (startEdgeLoadMoreComponent != null) {
                startEdgeLoadMoreComponent.setLoadMoreEnable((Boolean) extra);
            }
        });
    }
}
