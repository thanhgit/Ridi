package saveteam.com.ridesharing.presentation.home;

import android.app.Activity;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
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
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.MatchingResponseWithUser;
import saveteam.com.ridesharing.server.model.QueryRequest;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.BasicMapActivity;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;

public class MainActivity extends BasicMapActivity {
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

    MapMarker start_point;
    MapMarker end_point;
    List<MapMarker> markers;
    MapRoute mapRoute;
    List<Geo> geos;
    Trip tripSearch;

    private static final String TAG = "Sample";

    private static final String FROM_TIME_DATETIME_FRAGMENT = "FROM_TIME_DATETIME_FRAGMENT";

    private SwitchDateTimeDialogFragment dateTimeFragment;
    private MODE_USER mode_user;

    @Override
    public void addInteraction() {
        
    }

    @Override
    public void addView() {
        setContentView(R.layout.activity_main);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_where_main);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        geos = new ArrayList<>();
        tripSearch = new Trip();

        markers = new ArrayList<>();

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
        if (this.mapRoute != null) {
            this.map.removeMapObject(this.mapRoute);
        }

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
            tripSearch.startGeo = ActivityUtils.convertToGeo(start_point.getCoordinate());
            tripSearch.endGeo = ActivityUtils.convertToGeo(end_point.getCoordinate());
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
            tripSearch.startGeo = ActivityUtils.convertToGeo(start_point.getCoordinate());
            tripSearch.endGeo = ActivityUtils.convertToGeo(end_point.getCoordinate());
            tripSearch.path = new ArrayList<>();
            tripSearch.path.addAll(geos);

            dialog.show();

            DatabaseReference dbref = FirebaseDB.getInstance().child("testpaths");
            String key = dbref.push().getKey();
            dbref.child(key).setValue(tripSearch).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                map.removeMapObject(start_point);
                start_point = new MapMarker(ActivityUtils.convertFrom(start), ActivityUtils.getMarker());
                map.addMapObject(start_point);
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

                map.removeMapObject(end_point);
                end_point = new MapMarker(ActivityUtils.convertFrom(end), ActivityUtils.getMarker());
                map.addMapObject(end_point);
            }
        }

        if (start_point!=null && end_point!= null) {
            if (mapRoute!= null) {
                map.removeMapObject(mapRoute);
            }

            createRoute(start_point.getCoordinate(), end_point.getCoordinate(), Color.BLUE);
        }

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

    private void createRoute(GeoCoordinate start, GeoCoordinate end, int color) {
        CoreRouter coreRouter = new CoreRouter();
        RoutePlan routePlan = new RoutePlan();
        RouteOptions routeOptions = null;
        if (routeOptions == null) {
            routeOptions = new RouteOptions();
            routeOptions.setTransportMode(RouteOptions.TransportMode.BICYCLE);
            routeOptions.setHighwaysAllowed(false);
            routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
            routeOptions.setRouteCount(3);
            routePlan.setRouteOptions(routeOptions);
        }

        RouteWaypoint startPoint = new RouteWaypoint(start);
        RouteWaypoint destination = new RouteWaypoint(end);

        routePlan.addWaypoint(startPoint);
        routePlan.addWaypoint(destination);

        MyRoutingForMatching routingForMatching = new MyRoutingForMatching(this, map, mapRoute, color);
        routingForMatching.setListener(new MyRoutingForMatching.GetRouteListener() {
            @Override
            public void finished(List<RouteResult> routeResults) {
                geos.clear();
                geos.addAll(ActivityUtils.convertToGeo(routeResults.get(0).getRoute().getRouteGeometry()));
            }
        });
        coreRouter.calculateRoute(routePlan,routingForMatching);
    }

    public static class MyRoutingForMatching implements Router.Listener<List<RouteResult>, RoutingError> {
        private Activity context;
        private Map map;
        private MapRoute m_mapRoute;
        private int color;

        public interface GetRouteListener{
            void finished(List<RouteResult> routeResults);
        }

        GetRouteListener listener;

        public MyRoutingForMatching(Activity context, Map map, MapRoute m_mapRoute, int color) {
            this.context = context;
            this.map = map;
            this.m_mapRoute = m_mapRoute;
            this.color = color;
        }

        public void setListener(GetRouteListener listener) {
            this.listener = listener;
        }

        @Override
        public void onProgress(int i) {

        }

        @Override
        public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                             RoutingError routingError) {
            /* Calculation is done. Let's handle the result */
            if (routingError == RoutingError.NONE) {
                if (routeResults.get(0).getRoute() != null) {
                    /* Create a MapRoute so that it can be placed on the map */
                    if (m_mapRoute != null) {
                        map.removeMapObject(m_mapRoute);
                    }

                    m_mapRoute = new MapRoute(routeResults.get(0).getRoute());
                    m_mapRoute.setColor(color);

                    /* Show the maneuver number on top of the route */
                    m_mapRoute.setManeuverNumberVisible(true);

                    /* Add the MapRoute to the map */
                    map.addMapObject(m_mapRoute);

                    if (listener != null) {
                        listener.finished(routeResults);
                    }

                    /*
                     * We may also want to make sure the map view is orientated properly
                     * so the entire route can be easily seen.
                     */
                    GeoBoundingBox gbb = routeResults.get(0).getRoute()
                            .getBoundingBox();

                    Route route = routeResults.get(0).getRoute();


                    route.getRouteWaypoints();
                    for (RouteWaypoint routeWaypoint: route.getRouteWaypoints()) {
                        GeoCoordinate geo = routeWaypoint.getOriginalPosition();
                        Log.d("thanhuit", geo.getLatitude() + " - " + geo.getLongitude());
                    }

                    map.zoomTo(gbb, Map.Animation.NONE,
                            Map.MOVE_PRESERVE_ORIENTATION);
                } else {
                    Toast.makeText(context,
                            "Error:route results returned is not valid",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context,
                        "Error:route calculation returned error code: " + routingError,
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
