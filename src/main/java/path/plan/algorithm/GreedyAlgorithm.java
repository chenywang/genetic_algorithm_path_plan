package path.plan.algorithm;

import domain.Dada;
import domain.Location;
import domain.Path;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.dada.util.CommonUtil.getAllLocations;

@Data
public class GreedyAlgorithm implements Algorithm  {
    String name;
    public GreedyAlgorithm(){
        String[] algorithmName = GreedyAlgorithm.class.getName().split("\\.");
        name = algorithmName[algorithmName.length - 1];
    }

    public Path planPath(Dada dada, double[][] distanceMatrix) {
        List<Location> allLocations = getAllLocations(dada);
        boolean[] visited = new boolean[allLocations.size()];
        int[] chromosome = new int[allLocations.size()];
        int currentIndex = 0;
        double distance = 0;
        for (int i = 1;i < allLocations.size();i++){
            double minDistance = Double.MAX_VALUE;
            int bestNextIndex = -1;
            for (int j = 1;j < allLocations.size();j++){
                if (!visited[j] && ((j % 2) == 1 || visited[j - 1]) && distanceMatrix[currentIndex][j] < minDistance){
                    minDistance = distanceMatrix[currentIndex][j];
                    bestNextIndex = j;
                }
            }

            currentIndex = bestNextIndex;
            distance += minDistance;
            visited[bestNextIndex] = true;
            chromosome[i] = currentIndex;
        }
        List<Location> route = new ArrayList<>();
        for (int i = 0;i < chromosome.length;i++){
            route.add(allLocations.get(chromosome[i]));
        }
        Path path = new Path(route, distance);
        return path;
    }
}
