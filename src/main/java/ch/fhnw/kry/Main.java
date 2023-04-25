package ch.fhnw.kry;

public class Main {
    public static void main(String[] args)   {
        MiniRbTable rb = new MiniRbTable();

        rb.generateRainbowTable();
        System.out.println("__Password__");
        System.out.println(rb.getPasswordOf("1d56a37fb6b08aa709fe90e12ca59e12"));
        System.out.println(rb.getPasswordOf("437988e45a53c01e54d21e5dc4ae658a"));
    }
}