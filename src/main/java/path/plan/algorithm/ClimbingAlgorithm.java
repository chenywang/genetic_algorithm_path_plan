package path.plan.algorithm;

import domain.Dada;
import domain.Location;
import domain.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dada.util.CommonUtil.*;

public class ClimbingAlgorithm implements Algorithm {
    private static final double ITERATION_TIMES = 300;
    private static final double CANDIDATE_CHROMOSOME_COUNT = 10;

    public Path planPath(Dada dada, double[][] distanceMatrix) {
        List<Location> allLocations = getAllLocations(dada);
        List<int[]> candidateChromosome = new ArrayList<>();
        for (int i = 0; i < CANDIDATE_CHROMOSOME_COUNT; i++) {
            int[] chromosome = initChromosome(allLocations.size());
            shuffleChromosome(chromosome);
            candidateChromosome.add(chromosome);
        }

        int[] bestChromosome = new int[allLocations.size()];
        double bestDistance = Double.MAX_VALUE;
        for (int i = 0; i < candidateChromosome.size(); i++) {
            double distance = climb(candidateChromosome.get(i), distanceMatrix);
            if (distance < bestDistance) {
                bestChromosome = candidateChromosome.get(i);
                bestDistance = distance;
            }
        }

        List<Location> route = new ArrayList<>();
        for (int gene : bestChromosome) {
            route.add(allLocations.get(gene));
        }
        return new Path(route, bestDistance);
    }


    private double climb(int[] chromosome, double[][] distanceMatrix) {
        Map<Integer, Integer> marker = new HashMap<>();
        double distance = getDistance(chromosome, distanceMatrix);
        for (int k = 1; k < chromosome.length; k++) {
            marker.put(chromosome[k], k);
        }
        for (int i = 0; i < ITERATION_TIMES; i++) {
            int switchIndex1 = -1, switchIndex2 = -1;
            // 向下爬山
            for (int m = 1; m < chromosome.length - 1; m++) {
                for (int n = m + 1; n < chromosome.length; n++) {
                    if (chromosome[m] % 2 == 0 && marker.get(chromosome[m] - 1) >= n ||
                            chromosome[m] % 2 == 1 && marker.get(chromosome[m] + 1) <= n ||
                            chromosome[n] % 2 == 0 && marker.get(chromosome[n] - 1) >= m ||
                            chromosome[n] % 2 == 1 && marker.get(chromosome[n] + 1) <= m) {
                        continue;
                    }
                    swap(chromosome, m, n);
                    double currentDistance = getDistance(chromosome, distanceMatrix);
                    if (currentDistance < distance) {
                        distance = currentDistance;
                        switchIndex1 = m;
                        switchIndex2 = n;
                    }
                    swap(chromosome, m, n);
                }
            }
            if (switchIndex1 != -1) {
                swap(chromosome, switchIndex1, switchIndex2);
                marker.put(chromosome[switchIndex1], switchIndex1);
                marker.put(chromosome[switchIndex2], switchIndex2);
            }

        }
        return distance;
    }

}
