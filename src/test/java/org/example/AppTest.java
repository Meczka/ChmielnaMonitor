package org.example;

import static org.junit.Assert.assertTrue;

import org.example.mongo.MongoConnection;
import org.example.webhook.WebhookUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        MongoConnection mongoConnection = new MongoConnection();
        Item item = mongoConnection.getItemByPid("169644");
     //   WebhookUtils.restockWebhook(item,mongoConnection.getItemByName("ARKK Copenhagen Raven Nubuck S‑E15 (CR1403‑0099‑W)"));
        List<String> sizes = new ArrayList<String>(){{
            add("36");
            add("36.5");
        }};
        WebhookUtils.newItemWebhook(item);
    }
}
