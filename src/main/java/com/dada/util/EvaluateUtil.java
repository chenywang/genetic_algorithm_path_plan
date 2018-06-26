package com.dada.util;
import domain.Dada;
import domain.Path;
import path.plan.algorithm.Algorithm;
import domain.Location;
import java.util.List;

import static com.dada.util.CommonUtil.getAllLocations;
import static com.dada.util.CommonUtil.getLinearDistanceMatrix;

public class EvaluateUtil {
    public static void evaluate(Algorithm algorithm, Dada dada){
        List<Location> locationList = getAllLocations(dada);
        double[][] distanceMatrix = getLinearDistanceMatrix(locationList);

        long startTime = System.currentTimeMillis();
        Path result = algorithm.planPath(dada, distanceMatrix);
        long endTime = System.currentTimeMillis();


        String[] algorithmName = algorithm.getClass().getName().split("\\.");

        System.out.println("method:" + algorithmName[algorithmName.length - 1] +
                " points:" + locationList.size() +
                " time cost:" + (endTime - startTime) / 1000.0 +
                " distance:" + result.getDistance());
    }

}
