package com.ccwchina.map;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;

import com.ccwchina.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CCWMapActivity extends MapActivity {
	Location location;
	MapView mapView;
	LocationManager locationManager;
	MyLocationOverlay myLocation;
    MapController controller;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map);
        
        LocationManager locationManager;
        String seviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(seviceName);
        
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        
        String provider = locationManager.getBestProvider(criteria,true);
        location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        myLocation = new MyLocationOverlay(this, mapView);
        controller = mapView.getController();
        myLocation.enableMyLocation();
        
        List<Overlay> mapOverlays = mapView.getOverlays();
        mapOverlays.add(myLocation);
        Drawable drawable = this.getResources().getDrawable(R.drawable.ic_pin);
        KitchenItemizedOverlay itemizedoverlay = new KitchenItemizedOverlay(drawable, this);
        
        GeoPoint point = new GeoPoint(31208540, 121451177);
        OverlayItem overlayitem = new OverlayItem(point, "CCW Puxi Kitchen", "Room 108-109, No.2 Dong Ping Road");
        
        GeoPoint point2 = new GeoPoint(31256019, 121580550);
        OverlayItem overlayitem2 = new OverlayItem(point2, "CCW Pudong Kitchen", "Room 418. Building 1, No.3611 Zhang Yang Road");
        
        itemizedoverlay.addOverlay(overlayitem);
        itemizedoverlay.addOverlay(overlayitem2);
        mapOverlays.add(itemizedoverlay);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    private final LocationListener locationListener = new LocationListener(){
        public void onLocationChanged(String provider){
        	updateWithNewLocation(location);
        }
        public void onProviderDisabled(String provider){
        	updateWithNewLocation(null);
        }
        public void onProviderEnavled(String probider){
                
        }
        public void onStatusChanged(String provider,int status,Bundle extras){}
            @Override
            public void onLocationChanged(Location location) {
                    // TODO Auto-generated method stub
            }
            @Override
            public void onProviderEnabled(String provider) {
                    // TODO Auto-generated method stub
            }
    };
    
    private void updateWithNewLocation(Location location) {
        if (location != null){
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            controller.animateTo(new GeoPoint((int)(lat*1E6),(int)(lng*1E6)));
        }
    }
}
