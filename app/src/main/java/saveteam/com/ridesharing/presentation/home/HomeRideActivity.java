package saveteam.com.ridesharing.presentation.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.MatchingTripAdapter;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.presentation.LoginActivity;
import saveteam.com.ridesharing.presentation.SearchPlaceActivity;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.QueryRequest;
import saveteam.com.ridesharing.server.model.matching.MatchingResponse;
import saveteam.com.ridesharing.server.model.matching.SimilarSet;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.google.MyGoogleAuthen;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class HomeRideActivity extends HomeActivity {
    private static final int START_POINT_ACTIVITY = 9000;
    private static final int END_POINT_ACTIVITY = 9001;

    public enum MATCHING_STATUS {
        SUCCESS, FAIL, DOING, NONE;

        public static String toString(MATCHING_STATUS status) {
            switch (status) {
                case SUCCESS:
                    return "You are matching successfully";
                case FAIL:
                    return "You are matching fail";
                case DOING:
                    return "Loading ...";
                default:
                    return "";
            }
        }
    }

    private MATCHING_STATUS matching_status = MATCHING_STATUS.NONE;

    @BindView(R.id.btn_start_where_home)
    AppCompatButton btn_start;
    @BindView(R.id.btn_end_where_home)
    AppCompatButton btn_end;
    @BindView(R.id.btn_schedule_where_home)
    AppCompatButton btn_schedule;
    @BindView(R.id.recycler_view_where_home)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_message_where_home)
    TextView tv_message;

    @BindView(R.id.btn_drive_where_home_ride)
    AppCompatButton btn_drive;

    Geo start_point;
    Geo end_point;
    Trip tripSearch;

    List<SimilarSet> similarSet;
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

        tv_message.setText(MATCHING_STATUS.toString(MATCHING_STATUS.NONE));

        mRcvAdapter = new MatchingTripAdapter(similarTrips, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mRcvAdapter);

//        GetTripsTask getTripsTask = new GetTripsTask(this);
//        getTripsTask.execute();

    }

    @OnClick(R.id.btn_drive_where_home_ride)
    public void clickDrive(View view) {
        ActivityUtils.changeActivity(this, HomeDriveActivity.class);
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

                tripSearch.userName = SharedRefUtils.getEmail(HomeRideActivity.this);
                tripSearch.startGeo = start_point;
                tripSearch.endGeo = end_point;
                tripSearch.path = new ArrayList<>();
                tripSearch.path.addAll(ActivityUtils.convertToGeo(geos));

                mRcvAdapter.setTripSearch(tripSearch);

                Query query = new Query();
                query.key = tripSearch.userName;
                query.trip = tripSearch;

                QueryRequest queryRequest = new QueryRequest(0.01, query);

                Call<MatchingResponse> matchingResponseCall = ApiUtils.getUserClient().getMatchingFromPersonal(queryRequest);
                matchingResponseCall.enqueue(new Callback<MatchingResponse>() {
                    @Override
                    public void onResponse(Call<MatchingResponse> call, Response<MatchingResponse> response) {
                        MatchingResponse matchingResponse = response.body();
                        if (matchingResponse != null) {
                            similarTrips.clear();
                            for (Trip index : matchingResponse.getTrips()) {
                                ActivityUtils.displayLog("similar item is: " + index.userName);
                                similarTrips.add(index);
                            }

                            if (similarTrips.size() == 0) {
                                tv_message.setText(MATCHING_STATUS.toString(MATCHING_STATUS.SUCCESS));
                            } else {
                                tv_message.setText(MATCHING_STATUS.toString(MATCHING_STATUS.NONE));
                            }

                            mRcvAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<MatchingResponse> call, Throwable t) {
                        ActivityUtils.displayLog(t.getMessage());
                        tv_message.setText(MATCHING_STATUS.toString(MATCHING_STATUS.FAIL));
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
        start_point = null;
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,START_POINT_ACTIVITY);
    }

    @OnClick(R.id.btn_end_where_home)
    public void clickButtonEnd(View view){
        end_point = null;
        Intent intent = new Intent(this, SearchPlaceActivity.class);
        startActivityForResult(intent,END_POINT_ACTIVITY);
    }

    @OnClick(R.id.btn_schedule_where_home)
    public void clickButtonSchedule(View view) {
//        Call<MatchingResponse> matchingResponseCall = ApiUtils.getUserClient().getMatchingFromServer();
//
//        matchingResponseCall.enqueue(new Callback<MatchingResponse>() {
//            @Override
//            public void onResponse(Call<MatchingResponse> call, Response<MatchingResponse> response) {
//                if (response.body() != null) {
//                    MatchingResponse matchingResponse = response.body();
//                    similarSet = matchingResponse.getSimilarSet();
//                    for (Integer index : similarSet) {
//                        ActivityUtils.displayLog("similar item is: " + index);
//                        if (trips.size() >0) {
//                            similarTrips.add(trips.get(index));
//                        }
//                    }
//
//                    mRcvAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<MatchingResponse> call, Throwable t) {
//
//            }
//        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_POINT_ACTIVITY && resultCode == RESULT_OK) {
            start_point = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");
            if (start_point != null) {
                if (title.equals("")) {
                    btn_start.setText(start_point.toString());
                } else {
                    btn_start.setText(title);
                }
                btn_start.setBackground(null);
            }
        }

        if (requestCode == END_POINT_ACTIVITY && resultCode == RESULT_OK ) {
            end_point = (Geo) data.getSerializableExtra("data");
            String title = data.getStringExtra("title");

            if (end_point != null) {
                if (title.equals("")) {
                    btn_end.setText(end_point.toString());
                } else {
                    btn_end.setText(title);
                }
                btn_end.setBackground(null);
            }
        }

        if (start_point != null && end_point != null) {
            similarTrips.clear();
            mRcvAdapter.notifyDataSetChanged();
            MatchingRequestTask matchingRequestTask = new MatchingRequestTask(this);
            matchingRequestTask.execute();
        }


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
        // ProgressDialog dialog;

        public MatchingRequestTask(Context context) {
            // this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            tv_message.setText(MATCHING_STATUS.toString(MATCHING_STATUS.DOING));

//            dialog.setMessage("Doing something, please wait.");
//            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            createRoute();
            return null;
        }
    }
}
