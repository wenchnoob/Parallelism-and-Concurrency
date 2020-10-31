/**
 * Author: Wenchy Dutreuil
 * Date: 30 October 2020
 * ReciprocalSumSimple
 *
 * Comment...utilizing threads and trying to retrieve an instance variable within the runnable
 * threw a StackOverFlowError. (figured out stack overflow errow was simply due to a massive recursion depth of 1000000
 *
 * Now code is kinda functional...there appears to be a data race.
 * Code started working...I mean not crashing.
 * --Super tragic fail less 1/33 increase in performance. In other words our computation got much slower.
 * Multi-threading is a waste of time? or am I not smart.
 * Find out in the next practice file.
 * */


import java.util.Random;
import java.util.concurrent.*;

public class ReciprocalSumSimple {
    private final static int THRESHOLD = 10000000;


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] arr = genIntArr();

        System.out.println();
        long start = System.currentTimeMillis();
        System.out.printf("Your reciprocal sum using a single thread is: %f\n", reciprocalSum(arr));
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.printf("Elapsed time for this operations is %d millisecs\n",elapsedTime);

        System.out.println();
        System.out.println();
        long start2 = System.currentTimeMillis();
        System.out.printf("Your reciprocal sum using multi-threading is: %f\n", threadedReciprocalSum(arr, 1));
        long elapsedTime2 = System.currentTimeMillis() - start2;
        System.out.printf("Elapsed time for this operations is %d millisecs\n",elapsedTime2);
    }

    //Finds the reciprocal sum of an array using a simple linear method of addition.
    public static double reciprocalSum(int[] arr) {
        double sum = 0;
        for(int i = 0; i < arr.length; i++) sum += 1/arr[i];
        return sum;
    }

    public static double threadedReciprocalSum(int[] arr, int numThreads) throws ExecutionException, InterruptedException {
        double sum = 0;

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        int lasti = 0;
        for (int i = 0; i < arr.length - THRESHOLD; i += THRESHOLD) {
            lasti = i;
            SumThread sT = new SumThread(arr, i, i+THRESHOLD);
            sum += pool.submit(sT).get();
        }

        for(int i = lasti; i < arr.length; i++) sum += 1/arr[i];
        if (pool.awaitTermination(1000, TimeUnit.MILLISECONDS)) pool.shutdownNow();

        return sum;
    }


    public static double threadedReciprocalSum(int[] arr, int start, int end) throws ExecutionException, InterruptedException {
        if(end - start < THRESHOLD) {
            ExecutorService pool = Executors.newFixedThreadPool(1);
            SumThread sT = new SumThread(arr, start, end);
            return pool.submit(sT).get();
        } else{
            double left = threadedReciprocalSum(arr, start, end/2);
            double right = threadedReciprocalSum(arr, end/2, end);
            return left + right;
        }
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
class SumThread implements Callable<Double> {
    private final int[] arr;
    private final int start, end;
    private double sum;

    SumThread(int[] arr, int start, int end) {
        this.arr = arr;
        this.start = start;
        this.end = end;
    }

    @Override
    public Double call() throws Exception {
        sum = 0;
        for(int i = start; i < end; i++) sum += 1/arr[i];
        System.out.println(String.format("final sum is %f", sum));
        return sum;
    }
}
