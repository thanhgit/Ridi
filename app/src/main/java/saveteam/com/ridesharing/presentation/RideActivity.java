package saveteam.com.ridesharing.presentation;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.presentation.fragment.CurrentRideFragment;
import saveteam.com.ridesharing.presentation.fragment.HistoryRideFragment;

public class RideActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_where_ride)
    Toolbar toolbar;
    @BindView(R.id.tabs_where_ride)
    TabLayout tabLayout;
    @BindView(R.id.viewpager_where_ride)
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ride");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        RideViewPagerAdapter adapter = new RideViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CurrentRideFragment(), "Current");
        adapter.addFragment(new HistoryRideFragment(), "History");
        viewPager.setAdapter(adapter);
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

    class RideViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> tiles = new ArrayList<>();

        public RideViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            tiles.add(title);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tiles.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
