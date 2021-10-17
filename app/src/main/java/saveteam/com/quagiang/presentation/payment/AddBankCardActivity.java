package saveteam.com.quagiang.presentation.payment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.quagiang.R;

public class AddBankCardActivity extends AppCompatActivity {
    @BindView(R.id.txt_number_card_where_add_bank_card)
    EditText txt_number_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_card);
        ButterKnife.bind(this);

    }
}
