package com.cs328.myrmi;

public class NamingClientTest {
    public static void main(String[] args) {
        TestRemoteInterface test = (TestRemoteInterface) Naming.lookup("rmi://localhost:8080/test");
        test.print("Testing remote naming begins");
        println("increase server field to " + test.increase());
        println("square of 5 is " + test.square(5));
        println("add 5 to 6 is " + test.add(5, 6));
        println("value from server is " + test.getValue());
        try {
            test.testExp();
        } catch (Exception e) {
            println("Exception caught from server: " + e.getMessage());
        }
        println("test remote toString: " + test);
        TestRemoteInterface test2 = (TestRemoteInterface) Naming.lookup("rmi://localhost:8080/test");
        println("test remote equals: " + test.equals(test2));
        test.print("Testing remote naming finished");
    }

    private static void println(String string) {
        System.out.println(string);
    }
}
