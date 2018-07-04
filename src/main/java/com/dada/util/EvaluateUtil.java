package com.dada.util;

import domain.Dada;
import domain.EvaluateResult;
import domain.Location;
import domain.Path;
import path.plan.algorithm.*;

import java.util.ArrayList;
import java.util.List;

import static com.dada.util.CommonUtil.*;
import static com.dada.util.CommonUtil.println;
import static com.dada.util.GraphUtil.drawLineChart;

public class EvaluateUtil {
    public static EvaluateResult evaluate(Algorithm algorithm, Dada dada) {
        List<Location> locationList = getAllLocations(dada);
        double[][] distanceMatrix = getLinearDistanceMatrix(locationList);

        long startTime = System.currentTimeMillis();
        Path result = algorithm.planPath(dada, distanceMatrix);
        long endTime = System.currentTimeMillis();

        return new EvaluateResult(result.getDistance(),
                (endTime - startTime),
                algorithm.getName(),
                locationList.size(),
                result.getRoute()
        );
    }

    public static EvaluateResult evaluate(Algorithm algorithm, Dada dada, int iterateTimes) {
        long time = 0;
        double distance = 0;
        String name = "";
        int pointSize = 0;
        for (int i = 0; i < iterateTimes; i++) {
            EvaluateResult result = evaluate(algorithm, dada);
            distance += result.getDistance();
            time += result.getTimeCost();
            name = result.getMethod();
            pointSize = result.getPointSize();
        }
        return new EvaluateResult(distance / iterateTimes,
                time / (double) iterateTimes,
                name,
                pointSize,
                null
        );
    }

    public static void evaluteMultiAlgorithm(int startIndex, int endIndex, int iterateTimes,
                                             List<Algorithm> algorithmList){
        int algorithmCount = algorithmList.size();
        boolean[] tooSlow = new boolean[algorithmCount];

        List<List<double[]>> dataDistance = new ArrayList<>(), dataTime = new ArrayList<>();
        List<String> lineName = new ArrayList<>();
        for (int i = 0;i < algorithmCount;i++){
            dataDistance.add(new ArrayList<double[]>());
            dataTime.add(new ArrayList<double[]>());
        }

        for (int i = startIndex;i < endIndex;i += 2){
            Dada dada = getRandomDada(i);
            for (int j = 0;j < algorithmList.size();j++){
                if (tooSlow[j]){
                    continue;
                }
                Algorithm algorithm = algorithmList.get(j);
                EvaluateResult result = evaluate(algorithm, dada, iterateTimes);
                if (lineName.size() < algorithmCount){
                    lineName.add(result.getMethod());
                }
                dataDistance.get(j).add(new double[]{i, result.getDistance()});
                dataTime.get(j).add(new double[]{i, result.getTimeCost()});
                println(result);
                if (result.getTimeCost() > 500){
                    println("removed:" + result.getMethod());
                    tooSlow[j] = true;
                }
            }
            println("======================");
        }

        drawLineChart(dataDistance, lineName, "距离分析", "点的个数", "平均距离(km)");
        drawLineChart(dataTime, lineName, "时间分析", "点的个数", "平均耗时(ms)");
    }


}
