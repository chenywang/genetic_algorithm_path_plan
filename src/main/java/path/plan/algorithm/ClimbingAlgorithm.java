package path.plan.algorithm;

import domain.Dada;
import domain.Location;
import domain.Path;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dada.util.CommonUtil.*;

@Data
public class ClimbingAlgorithm implements Algorithm {
    private double iterationTimes = 300;
    private double candidateChromosomeCount = 10;
    public List<int[]> candidateChromosome;
    private String name;
    public ClimbingAlgorithm() {
        String[] algorithmName = ClimbingAlgorithm.class.getName().split("\\.");
        name = algorithmName[algorithmName.length - 1];
    }

    public ClimbingAlgorithm(double iterationTimes, double candidateChromosomeCount) {
        this.iterationTimes = iterationTimes;
        this.candidateChromosomeCount = candidateChromosomeCount;
    }

    public Path planPath(Dada dada, double[][] distanceMatrix) {
        candidateChromosome = new ArrayList<>();
        List<Location> allLocations = getAllLocations(dada);
        for (int i = 0; i < candidateChromosomeCount; i++) {
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
        for (int i = 0; i < iterationTimes; i++) {
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
