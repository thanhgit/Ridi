package saveteam.com.ridesharing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.RoutingError;
import com.here.services.internal.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveteam.com.ridesharing.adapter.MatchingTripAdapter;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.ServerApi;
import saveteam.com.ridesharing.server.ServerClient;
import saveteam.com.ridesharing.server.model.MatchingResponse;
import saveteam.com.ridesharing.server.model.QueryRequest;
import saveteam.com.ridesharing.utils.ActivityUtils;
import saveteam.com.ridesharing.utils.MyGoogleAuthen;
import saveteam.com.ridesharing.utils.SharedRefUtils;

public class HomeActivity extends AppCompatActivity {
    private static final int START_POINT_ACTIVITY = 9000;
    private static final int END_POINT_ACTIVITY = 9001;

    @BindView(R.id.btn_start_where_home)
    AppCompatButton btn_start;
    @BindView(R.id.btn_end_where_home)
    AppCompatButton btn_end;
    @BindView(R.id.btn_schedule_where_home)
    AppCompatButton btn_schedule;
    @BindView(R.id.recycler_view_where_home)
    RecyclerView mRecyclerView;

    Geo start_point;
    Geo end_point;
    Trip tripSearch;

    List<Integer> similarSet;
    List<Trip> trips;
    List<Trip> similarTrips;

    MatchingTripAdapter mRcvAdapter;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        trips = new ArrayList<>();
        similarTrips = new ArrayList<>();
        tripSearch = new Trip();

        user = MyGoogleAuthen.getCurrentUser(HomeActivity.this);


        mRcvAdapter = new MatchingTripAdapter(similarTrips, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRcvAdapter);

        GetTripsTask getTripsTask = new GetTripsTask(this);
        getTripsTask.execute();

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

                tripSearch.userName = SharedRefUtils.getEmail(HomeActivity.this);
                tripSearch.startGeo = start_point;
                tripSearch.endGeo = end_point;
                tripSearch.path = ActivityUtils.convertToGeo(geos);

                ActivityUtils.displayLog(tripSearch.toString());

                Query query = new Query();
                query.key = tripSearch.userName;
                query.trip = tripSearch;

                QueryRequest queryRequest = new QueryRequest(0.1, query);

                Call<MatchingResponse> matchingResponseCall = ApiUtils.getUserClient().getMatchingFromPersonal(queryRequest);
                matchingResponseCall.enqueue(new Callback<MatchingResponse>() {
                    @Override
                    public void onResponse(Call<MatchingResponse> call, Response<MatchingResponse> response) {
                        MatchingResponse matchingResponse = response.body();
                        if (matchingResponse != null) {
                            similarSet = matchingResponse.getSimilarSet();
                            similarTrips.clear();
                            for (Integer index : similarSet) {
                                ActivityUtils.displayLog("similar item is: " + index);
                                if (trips.size() >0) {
                                    similarTrips.add(trips.get(index));
                                }
                            }

                            mRcvAdapter.notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Call<MatchingResponse> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onProgress(int i) {

            }
        });
    }

    @OnClick(R.id.btn_start_where_home)
    public void clickButtonStart(View view) {
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,START_POINT_ACTIVITY);
    }

    @OnClick(R.id.btn_end_where_home)
    public void clickButtonEnd(View view){
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,END_POINT_ACTIVITY);
    }

    @OnClick(R.id.btn_schedule_where_home)
    public void clickButtonSchedule(View view) {
        Call<MatchingResponse> matchingResponseCall = ApiUtils.getUserClient().getMatchingFromServer();

        matchingResponseCall.enqueue(new Callback<MatchingResponse>() {
            @Override
            public void onResponse(Call<MatchingResponse> call, Response<MatchingResponse> response) {
                if (response.body() != null) {
                    MatchingResponse matchingResponse = response.body();
                    similarSet = matchingResponse.getSimilarSet();
                    for (Integer index : similarSet) {
                        ActivityUtils.displayLog("similar item is: " + index);
                        if (trips.size() >0) {
                            similarTrips.add(trips.get(index));
                        }
                    }

                    mRcvAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MatchingResponse> call, Throwable t) {

            }
        });
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signout() {
        MyGoogleAuthen.signOut(this, new MyGoogleAuthen.LogoutCompleteListener() {
            @Override
            public void done() {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_POINT_ACTIVITY && resultCode == RESULT_OK) {
            start_point = (Geo) data.getSerializableExtra("data");
            if (start_point != null) {
                btn_start.setText("( "+start_point.lat+", "+start_point.lng+")");
                btn_start.setBackground(null);
            }
        }

        if (requestCode == END_POINT_ACTIVITY && resultCode == RESULT_OK ) {
            end_point = (Geo) data.getSerializableExtra("data");
            if (end_point != null) {
                btn_end.setText("( "+end_point.lat+", "+end_point.lng+")");
                btn_end.setBackground(null);
            }
        }

        MatchingRequestTask matchingRequestTask = new MatchingRequestTask(this);
        matchingRequestTask.execute();

    }

    private class GetTripsTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        public GetTripsTask(Context context) {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRefSearch = FirebaseDatabase.getInstance().getReference("paths");
            dbRefSearch.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Trip trip = item.getValue(Trip.class);
                        // ActivityUtils.displayLog("key is " + trip.userName);
                        trips.add(trip);
                    }

                    if (trips.size() >0) {
                        ActivityUtils.displayLog("has");
                    } else {
                        ActivityUtils.displayLog("not has");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }

    private class MatchingRequestTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        public MatchingRequestTask(Context context) {
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Doing something, please wait.");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (start_point != null && end_point != null) {
                createRoute();
            }
            return null;
        }
    }
}
