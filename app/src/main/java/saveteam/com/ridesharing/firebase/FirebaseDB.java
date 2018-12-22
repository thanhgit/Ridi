package saveteam.com.ridesharing.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.model.Trip;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;

public class FirebaseDB {
    public static DatabaseReference getInstance() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static List<Trip> getTrips(String from) {
        final List<Trip> result = new ArrayList<>();

        FirebaseDB.getInstance().child(from).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Trip trip = item.getValue(Trip.class);
                    result.add(trip);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return result;
    }

    public static List<Query> getQueries(String from) {
        final List<Query> result = new ArrayList<>();

        FirebaseDB.getInstance().child(from).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    Query query = item.getValue(Query.class);
                    if (query != null) {
                        ActivityUtils.displayLog("key la" + query.key);
                    }

                    result.add(query);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return result;
    }
}