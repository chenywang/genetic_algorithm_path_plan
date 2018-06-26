package domain;
import lombok.Data;

@Data
public class Location {

    public double lng;

    public double lat;

    public int index;

    public Location(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public Location() {
    }

    public Location(double lng, double lat, int index) {
        this.lng = lng;
        this.lat = lat;
        this.index = index;
    }

    public Location(Location ll) {
        this.lat = ll.lat;
        this.lng = ll.lng;
        this.index = ll.index;
    }

    public Location clone() {
        return new Location(lng, lat, index);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Location(" + lng + "," + lat + "," + index + ")";
    }


}
