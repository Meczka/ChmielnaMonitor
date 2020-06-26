package org.example;

import org.example.webhook.WebhookUtils;

public class Comparator {
    public static synchronized void compare(Item webItem, Item databaseItem)
    {
        if(!webItem.getPrice().equals(databaseItem.getPrice()))
        {
            //WebhookUtils.priceChangeWebhook(webItem,databaseItem);
        }
        if(!WebhookUtils.serializeList(webItem.getSizes()).equals(WebhookUtils.serializeList(databaseItem.getSizes())))
        {
            if(webItem.getSizes().size()>databaseItem.getSizes().size()) {
               // WebhookUtils.restockWebhook(webItem, databaseItem);
            }
        }
    }
}
