package path.plan.algorithm;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.SchrimpfFactory;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;
import domain.Dada;
import domain.Location;
import domain.Order;
import domain.Path;

import java.util.*;

import static com.dada.util.CommonUtil.getDistance;


/**
 * 基于Jsprit进行路径规划的工具类
 */
public class JspritPathPlan implements Algorithm {


    public Path planPath(Dada dada, double[][] matrix) {
        return planPath(dada, false, matrix);
    }
    /**
     * 函数直接传入距离矩阵
     */
    public Path planPath(Dada dada, boolean withTimeWindow, double[][] matrix) {

        if (dada.getOrders().size() == 0) {
            List<Location> route = new ArrayList<>();
            route.add(dada.getCurrent());
            return new Path(route, 0);
        }

        List<Order> orders = dada.getOrders();
        Location current = dada.getCurrent();

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        VehicleRoutingTransportCosts costMatrix;
        if (!withTimeWindow) {
            addJobsWithOutTimeWindow(orders, vrpBuilder);
        } else {
            addJobsWithTimeWindow(orders, vrpBuilder);
        }
        costMatrix = getCostMatrix(matrix, withTimeWindow);

        VehicleImpl vehicle = initVehicle(dada.getCurrent());
        vrpBuilder.addVehicle(vehicle);

        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE).setRoutingCost(costMatrix);
        VehicleRoutingProblem problem = vrpBuilder.build();
        SchrimpfFactory a = new SchrimpfFactory();
        VehicleRoutingAlgorithm algorithm = a.createAlgorithm(problem);

        algorithm.setMaxIterations(10);
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        //不知道为什么，getCost跟实际情况不符合，自己定义cost
        Path path = new Path(getRoute(bestSolution, current, getVisited(orders)), bestSolution.getCost());

        path.setDistance(getDistance(path.getRoute(), matrix) );

        return path;
    }

    /**
     * Jsprit流程，初始化送货员
     */
    private static VehicleImpl initVehicle(Location current) {
        VehicleTypeImpl.Builder typeBuilder = VehicleTypeImpl.Builder.newInstance("vt0").addCapacityDimension(0, Integer.MAX_VALUE);
        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("v0");
        vehicleBuilder.setStartLocation(com.graphhopper.jsprit.core.problem.Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(current.getLng(), current.getLat())).setId("" + 0).build());
        vehicleBuilder.setType(typeBuilder.build());
        vehicleBuilder.setReturnToDepot(false);
        return vehicleBuilder.build();
    }

    /**
     * 添加需要访问的点
     */
    private static void addJobsWithOutTimeWindow(List<Order> orders, VehicleRoutingProblem.Builder vrpBuilder) {
        int index = 1;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            Location pickup = order.isPicked() ? order.getEnd() : order.getStart(),
                    delivery = order.getEnd();
            Shipment shipment = Shipment.Builder.newInstance("shipment" + i)
                    .addSizeDimension(0, 0)
                    .setPickupLocation(com.graphhopper.jsprit.core.problem.Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(pickup.getLng(), pickup.getLat())).setId("" + (index++)).build())
                    .setDeliveryLocation(com.graphhopper.jsprit.core.problem.Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(delivery.getLng(), delivery.getLat())).setId("" + (index++)).build())
                    .build();
            vrpBuilder.addJob(shipment);
        }
    }

    private static void addJobsWithTimeWindow(List<Order> orders, VehicleRoutingProblem.Builder vrpBuilder) {
        int index = 1;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            Location pickup = order.isPicked() ? order.getEnd() : order.getStart(),
                    delivery = order.getEnd();
            Shipment shipment = Shipment.Builder.newInstance("shipment" + i)
                    .addSizeDimension(0, 0)
                    .setPickupLocation(com.graphhopper.jsprit.core.problem.Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(pickup.getLng(), pickup.getLat())).setId("" + (index++)).build())
                    .setPickupTimeWindow(new TimeWindow(0, order.isPicked() ? Double.MAX_VALUE : order.getFetchTimeWindow().getEndTime()))
                    .setDeliveryLocation(com.graphhopper.jsprit.core.problem.Location.Builder.newInstance().setCoordinate(Coordinate.newInstance(delivery.getLng(), delivery.getLat())).setId("" + (index++)).build())
                    .setDeliveryTimeWindow(new TimeWindow(0, order.getDeliverTimeWindow().getEndTime()))
                    .build();
            vrpBuilder.addJob(shipment);
        }
    }

    private static VehicleRoutingTransportCosts getCostMatrix(double[][] distanceMatrix, boolean withTimeWindow) {
        VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder.newInstance(true);

        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix.length; j++) {
                double distance = distanceMatrix[i][j];
                costMatrixBuilder.addTransportDistance("" + i, "" + j, distance);
                if (withTimeWindow) {
                    costMatrixBuilder.addTransportTime("" + i, "" + j, distance / 0.00277778);
                }
            }
        }
        return costMatrixBuilder.build();
    }

    /**
     * 基于解出的solution转化成可用的达达路径格式
     */
    private static List<Location> getRoute(VehicleRoutingProblemSolution bestSolution, Location current, Set<Integer> visited) {

        List<TourActivity> activities = bestSolution.getRoutes().iterator().next().getActivities();
        List<Location> route = new ArrayList<>(activities.size());
        route.add(current);
        for (TourActivity ta : activities) {
            int id = Integer.valueOf(ta.getLocation().getId());
            if (!visited.contains(id)) {
                route.add(new Location(ta.getLocation().getCoordinate().getX(), ta.getLocation().getCoordinate().getY(), id));
            }
        }
        return route;
    }

    private static Set<Integer> getVisited(List<Order> orders) {
        Set<Integer> visited = new HashSet<>();
        int index = 0;
        for (Order order : orders) {
            if (order.isPicked()) {
                visited.add(index * 2 + 1);
            }
            index++;
        }
        return visited;
    }
}
