package com.march.quickrvlibs.inter;

import com.march.quickrvlibs.RvViewHolder;

/**
 * CdLibsTest     com.march.libs.recyclerlibs
 * Created by 陈栋 on 16/2/4.
 * 功能:
 */
public  interface OnItemClickListener<D>  {
    void onItemClick(int pos, RvViewHolder holder,D data);
}
