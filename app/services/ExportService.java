package services;

import engine.ExportEngine;

import java.util.*;

import static models.Kvit.*;


public class ExportService {


    private static Map<String,String> createKvit(int postal, int address, int id) {
        Map<String,String> kvit = new HashMap<>();

        kvit.put(POSTAL(), "postal_" + postal);
        kvit.put(ADDRESS_SHORT(), "address_" + address);
        kvit.put(ID(), new Integer(id).toString());

        return kvit;
    }

    public static List<Map<String,String>> run(String codeBase) {

        ArrayList<Map<String,String>> kvits = new ArrayList<>();

        int i = 0;
        for (int postal = 0; postal < 5; postal++) {
            for (int address = 0; address < 37; address++) {
                kvits.add(createKvit(postal,address,i));
            }
        }

        return kvits;
    }

}
