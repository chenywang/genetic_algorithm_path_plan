package gym;

import domain.Dada;

import static com.dada.util.CommonUtil.getRandomDada;
import static com.dada.util.CommonUtil.println;
import static com.dada.util.EvaluateUtil.evaluate;
import static com.dada.util.EvaluateUtil.evaluteMultiAlgorithm;
import static com.dada.util.GraphUtil.drawLineChart;

import domain.EvaluateResult;
import path.plan.algorithm.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlgorithmGym {
    public static void main(String[] args) throws Exception{
        multiIterationTest();
//        tuneGeneticAlgorithm();
    }
    public static void tuneGeneticAlgorithm() throws Exception{
        int startIndex = 0, endIndex = 50, iterateTimes = 10;
        List<Algorithm> algorithmList = new ArrayList<>();
        int algorithmCount = 3;

        for (int i = 0;i < algorithmCount;i++){
            GeneticAlgorithm algorithm = new GeneticAlgorithm("algorithm" + i);
            algorithmList.add(algorithm);
        }
        ((GeneticAlgorithm)algorithmList.get(1)).setArgument("recombine", 1);
        ((GeneticAlgorithm)algorithmList.get(2)).setArgument("recombine", 2);
        evaluteMultiAlgorithm(startIndex, endIndex, iterateTimes, algorithmList);
    }

    public static void multiIterationTest(){
        int startIndex = 0, endIndex = 50, iterateTimes = 10;
        List<Algorithm> algorithmList = new ArrayList<>();
        algorithmList.add(new GreedyAlgorithm());
        algorithmList.add(new ClimbingAlgorithm());
        algorithmList.add(new GeneticAlgorithm());
        algorithmList.add(new DynamicProgramPathPlan());
        algorithmList.add(new JspritPathPlan());
        algorithmList.add(new SimulateAnnealing());
        evaluteMultiAlgorithm(startIndex, endIndex, iterateTimes, algorithmList);
    }

}
