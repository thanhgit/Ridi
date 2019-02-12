package saveteam.com.ridi.presentation.splashscreen;

import android.os.Bundle;
import android.widget.Toast;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;

import java.util.ArrayList;
import java.util.List;

import saveteam.com.ridi.R;
import saveteam.com.ridi.utils.activity.ActivityUtils;
import saveteam.com.ridi.utils.activity.SharedRefUtils;

public class OnBoardingActivity extends OnboarderActivity {
    List<OnboarderPage> onboarderPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupOnboard();
    }

    @Override
    public void onFinishButtonPressed() {
        SharedRefUtils.saveOnboarding(this);
        ActivityUtils.changeActivity(this, LoginActivity.class);
    }

    @Override
    protected void onSkipButtonPressed() {
        super.onSkipButtonPressed();
        Toast.makeText(this, "Skip button was pressed!", Toast.LENGTH_SHORT).show();
    }

    private void setupOnboard() {
        OnboarderPage onboarderPage1 = new OnboarderPage("Secure", "Security is all", R.drawable.bg_security);
        OnboarderPage onboarderPage2 = new OnboarderPage("Good UX, useful", "Easy to use ", R.drawable.bg_easy_to_use);
        OnboarderPage onboarderPage3 = new OnboarderPage("Saving money", "Saving to other investment", R.drawable.bg_saving_money);

        onboarderPage1.setBackgroundColor(R.color.gray);
        onboarderPage2.setBackgroundColor(R.color.gray);
        onboarderPage3.setBackgroundColor(R.color.gray);

        List<OnboarderPage> pages = new ArrayList<>();

        pages.add(onboarderPage1);
        pages.add(onboarderPage2);
        pages.add(onboarderPage3);

        for (OnboarderPage page : pages) {
            page.setTitleColor(R.color.primaryColor);
            page.setDescriptionColor(R.color.secondaryColor);
        }

        setSkipButtonTitle("Skip");
        setFinishButtonTitle("Finish");

        setOnboardPagesReady(pages);
    }
}
