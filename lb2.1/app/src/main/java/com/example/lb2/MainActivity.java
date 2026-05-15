package com.example.lb2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private MapView mapView;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker locationMarker;
    private boolean locationUpdatesStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Обязательно до setContentView для osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Начальная точка — Москва
        mapView.getController().setZoom(12.0);
        mapView.getController().setCenter(new GeoPoint(55.7558, 37.6176));

        if (!hasLocationPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST);
        } else {
            startLocationUpdates();
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationUpdates() {
        if (!hasLocationPermission() || locationUpdatesStarted) {
            return;
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationListener == null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    showLocationOnMap(location);
                }
            };
        }

        Location lastLocation = getLastKnownLocation();
        if (lastLocation != null) {
            showLocationOnMap(lastLocation);
        }

        boolean hasFineLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocation = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (hasFineLocation) {
            locationUpdatesStarted |= requestLocationUpdates(LocationManager.GPS_PROVIDER);
        }
        if (hasFineLocation || hasCoarseLocation) {
            locationUpdatesStarted |= requestLocationUpdates(LocationManager.NETWORK_PROVIDER);
        }
    }

    private Location getLastKnownLocation() {
        Location bestLocation = null;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            bestLocation = getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (hasLocationPermission()) {
            Location networkLocation = getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (bestLocation == null
                    || (networkLocation != null
                    && networkLocation.getTime() > bestLocation.getTime())) {
                bestLocation = networkLocation;
            }
        }

        return bestLocation;
    }

    private Location getLastKnownLocation(String provider) {
        try {
            return locationManager.getLastKnownLocation(provider);
        } catch (IllegalArgumentException | SecurityException ignored) {
            return null;
        }
    }

    private boolean requestLocationUpdates(String provider) {
        try {
            if (locationManager.isProviderEnabled(provider)) {
                locationManager.requestLocationUpdates(provider, 3000, 5, locationListener);
                return true;
            }
        } catch (IllegalArgumentException | SecurityException ignored) {
            return false;
        }

        return false;
    }

    private void showLocationOnMap(@NonNull Location location) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

        mapView.getController().animateTo(geoPoint);
        mapView.getController().setZoom(15.0);

        if (locationMarker == null) {
            locationMarker = new Marker(mapView);
            locationMarker.setTitle("I am here");
            locationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(locationMarker);
        }

        locationMarker.setPosition(geoPoint);
        mapView.invalidate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && hasLocationPermission()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException ignored) {
                // Permission can be revoked while the app is in the background.
            }
        }
        locationUpdatesStarted = false;
    }
}
