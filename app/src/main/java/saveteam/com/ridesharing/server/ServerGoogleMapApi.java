package saveteam.com.ridesharing.server;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import saveteam.com.ridesharing.server.model.place.PlaceResponse;

public interface ServerGoogleMapApi {
    @GET("/maps/api/geocode/json?")
    Call<PlaceResponse> searchWithString(@Query("address") String address, @Query("key") String key);

    @GET("/maps/api/geocode/json?")
    Call<PlaceResponse> searchWithCoordinate(@Query("latlng") String coordinate, @Query("key") String key);
}
