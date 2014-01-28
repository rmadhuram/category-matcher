package matcher;
import au.com.bytecode.opencsv.CSVReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Raj
 * Date: 1/26/14
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */


class Category {
    public String id;
    public String name;
    public Category parent;
    public Category next; // linear traversal
}

class CategoryLoader {
    private HashMap<String, Category> catMap;

    public Category load(String fileName) throws FileNotFoundException, IOException {
        catMap = new HashMap<String, Category>();
        Category root = new Category();
        Category prevCat = root;

        root.id = "0";

        CSVReader reader = new CSVReader(new FileReader(fileName));
        List<String[]> myEntries = reader.readAll();
        for (String[] entry: myEntries) {
            String id = entry[0];

            Category thisCat = new Category();
            thisCat.id = id;
            thisCat.name = entry[1];

            String parentId = entry[2];
            if (parentId.equals("")) {
                thisCat.parent = root;
            } else {
                if (catMap.get(parentId) != null) {
                    thisCat.parent = catMap.get(parentId);
                } else {
                    System.out.println("Cannot find parent for " + entry[1]);
                }
            }

            catMap.put(id, thisCat);
            prevCat.next = thisCat;
            prevCat = thisCat;
        }
        return root;
    }
}

public class CategoryMatcher {

    public String[] tokenize(String str) {
        str = str.toLowerCase();
        str = str.replaceAll("[&-']", " ");
        str = str.replaceAll("[^a-zA-Z0-9 ]", "");
        str = str.replaceAll("[ ]+", " ");

        ArrayList<String> retList = new ArrayList<String>();
        for (String s: str.split(" ")) {
            if (s.equals("and"))
                continue;

            s = s.replaceAll("s$", "");
            retList.add(s);
        }

        return retList.toArray(new String[retList.size()]);
    }

    public boolean compare(String[] a1, String[] a2) {
        if (a1.length != a2.length)
            return false;

        for (int i=0; i<a1.length; i++) {
            if (!a1[i].equals(a2[i]))
                return false;
        }

        return true;
    }

    public void match(String category) throws FileNotFoundException, IOException {
        System.out.println("Matching: " + category);

        CategoryLoader cl = new CategoryLoader();
        Category cat = cl.load("categories.csv");
        String[] catMatch = tokenize(category);

        cat = cat.next;
        boolean found = false;
        while (cat != null) {

            if (compare(catMatch, tokenize(cat.name))) {
                System.out.println("Match: " + cat.id + " - " + cat.name);
                found = true;
            }

            cat = cat.next;
        }

        if (!found)
            System.out.println("No match found!");

        System.out.println("----------");
    }

     public static void main(String [] args) throws FileNotFoundException, IOException {
         CategoryMatcher cm = new CategoryMatcher();
         cm.match("Flowers and Gifts");
         cm.match("Driving School");
         cm.match("Men's Hair Salons");
         cm.match("dog walker");
     }
}
