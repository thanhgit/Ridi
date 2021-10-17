package saveteam.com.quagiang.utils.google;

import com.google.common.geometry.S2Cell;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;

public class S2Utils {
    private static int LEVEL = 12;
    public static S2Cell getCell(double latDegrees, double lngDegrees) {
        S2Cell cell = new S2Cell(S2LatLng.fromDegrees(latDegrees, lngDegrees).toPoint());
        cell.id().parent(LEVEL);
        return  cell;
    }

    public static S2CellId getCellId(double latDegrees, double lngDegrees) {
        S2CellId id = S2CellId.fromLatLng(S2LatLng.fromDegrees(latDegrees, lngDegrees)).parent(LEVEL);
        return id;
    }
}