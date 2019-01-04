package saveteam.com.ridesharing.presentation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.SearchPlaceAdapter;
import saveteam.com.ridesharing.database.RidesharingDB;
import saveteam.com.ridesharing.database.model.SearchPlaceHistory;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.BasicMapActivity;
import saveteam.com.ridesharing.utils.google.S2Utils;
import saveteam.com.ridesharing.utils.here.SearchPlace;
import saveteam.com.ridesharing.utils.here.SearchPlaceImpl;

public class SearchPlaceActivity extends BasicMapActivity {

    @BindView(R.id.drawer_layout_where_search_place)
    DrawerLayout drawerLayout;
    @BindView(R.id.rv_place_name_where_search_place)
    RecyclerView rv_place_name;
    @BindView(R.id.txt_search_where_search_place)
    EditText txt_search;
    @BindView(R.id.ibtn_close_where_search_place)
    ImageButton ibtn_close;
    @BindView(R.id.ibtn_menu_where_search_place)
    ImageButton ibtn_menu;
    @BindView(R.id.navigation_view_where_search_place)
    NavigationView navigationView;


    SearchPlaceAdapter searchPlaceAdapter;

    SearchPlace searchPlace;
    DiscoveryResultPage searchResults;
    List<DiscoveryResult> results;
    String querySearch = "";

    String titlePlaceName = "";

    MapMarker marker;

    public interface OnSelectMarkerListener {
        void selected(MapMarker marker);
    }

    @Override
    public void addInteraction() {
        if (mapFragment != null) {
            mapFragment.getMapGesture().addOnGestureListener(new MyOnGestureListener(new OnSelectMarkerListener() {
                @Override
                public void selected(MapMarker marker) {

                }
            }), 0, true);
        }

    }

