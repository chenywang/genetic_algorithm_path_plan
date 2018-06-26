package domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Path {

    private double distance;

    private List<Location> route;
    private List<Location> unassignJobs;

    private long delayTime;
    private long pickupTimeDelay;
    private long deliverTimeDelay;
    private boolean isDownGrade = false;

    public Path(List<Location> route, double distance) {
        this.route = route;
        this.distance = distance;
        unassignJobs = new ArrayList<>();
    }
    @Override
    public String toString() {
        return "Path{" + "distance=" + distance + ", route=" + route + '}';
    }
}
