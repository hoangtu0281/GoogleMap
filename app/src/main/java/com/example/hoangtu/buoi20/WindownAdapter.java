package com.example.hoangtu.buoi20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by HoangTu on 22/10/2017.
 */

public class WindownAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public WindownAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;// thay thế vào
    }

    @Override
    public View getInfoContents(Marker marker) {
        return getView(marker);
    }
    private View getView(Marker marker){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_view,null);
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvSninpet = view.findViewById(R.id.tvSnippit);
        tvTitle.setText(marker.getTitle());
        tvSninpet.setText(marker.getSnippet());
        return view;
    }
}
