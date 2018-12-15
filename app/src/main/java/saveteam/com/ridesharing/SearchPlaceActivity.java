package saveteam.com.ridesharing;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.Location;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.model.Geo;
import saveteam.com.ridesharing.utils.ActivityUtils;
import saveteam.com.ridesharing.utils.BasicMapActivity;
import saveteam.com.ridesharing.utils.S2Utils;
import saveteam.com.ridesharing.utils.SearchPlace;
import saveteam.com.ridesharing.utils.SearchPlaceImpl;

public class SearchPlaceActivity extends BasicMapActivity {

    @BindView(R.id.search_view_where_search_place)
    MaterialSearchView searchView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_choose_where_search_place)
    AppCompatButton btn_choose;

    SearchPlace searchPlace;
    DiscoveryResultPage searchResults;
    String[] result = new String[5];

    String querySearch = "";

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Where ?");

        initSearchView();
    }

    @OnClick(R.id.btn_choose_where_search_place)
    public void clickButtonChoose(View view) {
        double lat = marker.getCoordinate().getLatitude();
        double lng = marker.getCoordinate().getLongitude();
        long cellId = S2Utils.getCellId(lat, lng).id();
        Geo geo = new Geo(lat, lng, cellId);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("data", geo);
        setResult(RESULT_OK, resultIntent );
        finish();
    }

    private void initSearchView() {
        searchPlace = new SearchPlaceImpl(new GeoCoordinate(10.789148,106.6615424));
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

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
                                }
                                if (strings.size() > 1) {
                                    searchView.setQuery(strings.get(0), false);
                                }

                                result = strings.toArray(new String[strings.size()]);
                                searchView.setSuggestions(result);
                                searchView.setAdapter(new SearchAdapter(SearchPlaceActivity.this, result));
                            }
                        }
                    });
                    querySearch = query;
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

                ActivityUtils.displayLog("view show");
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                ActivityUtils.displayLog("view close");
            }
        });

        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                searchView.dismissSuggestions();
                searchView.closeSearch();
                Toast.makeText(SearchPlaceActivity.this, adapterView.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_place_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search_where_search_place_menu);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if using voice
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
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

            marker = new MapMarker(map.pixelToGeo(p), ActivityUtils.getMarker());
            map.addMapObject(marker);

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

}
