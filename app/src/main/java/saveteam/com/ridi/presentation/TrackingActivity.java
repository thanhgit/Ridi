package saveteam.com.ridi.presentation;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridi.R;
import saveteam.com.ridi.firebase.model.BookingFB;
import saveteam.com.ridi.firebase.model.ConfirmFB;
import saveteam.com.ridi.firebase.model.ProfileFB;
import saveteam.com.ridi.firebase.model.TrackingFB;
import saveteam.com.ridi.model.Geo;
import saveteam.com.ridi.utils.activity.ActivityUtils;
import saveteam.com.ridi.utils.activity.DateTimeUtils;
import saveteam.com.ridi.utils.activity.LocationUtils;
import saveteam.com.ridi.utils.activity.MapUtils;
import saveteam.com.ridi.utils.activity.NumberUtils;
import saveteam.com.ridi.utils.activity.SharedRefUtils;
import saveteam.com.ridi.utils.google.S2Utils;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    public enum TRIP_STATUS {
        START,
        PROCESSING,
        DONE
    }
    @BindView(R.id.toolbar_where_tracking)
    Toolbar toolbar;

    /**
     * Bottom
     */
    @BindView(R.id.tv_from_place_where_tracking)
    TextView tv_from_place;
    @BindView(R.id.tv_to_place_where_tracking)
    TextView tv_to_place;
    @BindView(R.id.tv_from_time_where_tracking)
    TextView tv_from_time;
    @BindView(R.id.tv_to_time_where_tracking)
    TextView tv_to_time;
    @BindView(R.id.tv_cash_where_tracking)
    TextView tv_cash;
    @BindView(R.id.btn_status_where_tracking)
    AppCompatButton btn_status;

    private GoogleMap mMap;

    private LocationUtils locationUtils;

    private String uid = "";

    private boolean isRider = false;

    private ConfirmFB confirmFB;
    private BookingFB bookingFB;
    private ProfileFB profileFB;

    private Marker offerMarker;
    private Marker findMarker;

    private TRIP_STATUS trip_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        ButterKnife.bind(this);

        uid = SharedRefUtils.getUid(this);
        trip_status = TRIP_STATUS.START;

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_where_tracking);
        mapFragment.getMapAsync(this);

        locationUtils = new LocationUtils(this, new LocationUtils.GpsListener() {
            @Override
            public void onLocationChanged(Location loc) {
                Geo geo = new Geo(loc.getLatitude(), loc.getLongitude(), S2Utils.getCellId(loc.getLatitude(), loc.getLongitude()).id());
                PutTrackingTask putTrackingTask = new PutTrackingTask(uid, geo, new PutTrackingTask.PutTrackingListener() {
                    @Override
                    public void success(Geo _lastGeo) {

                        if (isRider) {
                            if (findMarker != null) {
                                offerMarker.setPosition(new LatLng(_lastGeo.lat, _lastGeo.lng));
                            }
                        } else {
                            if (offerMarker != null) {
                                offerMarker.setPosition(new LatLng(_lastGeo.lat, _lastGeo.lng));
                            }
                        }

                        ActivityUtils.displayToast(TrackingActivity.this, "Tracking success");
                    }

                    @Override
                    public void fail() {
                        ActivityUtils.displayToast(TrackingActivity.this, "Tracking fail");
                    }
                });
                putTrackingTask.execute();

            }

            @Override
            public void onFail() {
                ActivityUtils.displayToast(TrackingActivity.this, "You haven't opened GPS yet");
            }
        });
        locationUtils.Gps();

        Intent intent = getIntent();
        confirmFB = (ConfirmFB) intent.getSerializableExtra("confirm");
        bookingFB = (BookingFB) intent.getSerializableExtra("booking");
        profileFB = (ProfileFB) intent.getSerializableExtra("profile");

        if (confirmFB != null) {
            isRider = false;

            tv_from_place.setText(confirmFB.getOfferRideFromPlace());
            tv_to_place.setText(confirmFB.getOfferRideToPlace());
            tv_cash.setText(NumberUtils.formatMoney(confirmFB.getCost())+" thousand VND");
            tv_from_time.setText(confirmFB.getOfferRideFromTime());
            tv_to_time.setText(confirmFB.getOfferRideToTime());
            getSupportActionBar().setTitle(confirmFB.getDistance()+" km");
        }

        if (bookingFB != null) {
            isRider = true;
            tv_from_place.setText(bookingFB.getOfferRideFromPlace());
            tv_to_place.setText(bookingFB.getOfferRideToPlace());
            tv_cash.setText(NumberUtils.formatMoney(bookingFB.getCost())+" thousand VND");
            tv_from_time.setText(DateTimeUtils.getShortDate(bookingFB.getOfferRideFromTime()));
            tv_to_time.setText(DateTimeUtils.getShortDate(bookingFB.getOfferRideToTime()));
            getSupportActionBar().setTitle(bookingFB.getDistance()+" km");
        }
    }

    @OnClick(R.id.btn_status_where_tracking)
    public void clickStatus(View view) {
        if (trip_status == TRIP_STATUS.START) {
            btn_status.setText("Processing");
            btn_status.setTextColor(getResources().getColor(R.color.red));
            trip_status = TRIP_STATUS.PROCESSING;
        } else if (trip_status == TRIP_STATUS.PROCESSING) {
            ActivityUtils.displayToast(this, "Ok, If your place in gps into finished place, It will done");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng here = new LatLng(10.8659698,106.8107944);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 13));

        if (isRider) {
            createRoute(MapUtils.convertFrom(Geo.toParse(bookingFB.getFindRideFromPlace())),
                    MapUtils.convertFrom(Geo.toParse(bookingFB.getFindRideToPlace())),
                    Color.argb(100, 255, 0, 0), false);

            createRoute(MapUtils.convertFrom(Geo.toParse(bookingFB.getOfferRideFromPlace())),
                    MapUtils.convertFrom(Geo.toParse(bookingFB.getOfferRideToPlace())),
                    Color.argb(100, 0, 255, 0), true);
        } else {
            createRoute(MapUtils.convertFrom(Geo.toParse(confirmFB.getFindRideFromPlace())),
                    MapUtils.convertFrom(Geo.toParse(confirmFB.getFindRideToPlace())),
                    Color.argb(100, 255, 0, 0), false);

            createRoute(MapUtils.convertFrom(Geo.toParse(confirmFB.getOfferRideFromPlace())),
                    MapUtils.convertFrom(Geo.toParse(confirmFB.getOfferRideToPlace())),
                    Color.argb(100, 0, 255, 0), true);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tracking_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.btn_sos_where_tracking_menu:
                ActivityUtils.displayToast(this, "Don't worry! I will report for the police and you will secure");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void createRoute(final LatLng start, final LatLng end, final int color, final boolean query) {
        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                .from(start)
                .to(end)
                .transitMode(TransportMode.BICYCLING)
                .alternativeRoute(true)
                .avoid(AvoidType.HIGHWAYS)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            if (query) {
                                if (isRider) {
                                    findMarker = mMap.addMarker(new MarkerOptions().position(start).title(bookingFB.getFindRideName()).snippet("I am here").icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));
                                } else {
                                    findMarker = mMap.addMarker(new MarkerOptions().position(start).title(confirmFB.getFindRideName()).snippet("I am here").icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));
                                    findMarker.showInfoWindow();
                                }
                                mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.to_place)));
                            } else {
                                mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.goal)));
                                if (isRider) {
                                    offerMarker = mMap.addMarker(new MarkerOptions().position(start).title(bookingFB.getOfferRideName()).snippet("I am going to your place").icon(BitmapDescriptorFactory.fromResource(R.drawable.offerride)));
                                    offerMarker.showInfoWindow();
                                } else {
                                    offerMarker = mMap.addMarker(new MarkerOptions().position(start).title(confirmFB.getOfferRideName()).snippet("I am going to your place").icon(BitmapDescriptorFactory.fromResource(R.drawable.offerride)));
                                }

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

    private static class PutTrackingTask extends AsyncTask<Void, Void, Void> {
        private String uid;
        private Geo lastGeo;
        private PutTrackingListener listener;

        public interface PutTrackingListener {
            void success(Geo geo);
            void fail();
        }

        public PutTrackingTask(String uid, Geo lastGeo, PutTrackingListener listener) {
            this.uid = uid;
            this.lastGeo = lastGeo;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(TrackingFB.DB_IN_FB);
            dbRef.child(this.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TrackingFB trackingFB = dataSnapshot.getValue(TrackingFB.class);
                    if (trackingFB != null && trackingFB.getUid() != null) {
                        trackingFB.getPaths().add(lastGeo);
                    } else {
                        List<Geo> paths = new ArrayList<>();
                        paths.add(lastGeo);
                        trackingFB = new TrackingFB(uid, paths);
                    }

                    dbRef.child(uid).setValue(trackingFB).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                listener.success(lastGeo);
                            } else {
                                listener.fail();
                            }
                        }
                    });
                    }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.fail();
                }
            });
            return null;
        }
    }
}
