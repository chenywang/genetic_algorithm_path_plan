package domain;

import lombok.Data;

import java.util.List;

@Data
public class EvaluateResult {
    private double distance, timeCost;
    private String method;
    private int pointSize;
    private List<Location> route;

    public EvaluateResult(double distance, double timeCost, String method, int pointSize, List<Location> route) {
        this.distance = distance;
        this.timeCost = timeCost;
        this.method = method;
        this.pointSize = pointSize;
        this.route = route;
    }

    @Override
    public String toString() {
        return "method:" + method +
                " points:" + pointSize +
                " time cost:" + timeCost + "ms" +
                " distance:" + distance;
    }
}
