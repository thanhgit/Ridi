package saveteam.com.ridesharing.presentation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

import java.util.List;

import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.model.TripFB;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.BasicMapActivity;

public class DisplayMapActivity extends BasicMapActivity {

    TripFB trip;
    Trip tripSearch;
    MapRoute mapRoute;

    MapMarker startMarker;
    MapMarker endMarker;

    @Override
    public void addInteraction() {
        createRoute(ActivityUtils.convertFrom(trip.getGeoStart()),
                ActivityUtils.convertFrom(trip.getGeoEnd()), Color.argb(100, 255, 0, 0));

        createRoute(ActivityUtils.convertFrom(tripSearch.startGeo),
                ActivityUtils.convertFrom(tripSearch.endGeo), Color.argb(100, 0, 0, 255));

        startMarker = new MapMarker(ActivityUtils.convertFrom(trip.getGeoStart()), ActivityUtils.getMarker(R.drawable.marker_start));
        endMarker = new MapMarker(ActivityUtils.convertFrom(trip.getGeoEnd()), ActivityUtils.getMarker(R.drawable.marker_end));

        map.addMapObject(startMarker);
        map.addMapObject(endMarker);
    }

    @Override
    protected void onStop() {
        super.onStop();
        map.removeMapObject(mapRoute);
        map.removeMapObject(startMarker);
        map.removeMapObject(endMarker);
    }

    @Override
    public void addView() {
        setContentView(R.layout.activity_display_map);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_where_display_map);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        trip = (TripFB) intent.getSerializableExtra("data");
        tripSearch = (Trip) intent.getSerializableExtra("tripSearch");

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

        MyRoutingForDisplay routingForDisplay = new MyRoutingForDisplay(this, map, mapRoute, color);
        coreRouter.calculateRoute(routePlan,routingForDisplay);
    }

    public static class MyRoutingForDisplay implements Router.Listener<List<RouteResult>, RoutingError> {
        private Activity context;
        private Map map;
        private MapRoute m_mapRoute;
        private int color;

        public MyRoutingForDisplay(Activity context, Map map, MapRoute m_mapRoute, int color) {
            this.context = context;
            this.map = map;
            this.m_mapRoute = m_mapRoute;
            this.color = color;
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
                    m_mapRoute = new MapRoute(routeResults.get(0).getRoute());
                    m_mapRoute.setColor(color);

                    /* Show the maneuver number on top of the route */
                    m_mapRoute.setManeuverNumberVisible(true);

                    /* Add the MapRoute to the map */
                    map.addMapObject(m_mapRoute);

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
