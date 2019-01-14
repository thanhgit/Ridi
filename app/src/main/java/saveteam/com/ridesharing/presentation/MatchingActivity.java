package saveteam.com.ridesharing.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.MatchingTripAdapter;
import saveteam.com.ridesharing.database.model.Profile;
import saveteam.com.ridesharing.firebase.model.TripFB;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.presentation.home.MainActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;

public class MatchingActivity extends AppCompatActivity {
    @BindView(R.id.rv_users_where_matching)
    RecyclerView rv_users;
    @BindView(R.id.btn_find_trip_again_where_matching)
    AppCompatButton btn_find_trip_again;

    MatchingTripAdapter usersAdapter;

    List<String> mUsers;

    List<TripFB> mTrips;

    ProgressDialog dialog;

    Trip tripSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        ButterKnife.bind(this);

        mTrips = new ArrayList<>();
        mUsers = new ArrayList<>();

        initApp();

        StartTask startTask = new StartTask(this, mUsers,
                new StartTask.GetTripListener() {
                    @Override
                    public void get(TripFB trip) {

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (trip != null && tripSearch!= null) {
                            if (TripContainPoint(trip, tripSearch.startGeo) && TripContainPoint(trip, tripSearch.endGeo)) {
                                mTrips.add(trip);
                                usersAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                    @Override
                    public void complete() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        if (mUsers.size() == 0) {
                            ActivityUtils.displayToast(MatchingActivity.this, "Not matching");
                        }
                    }
                });

        startTask.execute();

    }

    @OnClick(R.id.btn_find_trip_again_where_matching)
    public void clickFindTripAgain(View view) {
        Intent intent = new Intent(MatchingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public boolean TripContainPoint(TripFB _trip, Geo _geo) {
        for (Geo geo: _trip.getPaths()) {
            if (ActivityUtils.distanceBetween2Geo(geo, _geo) < 500) {
                return true;
            }
        }

        return false;
    }

    private void initApp() {

        List<String> matchingDTO =  getIntent().getStringArrayListExtra("matching");
        tripSearch = (Trip) getIntent().getSerializableExtra("query");

        if (matchingDTO != null) {
            mUsers.clear();
            mUsers.addAll(matchingDTO);
        }

        usersAdapter = new MatchingTripAdapter(mTrips, this);
        usersAdapter.setTripSearch(tripSearch);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_users.setLayoutManager(layoutManager);
        rv_users.setAdapter(usersAdapter);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading ...");
        dialog.setCancelable(false);
        dialog.show();
    }


    static class StartTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private List<String> users;
        private GetTripListener getTripListener;

        public interface GetTripListener {
            void get(TripFB trip);
            void complete();
        }

        public StartTask(Context context, List<String> users ,GetTripListener getTripListener) {
            this.context = context;
            this.users = users;
            this.getTripListener = getTripListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getTripListener.complete();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRefTrips = FirebaseDatabase.getInstance().getReference(TripFB.DB_IN_FB);
            for (String user : users) {
                dbRefTrips.child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        TripFB _trip = dataSnapshot.getValue(TripFB.class);
                        if (_trip != null) {
                            getTripListener.get(_trip);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            return null;
        }
    }

}
