package saveteam.com.quagiang.presentation;

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
import saveteam.com.quagiang.R;
import saveteam.com.quagiang.firebase.model.BookingFB;
import saveteam.com.quagiang.firebase.model.BookingListFB;
import saveteam.com.quagiang.firebase.model.ConfirmFB;
import saveteam.com.quagiang.firebase.model.ConfirmListFB;
import saveteam.com.quagiang.firebase.model.ProfileFB;
import saveteam.com.quagiang.firebase.model.TripFB;
import saveteam.com.quagiang.presentation.home.MainActivity;
import saveteam.com.quagiang.utils.activity.ActivityUtils;
import saveteam.com.quagiang.utils.activity.DataManager;
import saveteam.com.quagiang.utils.activity.MapUtils;
import saveteam.com.quagiang.utils.activity.SharedRefUtils;

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
    private BookingListFB bookingList;

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

        uid = SharedRefUtils.getUid(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();
        trip = (TripFB) intent.getSerializableExtra("data");
        //tripSearch = (TripFB) intent.getSerializableExtra("tripSearch");
        tripSearch = DataManager.getInstance().getFindRideTrip();

        StartTask startTask = new StartTask(this, trip.getUid(), new StartTask.GetProfileListener() {
            @Override
            public void get(ProfileFB profileFB, ConfirmListFB confirmListFB, BookingListFB bookingListFB) {
                profile = profileFB;
                confirmList = confirmListFB;
                bookingList = bookingListFB;

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

        createRoute(MapUtils.convertFrom(trip.getGeoStart()), MapUtils.convertFrom(trip.getGeoEnd()), Color.argb(100, 255, 0, 0), false);;
        createRoute(MapUtils.convertFrom(tripSearch.getGeoStart()), MapUtils.convertFrom(tripSearch.getGeoEnd()), Color.argb(100, 0, 255, 0), true);
    }

    @OnClick(R.id.btn_put_trip_where_display_map)
    public void clickPutTrip(View view) {
        progressDialog.show();
        ProfileFB myProfile = DataManager.getInstance().getProfile();
        ProfileFB profileMatching = DataManager.getInstance().getProfileMatching();

        ConfirmFB confirmFB = new ConfirmFB(profileMatching.getUid(),
                myProfile.getUid(), myProfile.getFirstName().trim() + " " + myProfile.getLastName().trim(),
                trip.getGeoStart().toStr(), trip.getGeoEnd().toStr(),
                profileMatching.getUid(), profileMatching.getFirstName().trim() + " " + profileMatching.getLastName().trim(),
                tripSearch.getGeoStart().toStr(), tripSearch.getGeoEnd().toStr(),
                DataManager.getInstance().getDistance(), DataManager.getInstance().getCost());
        confirmFB.setOfferRideFromTime(trip.getGeoStart().time);
        confirmFB.setOfferRideToTime(trip.getGeoEnd().time);
        confirmFB.setFindRideFromTime(tripSearch.getGeoStart().time);
        confirmFB.setFindRideToTime(tripSearch.getGeoEnd().time);

        BookingFB bookingFB = new BookingFB(myProfile.getUid(),
                myProfile.getUid(), myProfile.getFirstName().trim() + " " + myProfile.getLastName().trim(),
                trip.getGeoStart().toStr(), trip.getGeoEnd().toStr(),
                profileMatching.getUid(), profileMatching.getFirstName().trim() + " " + profileMatching.getLastName().trim(),
                tripSearch.getGeoStart().toStr(), tripSearch.getGeoEnd().toStr(),
                DataManager.getInstance().getDistance(), DataManager.getInstance().getCost());
        bookingFB.setFindRideFromTime(tripSearch.getGeoStart().time);
        bookingFB.setFindRideToTime(tripSearch.getGeoEnd().time);
        bookingFB.setOfferRideFromTime(trip.getGeoStart().time);
        bookingFB.setOfferRideToTime(trip.getGeoEnd().time);

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

        if (bookingList == null) {
            List<BookingFB> _bookings = new ArrayList<>();
            List<ProfileFB> _profile = new ArrayList<>();
            _bookings.add(bookingFB);
            _profile.add(profileMatching);
            bookingList = new BookingListFB(myProfile.getUid(),
                    myProfile.getFirstName().trim() + " " + myProfile.getLastName(),
                    _bookings, _profile);
        } else {
            bookingList.getBookings().add(bookingFB);
            bookingList.getProfiles().add(profileMatching);
        }

        PutBookingTask putBookingTask = new PutBookingTask(bookingList, new PutBookingTask.PutBookingListener() {
            @Override
            public void success() {
                PutConfirmTask putConfirmTask = new PutConfirmTask(confirmList, new PutConfirmTask.PutConfirmListener() {
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

            @Override
            public void fail() {
                ActivityUtils.displayToast(DisplayMapActivity.this, "sent confirm fail");
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        putBookingTask.execute();
    }

    private void createRoute(LatLng _start, LatLng _end,int _color, final boolean query) {
        final LatLng start = _start;
        final LatLng end = _end;
        final int color = _color;
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
                                Marker startOfferMarker = mMap.addMarker(new MarkerOptions().position(start).title(trip.getUserName()).snippet("I am here").icon(BitmapDescriptorFactory.fromResource(R.drawable.offerride)));
                                startOfferMarker.showInfoWindow();
                                Marker endOfferMarker = mMap.addMarker(new MarkerOptions().position(end).title(trip.getUserName()).snippet("This is my destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.goal)));
                                //endOfferMarker.showInfoWindow();
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
            void get(ProfileFB profileFB, ConfirmListFB confirmListFB, BookingListFB bookingListFB);
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
            final DatabaseReference dbRefBookings = FirebaseDatabase.getInstance().getReference(BookingListFB.DB_IN_FB);

            dbRefTrips.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final ProfileFB profileFB = dataSnapshot.getValue(ProfileFB.class);
                    if (profileFB != null) {
                        dbRefConfirms.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final ConfirmListFB confirmListFB = dataSnapshot.getValue(ConfirmListFB.class);
                                if (confirmListFB != null) {
                                    dbRefBookings.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            BookingListFB bookingListFB = dataSnapshot.getValue(BookingListFB.class);
                                            if (bookingListFB != null) {
                                                listener.get(profileFB, confirmListFB, bookingListFB);
                                            }

                                            listener.fail();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            listener.fail();
                                        }
                                    });
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
        private ConfirmListFB confirmList;
        private PutConfirmListener listener;

        public interface PutConfirmListener {
            void success();
            void fail();
        }

        public PutConfirmTask(ConfirmListFB confirmList, PutConfirmListener listener) {
            this.confirmList = confirmList;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ConfirmListFB.DB_IN_FB);
            ref.child(confirmList.getUid()).setValue(confirmList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        listener.success();
                    } else {
                        listener.fail();
                    }
                }
            });
            return null;
        }
    }

    static class PutBookingTask extends AsyncTask<Void, Void, Void> {
        private BookingListFB bookingList;
        private PutBookingListener listener;

        public interface PutBookingListener {
            void success();
            void fail();
        }

        public PutBookingTask(BookingListFB bookingList, PutBookingListener listener) {
            this.bookingList = bookingList;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRefBooking = FirebaseDatabase.getInstance().getReference(BookingListFB.DB_IN_FB);
            dbRefBooking.child(bookingList.getUid()).setValue(bookingList)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                listener.success();
                            } else {
                                listener.fail();
                            }
                        }
                    });
            return null;
        }
    }
}
