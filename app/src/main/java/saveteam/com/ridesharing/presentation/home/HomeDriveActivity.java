package saveteam.com.ridesharing.presentation.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.presentation.SearchPlaceActivity;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.QueryRequest;
import saveteam.com.ridesharing.server.model.matching.MatchingResponse;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class HomeDriveActivity extends HomeActivity {
    private static final int START_POINT_ACTIVITY = 9000;
    private static final int END_POINT_ACTIVITY = 9001;

    @BindView(R.id.btn_ride_where_home_drive)
    AppCompatButton btn_ride;
    @BindView(R.id.btn_post_as_drive_where_home)
    AppCompatButton btn_post_as_drive;
    @BindView(R.id.btn_start_where_home_drive)
    AppCompatButton btn_start;
    @BindView(R.id.btn_end_where_home_drive)
    AppCompatButton btn_end;

    Geo start_point;
    Geo end_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_drive);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_start_where_home)
    public void clickButtonStart(View view) {
//        start_point = null;
//        Intent intent = new Intent(this, SearchPlaceActivity.class);
//        startActivityForResult(intent,START_POINT_ACTIVITY);
    }

    @OnClick(R.id.btn_end_where_home)
    public void clickButtonEnd(View view){
//        end_point = null;
//        Intent intent = new Intent(this, SearchPlaceActivity.class);
//        startActivityForResult(intent,END_POINT_ACTIVITY);
    }

    @OnClick(R.id.btn_ride_where_home_drive)
    public void clickRide(View view) {
        ActivityUtils.changeActivity(this, HomeRideActivity.class);
    }

    @OnClick(R.id.btn_post_as_drive_where_home)
    public void clickPostAsDrive(View view) {

    }

    public void createRoute() {
        CoreRouter router = new CoreRouter();
        RoutePlan routePlan = new RoutePlan();
        routePlan.addWaypoint(new RouteWaypoint(ActivityUtils.convertFrom(start_point)));
        routePlan.addWaypoint(new RouteWaypoint(ActivityUtils.convertFrom(end_point)));

        RouteOptions routeOptions = new RouteOptions();
        routeOptions.setTransportMode(RouteOptions.TransportMode.BICYCLE);
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routePlan.setRouteOptions(routeOptions);

        router.calculateRoute(routePlan, new CoreRouter.Listener() {
            @Override
            public void onCalculateRouteFinished(List<RouteResult> list, RoutingError routingError) {
                List<GeoCoordinate> geos = list.get(0).getRoute().getRouteGeometry();

            }

            @Override
            public void onProgress(int i) {

            }
        });
    }
}
