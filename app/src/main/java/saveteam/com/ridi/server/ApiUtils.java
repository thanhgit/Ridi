package saveteam.com.ridi.server;

public class ApiUtils {
    public static final String BASE_URL ="http://35.237.166.237/";

    public static final String GOOGLE_MAP_URL = "https://maps.googleapis.com/";

    public static ServerMatchingApi getUserClient(){
        return ServerClient.getServer(BASE_URL).create(ServerMatchingApi.class);
    }

    public static ServerGoogleMapApi getServerGoogleMapApi() {
        return ServerClient.getServer(GOOGLE_MAP_URL).create(ServerGoogleMapApi.class);
    }
}
