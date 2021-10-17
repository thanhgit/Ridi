package saveteam.com.quagiang.presentation.home;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.quagiang.R;
import saveteam.com.quagiang.firebase.model.FeedbackFB;
import saveteam.com.quagiang.utils.activity.ActivityUtils;
import saveteam.com.quagiang.utils.activity.SharedRefUtils;

public class FeedbackActivity extends AppCompatActivity {
    @BindView(R.id.txt_title_where_feedback)
    EditText txt_title;
    @BindView(R.id.txt_description_where_feedback)
    EditText txt_description;
    @BindView(R.id.rt_satisfaction_level_where_feedback)
    RatingBar rt_satisfaction_level;
    @BindView(R.id.btn_submit_where_feedback)
    AppCompatButton btn_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.activity_title_feedback));

    }

    @OnClick(R.id.btn_submit_where_feedback)
    public void clickSubmit(View view) {
        if (!txt_title.getText().toString().trim().equals("")
                && !txt_description.getText().toString().trim().equals("")) {
            String uid = SharedRefUtils.getUid(this);
            String email = SharedRefUtils.getEmail(this);
            DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference(FeedbackFB.DB_IN_FB);
            FeedbackFB feedbackFB = new FeedbackFB(uid, email, txt_title.getText().toString().trim(),
                    txt_description.getText().toString().trim(),
                    (int) rt_satisfaction_level.getRating());
            feedbackRef.child(uid)
                    .setValue(feedbackFB)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                ActivityUtils.displayToast(FeedbackActivity.this, "Phản hồi hệ thống thành công ");
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                };
                                handler.postDelayed(runnable, 1000);
                            } else {
                                ActivityUtils.displayToast(FeedbackActivity.this, "Lỗi kết nối internet. Bạn vui lòng kiểm tra internet và hãy phản hồi lại cho chúng tôi để có trải nghiệm tốt hơn");
                            }
                        }
                    });
        } else {
            ActivityUtils.displayToast(this, "Vui lòng nhập thông tin đầy đủ để chúng tôi hiểu vấn đề của bạn!");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
