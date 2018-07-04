package path.plan.algorithm;


import domain.Dada;
import domain.Path;

public interface Algorithm {
    Path planPath(Dada dada, double[][] matrix);
    String getName();
}
