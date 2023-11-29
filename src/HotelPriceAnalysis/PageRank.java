package HotelPriceAnalysis;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

public class PageRank {

    public List<Page> getPageRank(String searchElement, String[] files) throws Exception{
        Map<String, Integer> wordCounts = new HashMap<>();
        int c = 0;
        String[] s ={"","",""};
        for(String y: files){
            StringBuilder temp = new StringBuilder();
            Path path = Paths.get(y);
            Reader reade = Files.newBufferedReader(path,
                    StandardCharsets.UTF_8);
            JsonParser p = new JsonParser();
            JsonElement tree = p.parse(reade);
            JsonArray array = tree.getAsJsonArray();
            for(JsonElement e : array){
                if(e.isJsonObject()){
                    JsonObject record = e.getAsJsonObject();
                    ArrayList<String> cities = new ArrayList<>(record.keySet());
                    for(String x : cities){
                        for( JsonElement e2:record.getAsJsonArray(x)){
                            if(e2.isJsonObject()){
                                JsonObject record2 = e2.getAsJsonObject();
                                wordCounts.put(record2.get("name").getAsString(), wordCounts.getOrDefault(record2.get("name").getAsString(), 0) + 1);
//                                System.out.println(record2.get("name").getAsString());
                                temp.append(" ").append(record2.get("name").getAsString().toLowerCase());
                            }
                        }
                    }
                }
            }
            s[c] = temp.toString();
            c++;

        }
        // Search keywords
        String[] keywords = {searchElement};
        for(int j =0 ; j<keywords.length;j++){
            keywords[j] = keywords[j].toLowerCase();
        }
        // Perform page ranking
        List<Page> rankedPages = rankPages(Arrays.asList(s[0], s[1], s[2]), keywords);

        // Display the ranked pages
        System.out.println("Ranked Pages:");
        for (Page page : rankedPages) {
            System.out.println(page);
        }
        return rankedPages;
    }

    private static List<Page> rankPages(List<String> pages, String[] keywords) {
        List<Page> rankedPages = new ArrayList<>();

        // Calculate the ranking for each page
        for (int i = 0; i < pages.size(); i++) {
            String pageContent = pages.get(i);
            int rank = calculateRank(pageContent, keywords);
            rankedPages.add(new Page(i + 1, rank));
        }

        // Sort the pages based on rank in descending order
        rankedPages.sort(Collections.reverseOrder());

        return rankedPages;
    }

    private static int calculateRank(String pageContent, String[] keywords) {
        int rank = 0;

        for (String keyword : keywords) {
            rank += countOccurrences(pageContent, keyword);
        }

        return rank;
    }
//    private static int calculateRankValue(JsonObject obj){
//
//    }
    private static int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = text.indexOf(keyword);

        while (index != -1) {
            count++;
            index = text.indexOf(keyword, index + 1);
        }

        return count;
    }

    public static class Page implements Comparable<Page> {
        int pageNumber;
        int rank;

        Page(int pageNumber, int rank) {
            this.pageNumber = pageNumber;
            this.rank = rank;
        }

        @Override
        public int compareTo(Page otherPage) {
            return Integer.compare(this.rank, otherPage.rank);
        }

        @Override
        public String toString() {
            return "Page " + pageNumber + ": Rank = " + rank;
        }
    }
}
