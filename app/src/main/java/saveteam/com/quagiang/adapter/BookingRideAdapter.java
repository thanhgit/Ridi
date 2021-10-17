package saveteam.com.quagiang.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.quagiang.R;
import saveteam.com.quagiang.firebase.FirebaseUtils;
import saveteam.com.quagiang.firebase.model.BookingFB;
import saveteam.com.quagiang.firebase.model.BookingListFB;
import saveteam.com.quagiang.firebase.model.ConfirmListFB;
import saveteam.com.quagiang.firebase.model.ProfileFB;
import saveteam.com.quagiang.presentation.TrackingActivity;
import saveteam.com.quagiang.utils.activity.ActivityUtils;
import saveteam.com.quagiang.utils.activity.DateTimeUtils;
import saveteam.com.quagiang.utils.activity.NumberUtils;
import saveteam.com.quagiang.utils.activity.SharedRefUtils;

public class BookingRideAdapter extends RecyclerView.Adapter<BookingRideAdapter.BookingRideHolder> {
    private Activity activity;
    private List<BookingFB> bookings;
    private List<ProfileFB> profiles;

    public BookingRideAdapter(Activity activity, List<BookingFB> bookings, List<ProfileFB> profiles) {
        this.activity = activity;
        this.bookings = bookings;
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public BookingRideHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_booking_ride_where_ride, viewGroup, false );
        return new BookingRideHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookingRideHolder bookingRideHolder, int i) {
        final BookingFB bookingFB = bookings.get(i);
        final ProfileFB profileFB = profiles.get(i);

        bookingRideHolder.tv_driver_name.setText(bookingFB.getOfferRideName());
        bookingRideHolder.tv_distance.setText(NumberUtils.format(bookingFB.getDistance()) + "km");
        bookingRideHolder.tv_cost.setText(NumberUtils.formatMoney(bookingFB.getCost())+" thousand VND");
        bookingRideHolder.tv_from_place.setText(bookingFB.getOfferRideFromPlace());
        bookingRideHolder.tv_to_place.setText(bookingFB.getOfferRideToPlace());

        bookingRideHolder.tv_from_time.setText(DateTimeUtils.getShortDate(bookingFB.getOfferRideFromTime()));
        bookingRideHolder.tv_to_time.setText(DateTimeUtils.getShortDate(bookingFB.getOfferRideToTime()));

        FirebaseUtils.downloadImageFile(bookingFB.getOfferRideId(), new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Glide.with(activity).load(bitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.5f)
                                .into(bookingRideHolder.iv_avatar_driver);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        bookingRideHolder.rt_driver.setRating(profileFB.getRating());

        bookingRideHolder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uid = SharedRefUtils.getUid(activity);
                String roomId = bookingFB.getFindRideId().hashCode() + bookingFB.getOfferRideId().hashCode() + "";
                Intent intent = new Intent(activity, TrackingActivity.class);
                intent.putExtra("booking", bookingFB);
                intent.putExtra("profile", profileFB);
                activity.startActivity(intent);
            }
        });

        bookingRideHolder.btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRefBookings = FirebaseDatabase.getInstance().getReference(BookingListFB.DB_IN_FB);
                final DatabaseReference dbRefConfirms = FirebaseDatabase.getInstance().getReference(ConfirmListFB.DB_IN_FB);
                dbRefBookings.child(bookingFB.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ActivityUtils.displayToast(activity, "Cancel booking successfully");
                            bookings.remove(bookingFB);
                            profiles.remove(profileFB);
                            notifyDataSetChanged();
                        } else {
                            ActivityUtils.displayToast(activity, "Cancel booking fail");
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (bookings == null) {
            return 0;
        }

        return bookings.size();
    }

    public class BookingRideHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_from_place_where_booking_ride)
        TextView tv_from_place;
        @BindView(R.id.tv_to_place_where_booking_ride)
        TextView tv_to_place;

        @BindView(R.id.tv_driver_name_where_booking_ride)
        TextView tv_driver_name;
        @BindView(R.id.tv_distance_where_booking_ride)
        TextView tv_distance;
        @BindView(R.id.tv_cost_where_booking_ride)
        TextView tv_cost;

        @BindView(R.id.iv_avatar_driver_where_booking_ride)
        ImageView iv_avatar_driver;
        @BindView(R.id.rt_driver_where_booking_ride)
        RatingBar rt_driver;
        @BindView(R.id.tv_vehicle_where_booking_ride)
        TextView tv_vehicle;

        @BindView(R.id.tv_from_time_where_booking_ride)
        TextView tv_from_time;
        @BindView(R.id.tv_to_time_where_booking_ride)
        TextView tv_to_time;

        @BindView(R.id.btn_ok_where_booking_ride)
        AppCompatButton btn_ok;
        @BindView(R.id.btn_cancel_where_booking_ride)
        AppCompatButton btn_cancel;

        public BookingRideHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
