package saveteam.com.ridesharing.presentation.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.database.RidesharingDB;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.firebase.FirebaseUtils;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.firebase.model.TripFB;
import saveteam.com.ridesharing.logic.MatchingForSearch;
import saveteam.com.ridesharing.model.FindTripDTO;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.MatchingDTO;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.presentation.MatchingActivity;
import saveteam.com.ridesharing.presentation.RideActivity;
import saveteam.com.ridesharing.presentation.SearchPlaceActivity;
import saveteam.com.ridesharing.presentation.chat.FriendActivity;
import saveteam.com.ridesharing.presentation.fragment.NoInternetFragment;
import saveteam.com.ridesharing.presentation.profile.ProfileActivity;
import saveteam.com.ridesharing.presentation.setting.SettingActivity;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.MatchingForSearchResponse;
import saveteam.com.ridesharing.server.model.searchplacewithtext.Result;
import saveteam.com.ridesharing.server.model.searchplacewithtext.SearchPlaceWithTextResponse;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.AnimationUtils;
import saveteam.com.ridesharing.utils.activity.DataManager;
import saveteam.com.ridesharing.utils.activity.DateTimePickerUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;
import saveteam.com.ridesharing.utils.google.S2Utils;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int START_POINT_ACTIVITY = 9000;
    private static final int END_POINT_ACTIVITY = 9001;
    private static final int COST_PER_KM = 5;

    public enum MODE_USER {
        FIND_RIDE,
        OFFER_RIDE
    }

    /**
     * Search box
     */
    @BindView(R.id.btn_from_place_where_main)
    AppCompatButton btn_from_where;
    @BindView(R.id.btn_from_time_where_main)
    AppCompatButton btn_from_time;
    @BindView(R.id.btn_time_on_search_where_main)
    AppCompatButton btn_time_on_search;
    @BindView(R.id.layout_seach_box_where_main)
    CardView layout_search_box;

    /**
     * Bottom sheet
     */
    @BindView(R.id.btn_to_place_where_main)
    AppCompatButton btn_to_where;
    @BindView(R.id.btn_options_where_main)
    AppCompatButton btn_options;
    @BindView(R.id.tv_cash_where_main)
    TextView tv_cash;
    @BindView(R.id.tv_distance_where_main)
    TextView tv_distance;
    @BindView(R.id.bottom_sheet_where_main)
    LinearLayout bottom_sheet;
    @BindView(R.id.btn_submit_where_main)
    AppCompatButton btn_submit;
    @BindView(R.id.layout_close_where_main)
    LinearLayout layout_close;
    @BindView(R.id.btn_mode_where_main)
    AppCompatButton btn_mode;
    @BindView(R.id.iv_mode_where_main)
    ImageView iv_mode;
    @BindView(R.id.tv_from_place_where_main)
    TextView tv_from_place;
    @BindView(R.id.tv_to_place_where_main)
    TextView tv_to_place;

    BottomSheetBehavior bottomSheetBehavior;

    /**
     * Bottom navigation
     */
    @BindView(R.id.tv_find_ride_where_main)
    TextView tv_find_ride;
    @BindView(R.id.tv_offer_ride_where_main)
    TextView tv_offer_ride;
