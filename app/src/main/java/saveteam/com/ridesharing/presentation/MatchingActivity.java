package saveteam.com.ridesharing.presentation;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.MatchingTripAdapter;
import saveteam.com.ridesharing.database.model.Profile;
import saveteam.com.ridesharing.firebase.FirebaseDB;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.model.MatchingDTO;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.server.model.MatchingResponseWithUser;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;

public class MatchingActivity extends AppCompatActivity {
    @BindView(R.id.rv_users_where_matching)
    RecyclerView rv_users;

    MatchingTripAdapter usersAdapter;

    List<String> mUsers;

    List<Trip> mTrips;

    List<Profile> mProfiles;

    ProgressDialog dialog;

    Trip tripSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        ButterKnife.bind(this);

        mTrips = new ArrayList<>();
        mUsers = new ArrayList<>();
        mProfiles = new ArrayList<>();

        StartTask startTask = new StartTask(this, new StartTask.GetProfileListener() {
            @Override
            public void done(List<Profile> profiles) {
                mProfiles.clear();
                mProfiles.addAll(profiles);
            }

            @Override
            public void fail(String error) {

            }
        },
        new StartTask.GetTripListener() {
            @Override
            public void done(List<Trip> trips) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (mUsers != null && tripSearch!= null) {
                    HashSet hashSet = new HashSet(mUsers);
                    for (Trip trip : trips) {
                        if (hashSet.contains(trip.userName.replaceFirst("thanh", "").replace("@gmail.com", ""))) {
                            if (TripContainPoint(trip, tripSearch.startGeo) || TripContainPoint(trip, tripSearch.endGeo)) {
                                mTrips.add(trip);
                                usersAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

            }

            @Override
            public void fail(String error) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        startTask.execute();

        initApp();
    }

    public boolean TripContainPoint(Trip _trip, Geo _geo) {
        for (Geo geo: _trip.path) {
            if (geo.cellId == _geo.cellId) {
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
        private GetProfileListener getProfileListener;
        private GetTripListener getTripListener;

        public interface GetProfileListener {
            void done(List<Profile> profiles);
            void fail(String error);
        }

        public interface GetTripListener {
            void done(List<Trip> trips);
            void fail(String error);
        }

        public StartTask(Context context, GetProfileListener getProfileListener, GetTripListener getTripListener) {
            this.context = context;
            this.getProfileListener = getProfileListener;
            this.getTripListener = getTripListener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRefTrips = FirebaseDatabase.getInstance().getReference("testpaths");
            dbRefTrips.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Trip> _trips = new ArrayList<>();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        Trip _trip = item.getValue(Trip.class);
                        _trips.add(_trip);
                    }

                    getTripListener.done(_trips);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    getTripListener.fail(databaseError.getMessage());
                }
            });

//            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("profiles");
//            dbRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    List<Profile> _profiles = new ArrayList<>();
//
//                    for (DataSnapshot item : dataSnapshot.getChildren()) {
//                        Profile _profile = item.getValue(Profile.class);
//                        _profiles.add(_profile);
//                    }
//
//                    getProfileListener.done(_profiles);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    getProfileListener.fail(databaseError.getMessage());
//                }
//            });

            return null;
        }
    }

}
