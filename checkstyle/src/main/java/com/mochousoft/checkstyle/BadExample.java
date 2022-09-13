package com.mochousoft.checkstyle;




public class BadExample {
    public static final long abc = 100;
    public BadExample()
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

    public int getTest() {
        return 1;
    }

}
