package saveteam.com.ridesharing.server;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import saveteam.com.ridesharing.model.Query;
import saveteam.com.ridesharing.server.model.MatchingResponse;
import saveteam.com.ridesharing.server.model.QueryRequest;

public interface ServerApi {

    @GET("v1/ridesharing/matching/server/0.1")
    Call<MatchingResponse> getMatchingFromServer();

    @POST("/v1/ridesharing/matching/personal")
    Call<MatchingResponse> getMatchingFromPersonal(@Body QueryRequest query);
}
