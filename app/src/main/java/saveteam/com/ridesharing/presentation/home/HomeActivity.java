package saveteam.com.ridesharing.presentation.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.DateTimePickerUtils;

public class HomeActivity extends AppCompatActivity {
    @BindView(R.id.actv_from_where_home)
    AutoCompleteTextView actv_from;
    @BindView(R.id.actv_to_where_home)
    AutoCompleteTextView actv_to;
    @BindView(R.id.btn_time_from_where_home)
    AppCompatButton btn_time_from;

    private DateTimePickerUtils startTime;


    private List<String> areaList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        areaList = new ArrayList<>();
        areaList.addAll(Arrays.asList(getResources().getStringArray(R.array.list_area)));
        ArrayAdapter<String>  adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item,areaList );
        actv_from.setThreshold(1);
        actv_from.setFilters(new InputFilter[]{

        });
        actv_from.setAdapter(adapter);
        actv_to.setThreshold(1);
        actv_to.setAdapter(adapter);

        startTime = new DateTimePickerUtils(this);
        startTime.initDateTimePicker("time",new DateTimePickerUtils.DateTimeListener() {
            @Override
            public void onPositiveButtonClick(Date date, String tagFragment) {

                btn_time_from.setText(ActivityUtils.getDateTimeFormat().format(date));
            }

            @Override
            public void onNegativeButtonClick(Date date, String tagFragment) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date, String tagFragment) {
                // Optional if neutral button does'nt exists
                btn_time_from.setText("");
            }
        });
    }

    @OnClick(R.id.btn_time_from_where_home)
    public void clickTimeFrom(View view) {
        startTime.openDateTimePicker();
    }
}
