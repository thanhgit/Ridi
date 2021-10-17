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
import saveteam.com.quagiang.firebase.model.ConfirmFB;
import saveteam.com.quagiang.firebase.model.ConfirmListFB;
import saveteam.com.quagiang.firebase.model.ProfileFB;
import saveteam.com.quagiang.presentation.TrackingActivity;
import saveteam.com.quagiang.utils.activity.ActivityUtils;
import saveteam.com.quagiang.utils.activity.DateTimeUtils;
import saveteam.com.quagiang.utils.activity.NumberUtils;
import saveteam.com.quagiang.utils.activity.SharedRefUtils;

public class OfferRideAdapter extends RecyclerView.Adapter<OfferRideAdapter.OfferRideHolder> {
    private Activity activity;
    private List<ConfirmFB> confirms;
    private List<ProfileFB> profiles;

    public OfferRideAdapter(Activity activity, List<ConfirmFB> confirms, List<ProfileFB> profiles) {
        this.activity = activity;
        this.confirms = confirms;
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public OfferRideHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_offer_ride_where_ride, viewGroup, false );
        return new OfferRideHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfferRideHolder offerRideHolder, int i) {
        final ConfirmFB confirmFB = confirms.get(i);
        final ProfileFB profileFB = profiles.get(i);

        offerRideHolder.tv_rider_name.setText(confirmFB.getFindRideName());
        offerRideHolder.tv_distance.setText(NumberUtils.format(confirmFB.getDistance()) + "km");
        offerRideHolder.tv_cost.setText(NumberUtils.formatMoney(confirmFB.getCost()) + "thousand VND");
        offerRideHolder.tv_from_place.setText(confirmFB.getFindRideFromPlace());
        offerRideHolder.tv_to_place.setText(confirmFB.getFindRideToPlace());
        offerRideHolder.tv_note.setText("nothing");

        offerRideHolder.tv_from_time.setText(DateTimeUtils.getShortDate(confirmFB.getFindRideFromTime()));
        offerRideHolder.tv_to_time.setText(DateTimeUtils.getShortDate(confirmFB.getFindRideToTime()));


        FirebaseUtils.downloadImageFile(confirmFB.getFindRideId(), new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Glide.with(activity).load(bitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.5f)
                                .into(offerRideHolder.iv_avatar_rider);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        offerRideHolder.rt_rider.setRating(profileFB.getRating());

        offerRideHolder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityUtils.displayToast(activity, "successfully");
                String uid = SharedRefUtils.getUid(activity);
                String roomId = confirmFB.getFindRideId().hashCode() + confirmFB.getOfferRideId().hashCode() + "";
//                Intent intent = new Intent(activity, ChatActivity.class);
//                intent.putExtra("data", roomId);
//                intent.putExtra("profile", profileFB);
//                activity.startActivity(intent);
                Intent intent = new Intent(activity, TrackingActivity.class);
                intent.putExtra("confirm", confirmFB);
                intent.putExtra("profile", profileFB);
                activity.startActivity(intent);
            }
        });

        offerRideHolder.btn_canel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbRefOfferRide = FirebaseDatabase.getInstance().getReference(ConfirmListFB.DB_IN_FB);
                dbRefOfferRide.child(confirmFB.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ActivityUtils.displayToast(activity, "Cancel confirm successfully");
                            confirms.remove(confirmFB);
                            profiles.remove(profileFB);
                            notifyDataSetChanged();
                        } else {
                            ActivityUtils.displayToast(activity, "Cancel confirm fail");
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        if (confirms == null) {
            return 0;
        }

        return confirms.size();
    }

    public class OfferRideHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_from_place_where_offer_ride)
        TextView tv_from_place;
        @BindView(R.id.tv_to_place_where_offer_ride)
        TextView tv_to_place;

        @BindView(R.id.tv_rider_name_where_offer_ride)
        TextView tv_rider_name;
        @BindView(R.id.tv_distance_where_offer_ride)
        TextView tv_distance;
        @BindView(R.id.tv_cost_where_offer_ride)
        TextView tv_cost;
        @BindView(R.id.tv_note_where_offer_ride)
        TextView tv_note;
        @BindView(R.id.iv_avatar_rider_where_booking_ride)
        ImageView iv_avatar_rider;
        @BindView(R.id.rt_rider_where_offer_ride)
        RatingBar rt_rider;

        @BindView(R.id.tv_from_time_where_offer_ride)
        TextView tv_from_time;
        @BindView(R.id.tv_to_time_where_offer_ride)
        TextView tv_to_time;

        @BindView(R.id.btn_ok_where_offer_ride)
        AppCompatButton btn_ok;
        @BindView(R.id.btn_cancel_where_offer_ride)
        AppCompatButton btn_canel;

        public OfferRideHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
