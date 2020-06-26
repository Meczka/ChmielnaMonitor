package org.example.utils;

import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DebugUtils {
        public static void printHttpCookies(List<HttpCookie> cookies)
        {
            System.out.println("PRINTING COOKIES------------------------");
            List<HttpCookie> cfduids = new ArrayList<>();
            for(HttpCookie cookie : cookies)
            {
                System.out.println(cookie.getName() + " = " + cookie.getValue());
                if(cookie.getName().equalsIgnoreCase("__cfduid"))
                {
                    System.out.println(cookie.getDomain()+" " + cookie.getVersion() + " " + cookie.getPath());
                    cfduids.add(cookie);
                }
            }
            System.out.println(cfduids.size());
            System.out.println("------------------------------------------");
        }
    public static void printMap(Map<String,String> map)
    {
        System.out.println("PRINTING MAP---------------------");
        for(Map.Entry<String,String> entry : map.entrySet())
        {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
        System.out.println("----------------------------");
    }
}
