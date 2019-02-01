package saveteam.com.ridesharing.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.model.ConfirmFB;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.presentation.TrackingActivity;
import saveteam.com.ridesharing.presentation.chat.ChatActivity;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class RequestRideAdapter extends RecyclerView.Adapter<RequestRideAdapter.RequestRideHolder> {
    private Activity activity;
    private List<ConfirmFB> confirms;
    private List<ProfileFB> profiles;

    public RequestRideAdapter(Activity activity, List<ConfirmFB> confirms, List<ProfileFB> profiles) {
        this.activity = activity;
        this.confirms = confirms;
        this.profiles = profiles;
    }

    @NonNull
    @Override
    public RequestRideHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_request_ride_where_ride, viewGroup, false );
        return new RequestRideHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestRideHolder requestRideHolder, int i) {
        final ConfirmFB confirmFB = confirms.get(i);
        final ProfileFB profileFB = profiles.get(i);

        requestRideHolder.tv_ride_name.setText(confirmFB.getFindRideName());
        requestRideHolder.tv_distance.setText(confirmFB.getDistance() + "km");
        requestRideHolder.tv_cost.setText(confirmFB.getCost()+" thousand VND");

        requestRideHolder.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.displayToast(activity, "successfully");
                String uid = SharedRefUtils.getUid(activity);
                String roomId = confirmFB.getFindRideId().hashCode() + confirmFB.getOfferRideId().hashCode() + "";
//                Intent intent = new Intent(activity, ChatActivity.class);
//                intent.putExtra("data", roomId);
//                intent.putExtra("profile", profileFB);
//                activity.startActivity(intent);
                Intent intent = new Intent(activity, TrackingActivity.class);
                activity.startActivity(intent);
            }
        });

        requestRideHolder.btn_canel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.displayToast(activity, "cancel");
            }
        });
    }

    @Override
    public int getItemCount() {
        return confirms.size();
    }

    public class RequestRideHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_ride_name_where_item_request_ride)
        TextView tv_ride_name;
        @BindView(R.id.tv_distance_where_item_request_ride)
        TextView tv_distance;
        @BindView(R.id.tv_cost_where_item_request_ride)
        TextView tv_cost;

        @BindView(R.id.btn_ok_where_item_request_ride)
        AppCompatButton btn_ok;
        @BindView(R.id.btn_cancel_where_item_request_ride)
        AppCompatButton btn_canel;

        public RequestRideHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
