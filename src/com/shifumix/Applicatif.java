package com.shifumix;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by u016272 on 23/10/2015.
 */
public abstract class Applicatif {
    public String name = "";
    public boolean isConnected = false;
    protected int nClick = 0;
    static WebDriver web = null;
    Logger log = Logger.getLogger(this.getClass().getSimpleName());

    protected Integer maxPosition = 15; //Position au dela du quel la recherche est considéré comme innactive

    protected void initWeb() {
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("disable-popup-blocking");
            DesiredCapabilities capabilities = DesiredCapabilities.chrome();
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);

            this.web = new RemoteWebDriver(new URL("http://localhost:9515"), capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.web.manage().timeouts().pageLoadTimeout(200,TimeUnit.SECONDS);
        this.web.manage().timeouts().setScriptTimeout(100, TimeUnit.SECONDS);
        this.web.manage().window().maximize();
    }

    public Applicatif() throws MalformedURLException {
        this.name = this.getClass().getSimpleName();
    }


    public String getSource(){return web.getPageSource();}
    public String getText(){return web.findElement(By.tagName("body")).getText();}
    public abstract String getContent();

    public void closeAllPopup(){
        for(String s:web.getWindowHandles()){
            web.switchTo().window(s);
            web.close();
        }
    }

    public WebElement back(){
        return webTo("back");
    }

    public String file(String url){
        String[] urls=url.split("/");
        return urls[urls.length-1];
    }

    public WebElement webTo(String url){
        if(web==null)initWeb();
        try{
            if(url.equals("back")) {
                web.navigate().back();
                sleep(3);
            }else{
                web.get(url);
                int nTry=20;

                String url_navigator=web.getCurrentUrl();
                if(!url_navigator.startsWith("http"))url_navigator="http://"+url_navigator;

                while(!file(url_navigator).equalsIgnoreCase(file(url)) && nTry>0){
                    url_navigator=web.getCurrentUrl();
                    sleep(1);
                    nTry--;
                    log.info("Attente de l'url "+url+" dans "+url_navigator);
                };
                if(nTry==0)
                    return null;
            }
            return web.findElement(By.tagName("body"));
        }catch (Exception e){
            return null;
        }
    }

