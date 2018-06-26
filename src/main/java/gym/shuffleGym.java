package gym;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static com.dada.util.CommonUtil.initChromosome;
import static com.dada.util.CommonUtil.printChromosome;
import static com.dada.util.CommonUtil.println;
import static sun.misc.Version.print;

public class shuffleGym {
    public static void main(String[] args) {
        int[] c = initChromosome(20);
//        printChromosome(c);
        long startTime = System.currentTimeMillis();
        shuffle1(c);
        long endTime = System.currentTimeMillis();
        System.out.println("shuffle1 : " + (endTime - startTime)/1000.0);
        printChromosome(c);

//        startTime = System.currentTimeMillis();
//        shuffle2(c);
//        endTime = System.currentTimeMillis();
//        System.out.println("shuffle2 : " + (endTime - startTime)/1000.0);
//        printChromosome(c);
    }

    private static void shuffle1(int[] chromosome) {
        Set<Integer> set = new HashSet<>();
        set.add(0);
        chromosome[0] = 0;
        Random rand = new Random();
        int index = 1;
        int randomTimes = 0;
        while (set.size() < chromosome.length) {
            randomTimes++;
            int r = rand.nextInt(chromosome.length);
            //you must fetch before finish
            if (set.contains(r)) continue;
            if (r % 2 == 0 && !set.contains(r - 1)) {
                set.add(r - 1);
                chromosome[index++] = r - 1;
            } else {
                set.add(r);
                chromosome[index++] = r;
            }
        }
    }

//    private static void shuffle2(int[] chromosome) {
//        int orderNum = (chromosome.length - 1) / 2;
//        boolean[] orderMarker = new boolean[orderNum];
//        HashSet<Integer> set = new HashSet<>();
//
//        for (int i = 0; i < orderNum; i++) {
//            set.add(i);
//        }
//
//        for (int i = 1; i < chromosome.length; i++) {
//            int orderIndex = getRandomValueFromCollection(set);
//            if (!orderMarker[orderIndex]) {
//                chromosome[i] = orderIndex * 2 + 1;
//                orderMarker[orderIndex] = true;
//            } else if (orderMarker[orderIndex]) {
//                chromosome[i] = orderIndex * 2 + 2;
//                set.remove(orderIndex);
//            }
//        }
//    }

    public static Integer getRandomValueFromCollection(Collection<Integer> collection) {
        return (Integer) collection.toArray()[new Random().nextInt(collection.size())];
    }

}
