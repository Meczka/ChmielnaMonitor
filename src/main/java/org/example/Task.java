package org.example;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.example.utils.CloudflareBypass;
import org.example.utils.WebUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Task{
    private OkHttpClient httpClient;
    private CookieManager cookieManager;
    private App app;
    final Logger LOGGER;
    private String proxy;
    private boolean isAvailable = true;
    public Task(String proxy,App app,int taskID)
    {
        LOGGER = Logger.getLogger("Task[" + taskID + "]");
        String[] spltted = proxy.split(":");
        cookieManager = new CookieManager();
        httpClient = new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .readTimeout(15, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(spltted[0],Integer.parseInt(spltted[1]))))
                .callTimeout(15,TimeUnit.SECONDS)
                .connectTimeout(15,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        this.app = app;
        this.proxy = proxy;

    }
    public void monitorLink(String link)
    {
        isAvailable = false;
        new Thread(){
            @Override
            public void run() {
                try {
                    if (CloudflareBypass.checkForCloudflare(httpClient, "https://chmielna20.pl", "buty")) {
                        CloudflareBypass.solveJSChallenge("https://chmielna20.pl", cookieManager, proxy);
                    }
                    LOGGER.info("MONITORING: " + link);
                    String response = WebUtils.sendGetRequest(link, WebUtils.getHeaders(), httpClient);
                    //System.out.println(response);
                    Document document = Jsoup.parse(response);
                    Elements products = document.getElementsByClass("col-sm-4 col-md-3 col-xs-6 products__item");
                    List<Item> items = new ArrayList<>();
                    String pidString = response.substring(response.indexOf("'pid'"));

                    pidString = pidString.substring(pidString.indexOf("["), pidString.indexOf("]"));
                    for (int i = 0; i < products.size(); i++) {
                        Element product = products.get(i);
                        String name = product.getElementsByClass("products__item-name").first().getElementsByTag("a").first().text();
                        String productLink = product.getElementsByClass("products__item-name").first().getElementsByTag("a").first().attr("href");
                        String price = product.getElementsByClass("products__item-price").first().getElementsByTag("span").text();
                        String imageUrl = product.getElementsByTag("img").first().attr("data-echo");

                        Elements sizes = product.getElementsByClass("sizes ").first().getElementsByTag("li");
                        List<String> sizesStrings = new ArrayList<>();
                        //System.out.println(productLink + " " + name + " " + price);
                        for (int j = 0; j < sizes.size(); j++) {
                            sizesStrings.add(sizes.get(j).getElementsByTag("span").first().text());
                        }

                        items.add(new Item(name, price, productLink, sizesStrings, imageUrl, pidString.split(",")[i]));
                    }
                    LOGGER.info("FOUND: " + products.size() + " PRODUCTS");
                    app.resolveItems(items);
                }catch (Exception e){e.printStackTrace();}
                try {
                    Thread.sleep(500);
                }catch (Exception e){e.printStackTrace();}
                isAvailable = true;
            }
        }.start();
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
