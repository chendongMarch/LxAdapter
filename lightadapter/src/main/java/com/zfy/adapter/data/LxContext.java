package com.zfy.adapter.data;

import android.os.Bundle;

import com.zfy.adapter.LxVh;

import java.util.List;

/**
 * CreateAt : 2019-08-31
 * Describe :
 *
 * @author chendong
 */
public class LxContext {

    public Object       data;
    public LxModel      model;
    public int          position;
    public LxVh         holder;
    public int          viewType;
    public List<String> payloads;
    public Bundle       condition;


    public void clear() {
        if (condition != null) {
            condition.clear();
            condition = null;
        }
        if (payloads != null) {
            payloads.clear();
            payloads = null;
        }
    }
}
