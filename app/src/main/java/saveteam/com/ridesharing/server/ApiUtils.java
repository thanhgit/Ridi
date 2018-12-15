package saveteam.com.ridesharing.server;

public class ApiUtils {
    public static final String BASE_URL ="http://35.237.166.237/";

    public static ServerApi getUserClient(){
        return ServerClient.getServer(BASE_URL).create(ServerApi.class);
    }
}
