package path.plan.algorithm;

import domain.Dada;
import domain.Location;
import domain.Order;
import domain.Path;

import java.util.ArrayList;
import java.util.List;

public class DynamicProgramPathPlan implements Algorithm{


    private final static int MAX_ORDER_COUNT = 19;
    private static int[] power3 = new int[MAX_ORDER_COUNT];

    {
        power3[0] = 1;
        for (int i = 1; i < MAX_ORDER_COUNT; i++) {
            power3[i] = power3[i - 1] * 3;
        }
    }

    public Path planPath(Dada dada, double[][] matrix) {
        return planPath(dada, matrix, false);
    }

    public static Path planPath(Dada dada, double[][] matrix
            , boolean pickFirst) {
        int orderCount = dada.getOrders().size();
        if (orderCount == 0) {
            List<Location> route = new ArrayList<Location>();
            route.add(dada.getCurrent());
            return new Path(route, 0);
        }

        // 将已取货点当做原点来处理。
        for (int i = 0; i < dada.getOrders().size(); i++) {
            if (dada.getOrders().get(i).isPicked()) {
                // 将已经原点到该点的距离改为0
                matrix[0][i * 2 + 1] = 0;
                matrix[i * 2 + 1][0] = 0;

                // 将所有非已取货点到该已取货点的的距离为非已取货点到原点的距离
                // 将其他已取货点到该取货点的距离设置为0
                for (int j = 0; j < dada.getOrders().size(); j++) {
                    if (dada.getOrders().get(j).isPicked()) {
                        matrix[j * 2 + 1][i * 2 + 1] = 0;
                        matrix[i * 2 + 1][j * 2 + 1] = 0;
                    } else {
                        matrix[j * 2 + 1][i * 2 + 1] = matrix[j * 2 + 1][0];
                        matrix[i * 2 + 1][j * 2 + 1] = matrix[0][j * 2 + 1];
                    }
                    matrix[j * 2 + 2][i * 2 + 1] = matrix[j * 2 + 2][0];
                    matrix[i * 2 + 1][j * 2 + 2] = matrix[0][j * 2 + 2];
                }
            }
        }

        double[][] dp = new double[orderCount][power3[orderCount]];

        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[0].length; j++) {
                dp[i][j] = Double.MAX_VALUE;
            }
        }

        for (int i = 0; i < dp.length; i++) {
            dp[i][power3[i]] = matrix[0][i * 2 + 1];
        }

        for (int code = 0; code < power3[orderCount]; ++code) {
            for (int i = 0; i < orderCount; i++) {
                int status1 = getStatusForOrderN(i, code);
                for (int j = 0; j < orderCount; j++) {

                    int status2 = getStatusForOrderN(j, code);
                    int next_code = code + power3[j];
                    if (status1 == 1) {
                        // i取点 -> j取点
                        if (status2 == 0) {
                            dp[j][next_code] = Math.min(dp[j][next_code], dp[i][code] + matrix[1 + i * 2][1 + j * 2]);
                        }
                        // i取点 -> j送点
                        else if (status2 == 1) {
                            dp[j][next_code] = Math.min(dp[j][next_code], dp[i][code] + matrix[1 + i * 2][2 + j * 2]);
                        }
                    } else if (status1 == 2) {
                        // i送点 -> j取点
                        if (status2 == 0) {
                            dp[j][next_code] = Math.min(dp[j][next_code], dp[i][code] + matrix[2 + i * 2][1 + j * 2]);
                        }
                        // i送点 -> j送点
                        else if (status2 == 1) {
                            dp[j][next_code] = Math.min(dp[j][next_code], dp[i][code] + matrix[2 + i * 2][2 + j * 2]);
                        }
                    }

                }
            }
        }
        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < orderCount; i++) {
            minDistance = Math.min(minDistance, dp[i][power3[orderCount] - 1]);
        }

        // 循环的方法获得路径
        List<Location> route = new ArrayList<>();
        int code = power3[orderCount] - 1, next_code = 0;
        for (int i = 0; i < orderCount * 2; i++) {

            Location currentLocation = null;
            double distance = Double.MAX_VALUE;
            for (int j = 0; j < orderCount; j++) {
                int status = getStatusForOrderN(j, code);
                // 找到当前位置到上一个点的距离
                double edge = 0;
                if (route.size() != 0) {
                    int lastLocationIndex = route.get(0).getIndex();
                    if (status == 1) {
                        edge = matrix[j * 2 + 1][lastLocationIndex];
                    } else if (status == 2) {
                        edge = matrix[j * 2 + 2][lastLocationIndex];
                    }
                }
                if (status != 0 && dp[j][code] + edge < distance) {
                    distance = dp[j][code] + edge;
                    if (status == 1) {
                        currentLocation = dada.getOrders().get(j).getStart();
                    } else if (status == 2) {
                        currentLocation = dada.getOrders().get(j).getEnd();
                    }
                    next_code = code - power3[j];
                }
            }
            Order order = dada.getOrders().get((currentLocation.getIndex() - 1) / 2);
            if (currentLocation.getIndex() % 2 == 0 || !order.isPicked()) {
                route.add(0, currentLocation);
            }
            code = next_code;
        }
        route.add(0, dada.getCurrent());

//        System.out.println("根据序列算得的距离：" + getDistance(route, matrix) + " 根据dp算得的距离：" + minDistance);

        Path path = new Path(route, minDistance);
        return path;
    }

    private static int getStatusForOrderN(int orderIndex, int code) {
        code /= power3[orderIndex];
        return code % 3;
    }


}
