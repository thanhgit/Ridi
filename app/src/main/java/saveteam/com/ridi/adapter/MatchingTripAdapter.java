package saveteam.com.ridi.adapter;

import android.content.Context;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridi.firebase.FirebaseUtils;
import saveteam.com.ridi.firebase.model.ProfileFB;
import saveteam.com.ridi.firebase.model.TripFB;
import saveteam.com.ridi.model.FindTripDTO;
import saveteam.com.ridi.presentation.DisplayMapActivity;
import saveteam.com.ridi.R;
import saveteam.com.ridi.utils.activity.DataManager;
import saveteam.com.ridi.utils.activity.NumberUtils;

public class MatchingTripAdapter extends RecyclerView.Adapter<MatchingTripAdapter.MatchingTripHolder> {
    private List<TripFB> trips;
    private List<ProfileFB> profiles;
    private Context context;
    private FindTripDTO findTripDTO;


    public MatchingTripAdapter(List<TripFB> trips, List<ProfileFB> profiles, Context context) {
        this.trips = trips;
        this.context = context;
        this.profiles = profiles;
    }

    public void setFindTripDTO(FindTripDTO findTripDTO) {
        this.findTripDTO = findTripDTO;
    }

    @NonNull
    @Override
    public MatchingTripHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_trip_where_home, viewGroup, false);
        return new MatchingTripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MatchingTripHolder matchingTripHolder, int i) {
        final TripFB trip = this.trips.get(i);
        final ProfileFB profile = this.profiles.get(i);

        DataManager.getInstance().setFindRideTrip(trip);
        DataManager.getInstance().setProfileMatching(profile);

        FirebaseUtils.downloadImageFile(trip.getUid(), new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        if (bytes != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Glide.with(context).load(bitmap)
                                    .apply(RequestOptions.circleCropTransform())
                                    .thumbnail(0.5f)
                                    .into(matchingTripHolder.iv_avatar);
                        }

                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        matchingTripHolder.tv_user_name.setText(profile.getFirstName() + " " + profile.getLastName());
        String txtPercent = (NumberUtils.format(findTripDTO.getPercentByUid(trip.getUid())*100)) + "% route";
        matchingTripHolder.tv_percent.setText(txtPercent);

        String[] officePlace = profile.getOfficePlace().split("\\|");
        matchingTripHolder.tv_company.setText(officePlace.length > 0 ? officePlace[0] : "");

        String[] homePlace = profile.getHomePlace().split("\\|");
        matchingTripHolder.tv_address.setText(homePlace.length > 0 ? homePlace[0] : "");

        matchingTripHolder.tv_start_time.setText(trip.getStartTime());

        matchingTripHolder.btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayMapActivity.class);
                intent.putExtra("data", trip);
                //intent.putExtra("tripSearch", findTripDTO.getTripSearch());
                DataManager.getInstance().setFindRideTrip(findTripDTO.getTripSearch());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class MatchingTripHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_user_name_where_item_trip)
        TextView tv_user_name;
        @BindView(R.id.tv_percent_where_item_trip)
        TextView tv_percent;
        @BindView(R.id.tv_user_company_where_item_trip)
        TextView tv_company;
        @BindView(R.id.iv_avatar_where_item_trip)
        ImageView iv_avatar;
        @BindView(R.id.tv_user_address_where_item_trip)
        TextView tv_address;
        @BindView(R.id.tv_start_time_where_item_trip)
        TextView tv_start_time;

        @BindView(R.id.btn_choose_where_item_trip)
        AppCompatButton btn_choose;

        public MatchingTripHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
