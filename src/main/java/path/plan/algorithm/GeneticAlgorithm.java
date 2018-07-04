package path.plan.algorithm;

import com.dada.util.CommonUtil;
import domain.Dada;
import domain.Location;
import domain.Path;
import lombok.Data;

import java.util.*;

import static com.dada.util.CommonUtil.*;

/**
 * @author Michael-Wang
 */
public class GeneticAlgorithm implements Algorithm {
    private final static double ELIMINATE_RATE = 0.2;
    private final double RECOMBINE_RATE = 0.75;
    private final double MUTATE_RATE = 0.35;
    private final int CANDIDATE_CHROMOSOME_COUNT = 100;
    private final int GENERATIONS = 500;
    private final int END_TIME = (int) (GENERATIONS * 0.05);

    private String name;
    private int currentSameBest;
    private double[][] distanceMatrix;
    private List<int[]> candidateChromosome;

    private int[] bestChromosome;
    private double shortestDistance;

    private Map<String,Integer> argument = new HashMap<>();
    public GeneticAlgorithm() {
        String[] algorithmName = GeneticAlgorithm.class.getName().split("\\.");
        name = algorithmName[algorithmName.length - 1];
    }

    public void setArgument(String key, Integer value){
        this.argument.put(key, value);
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public GeneticAlgorithm(String name) {
        this.name = name;
    }

    public Path planPath(Dada dada, double[][] distanceMatrix) {
        List<Location> allLocations = getAllLocations(dada);
        currentSameBest = 0;
        shortestDistance = Double.MAX_VALUE;
        candidateChromosome = new ArrayList<>();
        bestChromosome = new int[allLocations.size()];
        this.distanceMatrix = distanceMatrix;
        initPopulation(allLocations.size());
        int generation = 0;
        for (int i = 0; i < GENERATIONS; i++) {
            sortChromosomes();
            generation++;
//            if (isFinish()) break;
            eliminatePhase();
            recombinePhase();
            mutatePhase();
//            println("current best distance : " + shortestDistance);
        }
//        println(generation);
        List<Location> route = getRoute(bestChromosome, allLocations);
        return new Path(route, shortestDistance);
    }

    private boolean isFinish() {
        return currentSameBest > END_TIME;
    }

    private double getDistance(int[] chromosome) {
        double distance = 0;
        for (int i = 0; i < chromosome.length - 1; i++) {
            distance += distanceMatrix[chromosome[i]][chromosome[i + 1]];
        }
        return distance;
    }


    private void initPopulation(int size) {
        for (int i = 0; i < CANDIDATE_CHROMOSOME_COUNT; i++) {
            int[] chromosome = initChromosome(size);
            shuffleChromosome(chromosome);
            candidateChromosome.add(chromosome);
        }
    }

    private void initPopulationByClimbAlgorithm(Dada dada) {
        bestChromosome = new int[dada.getOrders().size() * 2 + 1];
        ClimbingAlgorithm ca = new ClimbingAlgorithm(30, CANDIDATE_CHROMOSOME_COUNT);
        Path p = ca.planPath(dada, distanceMatrix);
        candidateChromosome = ca.candidateChromosome;
    }

    private void eliminatePhase() {
        if (!argument.containsKey("eliminate") || argument.get("eliminate") == 0){
            eliminatePhase1();
        } else if(argument.get("eliminate") == 1){
            recombinePhase2();
        }else if (argument.get("eliminate") == 2){
            recombinePhase3();
        }else {
            eliminatePhase1();
        }
    }

    private void eliminatePhase1() {
        int end = CANDIDATE_CHROMOSOME_COUNT;
        while (candidateChromosome.size() > end)
            candidateChromosome.remove(end - 1);
    }

//    private void eliminatePhase2() {
//        double sum = 0;
//        double[] rates = new double[candidateChromosome.size()];
//        for (int i = 0;i < candidateChromosome.size();i++){
//            double rate = 1 / getDistance(candidateChromosome.get(i));
//            sum += rate;
//            rates[i] = rate;
//        }
//        Random random = new Random();
//        for (int i = 0;i < candidateChromosome.size();i++){
//            double ratio = rates[i] / sum;
//            if (random.nextDouble() < ratio){
//
//            }
//        }
//    }



    private void recombinePhase() {
        if (!argument.containsKey("recombine") || argument.get("recombine") == 0){
            recombinePhase1();
        } else if(argument.get("recombine") == 1){
            recombinePhase2();
        }else if (argument.get("recombine") == 2){
            recombinePhase3();
        }else {
            recombinePhase1();
        }
    }
    private void recombinePhase1() {
        Random rand = new Random();
        int recombineNum = (int) (candidateChromosome.size() * RECOMBINE_RATE
        );
        recombineNum = recombineNum - recombineNum % 3;
        Set<Integer> set = new HashSet<>();
        int[] recombines = new int[recombineNum];
        int recombinesIndex = 0;

        while (set.size() < recombineNum) {
            int r = rand.nextInt(candidateChromosome.size());
            if (set.contains(r)) continue;
            recombines[recombinesIndex++] = r;
            set.add(r);
        }

        for (int i = 0; i < recombines.length; i = i + 3) {
            int[] chromosome = recombine1(
                    candidateChromosome.get(recombines[i]),
                    candidateChromosome.get(recombines[i + 1]),
                    candidateChromosome.get(recombines[i + 2]));

                    candidateChromosome.add(chromosome);
        }
    }

    private int[] recombine1(int[] c1, int[] c2, int[] c3) {
        int[] copy1 = new int[c1.length];
        int[] copy2 = new int[c1.length];
        int[] copy3 = new int[c1.length];
        copyChromosome(c1, copy1);
        copyChromosome(c2, copy2);
        copyChromosome(c3, copy3);
        for (int i = 1; i < copy1.length; i++) {
            int gene =
                    distanceMatrix[copy1[i]][copy1[i - 1]] > distanceMatrix[copy2[i]][copy2[i - 1]] ?
                    (distanceMatrix[copy2[i]][copy2[i - 1]] > distanceMatrix[copy3[i]][copy3[i - 1]] ? copy3[i] : copy2[i]) :
                    (distanceMatrix[copy1[i]][copy1[i - 1]] > distanceMatrix[copy3[i]][copy3[i - 1]] ? copy3[i] : copy1[i]);
            rotate(copy1, i, findGene(copy1, gene));
            rotate(copy2, i, findGene(copy2, gene));
            rotate(copy3, i, findGene(copy3, gene));
        }
        return copy1;
    }

    private void recombinePhase2(){
        Random random = new Random();
        for (int i = 0;i < candidateChromosome.size();i += 2){
//            if (random.nextDouble() < RECOMBINE_RATE){
                recombine2(candidateChromosome.get(i), candidateChromosome.get(i + 1));
//            }
        }
    }

    private void recombine2(int[] chromosome1, int[] chromosome2){
        int[] child1 = new int[chromosome1.length], child2 = new int[chromosome2.length];
        int index1 = 1, index2 = 1;
        for (int i = 1;i < chromosome1.length;i++){
            int gene1 = chromosome1[i];
            if (index1 < chromosome1.length && findGene(child1, gene1) == -1 &&
                    (gene1 % 2 == 1 || findGene(child1, gene1 - 1) != -1)){
                child1[index1++] = gene1;
            } else{
                child2[index2++] = gene1;
            }

            int gene2 = chromosome2[i];
            if (index1 < chromosome1.length && findGene(child1, gene2) == -1 &&
                    (gene2 % 2 == 1 || findGene(child1, gene2 - 1) != -1)){
                child1[index1++] = gene2;
            } else{
                child2[index2++] = gene2;
            }
        }

        copyChromosome(child1, chromosome1);
        copyChromosome(child2, chromosome2);
    }

    private void recombinePhase3(){
        Random random = new Random();
        int size = candidateChromosome.size();
        for (int i = 0;i < size;i += 2){
            if (random.nextDouble() < RECOMBINE_RATE){
                recombine3(candidateChromosome.get(i), candidateChromosome.get(i + 1));
            }
        }
    }

    private void recombine3(int[] chromosome1, int[] chromosome2){
        int[] child1 = new int[chromosome1.length], child2 = new int[chromosome2.length];
        int index1 = 1, index2 = 1;
        for (int i = 1;i < chromosome1.length;i++){
            int gene1 = chromosome1[i],gene2 = chromosome2[i];
            if (index1 < chromosome1.length && findGene(child1, gene1) == -1 &&
                    (gene1 % 2 == 1 || findGene(child1, gene1 - 1) != -1)){
                child1[index1++] = gene1;
            } else{
                child2[index2++] = gene1;
            }

            if (index1 < chromosome1.length && findGene(child1, gene2) == -1 &&
                    (gene2 % 2 == 1 || findGene(child1, gene2 - 1) != -1)){
                child1[index1++] = gene2;
            } else{
                child2[index2++] = gene2;
            }
        }
        candidateChromosome.add(child1);
        candidateChromosome.add(child2);

    }

    private void sortChromosomes() {
        Collections.sort(candidateChromosome, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Double.compare(getDistance(a), getDistance(b));
            }
        });
        double distance = getDistance(candidateChromosome.get(0));
        if (shortestDistance > distance) {
            copyChromosome(candidateChromosome.get(0), bestChromosome);
            shortestDistance = distance;
            currentSameBest = 0;
        } else
            currentSameBest++;
    }


    private void mutatePhase() {
        Random rand = new Random();
        for (int[] chromosome : candidateChromosome) {
            if (rand.nextDouble() < MUTATE_RATE) {
                mutate(chromosome);
            }
        }
    }

    private void mutate(int[] chromosome) {
        switchRandomTwoValidPoint(chromosome);
    }

//    private void shuffle(int[] prototype) {
//        Set<Integer> set = new HashSet<>();
//        set.add(0);
//        prototype[0] = 0;
//        Random rand = new Random();
//        int index = 1;
//        while (set.size() < prototype.length) {
//            int r = rand.nextInt(prototype.length);
//            //you must fetch before finish
//            if (set.contains(r)) continue;
//            if (r % 2 == 0 && !set.contains(r - 1)) {
//                if (allLocations.get(r - 1) == null) {
//                    set.add(r - 1);
//                    set.add(r);
//                    prototype[index++] = r - 1;
//                    prototype[index++] = r;
//                }
//            } else {
//                set.add(r);
//                prototype[index++] = r;
//            }
//        }
//    }

}
