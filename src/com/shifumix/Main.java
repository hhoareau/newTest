package com.shifumix;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    List<String> lg=new ArrayList<String>();

    public static void log(String s){
        lg.add(System.currentTimeMillis()+":"+s);
    }

    public static void main(String[] args) throws MalformedURLException {
        long delay = 1000 * 60 * 20000;
        if (args.length > 4) delay = Long.parseLong(args[4]) * 1000 * 60;

        long start = System.currentTimeMillis();

        Shifumix a=new Shifumix();

        a.setDomain("https://shifumixweb.appspot.com");
        a.openClient("hhoareau@gmail.com", "hh4271!!");
        a.makeEvent("Test", "on va trop s'amuser", "12, rue martel paris");
        a.makeEvent("Test 2", "on va trop s'amuser", "14, rue de milan paris");
        a.makeEvent("Test 3", "on va trop s'amuser", "18 rue du chateau paris");
        a.selEvent("Test 2");
        a.addSong("cure",1);
        a.addSong("cure",2);
        a.addSong("cure",4);
        a.addSong("cure",5);

        System.out.println("Temps de traitement "+(delay/(1000*60)));
    }
}
