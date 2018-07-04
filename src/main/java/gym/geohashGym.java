package gym;
import ch.hsr.geohash.GeoHash;
public class geohashGym {
    public static void main(String[] args) {
        double lat =  24.514564, lng = 118.153735;
        GeoHash userGeohash = GeoHash.withCharacterPrecision(lat, lng, 7);
        System.out.println(userGeohash);
    }
}

