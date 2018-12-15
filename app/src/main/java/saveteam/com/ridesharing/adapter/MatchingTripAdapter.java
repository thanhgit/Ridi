package saveteam.com.ridesharing.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.model.Matching;
import saveteam.com.ridesharing.model.Trip;

public class MatchingTripAdapter extends RecyclerView.Adapter<MatchingTripAdapter.MatchingTripHolder> {
    private List<Trip> trips;
    private Context context;

    public MatchingTripAdapter(List<Trip> trips, Context context) {
        this.trips = trips;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchingTripHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_trip_where_home, viewGroup, false);
        return new MatchingTripHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchingTripHolder matchingTripHolder, int i) {
        final Trip trip = this.trips.get(i);
        matchingTripHolder.tv_user_name.setText(trip.userName);
        matchingTripHolder.btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
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
        @BindView(R.id.btn_choose_where_item_trip)
        AppCompatButton btn_choose;

        public MatchingTripHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
