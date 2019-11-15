package utils;

public class Printer {

    public static void safePrintf(String fmt, Object... args) {
        synchronized (System.out) {
            System.out.printf(fmt, args);
            System.out.println();
        }
    }

}
