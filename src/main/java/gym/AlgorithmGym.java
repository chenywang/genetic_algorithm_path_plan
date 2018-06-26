package gym;

import domain.Dada;

import static com.dada.util.CommonUtil.getRandomDada;
import static com.dada.util.EvaluateUtil.evaluate;

import path.plan.algorithm.ClimbingAlgorithm;
import path.plan.algorithm.DynamicProgramPathPlan;
import path.plan.algorithm.GeneticAlgorithm;
import path.plan.algorithm.JspritPathPlan;

public class AlgorithmGym {
    public static void main(String[] args) {
        Dada dada = getRandomDada(200);
        evaluate(new ClimbingAlgorithm(), dada);
        evaluate(new GeneticAlgorithm(), dada);
//        evaluate(new DynamicProgramPathPlan(), dada);
        evaluate(new JspritPathPlan(), dada);
    }
}
