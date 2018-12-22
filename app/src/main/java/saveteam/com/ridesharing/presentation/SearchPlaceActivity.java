package saveteam.com.ridesharing.presentation;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

import br.com.liveo.searchliveo.SearchLiveo;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.adapter.SearchPlaceAdapter;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.BasicMapActivity;
import saveteam.com.ridesharing.utils.google.S2Utils;
import saveteam.com.ridesharing.utils.here.SearchPlace;
import saveteam.com.ridesharing.utils.here.SearchPlaceImpl;

public class SearchPlaceActivity extends BasicMapActivity implements SearchLiveo.OnSearchListener {

//    @BindView(R.id.toolbar_where_search_place)
//    Toolbar toolbar;
    @BindView(R.id.btn_choose_where_search_place)
    AppCompatButton btn_choose;
    @BindView(R.id.rv_place_name_where_search_place)
    RecyclerView rv_place_name;
    @BindView(R.id.search_liveo_where_search_place)
    SearchLiveo searchLiveo;

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
                    if (btn_choose != null) {
                        btn_choose.setEnabled(true);
                    }
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

//        setSupportActionBar(toolbar);

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
                    searchLiveo.text("");
                    searchLiveo.hideKeyboardAfterSearch();
                }
            }
        });

        initSearchView();
    }

    @Override
    public void changedSearch(CharSequence charSequence) {
        String query = charSequence.toString();
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

    @OnClick(R.id.btn_choose_where_search_place)
    public void clickButtonChoose(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        if (marker != null) {
            double lat = marker.getCoordinate().getLatitude();
            double lng = marker.getCoordinate().getLongitude();
            long cellId = S2Utils.getCellId(lat, lng).id();
            Geo geo = new Geo(lat, lng, cellId);

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
        searchLiveo.with(this).minToSearch(0).searchDelay(1).hideVoice().
                build();
        searchLiveo.show();
    }


    public void createMarker(GeoCoordinate coordinate) {
        marker = new MapMarker(coordinate, ActivityUtils.getMarker());
        map.addMapObject(marker);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
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
            //listener.selected(marker);
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

    private class MyMenuItem implements MenuItem {

        @Override
        public int getItemId() {
            return R.id.action_search_where_search_place_menu;
        }

        @Override
        public int getGroupId() {
            return 0;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public MenuItem setTitle(CharSequence title) {
            return null;
        }

        @Override
        public MenuItem setTitle(int title) {
            return null;
        }

        @Override
        public CharSequence getTitle() {
            return null;
        }

        @Override
        public MenuItem setTitleCondensed(CharSequence title) {
            return null;
        }

        @Override
        public CharSequence getTitleCondensed() {
            return null;
        }

        @Override
        public MenuItem setIcon(Drawable icon) {
            return null;
        }

        @Override
        public MenuItem setIcon(int iconRes) {
            return null;
        }

        @Override
        public Drawable getIcon() {
            return null;
        }

        @Override
        public MenuItem setIntent(Intent intent) {
            return null;
        }

        @Override
        public Intent getIntent() {
            return null;
        }

        @Override
        public MenuItem setShortcut(char numericChar, char alphaChar) {
            return null;
        }

        @Override
        public MenuItem setNumericShortcut(char numericChar) {
            return null;
        }

        @Override
        public char getNumericShortcut() {
            return 0;
        }

        @Override
        public MenuItem setAlphabeticShortcut(char alphaChar) {
            return null;
        }

        @Override
        public char getAlphabeticShortcut() {
            return 0;
        }

        @Override
        public MenuItem setCheckable(boolean checkable) {
            return null;
        }

        @Override
        public boolean isCheckable() {
            return false;
        }

        @Override
        public MenuItem setChecked(boolean checked) {
            return null;
        }

        @Override
        public boolean isChecked() {
            return false;
        }

        @Override
        public MenuItem setVisible(boolean visible) {
            return null;
        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @Override
        public MenuItem setEnabled(boolean enabled) {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean hasSubMenu() {
            return false;
        }

        @Override
        public SubMenu getSubMenu() {
            return null;
        }

        @Override
        public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            return null;
        }

        @Override
        public ContextMenu.ContextMenuInfo getMenuInfo() {
            return null;
        }

        @Override
        public void setShowAsAction(int actionEnum) {

        }

        @Override
        public MenuItem setShowAsActionFlags(int actionEnum) {
            return null;
        }

        @Override
        public MenuItem setActionView(View view) {
            return null;
        }

        @Override
        public MenuItem setActionView(int resId) {
            return null;
        }

        @Override
        public View getActionView() {
            return null;
        }

        @Override
        public MenuItem setActionProvider(ActionProvider actionProvider) {
            return null;
        }

        @Override
        public ActionProvider getActionProvider() {
            return null;
        }

        @Override
        public boolean expandActionView() {
            return false;
        }

        @Override
        public boolean collapseActionView() {
            return false;
        }

        @Override
        public boolean isActionViewExpanded() {
            return false;
        }

        @Override
        public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
            return null;
        }
    }

}
