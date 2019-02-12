package saveteam.com.ridi.presentation.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridi.R;

public class NoInternetFragment extends DialogFragment {

    public static final String FRAGMENT_TAG = NoInternetFragment.class.getSimpleName();

    public NoInternetFragment() {

    }

    public static NoInternetFragment newInstance() {
        NoInternetFragment fragment = new NoInternetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @OnClick(R.id.btn_open_internet_where_no_internet)
    public void clickOpenInternet(View view) {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_internet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
