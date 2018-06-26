package path.plan.algorithm;

import domain.Dada;
import domain.Location;
import domain.Path;
import path.plan.algorithm.Algorithm;

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

    private int currentSameBest = 0;
    private double[][] distanceMatrix;
    private List<Location> allLocations;
    private List<int[]> candidate_chromosome;

    private int[] bestChromosome;
    private double shortestDistance = Double.MAX_VALUE;

    public GeneticAlgorithm() {
        this.candidate_chromosome = new ArrayList<>();
    }

    public Path planPath(Dada dada, double[][] distanceMatrix){
        this.allLocations = getAllLocations(dada);
        this.distanceMatrix = distanceMatrix;
        initPopulation();
        for (int i = 0; i < GENERATIONS; i++) {
            sortChromosomes();
            if (isFinish()) break;
            eliminatePhase();
            recombinePhase();
            mutatePhase();
//            println("current best distance : " + shortestDistance);
        }
        List<Location> route = new ArrayList<>();
        for(int gene : bestChromosome){
            route.add(allLocations.get(gene));
        }
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


    private void initPopulation() {
        int[] prototype = new int[allLocations.size()];
        bestChromosome = new int[allLocations.size()];
        for (int i = 0; i < allLocations.size(); i++) {
            prototype[i] = i;
        }

        for (int i = 0; i < CANDIDATE_CHROMOSOME_COUNT; i++) {
            shuffleChromosome(prototype);
            int[] chromosome = new int[allLocations.size()];
            copyChromosome(prototype, chromosome);
            candidate_chromosome.add(chromosome);
        }
    }

    private void fineChromosome(int[] chromosome){

    }


    private void eliminatePhase() {
        int end = CANDIDATE_CHROMOSOME_COUNT;
        while (candidate_chromosome.size() > end)
            candidate_chromosome.remove(end - 1);
    }

    private void recombinePhase() {
        Random rand = new Random();
        int recombineNum = (int) (candidate_chromosome.size() * RECOMBINE_RATE
        );
        recombineNum = recombineNum - recombineNum % 3;
        Set<Integer> set = new HashSet<>();
        int[] recombines = new int[recombineNum];
        int recombinesIndex = 0;

        while (set.size() < recombineNum) {
            int r = rand.nextInt(candidate_chromosome.size());
            if (set.contains(r)) continue;
            recombines[recombinesIndex++] = r;
            set.add(r);
        }
        for (int i = 0; i < recombines.length; i = i + 3) {
            int[] chromosome = recombine(candidate_chromosome.get(recombines[i])
                    , candidate_chromosome.get(recombines[i + 1])
                    , candidate_chromosome.get(recombines[i + 2]));
            candidate_chromosome.add(chromosome);
        }

    }

    private int[] recombine(int[] c1, int[] c2, int[] c3) {
        int starIndex = 1;
        int[] copy1 = new int[c1.length];
        int[] copy2 = new int[c1.length];
        int[] copy3 = new int[c1.length];
        copyChromosome(c1, copy1);
        copyChromosome(c2, copy2);
        copyChromosome(c3, copy3);
        for (int i = starIndex; i < copy1.length; i++) {
            int gene = distanceMatrix[copy1[i]][copy1[i - 1]] > distanceMatrix[copy2[i]][copy2[i - 1]] ? (distanceMatrix[copy2[i]][copy2[i - 1]] > distanceMatrix[copy3[i]][copy3[i - 1]] ? copy3[i] : copy2[i]) : (distanceMatrix[copy1[i]][copy1[i - 1]] > distanceMatrix[copy3[i]][copy3[i - 1]] ? copy3[i] : copy1[i]);
            rotate(copy1, i, findGene(copy1, gene));
            rotate(copy2, i, findGene(copy2, gene));
            rotate(copy3, i, findGene(copy3, gene));
        }
        return copy1;
    }

    private void sortChromosomes() {
        Collections.sort(candidate_chromosome, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Double.compare(getDistance(a), getDistance(b));
            }
        });
        double distance = getDistance(candidate_chromosome.get(0));
        if (shortestDistance > distance) {
            copyChromosome(candidate_chromosome.get(0), bestChromosome);
            shortestDistance = distance;
            currentSameBest = 0;
        } else
            currentSameBest++;
    }


    private void mutatePhase() {
        Random rand = new Random();
        for (int[] chromosome : candidate_chromosome) {
            if (rand.nextDouble() < MUTATE_RATE) {
                mutate(chromosome);
            }
        }
    }

    private void mutate(int[] chromosome) {

        if (chromosome.length <= 3) return;
        Random rand = new Random();
        int order1 = rand.nextInt(allLocations.size() / 2),
                order2 = rand.nextInt(allLocations.size() / 2),
                start1 = -1,
                start2 = -1,
                end1 = -1,
                end2 = -1;
        for(int i = 1;i < chromosome.length;i++){
            if(chromosome[i] == order1 * 2 + 1){
                start1 = i;
            }else if(chromosome[i] == order1 * 2 + 2){
                end1 = i;
            }
            if(chromosome[i] == order2 * 2 + 1){
                start2 = i;
            }else if(chromosome[i] == order2 * 2 + 2){
                end2 = i;
            }
        }
        swap(chromosome, start1, start2);
        swap(chromosome, end1, end2);
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
