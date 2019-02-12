package saveteam.com.ridi.presentation.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridi.R;

public class HistoryRideFragment extends Fragment {

    public HistoryRideFragment() {
        // Required empty public constructor
    }

    public static HistoryRideFragment newInstance() {
        HistoryRideFragment fragment = new HistoryRideFragment();
        return fragment;
    }

    @BindView(R.id.rv_history_ride)
    RecyclerView rv_history_ride;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_ride, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
