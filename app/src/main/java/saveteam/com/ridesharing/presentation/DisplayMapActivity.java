package saveteam.com.ridesharing.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

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
import com.google.android.gms.maps.model.MarkerOptions;
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
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.FirebaseUtils;
import saveteam.com.ridesharing.firebase.model.ConfirmFB;
import saveteam.com.ridesharing.firebase.model.ConfirmListFB;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.firebase.model.TripFB;
import saveteam.com.ridesharing.presentation.home.MainActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.DataManager;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class DisplayMapActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.btn_put_trip_where_display_map)
    AppCompatButton btn_put_trip;

    TripFB trip;
    TripFB tripSearch;

    String uid = "";
    ProfileFB profile;

    private GoogleMap mMap;

    private ProgressDialog progressDialog;

    private ConfirmListFB confirmList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);

        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading ...");
        progressDialog.show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_where_display_map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        trip = (TripFB) intent.getSerializableExtra("data");
        tripSearch = (TripFB) intent.getSerializableExtra("tripSearch");

        uid = SharedRefUtils.getUid(this);

        StartTask startTask = new StartTask(this, trip.getUid(), new StartTask.GetProfileListener() {
            @Override
            public void get(ProfileFB profileFB, ConfirmListFB confirmListFB) {
                profile = profileFB;
                confirmList = confirmListFB;
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void fail() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        startTask.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        createRoute(ActivityUtils.convertFrom(trip.getGeoStart()), ActivityUtils.convertFrom(trip.getGeoEnd()), Color.argb(100, 255, 0, 0), false);;
        createRoute(ActivityUtils.convertFrom(tripSearch.getGeoStart()), ActivityUtils.convertFrom(tripSearch.getGeoEnd()), Color.argb(100, 0, 255, 0), true);;
    }

    @OnClick(R.id.btn_put_trip_where_display_map)
    public void clickPutTrip(View view) {
        progressDialog.show();
        ProfileFB myProfile = DataManager.getInstance().getProfile();
        ProfileFB profileMatching = DataManager.getInstance().getProfileMatching();

        ConfirmFB confirmFB = new ConfirmFB(profileMatching.getUid(),
                myProfile.getUid(),myProfile.getFirstName() +" "+ myProfile.getLastName(),
                profileMatching.getUid(),profileMatching.getFirstName() + " " + profileMatching.getLastName(),
                DataManager.getInstance().getDistance(), DataManager.getInstance().getCost(),
                "nothing", "nothing",
                "nothing", "nothing",
                "nothing");

        if (confirmList == null) {
            List<ConfirmFB> _confirms = new ArrayList<>();
            List<ProfileFB> _profiles = new ArrayList<>();
            _confirms.add(confirmFB);
            _profiles.add(profileMatching);
            confirmList = new ConfirmListFB(profileMatching.getUid(),
                    profileMatching.getFirstName() +" " + profileMatching.getLastName(),
                    _confirms, _profiles);
        } else {
            confirmList.getConfirms().add(confirmFB);
            confirmList.getProfiles().add(profileMatching);
        }


        PutConfirmTask putConfirmTask = new PutConfirmTask(confirmList, new FirebaseUtils.PutConfirmListener() {
            @Override
            public void success() {
                ActivityUtils.displayToast(DisplayMapActivity.this, "sent confirm successfully");
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Intent intent = new Intent(DisplayMapActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            @Override
            public void fail() {
                ActivityUtils.displayToast(DisplayMapActivity.this, "sent confirm fail");
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        putConfirmTask.execute();
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
                                mMap.addMarker(new MarkerOptions().position(start).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_start)));
                                mMap.addMarker(new MarkerOptions().position(end).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_end)));
                            }


//                            for (int i = 0; i < direction.getRouteList().size(); i++) {
                                Route route = direction.getRouteList().get(0);
                                ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                mMap.addPolyline(DirectionConverter.createPolyline(DisplayMapActivity.this, directionPositionList, 5, color));
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

    static class StartTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private String uid;
        private GetProfileListener listener;

        public interface GetProfileListener {
            void get(ProfileFB profileFB, ConfirmListFB confirmListFB);
            void fail();
        }

        public StartTask(Context context, String uid, GetProfileListener listener) {
            this.context = context;
            this.uid = uid;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRefTrips = FirebaseDatabase.getInstance().getReference(ProfileFB.DB_IN_FB);
            final DatabaseReference dbRefConfirms = FirebaseDatabase.getInstance().getReference(ConfirmListFB.DB_IN_FB);
            dbRefTrips.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final ProfileFB profileFB = dataSnapshot.getValue(ProfileFB.class);
                    if (profileFB != null) {
                        dbRefConfirms.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ConfirmListFB confirmListFB = dataSnapshot.getValue(ConfirmListFB.class);
                                if (confirmListFB != null) {
                                    listener.get(profileFB, confirmListFB);
                                }

                                listener.fail();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                listener.fail();
                            }
                        });

                    } else {
                        listener.fail();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.fail();
                }
            });
            return null;
        }
    }

    static class PutConfirmTask extends AsyncTask<Void, Void, Void> {
        private ConfirmListFB confirm;
        private FirebaseUtils.PutConfirmListener listener;

        public PutConfirmTask(ConfirmListFB confirm, FirebaseUtils.PutConfirmListener listener) {
            this.confirm = confirm;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseUtils.putConfirm(confirm, listener);
            return null;
        }
    }


}
