package saveteam.com.ridesharing.presentation.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.RequestRideAdapter;
import saveteam.com.ridesharing.firebase.model.ConfirmFB;
import saveteam.com.ridesharing.firebase.model.ConfirmListFB;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class CurrentRideFragment extends Fragment {

    public CurrentRideFragment() {
    }


    public static CurrentRideFragment newInstance() {
        CurrentRideFragment fragment = new CurrentRideFragment();
        return fragment;
    }

    @BindView(R.id.rv_current_ride)
    RecyclerView rv_current_ride;

    RequestRideAdapter requestRideAdapter;
    List<ConfirmFB> confirms;
    List<ProfileFB> profiles;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_ride, container, false);
        ButterKnife.bind(this, view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        confirms = new ArrayList<>();
        profiles = new ArrayList<>();

        requestRideAdapter = new RequestRideAdapter(getActivity(), confirms, profiles);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_current_ride.setLayoutManager(layoutManager);
        rv_current_ride.setAdapter(requestRideAdapter);
        String uid = SharedRefUtils.getUid(getContext());
        StartTask startTask = new StartTask(getActivity(), uid, new StartTask.GetConfirmListener() {
            @Override
            public void success(final ConfirmListFB confirmFB) {
                if (confirmFB.getConfirms().size() > 0) {
                    confirms.clear();
                    profiles.clear();
                    confirms.addAll(confirmFB.getConfirms());
                    profiles.addAll(confirmFB.getProfiles());
                    requestRideAdapter.notifyDataSetChanged();
                }

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void fail() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        startTask.execute();
        return view;
    }

    static class StartTask extends AsyncTask<Void, Void, Void> {
        private Activity activity;
        private String uid;
        private GetConfirmListener listener;

        public StartTask(Activity activity, String uid, GetConfirmListener listener) {
            this.activity = activity;
            this.uid = uid;
            this.listener = listener;
        }

        public interface GetConfirmListener {
            void success(ConfirmListFB confirmFB);
            void fail();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("confirmv1");
            dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ConfirmListFB confirmFB = dataSnapshot.getValue(ConfirmListFB.class);
                    if (confirmFB != null) {
                        listener.success(confirmFB);

                    } else {
                        listener.fail();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.fail();
                }
            });
            return null;
        }
    }
}
