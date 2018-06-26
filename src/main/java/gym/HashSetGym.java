package gym;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HashSetGym {
    public static void main(String[] args) {
        testAddSpeed();
    }
    public static void testAddSpeed(){
        List<HashSet> setList = new ArrayList<>();
        int size = 1000000;
        int iterateTime = 1000;
        for(int i = 0;i < size;i++) {
            setList.add(new HashSet<>());
        }

        for(int i = 0;i < size;i++){
            long t1 = System.currentTimeMillis();
            for(int j = 0;j < iterateTime;j++){
                setList.get(j).add(i);
            }
            long t2 = System.currentTimeMillis();
            System.out.println("i : " + i + " time cost : " + (t2 - t1)/1000.0);
        }
    }
}
