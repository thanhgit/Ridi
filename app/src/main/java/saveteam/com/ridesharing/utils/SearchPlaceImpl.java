package saveteam.com.ridesharing.utils;

import android.util.Log;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.search.AutoSuggest;
import com.here.android.mpa.search.DiscoveryRequest;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.SearchRequest;
import com.here.android.mpa.search.TextAutoSuggestionRequest;

import java.util.List;

public class SearchPlaceImpl implements SearchPlace {
    GeoCoordinate center;

    public interface SearchResultListener {
        void done(DiscoveryResultPage discoveryResultPage);
    }

    public SearchPlaceImpl(GeoCoordinate center) {
        this.center = center;
    }

    @Override
    public DiscoveryResultPage searchPlaceByName(String name, SearchResultListener listener) {
        try {
            DiscoveryRequest request =
                    new SearchRequest(name).setSearchCenter(this.center);

            request.setCollectionSize(5);
            ErrorCode error = request.execute(new SearchRequestListener(listener));
//            Thread.sleep(15000);
            if( error != ErrorCode.NONE ) {
                // Handle request error
            }
        } catch (IllegalArgumentException ex) {
            // Handle invalid create search request parameters
        }

        return null;
    }

    private class SearchRequestListener implements ResultListener<DiscoveryResultPage> {
        SearchResultListener listener;

        public SearchRequestListener(SearchResultListener listener) {
            this.listener = listener;
        }

        @Override
        public void onCompleted(DiscoveryResultPage data, ErrorCode error) {
            if (error != ErrorCode.NONE) {
                Log.e(ActivityUtils.TAG, error.name());
            } else {
                for (DiscoveryResult item : data.getItems()) {
                    String str = item.getTitle();
                    ActivityUtils.displayLog(str);
                }

                listener.done(data);
            }
        }
    }

    private class AutoSuggestionQueryListener implements ResultListener<List<AutoSuggest>> {
        GeoCoordinate center;
        String querySearch;

        public AutoSuggestionQueryListener() {
        }

        public AutoSuggestionQueryListener(GeoCoordinate center, String querySearch) {
            this.center = center;
            this.querySearch = querySearch;
        }

        @Override
        public void onCompleted(List<AutoSuggest> data, ErrorCode error) {
            for (AutoSuggest r : data) {
                try {
                    TextAutoSuggestionRequest request = null;
                    request = new TextAutoSuggestionRequest(querySearch).setSearchCenter(this.center);
                    if (request.execute(new AutoSuggestionQueryListener()) !=
                            ErrorCode.NONE ) {
                        //Handle request error
                        //...
                    }
                } catch (IllegalArgumentException ex) {
                    //Handle invalid create search request parameters
                }

                for (AutoSuggest suggest : data) {
                    ActivityUtils.displayLog(suggest.getTitle());
                }
            }
        }
    }
}

