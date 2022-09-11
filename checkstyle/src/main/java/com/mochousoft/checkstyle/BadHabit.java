package com.mochousoft.checkstyle;




public class BadHabit {
    public static final long abc = 100;
    public BadHabit()
    {
        System.out.println( 1 ) ;

        String a = "abc";
        try{
            System.out.print(123);
            System.out.println(100);
        }catch(Exception e ){
            e.printStackTrace();
        }
        // TODO admin


    }


    public void test () {

    }

}
