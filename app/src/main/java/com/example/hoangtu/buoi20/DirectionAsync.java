package com.example.hoangtu.buoi20;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.util.ArrayList;

/**
 * Created by HoangTu on 25/10/2017.
 */

public class DirectionAsync extends AsyncTask<LatLng,Void,ArrayList<LatLng>> {
    public static final int WHAT_DIRECTION = 1;
    private Handler handler;

    public DirectionAsync(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected ArrayList<LatLng> doInBackground(LatLng... latLngs) {
        Direction direction = new Direction();
        Document document = direction.getDocument(latLngs[0],latLngs[1],Direction.MODE_DRIVING);
        return direction.getDirection(document);
    }

    @Override
    protected void onPostExecute(ArrayList<LatLng> latLngs) {
        super.onPostExecute(latLngs);
        Message message = new Message();
        message.what = WHAT_DIRECTION;
        message.obj=latLngs;
        handler.sendMessage(message);
    }
}
