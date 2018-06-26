package com.dada.util;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import domain.Dada;
import domain.Location;
import domain.Order;

import java.util.*;

/**
 * @author michael-wang
 */
public class CommonUtil {

    public static double[][] getLinearDistanceMatrix(List<Location> locationList) {
        double[][] matrix = new double[locationList.size()][locationList.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = i + 1; j < matrix.length; j++) {
                matrix[i][j] = getLinearDistance(locationList.get(i), locationList.get(j));
                matrix[j][i] = matrix[i][j];
            }
        }
        return matrix;
    }

    public static double getLinearDistance(Location location1, Location location2) {
        double R = 6378.137;
        if (location1 == null || location2 == null) {
            return Double.MAX_VALUE;
        }
        double a, b;
        double lat1 = location1.lat, lat2 = location2.lat, lng1 = location1.lng, lng2 = location2.lng;

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        a = lat1 - lat2;
        b = Math.toRadians((lng1 - lng2));
        double d;
        d = 2 * Math.asin(
                Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        d = d * R;

        return d < 0 ? -d : d;
    }


    public static void printChromosome(int[] c) {
        for (int i : c) System.out.print(i + " ");
        System.out.println();
    }

    public static boolean isValidChromosome(int[] c){
        for (int i = 1;i < c.length;i++){
            if (c[i] % 2 == 0){
                boolean fine = false;
                for (int j = 1;j < i;j++){
                    if (c[j] == c[i] - 1){
                        fine = true;
                        break;
                    }
                }
                if (!fine){
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] initChromosome(int size){
        if(size % 2 == 0){
            size++;
        }
        int[] chromosome = new int[size];
        for(int i = 0;i < size;i++){
            chromosome[i] = i;
        }
        return chromosome;
    }

    public static String chromosomeToString(int[] chromosome) {
        String s = "";
        for (int i : chromosome) s = s + i + " ";
        return s.trim();
    }

    public static int findGene(int[] chromosome, int gene) {
        for (int i = 0; i < chromosome.length; i++)
            if (gene == chromosome[i]) return i;
        return -1;
    }


    public static void print(Object o) {
        System.out.print(o);
    }

    public static void println(Object o) {
        System.out.println(o);
    }

    public static void println() {
        System.out.println();
    }

    public static void rotate(int[] chromosome, int startIndex, int rotateIndex) {
        if (rotateIndex >= chromosome.length) return;
        int[] copy = new int[chromosome.length];
        copyChromosome(chromosome, copy);
//        println("startIndex,rotateIndex:"+startIndex+" "+rotateIndex);
        int j = startIndex;
        for (int i = rotateIndex; i < chromosome.length; i++) {
            if (chromosome[i] % 2 == 0) {
                boolean isFound = false;
                int find = chromosome[i] - 1;
                for (int k = rotateIndex; k < i; k++) {
                    if (chromosome[k] == find) {
                        isFound = true;
                        break;
                    }
                }
                for (int k = 1; k < startIndex; k++) {
                    if (chromosome[k] == find) {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound) break;
            }
            chromosome[j++] = chromosome[i];
        }
        for (int i = startIndex; i < rotateIndex; i++) {
            chromosome[j++] = copy[i];
        }
    }

    public static void JDRotate(int[] chromosome, int startIndex, int rotateIndex, boolean isReturn) {
        if (rotateIndex >= chromosome.length) return;
        int[] copy = new int[chromosome.length];
        copyChromosome(chromosome, copy);
        int j = startIndex, rotateEnd = isReturn ? chromosome.length - 1 : chromosome.length;
        for (int i = rotateIndex; i < rotateEnd; i++) {
            chromosome[j++] = chromosome[i];
        }
        for (int i = startIndex; i < rotateIndex; i++) {
            chromosome[j++] = copy[i];
        }
    }

    public static int[] getRealChromosom(int n, boolean isReturn) {
        int resultLen;
        if (isReturn) resultLen = n + 1;
        else resultLen = n;
        int[] result = new int[resultLen];
        for (int i = 0; i < n; i++)
            result[i] = i;
        if (isReturn) result[result.length - 1] = 0;
        return result;
    }

    public static void copyChromosome(int[] from, int[] to) {
        for (int i = 0; i < from.length; i++) {
            to[i] = from[i];
        }
    }

    public static void JDShuffle(int[] prototype, boolean isReturn) {
        Set<Integer> set = new HashSet<>();
        if (isReturn) {
            set.add(prototype.length - 1);
            prototype[prototype.length - 1] = prototype.length - 1;
        }
        set.add(0);
        prototype[0] = 0;
        Random rand = new Random();
        int index = 1;
        while (set.size() < prototype.length) {
            int r = rand.nextInt(prototype.length);
            //you dont have to fetch before finish
            if (set.contains(r)) continue;
            set.add(r);
            prototype[index++] = r;
        }
    }

    public static double getStraightDistance(Location l1, Location l2) {
        if (l1 == null || l2 == null) return Double.MAX_VALUE;
        double a, b, R;
        double lat1 = l1.lat, lat2 = l2.lat, long1 = l1.lng, long2 = l2.lng;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2
                * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    public static double getDistanceByXY(Location p1, Location p2) {
        return Math.sqrt(Math.pow(p1.lat - p2.lat, 2) + Math.pow(p1.lng - p2.lng, 2));
    }
    public static double getDistance(List<Location> route, double[][] matrix) {
        double distance = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            distance += matrix[route.get(i).getIndex()][route.get(i + 1).getIndex()];
        }
        return distance;
    }
    public static double getDistance(int[] chromosome, double[][] matrix){
        double distance = 0;
        for (int i = 0; i < chromosome.length - 1; i++) {
            distance += matrix[chromosome[i]][chromosome[i + 1]];
        }
        return distance;
    }

    public static Location getRandomLocation() {
        Random rand = new Random();
        return new Location(121 + 3 * rand.nextDouble(), 31 + 3 * rand.nextDouble());
    }

    public static List<Location> getRandomLocationList(int size) {
        List<Location> randomLocationList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            randomLocationList.add(getRandomLocation());
        }
        return randomLocationList;
    }

    public static Dada getRandomDada(int pointSize) {
        if (pointSize % 2 == 0) {
            pointSize++;
        }

        List<Location> locationList = getRandomLocationList(pointSize);
        for (int i = 0; i < pointSize; i++) {
            locationList.get(i).setIndex(i);
        }

        List<Order> orderList = new ArrayList<>();
        for (int i = 1; i < locationList.size(); i = i + 2) {
            orderList.add(new Order(locationList.get(i), locationList.get(i + 1), false));
        }
        return new Dada(locationList.get(0), orderList);
    }


    public static void swap(int[] array, int index1, int index2){
        int temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }

    public static List<Location> getAllLocations(Dada dada) {
        List<Location> allLocations = new ArrayList<>(dada.getOrders().size() * 2 + 1);
        allLocations.add(dada.getCurrent());
        for (Order o : dada.getOrders()) {
            allLocations.add(o.getStart());
            allLocations.add(o.getEnd());
        }
        return allLocations;
    }

    public static void array_shuffle(int[] array){
        Random rand = new Random();
        for(int i = 0;i < array.length;i++){
            int p = rand.nextInt(array.length - i);
            swap(array, i, p);
        }
    }

    public static void shuffleChromosome(int[] chromosome) {
        int orderNum = (chromosome.length - 1) / 2;
        boolean[] orderMarker = new boolean[orderNum];
        HashSet<Integer> set = new HashSet<>();
        Random rand = new Random();

        for (int i = 0; i < orderNum; i++) {
            set.add(i);
        }

        for (int i = 1; i < chromosome.length; i++) {
            int orderIndex = (Integer) set.toArray()[rand.nextInt(set.size())];
            if (!orderMarker[orderIndex]) {
                chromosome[i] = orderIndex * 2 + 1;
                orderMarker[orderIndex] = true;
            } else if (orderMarker[orderIndex]) {
                chromosome[i] = orderIndex * 2 + 2;
                set.remove(orderIndex);
            }
        }
    }

}
