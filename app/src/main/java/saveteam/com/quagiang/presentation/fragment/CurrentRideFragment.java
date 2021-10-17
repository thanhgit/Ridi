package saveteam.com.quagiang.presentation.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.quagiang.R;
import saveteam.com.quagiang.adapter.BookingRideAdapter;
import saveteam.com.quagiang.adapter.OfferRideAdapter;
import saveteam.com.quagiang.firebase.model.BookingListFB;
import saveteam.com.quagiang.firebase.model.ConfirmListFB;
import saveteam.com.quagiang.firebase.model.TripFB;
import saveteam.com.quagiang.utils.activity.SharedRefUtils;

public class CurrentRideFragment extends Fragment {

    public CurrentRideFragment() {
    }


    public static CurrentRideFragment newInstance() {
        CurrentRideFragment fragment = new CurrentRideFragment();
        return fragment;
    }

    @BindView(R.id.rv_offer_ride_where_current_ride)
    RecyclerView rv_offer_ride;
    @BindView(R.id.rv_bookings_current_ride)
    RecyclerView rv_bookings;

    @BindView(R.id.layout_offer_ride_where_current_ride)
    LinearLayout layout_offer_ride;
    @BindView(R.id.layout_booking_where_current_ride)
    LinearLayout layout_booking_ride;

    OfferRideAdapter offerRideAdapter;
    BookingRideAdapter bookingRideAdapter;

    ConfirmListFB confirmList;
    BookingListFB bookingList;

    TripFB myTrip;

    ProgressDialog progressDialog;

    /**
     * My offer ride
     */
    @BindView(R.id.tv_from_place_where_current_ride)
    TextView tv_from_place;
    @BindView(R.id.tv_to_place_where_current_ride)
    TextView tv_to_place;
    @BindView(R.id.tv_from_time_where_current_ride)
    TextView tv_from_time;
    @BindView(R.id.tv_to_time_where_current_ride)
    TextView tv_to_time;
    @BindView(R.id.cv_offer_ride_where_current_ride)
    CardView cv_offer_ride;

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

        confirmList = new ConfirmListFB();
        bookingList = new BookingListFB();

        setupOfferRideAdapter();
        setupBookingRideAdapter();

        final String uid = SharedRefUtils.getUid(getContext());

        DatabaseReference dbRefOffer = FirebaseDatabase.getInstance().getReference(TripFB.DB_IN_FB);
        dbRefOffer.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TripFB tripFB = dataSnapshot.getValue(TripFB.class);
                if (tripFB != null && tripFB.getUid() != null) {
                    myTrip = tripFB;

                    tv_from_place.setText(myTrip.getGeoStart().title);
                    tv_to_place.setText(myTrip.getGeoEnd().title);
                    tv_from_time.setText(myTrip.getGeoStart().time);
                    tv_to_time.setText(myTrip.getGeoEnd().time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        StartTask startTask = new StartTask(getActivity(), uid, new StartTask.StartListener() {
            @Override
            public void success(final ConfirmListFB _confirmListFB, BookingListFB _bookingListFB) {
                if (_confirmListFB != null && _confirmListFB.getConfirms() != null && _confirmListFB.getConfirms().size() > 0) {
                    layout_offer_ride.setVisibility(View.VISIBLE);
                    confirmList.getConfirms().clear();
                    confirmList.getProfiles().clear();
                    confirmList.getConfirms().addAll(_confirmListFB.getConfirms());
                    confirmList.getProfiles().addAll(_confirmListFB.getProfiles());
                    offerRideAdapter.notifyDataSetChanged();
                } else if (myTrip != null) {
                    layout_offer_ride.setVisibility(View.VISIBLE);
                } else {
                    layout_offer_ride.setVisibility(View.GONE);
                }

                if (myTrip != null) {
                    cv_offer_ride.setVisibility(View.VISIBLE);
                } else {
                    cv_offer_ride.setVisibility(View.GONE);
                }

                if (_bookingListFB != null && _bookingListFB.getBookings() != null && _bookingListFB.getBookings().size() > 0) {
                    layout_booking_ride.setVisibility(View.VISIBLE);
                    bookingList.getBookings().clear();
                    bookingList.getProfiles().clear();
                    bookingList.getBookings().addAll(_bookingListFB.getBookings());
                    bookingList.getProfiles().addAll(_bookingListFB.getProfiles());
                    bookingRideAdapter.notifyDataSetChanged();
                } else {
                    layout_booking_ride.setVisibility(View.GONE);
                }

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void fail() {
                if (myTrip != null) {
                    cv_offer_ride.setVisibility(View.VISIBLE);
                } else {
                    cv_offer_ride.setVisibility(View.GONE);
                }
                layout_offer_ride.setVisibility(View.GONE);
                layout_booking_ride.setVisibility(View.GONE);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        startTask.execute();


        return view;
    }

    private void setupOfferRideAdapter() {
        offerRideAdapter = new OfferRideAdapter(getActivity(), confirmList.getConfirms(), confirmList.getProfiles());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_offer_ride.setLayoutManager(layoutManager);
        rv_offer_ride.setAdapter(offerRideAdapter);
    }

    private void setupBookingRideAdapter() {
        bookingRideAdapter = new BookingRideAdapter(getActivity(), bookingList.getBookings(), bookingList.getProfiles());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_bookings.setLayoutManager(layoutManager);
        rv_bookings.setAdapter(bookingRideAdapter);
    }

    static class StartTask extends AsyncTask<Void, Void, Void> {
        private Activity activity;
        private String uid;
        private StartListener listener;

        public StartTask(Activity activity, String uid, StartListener listener) {
            this.activity = activity;
            this.uid = uid;
            this.listener = listener;
        }

        public interface StartListener {
            void success(ConfirmListFB confirmListFB, BookingListFB bookingListFB);
            void fail();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRefConfirm = FirebaseDatabase.getInstance().getReference(ConfirmListFB.DB_IN_FB);
            final DatabaseReference dbRefBooking = FirebaseDatabase.getInstance().getReference(BookingListFB.DB_IN_FB);
            dbRefConfirm.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final ConfirmListFB confirmListFB = dataSnapshot.getValue(ConfirmListFB.class);
                        dbRefBooking.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                BookingListFB bookingListFB = dataSnapshot.getValue(BookingListFB.class);
                                if (bookingListFB != null) {
                                    listener.success(confirmListFB, bookingListFB);
                                } else {
                                    listener.success(confirmListFB, null);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                listener.success(confirmListFB, null);
                            }
                        });

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
