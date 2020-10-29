public class Threading {
    //When this method is run...the two threads appear to be taking turns printing to the screen. There does not seem to be any sort
    // of precedence. They appear to be running at the same time.
    public static void main(String[] args) {
        Thread t1 = new Thread(new Printer("Thread one: "));
        Thread t2 = new Thread(new Printer("Thread two: "));
        t1.start();
        t2.start();
    }



}
class Printer implements Runnable {
    String identity;

    Printer (String identity) {
        this.identity = identity;
    }

    @Override
    public void run() {
        int i = 0;
        while (i < 100) {
            System.out.println(identity + "I am running...");
            i++;
        }
    }
}