    @Override
    public void addView() {
        setContentView(R.layout.activity_search_place);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_where_search_place_activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        results = new ArrayList<>();

        configRecycleView();
        searchPlaceAdapter = new SearchPlaceAdapter(getApplicationContext(), results);
        rv_place_name.setAdapter(searchPlaceAdapter);

        searchPlaceAdapter.setOnClickItemSearchPlaceListener(new SearchPlaceAdapter.OnClickItemSearchPlaceListener() {
            @Override
            public void selected(int position) {
                if (searchResults != null) {
                    GeoCoordinate geo = searchResults.getPlaceLinks().get(position).getPosition();
                    titlePlaceName = searchResults.getPlaceLinks().get(position).getTitle();
                    createMarker(geo);
                    map.setCenter(marker.getCoordinate(), Map.Animation.NONE);
                    map.setZoomLevel(17);
                    results.clear();
                    searchPlaceAdapter.notifyDataSetChanged();

                    clickButtonChoose();
                }
            }
        });

        initSearchView();

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (!query.trim().equals("") && !query.equals(querySearch)){
                    searchResults = searchPlace.searchPlaceByName(query, new SearchPlaceImpl.SearchResultListener() {
                        @Override
                        public void done(DiscoveryResultPage discoveryResultPage) {
                            if (discoveryResultPage != null) {
                                searchResults = discoveryResultPage;
                                List<String> strings = new ArrayList<>();
                                for (DiscoveryResult item : discoveryResultPage.getItems()) {
                                    String str = item.getTitle();
                                    strings.add(str);
                                    ActivityUtils.displayLog("title is: " + str);
                                }

                                results.clear();
                                results.addAll(discoveryResultPage.getItems());
                                searchPlaceAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                    querySearch = query;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
               if (s.length() > 0) {
                   ibtn_close.setVisibility(View.VISIBLE);
               } else {
                   ibtn_close.setVisibility(View.GONE);
                   results.clear();
                   searchPlaceAdapter.notifyDataSetChanged();
               }
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                ActivityUtils.hideKeyboard(SearchPlaceActivity.this, txt_search);
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                ActivityUtils.showKeyboard(SearchPlaceActivity.this, txt_search);
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                txt_search.setText(menuItem.getTitle());
                drawerLayout.closeDrawer(Gravity.LEFT);
                return true;
            }
        });
    }

    @OnClick(R.id.ibtn_menu_where_search_place)
    public void clickButtonMenu(View view) {
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.ibtn_close_where_search_place)
    public void clickButtonClose(View view) {
        txt_search.setText("");
    }

    public void clickButtonChoose() {
        if (marker != null) {
            final double lat = marker.getCoordinate().getLatitude();
            final double lng = marker.getCoordinate().getLongitude();
            final long cellId = S2Utils.getCellId(lat, lng).id();
            Geo geo = new Geo(lat, lng, cellId);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    RidesharingDB.getInstance(SearchPlaceActivity.this)
                            .getSearchPlaceHistoryDao()
                            .insertSearchPlaceHistorys(new SearchPlaceHistory(lat, lng, cellId, titlePlaceName ));
                }
            });
            Intent resultIntent = new Intent();
            resultIntent.putExtra("data", geo);
            resultIntent.putExtra("title", titlePlaceName);
            setResult(RESULT_OK, resultIntent );
            finish();
        } else {
            ActivityUtils.displayToast(this, "You haven't place");
        }

    }

    private void configRecycleView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_place_name.addItemDecoration( new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        rv_place_name.setItemAnimator( new DefaultItemAnimator());
        rv_place_name.setHasFixedSize(true);
        rv_place_name.setLayoutManager(layoutManager);
    }

    private void initSearchView() {
        searchPlace = new SearchPlaceImpl(new GeoCoordinate(10.789148,106.6615424));
    }


    public void createMarker(GeoCoordinate coordinate) {
        marker = new MapMarker(coordinate, ActivityUtils.getMarker());
        map.addMapObject(marker);
    }

    @Override
    protected void onStart() {
        super.onStart();

        StartTask startTask = new StartTask(this, new StartTask.GetSearchPlaceHistoryListener() {
            @Override
            public void done(List<SearchPlaceHistory> histories) {
                createMenu(histories);
            }
        });
        startTask.execute();
    }

    public void createMenu(List<SearchPlaceHistory> histories){
        Menu menu = navigationView.getMenu();
        menu.clear();
        Menu submenu = menu.addSubMenu("History");
        for (SearchPlaceHistory history : histories) {
            submenu.add(history.getTitle());
        }

        navigationView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        txt_search.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private static class StartTask extends AsyncTask<Void, Void, Void> {
        Activity activity;
        ProgressDialog progressDialog;
        GetSearchPlaceHistoryListener listener;

        public interface GetSearchPlaceHistoryListener {
            void done(List<SearchPlaceHistory> histories);
        }

        public StartTask(Activity activity, GetSearchPlaceHistoryListener listener) {
            this.activity = activity;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<SearchPlaceHistory> histories;
            histories = RidesharingDB.getInstance(activity).getSearchPlaceHistoryDao().loadAllSearchPlaceHistorys();
            listener.done(histories);
            return null;
        }
    }

    private class MyOnGestureListener implements MapGesture.OnGestureListener {
        private OnSelectMarkerListener listener;

        public MyOnGestureListener(OnSelectMarkerListener listener) {
            this.listener = listener;
        }

        public MyOnGestureListener() {
        }

        @Override
        public void onPanStart() {
        }

        @Override
        public void onPanEnd() {
        }

        @Override
        public void onMultiFingerManipulationStart() {
        }

        @Override
        public void onMultiFingerManipulationEnd() {
        }

        @Override
        public boolean onMapObjectsSelected(List<ViewObject> objects) {
            return false;
        }

        @Override
        public boolean onTapEvent(PointF p) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF p) {
            return false;
        }

        @Override
        public void onPinchLocked() {
        }

        @Override
        public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
            return false;
        }

        @Override
        public void onRotateLocked() {
        }

        @Override
        public boolean onRotateEvent(float rotateAngle) {
            return false;
        }

        @Override
        public boolean onTiltEvent(float angle) {
            return false;
        }

        @Override
        public boolean onLongPressEvent(PointF p) {
            if (marker != null) {
                map.removeMapObject(marker);
            }

            createMarker(map.pixelToGeo(p));
            titlePlaceName = "";
            return false;
        }

        @Override
        public void onLongPressRelease() {
        }

        @Override
        public boolean onTwoFingerTapEvent(PointF p) {
            return false;
        }
    }
}
