package com.example.hoangtu.buoi20;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private GoogleMap mMap;// đối tượng map
    private WindownAdapter adapter;
    private Location mLocation;
    private Marker mMarker;
    private Geocoder geocoder;

    private EditText edtBegin;
    private EditText edtEnd;
    private Button btnFind;

    private ProgressDialog dialog;

    private Polyline polylineWay;
    private Marker markerBegin;
    private Marker markerEnd;


    private static final String[] PERMISSION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION

    };
    public static final int GRANTED = PackageManager.PERMISSION_GRANTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //dialog.show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initDiaLog();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);// load map ra
    }

    private void initDiaLog() {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading........");
        dialog.setCancelable(false);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            for (String p : PERMISSION){
                if (checkSelfPermission(p)!=GRANTED){
                    requestPermissions(PERMISSION,0);
                    return;
                }
            }
        }
        // vẽ vị trí
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        initMap();
        initViews();
        dialog.dismiss();
    }

    private void initViews() {
        edtBegin = findViewById(R.id.edtBegin);
        edtEnd = findViewById(R.id.edtEnd);
        btnFind = findViewById(R.id.btnFind);
        btnFind.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int resule : grantResults){
            if (resule!=GRANTED){
                finish();
                return;
            }
        }
        initMap();

    }

    private void initMap() {
        geocoder = new Geocoder(this);

        adapter = new WindownAdapter(this);
        mMap.setInfoWindowAdapter(adapter);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);// vẽ ra vị trí hiện tại
        mMap.getUiSettings().setMyLocationButtonEnabled(true);// là nút bấm để hiện trí
        mMap.getUiSettings().setZoomControlsEnabled(true);// 2 button để phóng to và thu nhỏ
       // mMap.setMapStyle()
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,10,this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,10,this);

        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }
    private Marker draMarker(String title, String snipet, float hue, LatLng positon){
        MarkerOptions options = new MarkerOptions();
        options.title(title);
        options.snippet(snipet);
        options.position(positon);
        options.icon(BitmapDescriptorFactory.defaultMarker(hue));
        return mMap.addMarker(options);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        if (mMarker==null){
            mMarker=draMarker("My Location", "buu chinh", BitmapDescriptorFactory.HUE_RED,latLng);
            CameraPosition position = new CameraPosition(latLng,17,0,0);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
        else {
            mMarker.setPosition(latLng);
        }
        mMarker.setSnippet(getPositionFromLocation(latLng));


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    // lấy cái tên địa chỉ đường từ kinh độ và vĩ độ
    private String getPositionFromLocation(LatLng latLng){
        String positionName ="";
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList.size() == 1){
                positionName = addressList.get(0).getAddressLine(0);
                positionName +=" - "+addressList.get(0).getAddressLine(1);
                positionName +=" - "+addressList.get(0).getAddressLine(2);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return positionName;
    }
    private LatLng getLocationFromName(String name){
        LatLng latLng = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(name,1);
            if (addresses.size()==1){
                latLng = new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());// lấy ra kinh độ và vĩ độ hiện tại
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return latLng;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        draMarkerClick(latLng);
    }

    private void draMarkerClick(LatLng latLng) {
        String positionName = getPositionFromLocation(latLng);
        draMarker("Position Click",positionName,BitmapDescriptorFactory.HUE_BLUE,latLng);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        draMarkerClick(latLng);
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        String positionName = marker.getSnippet();
        Toast.makeText(this,positionName,Toast.LENGTH_LONG).show();
        return false;
    }
    @Override
    public void onClick(View view) {
        String begin = edtBegin.getText().toString();
        String end = edtEnd.getText().toString();
        if (begin.isEmpty()||end.isEmpty()){
            Toast.makeText(this,"chưa nhập vị trí",Toast.LENGTH_LONG).show();
            return;
        }
        LatLng lnBengin = getLocationFromName(begin);
        LatLng lnEnd = getLocationFromName(end);
        // nếu tồn ko tồn tại toast lên
        if (lnBengin==null||lnEnd==null){
            Toast.makeText(this,"vị trí không tồn tại",Toast.LENGTH_LONG).show();
            return;
        }
        dialog.show();
        // làm việc lấy diuwx liệu về
        DirectionAsync async = new DirectionAsync(handler);
        async.execute(lnBengin,lnEnd);
        // vẽ nó ra
//        PolylineOptions options = new PolylineOptions();// vẽ đường thẳng giữa 2 đường với nhau.
//        options.width(10);
//        options.color(Color.GREEN);
//        options.add(lnBengin,lnEnd);
//        mMap.addPolyline(options);
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DirectionAsync.WHAT_DIRECTION){

                if (polylineWay!=null){
                    polylineWay.remove();
                    markerBegin.remove();
                    markerEnd.remove();
                }

                ArrayList<LatLng> arr = (ArrayList<LatLng>) msg.obj;
                PolylineOptions options = new PolylineOptions();// vẽ đường thẳng giữa 2 đường với nhau.
                options.width(10);
                options.color(Color.BLACK);
                options.addAll(arr);
                polylineWay=mMap.addPolyline(options);
                dialog.dismiss();

                LatLng beign = arr.get(0);
                LatLng end = arr.get(arr.size()-1);
                String nameBegin = getPositionFromLocation(beign);
                String nameEnd = getPositionFromLocation(end);

                markerBegin=draMarker("Begin",nameBegin,BitmapDescriptorFactory.HUE_BLUE,beign);
                markerEnd=draMarker("End",nameEnd,BitmapDescriptorFactory.HUE_RED,end);

            }
        }
    };
}
