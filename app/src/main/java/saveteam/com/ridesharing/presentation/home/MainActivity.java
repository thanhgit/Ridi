package saveteam.com.ridesharing.presentation.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.database.RidesharingDB;
import saveteam.com.ridesharing.database.model.Profile;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.firebase.FirebaseUtils;
import saveteam.com.ridesharing.firebase.model.TripFB;
import saveteam.com.ridesharing.logic.MatchingForSearch;
import saveteam.com.ridesharing.model.FindTripDTO;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.MatchingDTO;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.presentation.MatchingActivity;
import saveteam.com.ridesharing.presentation.SearchPlaceActivity;
import saveteam.com.ridesharing.presentation.chat.FriendActivity;
import saveteam.com.ridesharing.presentation.profile.ProfileActivity;
import saveteam.com.ridesharing.presentation.setting.SettingActivity;
import saveteam.com.ridesharing.server.model.MatchingResponseWithUser;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int START_POINT_ACTIVITY = 9000;
    private static final int END_POINT_ACTIVITY = 9001;

    public enum MODE_USER {
        FIND_RIDE,
        OFFER_RIDE
    }

    @BindView(R.id.rb_find_ride_where_main)
    RadioButton rb_find_ride;
    @BindView(R.id.rb_offer_ride_where_main)
    RadioButton rb_offer_ride;
    @BindView(R.id.btn_submit_where_main)
    AppCompatButton btn_submit;

    @BindView(R.id.btn_from_place_where_main)
    AppCompatButton btn_from_where;
    @BindView(R.id.btn_from_time_where_main)
    AppCompatButton btn_from_time;
    @BindView(R.id.btn_time_on_search_where_main)
    AppCompatButton btn_time_on_search;

    @BindView(R.id.btn_to_place_where_main)
    AppCompatButton btn_to_where;
    @BindView(R.id.btn_options_where_main)
    AppCompatButton btn_options;

    @BindView(R.id.bottom_sheet_where_main)
    LinearLayout bottom_sheet;

    boolean isQuit = false;


    /**
     * Drawer layout
     */
    @BindView(R.id.drawer_layout_where_main)
    DrawerLayout drawerLayout;
    @BindView(R.id.ibtn_menu_where_main)
    ImageButton ibtn_menu;
    @BindView(R.id.nav_view_where_main)
    NavigationView nav_view;

    BottomSheetBehavior bottomSheetBehavior;

    View nav_header;
    TextView tv_name;
    TextView tv_email;
    ImageView iv_profile;

    ProgressDialog dialog;
    String email = "";
    Profile profile;
    String uid = "";

    LatLng start_point;
    LatLng end_point;

    Trip tripSearch;
    List<Geo> geos;

    GoogleMap mMap;

    private static final String TAG = "Sample";

    private static final String FROM_TIME_DATETIME_FRAGMENT = "FROM_TIME_DATETIME_FRAGMENT";

    private SwitchDateTimeDialogFragment dateTimeFragment;
    private MODE_USER mode_user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        uid = SharedRefUtils.getUid(this);

        geos = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_where_main);
        mapFragment.getMapAsync(this);

        tripSearch = new Trip();

        nav_header = nav_view.getHeaderView(0);
        tv_name = nav_header.findViewById(R.id.tv_name_where_nav_header_main);
        tv_email = nav_header.findViewById(R.id.tv_email_where_nav_header_main);
        iv_profile = nav_header.findViewById(R.id.iv_profile_where_nav_header_main);
        tv_email.setText(SharedRefUtils.getEmail(this));

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.changeActivity(MainActivity.this, ProfileActivity.class);
            }
        });

        initApp();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng here = new LatLng(10.8659698,106.8107944);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 13));
    }

    private void initApp() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);

        final String uid = SharedRefUtils.getUid(this);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<Profile> profiles = RidesharingDB.getInstance(MainActivity.this)
                        .getProfileDao().loadProfileBy(uid);
                if (profiles != null && profiles.size() > 0) {
                    Profile profile = profiles.get(0);
                    tv_name.setText(profile.getFirstName() + " " + profile.getLastName());
                }
            }
        });

        FirebaseUtils.downloadImageFile(uid, new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Glide.with(MainActivity.this).load(bitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.5f)
                                .into(iv_profile);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        mode_user = MODE_USER.FIND_RIDE;
        changeMode(mode_user);

        initDateTimePicker();

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.logout_where_drawer_navigation_main_menu:
                        signout();
                        return true;
                    case R.id.nav_chat_where_drawer_navigation_main_menu:
                        ActivityUtils.changeActivity(MainActivity.this, FriendActivity.class);
                        return true;
                    case R.id.nav_notification_where_drawer_navigation_main_menu:
                        ActivityUtils.changeActivity(MainActivity.this, NotificationActivity.class);
                        return true;
                    case R.id.nav_setting_where_drawer_navigation_main_menu:
                        ActivityUtils.changeActivity(MainActivity.this, SettingActivity.class);
                        return true;
                    case R.id.nav_home_where_drawer_navigation_main_menu:
                        return true;
                    case R.id.nav_about_us_where_drawer_navigation_main_menu:
                        ActivityUtils.changeActivity(MainActivity.this, AboutUsActivity.class);
                        return true;
                    case R.id.nav_privacy_policy_where_drawer_navigation_main_menu:
                        ActivityUtils.changeActivity(MainActivity.this, PolicyActivity.class);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (start_point!=null && end_point!=null) {
            clickFromTime(btn_from_time);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isQuit) {
            ActivityUtils.displayToast(this, "Click 1 time again to quit");
            isQuit = true;
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }

    }

    private void initDateTimePicker() {
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(FROM_TIME_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }

        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        // Init format
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
        final SimpleDateFormat myTimeFormat = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2015, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                btn_from_time.setText(myDateFormat.format(date));
                btn_time_on_search.setText(myTimeFormat.format(date));
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
                btn_from_time.setText("");
            }
        });
    }

    @OnClick(R.id.iv_exchange_place_where_main)
    public void clickExchangePlace(View view) {
        ActivityUtils.displayToast(this, "change");

        LatLng tmp = start_point;
        start_point = end_point;
        end_point = tmp;

        String strTmp = btn_from_where.getText().toString();
        btn_from_where.setText(btn_to_where.getText().toString());
        btn_to_where.setText(strTmp);

        if (start_point != null && end_point != null) {
            createRoute();
        }
    }

    @OnClick(R.id.ibtn_menu_where_main)
    public void clickIBtnMenu(View view) {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    /**
     * button from
     */

    @OnClick({R.id.btn_from_time_where_main, R.id.btn_time_on_search_where_main})
    public void clickFromTime(View view) {
        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.setDefaultDateTime(Calendar.getInstance().getTime());
        dateTimeFragment.show(getSupportFragmentManager(), FROM_TIME_DATETIME_FRAGMENT);
    }

    @OnClick(R.id.btn_from_place_where_main)
    public void clickFromWhere(View view) {
        start_point = null;
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,START_POINT_ACTIVITY);
    }

    /**
     * button to
     */

    @OnClick(R.id.btn_options_where_main)
    public void clickToTime(View view) {
        ActivityUtils.displayToast(this, "This is default feature");
    }

    @OnClick(R.id.btn_to_place_where_main)
    public void clickToWhere(View view) {
        end_point = null;
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,END_POINT_ACTIVITY);
    }

    /**
     * button submit
     */
    @OnClick(R.id.btn_submit_where_main)
    public void clickSubmit(View view) {
        if (mode_user == MODE_USER.FIND_RIDE) {
            tripSearch.userName = SharedRefUtils.getEmail(MainActivity.this);
            tripSearch.startGeo = ActivityUtils.convertToGeo(start_point);
            tripSearch.endGeo = ActivityUtils.convertToGeo(end_point);
            tripSearch.path = new ArrayList<>();
            tripSearch.path.addAll(geos);

            Query query = new Query();
            query.key = tripSearch.userName;
            query.trip = tripSearch;

            dialog.show();

            MatchingForSearch matchingForSearch = new MatchingForSearch(0.5, query);
            matchingForSearch.matching(new Callback<MatchingResponseWithUser>() {
                @Override
                public void onResponse(Call<MatchingResponseWithUser> call, Response<MatchingResponseWithUser> response) {
                    MatchingResponseWithUser matchingResponse = response.body();
                    if (matchingResponse != null) {

                        List<MatchingDTO> matchingDTOS = new ArrayList<>();

                        for (int index = 0; index < matchingResponse.getUsers().size(); index++) {
                            matchingDTOS.add(new MatchingDTO(
                                    matchingResponse.getUsers().get(index),
                                    matchingResponse.getPercents().get(index)
                            ));
                        }

                        for (String user : matchingResponse.getUsers()) {
                            ActivityUtils.displayLog(user);
                        }

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        Intent intent = new Intent(MainActivity.this, MatchingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("matching", new FindTripDTO(tripSearch, matchingDTOS));
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<MatchingResponseWithUser> call, Throwable t) {
                    ActivityUtils.displayLog(t.getMessage());
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        } else if (mode_user == MODE_USER.OFFER_RIDE) {
            tripSearch.userName = SharedRefUtils.getEmail(MainActivity.this);
            tripSearch.startGeo = ActivityUtils.convertToGeo(start_point);
            tripSearch.endGeo = ActivityUtils.convertToGeo(end_point);
            tripSearch.path = new ArrayList<>();
            tripSearch.path.addAll(geos);

            TripFB tripFB = new TripFB();
            tripFB.setUid(uid);
            tripFB.setGeoStart(ActivityUtils.convertToGeo(start_point));
            tripFB.setGeoEnd(ActivityUtils.convertToGeo(end_point));
            tripFB.setPaths(geos);
            tripFB.setSize(geos.size());

            dialog.show();

            DatabaseReference dbref = FirebaseDB.getInstance().child(TripFB.DB_IN_FB);
            dbref.child(uid).setValue(tripFB).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        ActivityUtils.displayToast(MainActivity.this, "Add offer ride successfully");
                    } else {
                        ActivityUtils.displayToast(MainActivity.this, "Add offer ride fail");
                    }

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }


            });
        } else {
            ActivityUtils.displayToast(this, "Error");
        }
    }

    /**
     * change mode
     */

    @OnClick(R.id.rb_find_ride_where_main)
    public void clickFindRide() {
        changeMode(MODE_USER.FIND_RIDE);
    }

    @OnClick(R.id.rb_offer_ride_where_main)
    public void clickOfferRide() {
        changeMode(MODE_USER.OFFER_RIDE);
    }

    private void changeMode(MODE_USER mode_user) {
        if (mode_user == MODE_USER.FIND_RIDE) {
            rb_find_ride.setChecked(true);
            rb_offer_ride.setChecked(false);
            btn_submit.setText("Find Ride");
            btn_options.setText("1 slot");
            this.mode_user = MODE_USER.FIND_RIDE;
        } else {
            rb_find_ride.setChecked(false);
            rb_offer_ride.setChecked(true);
            btn_submit.setText("Offer Ride");
            btn_options.setText("Motor bike");
            this.mode_user= MODE_USER.OFFER_RIDE;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_POINT_ACTIVITY && resultCode == RESULT_OK) {
            Geo start = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");
            String placeId = data.getStringExtra("placeId");

            if (start != null) {
                if (title.equals("")) {
                    btn_from_where.setText(start.toString());
                } else {
                    btn_from_where.setText(title);
                }
                btn_from_where.setBackground(null);

                start_point = new LatLng(start.lat, start.lng);
                mMap.addMarker(new MarkerOptions().position(start_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));
            }
        }

        if (requestCode == END_POINT_ACTIVITY && resultCode == RESULT_OK ) {
            Geo end = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");
            String placeId = data.getStringExtra("placeId");

            if (end != null) {
                if (title.equals("")) {
                    btn_to_where.setText(end.toString());
                } else {
                    btn_to_where.setText(title);
                }
                btn_to_where.setBackground(null);

                end_point = new LatLng(end.lat, end.lng);
                mMap.addMarker(new MarkerOptions().position(end_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.to_place)));

            }
        }

        if (start_point!=null && end_point!= null) {
            createRoute();
        }

    }

    private void createRoute() {
        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                .from(start_point)
                .to(end_point)
                .transitMode(TransportMode.BICYCLING)
                .alternativeRoute(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(start_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));
                            mMap.addMarker(new MarkerOptions().position(end_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.to_place)));

//                                for (int i = 0; i < direction.getRouteList().size(); i++) {
                            Route route = direction.getRouteList().get(0);
                            int color = Color.argb(100, 255,0,0);
                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            geos.clear();
                            geos.addAll(ActivityUtils.convertToGeo(directionPositionList));
                            mMap.addPolyline(DirectionConverter.createPolyline(MainActivity.this, directionPositionList, 5, color));
//                                }
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

    public void signout() {
        MyGoogleAuthen.signOut(this, new MyGoogleAuthen.LogoutCompleteListener() {
            @Override
            public void done() {
                SharedRefUtils.saveEmail("", MainActivity.this);
                SharedRefUtils.saveUid("", MainActivity.this);

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
