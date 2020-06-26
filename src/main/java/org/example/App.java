package org.example;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.apache.log4j.Logger;
import org.example.mongo.MongoConnection;
import org.example.utils.*;
import org.example.webhook.WebhookUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    private List<String> proxy;
    private List<String> links;
    final Logger LOGGER = Logger.getLogger("Monitor");
    private MongoConnection mongoConnection;
    private Task[] tasks;
    private int nextHttpClient = 0;
    public static void main( String[] args )
    {
        App app = new App();
        app.start();
    }
    public App()
    {
        proxy = FileManager.readFileAsList(new File("proxy.txt"));
        links = FileManager.readFileAsList(new File("links.txt"));
        mongoConnection = new MongoConnection();
        tasks = new Task[proxy.size()];
        for(int i = 0; i < proxy.size(); i++)
        {
           tasks[i] = new Task(proxy.get(i),this,i);
        }
    }
    public void start()
    {
        while (true) {
            for (String link : links) {
                boolean found = false;
                while(!found) {
                    for (Task task : tasks) {
                        if (task.isAvailable()) {
                            task.monitorLink(link);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
    }
    public void monitorLink(String link, OkHttpClient httpClient)
    {
        LOGGER.info("MONITORING: " + link);
        String response = WebUtils.sendGetRequest(link, WebUtils.getHeaders(), httpClient);
        //System.out.println(response);
        Document document = Jsoup.parse(response);
        Elements products = document.getElementsByClass("col-sm-4 col-md-3 col-xs-6 products__item");
        List<Item> items = new ArrayList<>();
        String pidString = response.substring(response.indexOf("'pid'"));
        pidString = pidString.substring(pidString.indexOf("["),pidString.indexOf("]"));
        for(int i = 0; i < products.size();i++)
        {
            Element product = products.get(i);
            String name = product.getElementsByClass("products__item-name").first().getElementsByTag("a").first().text();
            String productLink = product.getElementsByClass("products__item-name").first().getElementsByTag("a").first().attr("href");
            String price = product.getElementsByClass("products__item-price").first().getElementsByTag("span").text();
            String imageUrl = product.getElementsByTag("img").first().attr("data-echo");

            Elements sizes = product.getElementsByClass("sizes ").first().getElementsByTag("li");
            List<String> sizesStrings = new ArrayList<>();
            //System.out.println(productLink + " " + name + " " + price);
            for(int j = 0;j < sizes.size();j++)
            {
                sizesStrings.add(sizes.get(j).getElementsByTag("span").first().text());
            }
            LOGGER.info(pidString);
            items.add(new Item(name,price,productLink,sizesStrings,imageUrl,pidString.split(",")[i]));
        }
        LOGGER.info("FOUND: " + products.size() + " PRODUCTS");
        resolveItems(items);
    }
    public synchronized void resolveItems(List<Item> items)
    {
        for(Item item : items)
        {
            String pid = item.getPid();
            Item databseItem = mongoConnection.getItemByPid(pid);
            if(databseItem == null)
            {
                WebhookUtils.newItemWebhook(item);
                mongoConnection.addNewItem(item.toJSON());
            }
            else if(!item.equals(databseItem))
            {
                Comparator.compare(item,databseItem);
                mongoConnection.updateItem(item);
                LOGGER.info("UPDATED: " + item.toJSON());
            }
        }
    }
}