//    @BindView(R.id.tv_ride_where_main)
//    TextView tv_ride;

    /**
     * Drawer layout
     */
    @BindView(R.id.drawer_layout_where_main)
    DrawerLayout drawerLayout;
    @BindView(R.id.ibtn_menu_where_main)
    ImageButton ibtn_menu;
    @BindView(R.id.nav_view_where_main)
    NavigationView nav_view;

    /**
     * Map
     */
    @BindView(R.id.iv_my_location_where_main)
    ImageView iv_my_location;
    @BindView(R.id.iv_forword_where_main)
    ImageView iv_forword;

    View nav_header;
    TextView tv_name;
    TextView tv_email;
    ImageView iv_profile;

    ProgressDialog dialog;
    String uid = "";

    LatLng start_point;
    LatLng end_point;

    TripFB tripSearch;
    List<Geo> geos;

    GoogleMap mMap;

    private MODE_USER mode_user;

    private double distance = 0;

    private DateTimePickerUtils startTime;

    private Merlin network;

    boolean isQuit = false;

    private ProfileFB profile;

    private Location myLocation;
    private LatLng center;
    private boolean isStartPoint = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        uid = SharedRefUtils.getUid(this);
        profile = DataManager.getInstance().getProfile();

        geos = new ArrayList<>();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_where_main);
        mapFragment.getMapAsync(this);

        tripSearch = new TripFB();

        nav_header = nav_view.getHeaderView(0);
        tv_name = nav_header.findViewById(R.id.tv_name_where_nav_header_main);
        tv_email = nav_header.findViewById(R.id.tv_email_where_nav_header_main);
        iv_profile = nav_header.findViewById(R.id.iv_profile_where_nav_header_main);
        tv_name.setText(profile.getFirstName() + " " + profile.getLastName());
        tv_email.setText(SharedRefUtils.getEmail(this));

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.changeActivity(MainActivity.this, ProfileActivity.class);
            }
        });

        initApp();

        btn_from_time.setText(ActivityUtils.getNow());
    }

    private void initNetwork() {
        network = new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .build(this);
        network.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment fragment = fragmentManager.findFragmentByTag(NoInternetFragment.FRAGMENT_TAG);
                if (fragment != null && fragment instanceof NoInternetFragment) {
                    NoInternetFragment noInternetFragment = (NoInternetFragment) fragment;
                    fragmentManager.beginTransaction().remove(noInternetFragment).commit();
                }
            }
        });
        network.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {
                NoInternetFragment noInternetFragment = NoInternetFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                noInternetFragment.setCancelable(false);
                noInternetFragment.show(fragmentManager, NoInternetFragment.FRAGMENT_TAG);

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng here = new LatLng(10.8659698,106.8107944);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 13));

        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (!isStartPoint || (start_point != null && end_point != null)) {
                    return;
                }

                checkDisplayForward();

                mMap.clear();
                center = mMap.getCameraPosition().target;

                mMap.addMarker(new MarkerOptions()
                        .position(center)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (!isStartPoint || (start_point != null && end_point != null)) {
                    return;
                }

                mMap.clear();

                if (mMap != null) {
                    center = mMap.getCameraPosition().target;

                    checkDisplayForward();

                    mMap.addMarker(new MarkerOptions()
                            .position(center)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));

                    Call<SearchPlaceWithTextResponse> searchPlaceWithTextResponseCall = ApiUtils.getServerGoogleMapApi()
                            .searchPlaceWithText(center.latitude + "," + center.longitude, getResources().getString(R.string.google_maps_key));
                    searchPlaceWithTextResponseCall.enqueue(new Callback<SearchPlaceWithTextResponse>() {
                        @Override
                        public void onResponse(Call<SearchPlaceWithTextResponse> call, Response<SearchPlaceWithTextResponse> response) {
                            if (response.isSuccessful()) {
                                SearchPlaceWithTextResponse placeResponse = response.body();
                                if (placeResponse.getResults().size() > 0) {
                                    Result result = placeResponse.getResults().get(0);
                                    String titlePlaceName = result.getFormattedAddress();
                                    start_point = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                                    btn_from_where.setHint(titlePlaceName);
                                    tv_from_place.setText(titlePlaceName);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SearchPlaceWithTextResponse> call, Throwable t) {

                        }
                    });
                }
            }
        });
    }

    private void initApp() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setSkipCollapsed(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    AnimationUtils.slideToLeft(layout_search_box);
                    tv_find_ride.setVisibility(View.GONE);
                    tv_offer_ride.setVisibility(View.GONE);
                }

                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    AnimationUtils.slideToRight(layout_search_box);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        FirebaseUtils.downloadImageFile(uid, new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Glide.with(getApplicationContext()).load(bitmap)
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

        startTime = new DateTimePickerUtils(this);
        startTime.initDateTimePicker("time",new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {

                btn_from_time.setText(ActivityUtils.getDateTimeFormat().format(date));
                btn_time_on_search.setText(ActivityUtils.getTimeFormat().format(date));
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                // Optional if neutral button does'nt exists
                btn_from_time.setText("");
            }
        });

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
                        ActivityUtils.changeActivity(MainActivity.this, HomeActivity.class);
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

    /**
     * life cycle
     */

    @Override
    protected void onResume() {
        super.onResume();

        initNetwork();
        network.bind();

        if (start_point!=null && end_point!=null && btn_time_on_search.getText().toString().trim().equals("")) {
            clickFromTime(btn_from_time);
        }

        if (profile != null && start_point != null && end_point != null) {
            if (profile.getMode().equals("find_ride")) {
                clickLayoutFindRide(null);
            } else if (profile.getMode().equals("offer_ride")) {
                clickLayoutOfferRide(null);
            }
        }
    }

    @Override
    protected void onPause() {
        network.unbind();
        super.onPause();

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

    @OnClick(R.id.layout_close_where_main)
    public void clickCloseBottomNavigation(View view) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        AnimationUtils.slideToLeft(layout_search_box);
    }

    @OnClick(R.id.iv_exchange_place_where_main)
    public void clickExchangePlace(View view) {
        LatLng tmp = start_point;
        start_point = end_point;
        end_point = tmp;

        String strTmp = btn_from_where.getHint().toString();
        btn_from_where.setHint(btn_to_where.getHint().toString());
        btn_to_where.setHint(strTmp);

        if (start_point != null && end_point != null) {
            createRoute();
        } else if (start_point != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(start_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));
        } else if (end_point != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(end_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.to_place)));
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
        startTime.openDateTimePicker();
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
            final TripFB findRideTrip = new TripFB();
            findRideTrip.setUid(uid);
            findRideTrip.setGeoStart(ActivityUtils.convertToGeo(start_point));
            findRideTrip.setGeoEnd(ActivityUtils.convertToGeo(end_point));
            findRideTrip.setPaths(geos);
            findRideTrip.setSize(geos.size());
            findRideTrip.setUserName(profile.getFirstName()+" "+profile.getLastName());
            findRideTrip.setStartTime(btn_from_time.getText().toString());

            DataManager.getInstance().setFindRideTrip(findRideTrip);

            Query query = new Query();
            query.setKey(uid);
            query.setTrip(findRideTrip);

            dialog.show();

            MatchingForSearch matchingForSearch = new MatchingForSearch(0.5, query);
            matchingForSearch.matching(new Callback<MatchingForSearchResponse>() {
                @Override
                public void onResponse(Call<MatchingForSearchResponse> call, Response<MatchingForSearchResponse> response) {
                    MatchingForSearchResponse matchingResponse = response.body();
                    if (matchingResponse != null) {
                        List<MatchingDTO> matchingDTOS = new ArrayList<>();
                        if (matchingResponse.getUsers() != null) {
                            for (int index = 0; index < matchingResponse.getUsers().size(); index++) {
                                matchingDTOS.add(new MatchingDTO(
                                        matchingResponse.getUsers().get(index),
                                        matchingResponse.getPercents().get(index)
                                ));
                            }
                        }


                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        Intent intent = new Intent(MainActivity.this, MatchingActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("matching", new FindTripDTO(findRideTrip, matchingDTOS));
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<MatchingForSearchResponse> call, Throwable t) {
                    ActivityUtils.displayLog(t.getMessage());
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        } else if (mode_user == MODE_USER.OFFER_RIDE) {
            TripFB offerRideTrip = new TripFB();
            offerRideTrip.setUid(uid);
            Geo geoStart = new Geo(start_point.latitude, start_point.longitude, S2Utils.getCellId(start_point.latitude, start_point.longitude).id());
            geoStart.setTitle(btn_from_where.getHint().toString());
            offerRideTrip.setGeoStart(geoStart);

            Geo geoEnd = new Geo(end_point.latitude, end_point.longitude, S2Utils.getCellId(end_point.latitude, end_point.longitude).id());
            geoStart.setTitle(btn_to_where.getHint().toString());
            offerRideTrip.setGeoEnd(ActivityUtils.convertToGeo(end_point));

            offerRideTrip.setPaths(geos);
            offerRideTrip.setSize(geos.size());
            offerRideTrip.setUserName(profile.getFirstName()+" "+profile.getLastName());
            offerRideTrip.setStartTime(btn_from_time.getText().toString());

            DataManager.getInstance().setOfferRideTrip(offerRideTrip);

            dialog.show();

            DatabaseReference dbref = FirebaseDB.getInstance().child(TripFB.DB_IN_FB);
            dbref.child(uid).setValue(offerRideTrip).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void changeMode(MODE_USER mode_user) {
        if (mode_user == MODE_USER.FIND_RIDE) {
            btn_submit.setText(getResources().getString(R.string.find_ride_where_main));
            btn_options.setText(getResources().getString(R.string.option_find_ride_where_bottom_navigation_where_main));
            btn_mode.setText(getResources().getString(R.string.find_ride_where_bottom_navigation_where_main));
            iv_mode.setBackgroundResource(R.drawable.findride);
            this.mode_user = MODE_USER.FIND_RIDE;
        } else {
            btn_submit.setText(getResources().getString(R.string.offer_ride_where_main));
            btn_options.setText(getResources().getString(R.string.option_offer_ride_where_bottom_navigation_where_main));
            btn_mode.setText(getResources().getString(R.string.offer_ride_where_bottom_navigation_where_main));
            iv_mode.setBackgroundResource(R.drawable.offerride);
            this.mode_user= MODE_USER.OFFER_RIDE;
        }
    }

    /**
     * Footer controller
     */

    @OnClick(R.id.layout_profile_where_main)
    public void clickLayoutProfile(View view) {
        ActivityUtils.changeActivity(this, ProfileActivity.class);
    }

    @OnClick(R.id.layout_chat_where_main)
    public void clickLayoutChat(View view) {
        ActivityUtils.changeActivity(this, FriendActivity.class);
    }

    @OnClick(R.id.layout_ride_where_main)
    public void clickLayoutRide(View view) {
        ActivityUtils.changeActivity(this, RideActivity.class);
    }

    @OnClick(R.id.layout_find_ride_where_main)
    public void clickLayoutFindRide(View view) {
        tv_offer_ride.setVisibility(View.GONE);
        if (tv_find_ride.getVisibility() == View.VISIBLE) {
            return;
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            changeMode(MODE_USER.FIND_RIDE);
            tv_find_ride.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.layout_offer_ride_where_main)
    public void clickLayoutOfferRide(View view) {
        tv_find_ride.setVisibility(View.GONE);
        if (tv_offer_ride.getVisibility() == View.VISIBLE) {
            return;
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            changeMode(MODE_USER.OFFER_RIDE);
            tv_offer_ride.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.iv_forword_where_main)
    public void clickForword(View view) {
        if (start_point != null) {
            end_point = null;
            Intent intent = new Intent(this, SearchPlaceActivity.class);
            startActivityForResult(intent,END_POINT_ACTIVITY);
        } else if (end_point != null) {
            start_point = null;
            Intent intent = new Intent(this, SearchPlaceActivity.class);
            startActivityForResult(intent,START_POINT_ACTIVITY);
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
                    btn_from_where.setHint(start.toString());
                    tv_from_place.setText(start.toString());
                } else {
                    btn_from_where.setHint(title);
                    tv_from_place.setText(title);
                }
                btn_from_where.setBackground(null);

                mMap.clear();
                isStartPoint = false;

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
                    btn_to_where.setHint(end.toString());
                    tv_to_place.setText(end.toString());
                } else {
                    btn_to_where.setHint(title);
                    tv_to_place.setText(title);
                }
                btn_to_where.setBackground(null);

                iv_my_location.setVisibility(View.GONE);

                end_point = new LatLng(end.lat, end.lng);
                mMap.addMarker(new MarkerOptions().position(end_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.to_place)));

            }
        }

        checkDisplayForward();

    }

    private void checkDisplayForward() {
        if (start_point!=null && end_point!= null) {
            iv_forword.setVisibility(View.GONE);
            createRoute();
        } else if (start_point != null || end_point != null) {
            iv_forword.setVisibility(View.VISIBLE);
        } else {
            iv_forword.setVisibility(View.GONE);
        }
    }

    private void createRoute() {
        GoogleDirection.withServerKey(getResources().getString(R.string.google_maps_key))
                .from(start_point)
                .to(end_point)
                .transitMode(TransportMode.BICYCLING)
                .alternativeRoute(true)
                .unit(Unit.METRIC)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {

                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(start_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.from_place)));
                            mMap.addMarker(new MarkerOptions().position(end_point).icon(BitmapDescriptorFactory.fromResource(R.drawable.to_place)));

//                                for (int i = 0; i < direction.getRouteList().size(); i++) {
                            Route route = direction.getRouteList().get(0);
                            distance = 0;
                            for (Leg leg : route.getLegList()) {
                                distance += Integer.parseInt(leg.getDistance().getValue());
                            }

                            distance /=1000;
                            tv_distance.setText(distance +" km");
                            double cash = distance * COST_PER_KM;
                            tv_cash.setText((int)cash + " thousand VND");

                            DataManager.getInstance().setDistance(distance);
                            DataManager.getInstance().setCost(cash);
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

    /**
     * My location
     */
    @OnClick(R.id.iv_my_location_where_main)
    public void clickMyLocation(View view) {
        mMap.clear();
        if (mMap != null) {
            myLocation = mMap.getMyLocation();

            if (myLocation != null) {

                retrofit2.Call<SearchPlaceWithTextResponse> placeResponseCall = ApiUtils.getServerGoogleMapApi()
                        .searchPlaceWithText(myLocation.getLatitude()+","+myLocation.getLongitude(), getResources().getString(R.string.google_maps_key));
                placeResponseCall.enqueue(new Callback<SearchPlaceWithTextResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<SearchPlaceWithTextResponse> call, Response<SearchPlaceWithTextResponse> response) {
                        if (response.isSuccessful()) {
                            SearchPlaceWithTextResponse placeResponse = response.body();
                            if (placeResponse.getResults().size() > 0) {
                                Result result = placeResponse.getResults().get(0);
                                String titlePlaceName = result.getFormattedAddress();
                                start_point = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                                btn_from_where.setHint(titlePlaceName);
                                tv_from_place.setText(titlePlaceName);
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<SearchPlaceWithTextResponse> call, Throwable t) {
                        ActivityUtils.displayToast(MainActivity.this, t.getMessage());
                    }
                });
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .zoom(13)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(cameraUpdate);
            }

        }
    }
}
