package saveteam.com.ridesharing.presentation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.SearchPlaceAdapter;
import saveteam.com.ridesharing.database.RidesharingDB;
import saveteam.com.ridesharing.database.model.SearchPlaceHistory;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.server.ApiUtils;
import saveteam.com.ridesharing.server.model.searchplacewithtext.Result;
import saveteam.com.ridesharing.server.model.searchplacewithtext.SearchPlaceWithTextResponse;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.google.S2Utils;

public class SearchPlaceActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.drawer_layout_where_search_place)
    DrawerLayout drawerLayout;
    @BindView(R.id.rv_place_name_where_search_place)
    RecyclerView rv_place_name;
    @BindView(R.id.txt_search_where_search_place)
    EditText txt_search;
    @BindView(R.id.ibtn_close_where_search_place)
    ImageView ibtn_close;
    @BindView(R.id.ibtn_menu_where_search_place)
    ImageView ibtn_menu;
    @BindView(R.id.navigation_view_where_search_place)
    NavigationView navigationView;

    @BindView(R.id.iv_search_location_where_search_place)
    ImageView iv_search_location;


    SearchPlaceAdapter searchPlaceAdapter;

    String querySearch = "";

    /**
     * data
     */
    String titlePlaceName = "";
    Geo choose;
    String placeId;

    List<SearchPlaceHistory> histories;

    List<Result> results;

    private GoogleMap mMap;
    private Location myLocation;
    private LatLng center;
    private boolean searchLocation = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng here = new LatLng(10.8659698,106.8107944);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, 13));

        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (searchLocation) {
                    mMap.clear();
                    center = mMap.getCameraPosition().target;

                    mMap.addMarker(new MarkerOptions()
                            .position(center)
                            .title("Your position"));
                }

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (searchLocation) {
                    mMap.clear();
                    if (mMap != null && searchLocation) {
                        center = mMap.getCameraPosition().target;

                        mMap.addMarker(new MarkerOptions()
                                .position(center)
                                .title("Your position"));

                        Call<SearchPlaceWithTextResponse> searchPlaceWithTextResponseCall = ApiUtils.getServerGoogleMapApi()
                                .searchPlaceWithText(center.latitude+","+center.longitude, getResources().getString(R.string.google_maps_key));
                        searchPlaceWithTextResponseCall.enqueue(new Callback<SearchPlaceWithTextResponse>() {
                            @Override
                            public void onResponse(Call<SearchPlaceWithTextResponse> call, Response<SearchPlaceWithTextResponse> response) {
                                if (response.isSuccessful()) {
                                    SearchPlaceWithTextResponse placeResponse = response.body();
                                    if (placeResponse.getResults().size() > 0) {
                                        Result result = placeResponse.getResults().get(0);
                                        titlePlaceName = result.getName();
                                        choose = new Geo(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), 0);
                                        txt_search.setText(result.getFormattedAddress());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<SearchPlaceWithTextResponse> call, Throwable t) {

                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        ButterKnife.bind(this);

        results = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_where_search_place_activity);
        mapFragment.getMapAsync(this);

        configRecycleView();
        searchPlaceAdapter = new SearchPlaceAdapter(getApplicationContext(), results);
        rv_place_name.setAdapter(searchPlaceAdapter);

        searchPlaceAdapter.setOnClickItemSearchPlaceListener(new SearchPlaceAdapter.OnClickItemSearchPlaceListener() {
            @Override
            public void selected(Result result) {
                placeId = result.getPlaceId();
                titlePlaceName = result.getName();
                choose = new Geo(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), 0);
                clickButtonChoose();
            }
        });

        initSearchView();

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.trim().equals("") && !query.equals(querySearch)){

                    retrofit2.Call<SearchPlaceWithTextResponse> searchPlaceWithTextResponseCall = ApiUtils.getServerGoogleMapApi()
                            .searchPlaceWithText(query.replaceAll(" ", "+").toLowerCase(),
                                    getResources().getString(R.string.google_maps_key));
                    searchPlaceWithTextResponseCall.enqueue(new Callback<SearchPlaceWithTextResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<SearchPlaceWithTextResponse> call, Response<SearchPlaceWithTextResponse> response) {
                            if (response.isSuccessful()) {
                                results.clear();
                                results.addAll(response.body().getResults());
                                searchPlaceAdapter.notifyDataSetChanged();

                                for (Result result : response.body().getResults()) {
                                    ActivityUtils.displayLog(result.getFormattedAddress());
                                }
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<SearchPlaceWithTextResponse> call, Throwable t) {

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
                   searchPlaceAdapter.notifyDataSetChanged();
               }
            }
        });

        txt_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // ActivityUtils.displayToast(SearchPlaceActivity.this, "Doing proccess");
                    return true;
                }
                return false;
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

        StartTask startTask = new StartTask(this, new StartTask.GetSearchPlaceHistoryListener() {
            @Override
            public void done(List<SearchPlaceHistory> _histories) {
                histories = _histories;
            }
        });
        startTask.execute();
    }

    @OnClick(R.id.iv_my_location_where_search_place)
    public void clickMyLocation(View view) {
        mMap.clear();
        if (mMap != null) {
            myLocation = mMap.getMyLocation();

            if (myLocation != null) {

                retrofit2.Call<SearchPlaceWithTextResponse> placeResponseCall = ApiUtils.getServerGoogleMapApi()
                        .searchPlaceWithText(myLocation.getLatitude()+","+myLocation.getLongitude(), getResources().getString(R.string.google_maps_key));
                placeResponseCall.enqueue(new Callback<SearchPlaceWithTextResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<SearchPlaceWithTextResponse> call, Response<SearchPlaceWithTextResponse> response) {
                        if (response.isSuccessful()) {
                            SearchPlaceWithTextResponse placeResponse = response.body();
                            if (placeResponse.getResults().size() > 0) {
                                Result result = placeResponse.getResults().get(0);
                                titlePlaceName = result.getName();
                                choose = new Geo(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), 0);
                                txt_search.setText(result.getName());
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<SearchPlaceWithTextResponse> call, Throwable t) {
                        ActivityUtils.displayToast(SearchPlaceActivity.this, t.getMessage());
                    }
                });
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                        .zoom(13)
                        .build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                mMap.animateCamera(cameraUpdate);
            }

        }
    }

    @OnClick(R.id.iv_search_location_where_search_place)
    public void clickSearchLocation(View view) {
        if (searchLocation) {
            searchLocation = false;
            iv_search_location.setBackgroundResource(R.drawable.ic_edit_location_black_24dp);
            if (mMap != null) {
                mMap.clear();
            }
        } else {
            ActivityUtils.hideKeyboard(this, txt_search);
            searchLocation = true;
            iv_search_location.setBackgroundResource(R.drawable.ic_edit_location_pink_400_24dp);
            mMap.clear();
            center = mMap.getCameraPosition().target;

            mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .title("Your position"));
        }
    }

    @OnClick(R.id.ibtn_menu_where_search_place)
    public void clickButtonMenu(View view) {
        createMenu(this.histories);
        drawerLayout.openDrawer(Gravity.LEFT);
    }

    @OnClick(R.id.ibtn_close_where_search_place)
    public void clickButtonClose(View view) {
        txt_search.setText("");
        results.clear();
        searchPlaceAdapter.notifyDataSetChanged();
    }

    public void clickButtonChoose() {
        if (choose != null) {
            final long cellId = S2Utils.getCellId(choose.lat, choose.lng).id();
            Geo geo = new Geo(choose.lat, choose.lng, cellId);

            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    RidesharingDB.getInstance(SearchPlaceActivity.this)
                            .getSearchPlaceHistoryDao()
                            .insertSearchPlaceHistorys(new SearchPlaceHistory(choose.lat, choose.lng, cellId, titlePlaceName ));
                }
            });
            Intent resultIntent = new Intent();
            resultIntent.putExtra("data", geo);
            resultIntent.putExtra("title", titlePlaceName);
            resultIntent.putExtra("placeId", placeId);
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

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    public void createMenu(List<SearchPlaceHistory> histories){
        Menu menu = navigationView.getMenu();
        menu.clear();
        if (histories.size() > 0 && menu != null) {
            Menu submenu = menu.addSubMenu("History");
            for (int index = 0; index < histories.size(); index++) {

                MenuItem menuItem = submenu.add(0, index, index, histories.get(index).getTitle());
                menuItem.setIcon(R.drawable.to_place);
            }

            navigationView.invalidate();
        }

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
}
