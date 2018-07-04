package path.plan.algorithm;

import domain.Dada;
import domain.Location;
import domain.Path;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.dada.util.CommonUtil.*;

@Data
public class SimulateAnnealing implements Algorithm{
    private double delta = 0.99, minTemperature = 0.00001;
    int candidateCount = 20;
    private String name;

    public SimulateAnnealing(){
        String[] algorithmName = SimulateAnnealing.class.getName().split("\\.");
        name = algorithmName[algorithmName.length - 1];
    }
    public SimulateAnnealing(String name){
        this.name = name;
    }

    public Path planPath(Dada dada, double[][] matrix){

        List<Location> allLocations = getAllLocations(dada);
        List<int[]> chromosomes = new ArrayList<>();
        for (int i = 0;i < candidateCount;i++){
            int[] chromosome = initChromosome(allLocations.size());
            shuffleChromosome(chromosome);
            chromosomes.add(chromosome);
        }
        double minDistance = Double.MAX_VALUE;
        int[] chromosome = null;
        for (int i = 0;i < chromosomes.size();i++){
            double distance = simulateAnnealing(chromosomes.get(i), matrix);
            if (distance < minDistance){
                chromosome = chromosomes.get(i);
                minDistance = distance;
            }
        }

        List<Location> route = getRoute(chromosome, allLocations);
        return new Path(route, minDistance);
    }
    private double simulateAnnealing(int[] chromosome, double[][] matrix){
        double distance = getDistance(chromosome, matrix), temperature = 1;
        Random random = new Random();
        while (temperature > minTemperature){
            int[] nextChromosome = cloneChromosome(chromosome);
            switchRandomTwoValidPoint(chromosome);
            double nextDistance = getDistance(nextChromosome, matrix);

            // 如果新的解比较优秀，那么选择该解
            if (nextDistance < distance){
                distance = nextDistance;
                copyChromosome(nextChromosome, chromosome);
            }
            // 如果旧的解比较优秀
            else {
                double threshold = getThreshold(temperature, distance - nextDistance);
                // 如果低于该阈值，则接受该次优解
                if (random.nextDouble() < threshold){
                    distance = nextDistance;
                    copyChromosome(nextChromosome, chromosome);
                }
            }
            temperature *= delta;
        }
        return distance;
    }

    // deltaEnergy为负数
    private double getThreshold(double temperature, double deltaEnergy){
        return Math.exp(deltaEnergy / temperature);
    }
}
