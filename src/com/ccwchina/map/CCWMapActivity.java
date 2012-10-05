package com.ccwchina.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.ccwchina.R;
import com.ccwchina.common.CCWChinaConst;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
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
    Drawable myDrawable;
	MyMapOverlay myOverlay;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.map);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        myLocation = new MyLocationOverlay(this, mapView);
        myLocation.enableMyLocation();
        controller = mapView.getController();
        
        String seviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(seviceName);
        
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        myDrawable = this.getResources().getDrawable(R.drawable.ic_star);
        UpdateMapView(location);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
        
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.reportErr:
	        	sendEmail(CCWChinaConst.REPORT_ERROR_EMAIL, CCWChinaConst.REPORT_ERROR_EMAIL_TITLE, CCWChinaConst.REPORT_ERROR_EMAIL_CONTENT);
	            return true;
	        case R.id.sendEmail:
	        	sendEmail(CCWChinaConst.SEND_EMAIL, CCWChinaConst.SEND_EMAIL_TITLE, CCWChinaConst.SEND_EMAIL_CONTENT);
	        	return true;
	        case R.id.callCCW:
	        	Intent callIntent = new Intent(Intent.ACTION_CALL);
	            callIntent.setData(Uri.parse("tel:" + CCWChinaConst.PHONE_CALL_NUMBER));
	            startActivity(callIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void sendEmail(String to, String subject, String content) {
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        final PackageManager pm = getPackageManager();     
        @SuppressWarnings("static-access")
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, pm.MATCH_DEFAULT_ONLY);     
        ResolveInfo best = null;     
        for (final ResolveInfo info : matches) {
        	if (info.activityInfo.name.toLowerCase().contains("mail"))
            	best = info;
        }
        if (best != null) {
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name); 
            startActivity(emailIntent);
        }else {
        	Toast.makeText(CCWMapActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
	}
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(String provider) {
        	UpdateMapView(location);
        }
        public void onProviderDisabled(String provider) {
        	
        }
        public void onProviderEnavled(String probider) {
                
        }
        public void onStatusChanged(String provider,int status,Bundle extras) {
        	
        }
        @Override
        public void onLocationChanged(Location location) {
        
        }
        @Override
        public void onProviderEnabled(String provider) {
        
        }
    };
    
    private void UpdateMapView(Location location)
    {
    	if (location != null) {
			Double lat = location.getLatitude() * 1E6;
			Double lng = location.getLongitude() * 1E6;
			GeoPoint point = new GeoPoint(lat.intValue(), lng.intValue());
			controller.setCenter(point);
			controller.setZoom(12);
			
			myOverlay = new MyMapOverlay(myDrawable);
            myOverlay.setItem(point);
            mapView.getOverlays().add(myOverlay);
			
			controller.animateTo(point);
    	}
    }
    
    class MyMapOverlay extends ItemizedOverlay<OverlayItem> {
        private List<GeoPoint> mItems = new ArrayList<GeoPoint>(); 
        
        public MyMapOverlay(Drawable marker) {
              super(boundCenterBottom(marker));
        } 
   
        public void setItems(ArrayList<GeoPoint> items) {
              mItems = items;
              populate();
        } 
   
        public void setItem(GeoPoint item) {
              mItems.add(item);
              populate();
        } 
   
        @Override
        protected OverlayItem createItem(int i) {
             return new OverlayItem(mItems.get(i), null, null);
        } 
   
        @Override
        public int size() {
             return mItems.size();
        } 
   
//        @Override
//        protected boolean onTap(int i) {
//             Toast.makeText(CCWMapActivity.this,
//             "Current Position：\n "+ myLocation.getLatitude() + ", "+myLocation.getLongitude(),
//             Toast.LENGTH_SHORT).show();
//             return true;
//        }
    }
}
