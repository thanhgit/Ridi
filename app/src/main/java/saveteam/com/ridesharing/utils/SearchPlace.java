package saveteam.com.ridesharing.utils;

import com.here.android.mpa.search.DiscoveryResultPage;


public interface SearchPlace {
    DiscoveryResultPage searchPlaceByName(String name, SearchPlaceImpl.SearchResultListener listener);
}