    public static void sleep(Integer secondes) {
        try {
            Thread.sleep(secondes * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

     public void maximize() {
        web.manage().window().setSize(new Dimension(1200,800));
    }

    public void minimize() {
        web.manage().window().setPosition(new Point(0, 0));
        web.manage().window().setSize(new Dimension(200, 200));
    }

    public boolean send(String text, WebElement elt) {
        if (elt == null) {
            return false;
        }
        if (text == null) return false;

        if (elt.getTagName().equals("input"))
            elt.clear();

        elt.sendKeys(text);
        return true;
    }


    public boolean click(WebElement elt) {
        if (elt == null || !elt.isEnabled() || !elt.isDisplayed()) return false;
        this.nClick++;
        elt.click();
        return true;
    }


    public boolean click(String id) {
        return click(getElt(id));
    }

    List<WebElement> getElts(String attrName, String value, String tagName) throws TimeoutException {
        return getEltsByType(tagName, attrName, value);
    }



    List<WebElement> getEltsByType(String tagName, String attrName, String value) throws TimeoutException {
        List<WebElement> rc = new ArrayList<WebElement>();
        for (WebElement elt : web.findElements(By.tagName(tagName))) {
            String eltValue = null;
            try {
                eltValue = elt.getAttribute(attrName);
                //System.out.println(eltValue);
            } catch (StaleElementReferenceException e) {
                e.printStackTrace();
            }
            if (eltValue != null && eltValue.startsWith(value))
                rc.add(elt);
        }

        return rc;
    }


    protected ArrayList<String> extractValuesFrom(List<WebElement> lst, String attrName,String filter) throws TimeoutException {
        ArrayList<String> rc = new ArrayList<String>();
        for (WebElement e : lst) {
            String url=e.getAttribute(attrName);
            Boolean bAdd=true;
            if(filter!=null) {
                if (filter.startsWith("!")) {
                    if (url.indexOf(filter.substring(1)) >= 0) bAdd = false;
                } else if (url.indexOf(filter) < 0) bAdd = false;
            }

            if(bAdd)rc.add(url);
        }
        return rc;
    }



    protected ArrayList<String> extractValuesFrom(List<WebElement> lst, String attrName) throws TimeoutException {
        return extractValuesFrom(lst,attrName,null);
    }


    List<WebElement> getElts(String s, WebElement elt) throws TimeoutException {

        if (elt == null) elt= (WebElement) web;

        List<WebElement> rc = new ArrayList<WebElement>();

        try {
            rc.addAll(elt.findElements(By.id(s)));
            if (rc.size() == 0) rc.addAll(elt.findElements(By.name(s)));
            if (rc.size() == 0) rc.addAll(elt.findElements(By.linkText(s)));
            if (rc.size() == 0) rc.addAll(elt.findElements(By.partialLinkText(s)));
            if (rc.size() == 0) rc.addAll(elt.findElements(By.className(s)));
            if (rc.size() == 0) rc.addAll(elt.findElements(By.tagName(s)));
            if (rc.size() == 0) rc.addAll(elt.findElements(By.xpath(s)));

        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return rc;
    }

    WebElement getElt(String s, WebElement elt) throws TimeoutException {
        List<WebElement> rc = getElts(s, elt);
        if (rc.size() == 0)
            return null;
        else
            return rc.get(0);
    }

    WebElement getElt(String attr, String value, List<WebElement> elts) throws TimeoutException {
        List<WebElement> rc = getElts(attr, value, elts);
        if (rc.size() == 0)
            return null;
        else
            return rc.get(0);
    }


    List<WebElement> getElts(String attr, String value, List<WebElement> elts) {
        List<WebElement> rc = new ArrayList<WebElement>();
        if (elts == null) return rc;

        for (WebElement elt : elts) {
            String val = null;
            try {
                val = elt.getAttribute(attr);
            } catch (StaleElementReferenceException e) {
                e.printStackTrace();
            }
            //String log=toString(elt);
            if (val != null && ((value.endsWith("*") && val.startsWith(value.substring(0, value.length() - 1)) ||
                    val.equals(value) ||
                    (value.startsWith("*") && val.endsWith(value.substring(1))))))
                if (elt.isDisplayed())
                    rc.add(elt);
        }
        return rc;
    }


    protected String toString(WebElement elt) {
        if (elt == null) return "null";
        String s = "";
        s = elt.getTagName() + " de id=" + elt.getAttribute("id") + " name=" + elt.getAttribute("name") + " code=" + elt.getAttribute("outerHTML");
        return s;
    }


    WebElement getElt(String attr, String value, String tagName) throws SearchException {
        return getElt(attr, value, tagName, null);
    }


    WebElement getElt(String attr, String value, String tagName, WebElement zone) throws SearchException {
        if (zone == null) zone = web.findElement(By.tagName("BODY"));
        List<WebElement> elts = getElts(attr, value, zone.findElements(By.tagName(tagName)));
        if (elts.size() > 0) {
            //elts.get(0).sendKeys(" ");
            return elts.get(0);
        }
        return null;
        //throw new SearchException(attr+"="+value+" pour "+tagName+" introuvable sur "+web.getCurrentUrl());
    }

    protected WebElement getElt(String str) {
        if (str == null) return null;

        List<WebElement> rc = getElts(str);
        if (rc.size() == 0) {
            return null;
        } else
            return rc.get(0);
    }


    List<WebElement> getElts(String s) {
        return getElts(s, web.findElement(By.tagName("body")));
    }

    /**
     *
     * @param name
     * @param tagName
     * @return
     */
    List<WebElement> getElts(String name, String tagName) {
        return getElts("name", name, web.findElements(By.tagName(tagName)));
    }


    public List<WebElement> getLinks() {
        return web.findElements(By.tagName("a"));
    }



    public List<String> getLinks(String url) {
        List<String> rc = new ArrayList<String>();
        for (WebElement e : web.findElements(By.tagName("a"))) {
            String s = e.getAttribute("href");
            if(s!=null)
                if ((s.indexOf(url)>=0 || (url==null) || (url.length()==0))) rc.add(s);
        }

        return rc;
    }



    //indique si une url est présente dans la page courante
    protected boolean isTextInPage(String textToFind,String tagName) {
        if(textToFind.startsWith("http"))tagName="a";
        if(tagName==null || tagName.length()==0) {
            WebElement body=web.findElement(By.tagName("body"));
            String text=body.getText();
            return (getContent().indexOf(textToFind) > 0);
        }else{
            return getElts(tagName,textToFind).size()>0;
        }
    }


    /**
     * Déclenche la recherche d'un texte dans l'ensemble des liens d'une zone
     * @param "zone" designe la zone contenant les lien, si null on prend la page
     * @param textToFind
     * @return l'url dans laquelle on a trouvé le texte et sa position dans la page
     */
    public String findText(List<String> urls, String textToFind, String tagName) {
        ArrayList<String> rc = new ArrayList<String>();
        int i=0;

        ((JavascriptExecutor)web).executeScript( "window.onbeforeunload=function(e){};window.onload=function(e){};");

        for (String url : urls) {
            i++;
            if(webTo(url)!=null && url.indexOf("www.google.fr")<0){
                if(isTextInPage(textToFind,tagName)) return "{'position':"+i+",'url':'"+url+ "'}";
            }
        }
        return "{'position':"+this.maxPosition+",'url':''}";
    }



    protected String findText(WebElement zone, String textToFind, String filter) {
        return this.findText(extractValuesFrom(getElts("a", zone),"href"),textToFind,filter);
    }

    public void proxyPopup(){

    }

    public List<WebElement> getLinks(WebElement e) {
        if (e == null) return null;
        return e.findElements(By.tagName("a"));
    }



    public String extract(String text, String debut, String fin) {
        int P1 = text.indexOf(debut);
        if (P1 == -1) P1 = 0;
        else P1 += debut.length();

        int P2 = text.indexOf(fin, P1 + 1);
        if (P2 == -1) P2 = text.length() - 1;

        return text.substring(P1, P2);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public abstract void closeClient();
    public abstract void openClient(String user,String password);


    public String getTitle() {
        return this.web.getTitle();
    }
}