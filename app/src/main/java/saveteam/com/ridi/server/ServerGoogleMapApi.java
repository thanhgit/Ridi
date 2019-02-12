package saveteam.com.ridi.server;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import saveteam.com.ridi.server.model.place.PlaceResponse;
import saveteam.com.ridi.server.model.searchplacewithtext.SearchPlaceWithTextResponse;

public interface ServerGoogleMapApi {
    @GET("/maps/api/geocode/json?")
    Call<PlaceResponse> searchWithString(@Query("address") String address, @Query("key") String key);

    @GET("/maps/api/place/textsearch/json?")
    Call<SearchPlaceWithTextResponse> searchPlaceWithText(@Query("query")String query, @Query("key")String key);

    @GET("/maps/api/geocode/json?")
    Call<PlaceResponse> searchWithCoordinate(@Query("latlng") String coordinate, @Query("key") String key);
}
