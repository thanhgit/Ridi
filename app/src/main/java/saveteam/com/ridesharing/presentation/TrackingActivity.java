package saveteam.com.ridesharing.presentation;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.model.TripFB;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.toolbar_where_tracking)
    Toolbar toolbar;

    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("20 km, 1h30");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_where_tracking);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng here = new LatLng(10.8659698,106.8107944);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 13));

        TripFB trip = new TripFB();
        trip.setGeoStart(new Geo(10.8659104,106.8007542,0));
        trip.setGeoStart(new Geo(10.7792249,106.6969578, 0));
        TripFB tripSearch = new TripFB();
        tripSearch.setGeoStart(new Geo(10.8659104,106.8007542,0));
        tripSearch.setGeoStart(new Geo(10.7792249,106.6969578, 0));

        createRoute(ActivityUtils.convertFrom(new Geo(10.8806575,106.8097814,0)),
                ActivityUtils.convertFrom(new Geo(10.7792249,106.6969578, 0)),
                Color.argb(100, 255, 0, 0), false);

        createRoute(ActivityUtils.convertFrom(new Geo(10.8659104,106.8007542,0)),
                ActivityUtils.convertFrom(new Geo(10.7792249,106.6969578, 0)),
                Color.argb(100, 0, 255, 0), true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void createRoute(final LatLng start, final LatLng end, final int color, final boolean query) {
        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                .from(start)
                .to(end)
                .transitMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .avoid(AvoidType.HIGHWAYS)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            if (query) {
                                mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));
                                mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.to_place)));
                            } else {
//                                mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)));
//                                mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_end)));
                                Marker marker = mMap.addMarker(new MarkerOptions().position(start).title("Thanh Nguyen").snippet("I am going to your place").icon(BitmapDescriptorFactory.fromResource(R.drawable.offerride)));
                                marker.showInfoWindow();
                            }


//                            for (int i = 0; i < direction.getRouteList().size(); i++) {
                            Route route = direction.getRouteList().get(0);
                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            mMap.addPolyline(DirectionConverter.createPolyline(TrackingActivity.this, directionPositionList, 5, color));
//                            }
                            setCameraWithCoordinationBounds(direction.getRouteList().get(0));
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }
}
