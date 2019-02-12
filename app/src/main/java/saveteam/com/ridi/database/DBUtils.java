package saveteam.com.ridi.database;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import saveteam.com.ridi.database.model.User;
import saveteam.com.ridi.firebase.model.ProfileFB;

public class DBUtils {
    /**
     * profiles
     */
    public static class InsertProfileTask extends AsyncTask<Void, Void, Void> {
        Activity activity;
        ProfileFB profile;

        public InsertProfileTask(Activity activity, ProfileFB profile) {
            this.activity = activity;
            this.profile = profile;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            RidesharingDB.getInstance(activity).getProfileDao().insertProfiles(profile);
            return null;
        }
    }

    public static class GetProfileByIdTask extends AsyncTask<Void, Void, Void> {
        Context context;
        String uid;
        GetProfileListener listener;

        public interface GetProfileListener {
            void done(ProfileFB profile);
        }

        public GetProfileByIdTask(Context context, String uid, GetProfileListener listener ) {
            this.context = context;
            this.uid = uid;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbref = FirebaseDatabase.getInstance().getReference(ProfileFB.DB_IN_FB).child(uid);
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProfileFB profile = dataSnapshot.getValue(ProfileFB.class);
                    listener.done(profile);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.done(null);
                }
            });
            return null;
        }
    }

    /**
     * users
     */
    public static class GetAllUserTask extends AsyncTask<Void, Void, Void> {
        Activity activity;
        GetAllUserListener listener;

        public interface GetAllUserListener {
            void done(List<User> users);
        }

        public GetAllUserTask(Activity activity, GetAllUserListener listener) {
            this.activity = activity;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<User> users = RidesharingDB.getInstance(activity).getUserDao().loadAllUsers();
            listener.done(users);
            return null;
        }
    }
}
