package HotelPriceAnalysis;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TrieNode {
    Map<Integer, Integer> documentFrequency;
    Map<Character, TrieNode> children;
    JsonObject keys;
    public TrieNode() {
        this.documentFrequency = new HashMap<>();
        this.children = new HashMap<>();
        this.keys = new JsonObject();
    }
}

public class InvertedIndex {
    public static TrieNode root = new TrieNode();
    public static String[] websites = {"HotelsCA.com","ExpediaCA.com","Booking.com"};

    public List<JsonObject> index(String[] files) throws IOException {
        String[] s ={"","",""};
        int c= 0;
        int lineNumber = 0;
        List<JsonObject> allResults = new ArrayList<>();
        for(String y: files){
            String temp = "";
            lineNumber++;
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
                        int c1 = 0;
                        for(JsonElement fd : record.getAsJsonArray(x)){

                            if(fd.isJsonObject()){
                                JsonObject record2 = fd.getAsJsonObject();

                                temp =  record2.get("name").getAsString();
                                allResults.add(record2);
                                String[] words = temp.split(" ");

                                for (String word : words) {
                                    temp = word.toLowerCase();

                                    if (!word.isEmpty()) {
                                        record2.addProperty("URL",websites[lineNumber-1]);
                                        insertIntoTrie(root, temp, lineNumber,record2);
                                    }
                                }

                            }
                            c1++;
                        }

                    }
                }
            }
            s[c] = temp;
            c++;

        }
        return allResults;
    }

    public List<SearchResult> search(String searchTerm){
        List<SearchResult> searchResults = searchInvertedIndex(root, searchTerm);
        List<Integer> docIds = new ArrayList<>();
        for (SearchResult result : searchResults) {
            docIds.add(result.getDocumentId() -1 );
            result.getJsonObjectDetails();
        }
        return searchResults;
    }

    private void insertIntoTrie(TrieNode root, String word, int lineNumber, JsonObject e) {

        TrieNode current = root;

        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }

        current.documentFrequency.put(lineNumber, current.documentFrequency.getOrDefault(lineNumber, 0) + 1);
        current.keys = e;
    }

    private void printInvertedIndex(TrieNode node, String prefix) {
        for (char c : node.children.keySet()) {
            TrieNode childNode = node.children.get(c);
            printInvertedIndex(childNode, prefix + c);
        }

        if (!node.documentFrequency.isEmpty()) {
            System.out.print(prefix + " -> ");
            for (int docId : node.documentFrequency.keySet()) {
                System.out.print(docId + ":" + node.documentFrequency.get(docId) + " ");
                System.out.println(node.keys);
            }
            System.out.println();
        }
    }
    private List<SearchResult> searchInvertedIndex(TrieNode root, String searchTerm) {
        List<SearchResult> results = new ArrayList<>();
        TrieNode current = root;

        for (char c : searchTerm.toCharArray()) {
            if (!current.children.containsKey(c)) {
                // Term not found in the inverted index
                return results;
            }

            current = current.children.get(c);
        }

        // Traverse to the end of the term and collect results
        collectResults(current, results);

        return results;
    }
    private void collectResults(TrieNode node, List<SearchResult> results) {

        for (int docId : node.documentFrequency.keySet()) {
            int frequency = node.documentFrequency.get(docId);

            results.add(new SearchResult(docId, frequency, node.keys));
        }

        for (char c : node.children.keySet()) {
            collectResults(node.children.get(c), results);
        }
    }

    public static class SearchResult {
        private final int documentId;
        private final int frequency;

        private final JsonObject keys;


        public SearchResult(int documentId, int frequency, JsonObject keys) {
            this.documentId = documentId;
            this.frequency = frequency;
            this.keys = keys;
        }

        public int getDocumentId() {
            return documentId;
        }

        public int getFrequency() {
            return frequency;
        }

        public JsonObject getJsonObjectDetails(){ return this.keys; }
    }
}
