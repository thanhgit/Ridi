package saveteam.com.ridesharing.presentation.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashSet;
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
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.MatchingDTO;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.presentation.DisplayMapActivity;
import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.presentation.MatchingActivity;
import saveteam.com.ridesharing.presentation.SearchPlaceActivity;
import saveteam.com.ridesharing.presentation.profile.ActivateCodeActivity;
import saveteam.com.ridesharing.presentation.profile.ProfileActivity;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.MatchingResponseWithUser;
import saveteam.com.ridesharing.server.model.QueryRequest;
import saveteam.com.ridesharing.server.model.matching.MatchingResponse;
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

    @BindView(R.id.layout_where_main)
    LinearLayout layout;
    @BindView(R.id.rb_find_ride_where_main)
    RadioButton rb_find_ride;
    @BindView(R.id.rb_offer_ride_where_main)
    RadioButton rb_offer_ride;
    @BindView(R.id.btn_submit_where_main)
    AppCompatButton btn_submit;

    @BindView(R.id.btn_from_where_where_main)
    AppCompatButton btn_from_where;
    @BindView(R.id.btn_from_time_where_main)
    AppCompatButton btn_from_time;

    @BindView(R.id.btn_to_where_where_main)
    AppCompatButton btn_to_where;
    @BindView(R.id.btn_options_where_main)
    AppCompatButton btn_options;

    ProgressDialog dialog;

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

        initApp();

    }

    private void initApp() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);

        mode_user = MODE_USER.FIND_RIDE;
        changeMode(mode_user);

        initDateTimePicker();
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
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", java.util.Locale.getDefault());
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

    /**
     * button from
     */

    @OnClick(R.id.btn_from_time_where_main)
    public void clickFromTime(View view) {
        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.setDefaultDateTime(Calendar.getInstance().getTime());
        dateTimeFragment.show(getSupportFragmentManager(), FROM_TIME_DATETIME_FRAGMENT);
    }

    @OnClick(R.id.btn_from_where_where_main)
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

    @OnClick(R.id.btn_to_where_where_main)
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

            QueryRequest queryRequest = new QueryRequest(0.5, query);

            Call<MatchingResponseWithUser> matchingResponseCall = ApiUtils.getUserClient().getMatchingFromPersonalResultUser(queryRequest);
            matchingResponseCall.enqueue(new Callback<MatchingResponseWithUser>() {
                @Override
                public void onResponse(Call<MatchingResponseWithUser> call, Response<MatchingResponseWithUser> response) {
                    MatchingResponseWithUser matchingResponse = response.body();
                    if (matchingResponse != null) {

                        for (String user : matchingResponse.getUsers()) {
                            ActivityUtils.displayLog(user);
                        }

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        Intent intent = new Intent(MainActivity.this, MatchingActivity.class);
                        intent.putExtra("query", tripSearch);
                        intent.putStringArrayListExtra("matching", (ArrayList<String>) matchingResponse.getUsers());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_signout_where_home_menu:
                signout();
                return true;
            case R.id.btn_update_profile_where_home_menu:
                ActivityUtils.changeActivity(this, ProfileActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signout() {
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
