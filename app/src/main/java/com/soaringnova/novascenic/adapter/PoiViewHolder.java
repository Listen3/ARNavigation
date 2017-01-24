package com.soaringnova.novascenic.adapter;

import android.view.View;
import android.widget.TextView;

import com.soaringnova.novascenic.R;
import com.soaringnova.novascenic.base.RecyclerHolder;

/**
 * Created by Be on 2017/1/24.
 */

public class PoiViewHolder extends RecyclerHolder {
    TextView poiName;
    TextView poiDistance;
    TextView poiStar;
    public PoiViewHolder(View itemView) {
        super(itemView);
        poiName = (TextView) itemView.findViewById(R.id.itemPoiName);
        poiDistance = (TextView) itemView.findViewById(R.id.itemPoiDistance);
        poiStar = (TextView) itemView.findViewById(R.id.itemPoiStar);
    }
}
