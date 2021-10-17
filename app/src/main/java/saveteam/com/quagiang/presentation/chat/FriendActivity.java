package saveteam.com.quagiang.presentation.chat;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.quagiang.R;
import saveteam.com.quagiang.firebase.model.ProfileFB;
import saveteam.com.quagiang.utils.activity.SharedRefUtils;

public class FriendActivity extends AppCompatActivity {
    @BindView(R.id.rv_friend_where_friend)
    RecyclerView rv_friend;

    String uid = "";
    List<ProfileFB> profiles;
    FriendAdapter friendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Friend list");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uid = SharedRefUtils.getUid(this);

        profiles = new ArrayList<>();
        friendAdapter = new FriendAdapter(this, profiles, uid);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_friend.setLayoutManager(layoutManager);
        rv_friend.setAdapter(friendAdapter);

        StartTask startTask = new StartTask(this, uid, new StartTask.GetProfilesListener() {
            @Override
            public void done(ProfileFB profile) {
                profiles.add(profile);
                friendAdapter.notifyDataSetChanged();
            }
        });

        startTask.execute();

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

    private static class StartTask extends AsyncTask<Void, Void, Void> {

        private Activity activity;
        private GetProfilesListener listener;
        private String uid;

        public StartTask(Activity activity, String uid, GetProfilesListener listener) {
            this.activity = activity;
            this.listener = listener;
            this.uid = uid;
        }

        public interface GetProfilesListener {
            void done(ProfileFB profile);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbProfileRef = FirebaseDatabase.getInstance().getReference(ProfileFB.DB_IN_FB);
            dbProfileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        ProfileFB profileFB = item.getValue(ProfileFB.class);
                        if (!profileFB.getUid().equals(uid)) {
                            listener.done(profileFB);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return null;
        }
    }
}
