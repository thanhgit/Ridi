package saveteam.com.ridesharing.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.MatchingTripAdapter;
import saveteam.com.ridesharing.model.MatchingDTO;

public class MatchingActivity extends AppCompatActivity {
    @BindView(R.id.rv_users_where_matching)
    RecyclerView rv_users;

    MatchingTripAdapter usersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        ButterKnife.bind(this);

        MatchingDTO matchingDTO = (MatchingDTO) getIntent().getSerializableExtra("matching");

        if (matchingDTO != null) {
            usersAdapter = new MatchingTripAdapter(matchingDTO.getTrips(), this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

            rv_users.setLayoutManager(layoutManager);
            rv_users.setAdapter(usersAdapter);
        }
    }
}
