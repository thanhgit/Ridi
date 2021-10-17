package saveteam.com.quagiang.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.quagiang.R;
import saveteam.com.quagiang.adapter.MatchingTripAdapter;
import saveteam.com.quagiang.firebase.model.ProfileFB;
import saveteam.com.quagiang.firebase.model.TripFB;
import saveteam.com.quagiang.model.FindTripDTO;
import saveteam.com.quagiang.model.Geo;
import saveteam.com.quagiang.model.MatchingDTO;
import saveteam.com.quagiang.presentation.home.MainActivity;
import saveteam.com.quagiang.utils.activity.MapUtils;

public class  MatchingActivity extends AppCompatActivity {
    @BindView(R.id.rv_users_where_matching)
    RecyclerView rv_users;
    @BindView(R.id.btn_find_trip_again_where_matching)
    AppCompatButton btn_find_trip_again;
    @BindView(R.id.layout_no_matching_where_matching)
    LinearLayout layout_no_matching;

    @BindView(R.id.shimmer_container_where_matching)
    ShimmerFrameLayout shimmer_container;

    MatchingTripAdapter usersAdapter;

    List<MatchingDTO> matchingDTOS;

    List<TripFB> mTrips;
    List<ProfileFB> mProfiles;

    TripFB tripSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Find a ride");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTrips = new ArrayList<>();
        mProfiles = new ArrayList<>();
        matchingDTOS = new ArrayList<>();

        initApp();

    }

    @OnClick(R.id.btn_find_trip_again_where_matching)
    public void clickFindTripAgain(View view) {
        Intent intent = new Intent(MatchingActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void initApp() {

        FindTripDTO findTripDTO = (FindTripDTO) getIntent().getSerializableExtra("matching");
        tripSearch = findTripDTO.getTripSearch();
        matchingDTOS = findTripDTO.getMatchingDTOS();

        if (matchingDTOS.size() == 0) {
            shimmer_container.stopShimmerAnimation();
            shimmer_container.setVisibility(View.GONE);
            layout_no_matching.setVisibility(View.VISIBLE);
        } else {
            StartTask startTask = new StartTask(this, matchingDTOS, null,
                    new StartTask.GetTripListener() {
                        @Override
                        public void getTripAndProfile(TripFB trip, ProfileFB profile) {

                            mTrips.add(trip);
                            mProfiles.add(profile);

                            if (mTrips.size() == 5) {
                                usersAdapter.notifyDataSetChanged();
                                shimmer_container.stopShimmerAnimation();
                                shimmer_container.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void complete() {
                            usersAdapter.notifyDataSetChanged();
                            shimmer_container.stopShimmerAnimation();
                            shimmer_container.setVisibility(View.GONE);
                        }
                    });

            startTask.execute();
        }

        usersAdapter = new MatchingTripAdapter(mTrips, mProfiles, this);
        usersAdapter.setFindTripDTO(findTripDTO);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_users.setLayoutManager(layoutManager);
        rv_users.setAdapter(usersAdapter);
        rv_users.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 5 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached) {
                    usersAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTrips.size() == 0) {
            shimmer_container.startShimmerAnimation();
        }

    }

    @Override
    protected void onPause() {
        shimmer_container.stopShimmerAnimation();
        super.onPause();
    }

    static class StartTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private List<MatchingDTO> users;
        private GetTripListener getTripListener;
        //private TripFB tripSearch;

        public interface GetTripListener {
            void getTripAndProfile(TripFB trip, ProfileFB profile);
            void complete();
        }

        public StartTask(Context context, List<MatchingDTO> users, TripFB tripSearch ,GetTripListener getTripListener) {
            this.context = context;
            this.users = users;
            //this.tripSearch = tripSearch;
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
            final MatchingDTO userLasted = users.get(users.size()-1);
            final DatabaseReference dbRefTrips = FirebaseDatabase.getInstance().getReference(TripFB.DB_IN_FB);
            final DatabaseReference dbRefProfiles = FirebaseDatabase.getInstance().getReference(ProfileFB.DB_IN_FB);
            for (final MatchingDTO user : users) {

                dbRefProfiles.child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final ProfileFB profileFB = dataSnapshot.getValue(ProfileFB.class);
                        if (profileFB != null) {
                            dbRefTrips.child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    TripFB _trip = dataSnapshot.getValue(TripFB.class);
                                    if (_trip != null) {
                                        getTripListener.getTripAndProfile(_trip, profileFB);
                                    }

                                    if (userLasted.getUserId().equals(_trip.getUid())) {
                                        getTripListener.complete();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            return null;
        }

        public boolean TripContainPoint(TripFB _trip, Geo _geo) {
            for (Geo geo: _trip.getPaths()) {
                if (MapUtils.distanceBetween2Geo(geo, _geo) < 5000) {
                    return true;
                }
            }

            return false;
        }
    }

}
