/**
 * Author: Wenchy Dutreuil
 * Date: 30 October 2020
 * ReciprocalSumSimple
 *
 * Comment...utilizing threads and trying to retrieve an instance variable within the runnable
 * threw a StackOverFlowError. (figured out stack overflow errow was simply due to a massive recursion depth of 1000000
 *
 * Now code is kinda functional...there appears to be a data race.
 * */


import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReciprocalSumSimple {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] arr = genIntArr();
        long start = System.currentTimeMillis();
        System.out.printf("Your reciprocal sum is: %f\n", reciprocalSum(arr));
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.printf("Elapsed time for this operations is %d millisecs\n",elapsedTime);

        ExecutorService pool = Executors.newFixedThreadPool(4);
        System.out.printf("Your reciprocal sum is: %f\n", threadedReciprocalSum(arr, 0, arr.length, pool));
    }

    //Finds the reciprocal sum of an array using a simple linear method of addition.
    public static double reciprocalSum(int[] arr) {
        double sum = 0;
        for(double i: arr) sum += 1/i;
        return sum;
    }

    public static double threadedReciprocalSum(int[] arr, int start, int end, ExecutorService pool) throws ExecutionException, InterruptedException {
        if(end - start < 10000000) {
            SumThread sT = new SumThread(arr, start, end);
            Thread t = new Thread(sT);
            t.start();
            t.join();
            return pool.submit((Callable<Double>) sT).get();
        }
        double left = threadedReciprocalSum(arr, start, end/2, pool);
        double right = threadedReciprocalSum(arr, end/2, end, pool);
        return left + right;
    }

    //Generates the int array that will be used to generate the reciprocal sum.
    public static int[] genIntArr() {
        Random r = new Random();
        int[] arr = new int[r.nextInt(10000000)+1000000];
        for(int i = 0; i < arr.length; i++) arr[i] = r.nextInt(100)+1;
        return arr;
    }
}

//Runnable class that will perform our summing computation.
class SumThread implements Runnable, Callable<Double> {
    private final int[] arr;
    private final int start, end;
    private double sum;

    SumThread(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    public void run() {
        sum = 0;
        for(int i = start; i < end; i++) sum += 1/arr[i];
    }

    @Override
    public Double call() throws Exception {
        return sum;
    }
}
