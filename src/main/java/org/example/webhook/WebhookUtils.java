package org.example.webhook;

import me.meczka.utils.DiscordWebhook;
import org.example.Item;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


public class WebhookUtils {
    private final static String WEBHOOK_URL = "https://discordapp.com/api/webhooks/723977168232644630/saqHR2E8uV-a4-hMeptwUb3ccecO2pchifYnNWLTouZ3oGpqPuBaeiv1nO1p-pYPZuUs",
    CL_20_IMG = "https://scontent-waw1-1.xx.fbcdn.net/v/t1.0-9/60908718_2236986336377977_6381988395861671936_o.png?_nc_cat=102&_nc_sid=09cbfe&_nc_ohc=9E-fzoRAkiIAX-PsBb3&_nc_ht=scontent-waw1-1.xx&oh=98ce9d2b9b3dddc42930b4c5c6c3eef0&oe=5F11CE16";
    public static void newItemWebhook(Item item)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        DiscordWebhook discordWebhook = new DiscordWebhook(WEBHOOK_URL);
        discordWebhook.setAvatarUrl(CL_20_IMG);
        discordWebhook.setUsername("CL20 Monitor");
        System.out.println(item.getImageUrl());
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setUrl(item.getItemLink())
                .setTitle(item.getName())
                .setAuthor("NEW PRODUCT",null,null)
                .setThumbnail(item.getImageUrl())
                .setFooter("Hypemonitor " + dtf.format(now),"https://res.cloudinary.com/dklrin11o/image/twitter_name/w_600/Hypemonitorpl.jpg")
                .addField("Price",item.getPrice(),false)
                .addField("Sizes",serializeList(item.getSizes()),false)
                .setColor(Color.GREEN);
        discordWebhook.addEmbed(embed);
        try{
            discordWebhook.execute();
        }catch (Exception e){e.printStackTrace();}
    }
    public static void priceChangeWebhook(Item newItem, Item previousItem)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        DiscordWebhook discordWebhook = new DiscordWebhook(WEBHOOK_URL);
        discordWebhook.setAvatarUrl(CL_20_IMG);
        discordWebhook.setUsername("CL20 Monitor");
        System.out.println(newItem.getImageUrl());
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setUrl(newItem.getItemLink())
                .setTitle(newItem.getName())
                .setAuthor("PRICE CHANGE",null,null)
                .setThumbnail(newItem.getImageUrl())
                .setFooter("Hypemonitor " + dtf.format(now),"https://res.cloudinary.com/dklrin11o/image/twitter_name/w_600/Hypemonitorpl.jpg")
                .addField("Previous Price",previousItem.getPrice(),false)
                .addField("New Price",newItem.getPrice(),false)
                .addField("Sizes",serializeList(newItem.getSizes()),false)
                .setColor(Color.BLUE);
        discordWebhook.addEmbed(embed);
        try{
            discordWebhook.execute();
        }catch (Exception e){e.printStackTrace();}
    }
    public static void restockWebhook(Item newItem, Item previousItem)
    {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        DiscordWebhook discordWebhook = new DiscordWebhook(WEBHOOK_URL);
        discordWebhook.setAvatarUrl(CL_20_IMG);
        discordWebhook.setUsername("CL20 Monitor");
        System.out.println(newItem.getImageUrl());
        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
                .setUrl(newItem.getItemLink())
                .setTitle(newItem.getName())
                .setAuthor("RESTOCK",null,null)
                .setThumbnail(newItem.getImageUrl())
                .setFooter("Hypemonitor " + dtf.format(now),"https://res.cloudinary.com/dklrin11o/image/twitter_name/w_600/Hypemonitorpl.jpg")
                .addField("Price",newItem.getPrice(),false)
                .addField("New Sizes",serializeList(newItem.getSizes()),false)
                .addField("Previous Sizes",serializeList(previousItem.getSizes()),false)
                .setColor(Color.RED);
        discordWebhook.addEmbed(embed);
        try{
            discordWebhook.execute();
        }catch (Exception e){e.printStackTrace();}
    }
    public static String serializeList(List<String> list)
    {
        StringBuilder sb = new StringBuilder();
        for(String l : list)
        {
            sb.append(l+"\\n");
        }
        return sb.toString();
    }
}
