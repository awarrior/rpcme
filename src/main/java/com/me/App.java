package com.me;

/**
 * Hello world!
 */
public class App {
    private static ThreadLocal<Integer> tl = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return 12;
        }
    };
    private static ThreadLocal<Integer> pp = new ThreadLocal<>();

    static {
        pp.set(33);
    }

    public static void main(String[] args) {
        System.out.println(tl.get());
        System.out.println(pp.get());
    }
}
