package saveteam.com.ridi.utils.activity;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import saveteam.com.ridi.R;

public class DateTimePickerUtils {
    private static final String TAG = DateTimePickerUtils.class.getName();
    private FragmentActivity activity;
    private SwitchDateTimeDialogFragment dateTimeFragment;
    private String tagFragment;

    public interface DateTimeListener {
        void onPositiveButtonClick(Date date, String tagFragment);

        void onNegativeButtonClick(Date date, String tagFragment);

        void onNeutralButtonClick(Date date, String tagFragment);
    }

    public DateTimePickerUtils(FragmentActivity context) {
        this.activity = context;
    }

    public void initDateTimePicker(final String tagFragment, final DateTimeListener listener) {
        this.tagFragment = tagFragment;
        dateTimeFragment = (SwitchDateTimeDialogFragment) activity.getSupportFragmentManager().findFragmentByTag(tagFragment);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    activity.getString(R.string.label_datetime_dialog),
                    activity.getString(android.R.string.ok),
                    activity.getString(android.R.string.cancel)
            );
        }

        // Optionally define a timezone
        dateTimeFragment.setTimeZone(TimeZone.getDefault());

        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2029, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                listener.onPositiveButtonClick(date, tagFragment);
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                listener.onNegativeButtonClick(date, tagFragment);
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                listener.onNeutralButtonClick(date, tagFragment);
            }
        });
    }

    public void openDateTimePicker() {
        dateTimeFragment.startAtCalendarView();
        dateTimeFragment.setDefaultDateTime(Calendar.getInstance().getTime());
        dateTimeFragment.show(activity.getSupportFragmentManager(), tagFragment);
    }

    public void openTimePicker() {
        dateTimeFragment.startAtTimeView();
        dateTimeFragment.setDefaultDateTime(Calendar.getInstance().getTime());
        dateTimeFragment.show(activity.getSupportFragmentManager(), tagFragment);
    }

}
