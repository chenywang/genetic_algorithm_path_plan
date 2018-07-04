package path.plan.algorithm;

import domain.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.dada.util.CommonUtil.getAllLocations;

/**
 * this class is for planning path in brute force way
 */
@Data
public class BruteForcePathPlan implements Algorithm{
    private String name;
    public BruteForcePathPlan(){
        String[] algorithmName = BruteForcePathPlan.class.getName().split("\\.");
        name = algorithmName[algorithmName.length - 1];
    }

    public Path planPath(Dada dada, double[][] matrix) {
        return planPath(dada, false, matrix, false);
    }

    public Path planPath(Dada dada, boolean withTimeWindow, double[][] matrix) {
        return planPath(dada, withTimeWindow, matrix, false);
    }

    private static Path planPath(Dada dada, boolean withTimeWindow, double[][] matrix, boolean pickFirst) {
        List<Location> locationList = getAllLocations(dada);
        double[] bestDistance = new double[1];
        //the node that we are going to visited, ignore the already picked.
        int[] searchSize = new int[]{locationList.size()};
        boolean[] visited = getVisited(dada, searchSize);
        int[] bestList = new int[searchSize[0]], currentList = new int[searchSize[0]];

        bestDistance[0] = Double.MAX_VALUE;
        dfs(bestList, bestDistance, locationList, currentList
                , 1, visited, matrix, withTimeWindow
                , dada.getOrders(), pickFirst);
        List<Location> route = new ArrayList<>(locationList.size());

        for (int locationIndex : bestList) {
            Location location = new Location(locationList.get(locationIndex));
            route.add(location);
        }

//        System.out.println("根据序列算得的距离：" + CommonTool.getDistance(route, matrix) + " 根据bf算得的距离：" + bestDistance[0]);
        Path path = new Path(route, bestDistance[0]);
        return path;
    }

    private static void dfs(int[] bestList, double[] bestDistance, List<Location> all
            , int[] currentArray, int visitedCount, boolean[] visited
            , double[][] matrix, boolean withTimeWindow
            , List<Order> orders, boolean pickFirst) {
        if (visitedCount == bestList.length) {

            double distance = withTimeWindow ? getDistanceWithTimeWindow(currentArray, matrix, orders) : getDistance(currentArray, matrix);
            if (bestDistance[0] > distance) {
                bestDistance[0] = distance;
                System.arraycopy(currentArray, 0, bestList, 0, currentArray.length);
            }
            return;
        }
        for (int i = 0; i < all.size(); i++) {
            if (visited[i] || i % 2 == 0 && !visited[i - 1]) {
                continue;
            }
            if (pickFirst) {
                if (i % 2 == 0) {
                    boolean pickedAll = true;
                    for (int j = 1; j < all.size(); j += 2) {
                        if (!visited[j]) {
                            pickedAll = false;
                            break;
                        }
                    }
                    if (!pickedAll) {
                        continue;
                    }
                }
            }
            currentArray[visitedCount] = i;
            visited[i] = true;
            visitedCount++;
            dfs(bestList, bestDistance, all, currentArray, visitedCount, visited, matrix, withTimeWindow, orders, pickFirst);
            visitedCount--;
            visited[i] = false;
            currentArray[visitedCount] = 0;
        }
    }


    private static double getDistance(int[] currentList, double[][] matrix) {
        double distance = 0;
        for (int i = 0; i < currentList.length - 1; i++) {
            distance += matrix[currentList[i]][currentList[i + 1]];
        }
        return distance;
    }


    //add violate time window punishment
    private static double getDistanceWithTimeWindow(int[] currentList, double[][] matrix, List<Order> orders) {
        double currentDistance = 0, currentTimeUsed = 0;
        int punishTimes = 0;
        for (int i = 0; i < currentList.length - 1; i++) {
            double distance = matrix[currentList[i]][currentList[i + 1]];
            double time = distance / 0.0027;

            currentDistance += matrix[currentList[i]][currentList[i + 1]];
            currentTimeUsed += time;

            boolean isPickUpLocation = currentList[i + 1] % 2 == 1;
            int orderNumber = (currentList[i + 1] - 1) / 2;
            TimeWindow tw = isPickUpLocation ? orders.get(orderNumber).getFetchTimeWindow() : orders.get(orderNumber).getDeliverTimeWindow();

            if (currentTimeUsed > tw.getEndTime()) {
                punishTimes++;
            }

        }

        return currentDistance + punishTimes * 100;
    }


    private static boolean[] getVisited(Dada dada, int[] searchSize) {
        boolean[] visited = new boolean[dada.getOrders().size() * 2 + 1];
        visited[0] = true;
        for (int i = 0; i < dada.getOrders().size(); i++) {
            if (dada.getOrders().get(i).isPicked()) {
                visited[i * 2 + 1] = true;
                searchSize[0]--;
            }
        }
        return visited;
    }

}
