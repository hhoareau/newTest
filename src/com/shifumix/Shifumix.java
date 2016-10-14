package com.shifumix;

import java.net.MalformedURLException;

/**
 * Created by u016272 on 04/10/2016.
 */
public class Shifumix extends Applicatif {

    String domain="http://localhost:8080";

    public Shifumix() throws MalformedURLException {
        super();
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public String getContent() {
     return null;
    }



    @Override
    public void closeClient() {

    }

    @Override
    public void openClient(String user, String password) {
        webTo("https://fr-fr.facebook.com/");
        getElt("email").sendKeys(user);
        getElt("pass").sendKeys(password);
        getElt("u_0_n").click();
        webTo(domain + "/index.html");
    }

    public String makeEvent(String name,String teaser,String adresse){
        getElt("makeEvent").click();
        getElt("nameEvent").sendKeys(name);
        getElt("description").sendKeys(teaser);
        return "idEvent";
    }

    public String addSong(String title,Integer index){
        getElt("btAddSong").click();
        getElt("search").sendKeys(title);
        getElt("btSearch").click();
        return "idsong";
    }


    public String selEvent(String s) {
        return null;
    }


    public String selEvent(Double lng,Double lat) {
        return null;
    }


}
