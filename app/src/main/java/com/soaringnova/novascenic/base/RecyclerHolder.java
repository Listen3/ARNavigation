package com.soaringnova.novascenic.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Be on 2017/1/24.
 */

public class RecyclerHolder extends RecyclerView.ViewHolder {
    public View rootView;

    public RecyclerHolder(View itemView) {
        super(itemView);
        rootView = itemView;
    }
}
