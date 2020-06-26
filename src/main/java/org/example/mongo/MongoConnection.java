package org.example.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.example.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MongoConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection items;
    private Object String;

    public MongoConnection()
    {
        mongoClient = new MongoClient();
        database = mongoClient.getDatabase("monitor");
        items = database.getCollection("items");
    }
    public void addNewItem(String json)
    {
        items.insertOne(Document.parse(json));
    }
    public Item getItemByPid(String pid)
    {

        Document document  = (Document) items.find(Filters.eq("pid",pid)).first();
        if(document == null)
        {
            return null;
        }
        List<String> sizes = (List<String>) document.get("sizes");
        return new Item(document.getString("name"),document.getString("price"),document.getString("itemLink"),sizes,document.getString("imageUrl")
        ,document.getString("pid"));
    }
    public void updateItem(Item item)
    {
        items.replaceOne(Filters.eq("pid",item.getPid()),
                Document.parse(item.toJSON())
                );
    }


}
