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
    List<JsonObject> keys;
    public TrieNode() {
        this.documentFrequency = new HashMap<>();
        this.children = new HashMap<>();
        this.keys = new ArrayList();
    }
}

public class InvertedIndex {
    public static TrieNode root = new TrieNode();
    public void index(String[] files) throws IOException {
//        String[] files = {"D:\\ACC Project\\ACCProject\\hotels.json","D:\\ACC Project\\ACCProject\\airbnb.json","D:\\ACC Project\\ACCProject\\booking.json"};
        String[] s ={"","",""};
        int c= 0;
        int lineNumber = 0;

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
//                        System.out.println(record.getAsJsonArray(x));
                        int c1 = 0;
                        for(JsonElement fd : record.getAsJsonArray(x)){

                            if(fd.isJsonObject()){
//                                List tempList = new ArrayList();
//                                tempList.add(x);
//                                tempList.add(c1);

                                JsonObject record2 = fd.getAsJsonObject();
//                                System.out.println(record2.get("name"));

                                temp =  record2.get("name").getAsString();
//                                System.out.println(temp);
                                String[] words = temp.split(" ");

                                for (String word : words) {
//                                    System.out.println(word);
                                    temp = word.toLowerCase();

                                    if (!word.isEmpty()) {
                                        insertIntoTrie(root, temp, lineNumber,record2);
//                                insertIntoTrie(root, temp, lineNumber);

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
//        String filePath = "D:\\ACC Project\\ACCProject\\src\\HotelPriceAnalysis\\file1.txt"; // Replace with your own file path
//        BufferedReader reader = new BufferedReader(new FileReader(filePath));
//        String line;

        // Root of the Trie


//        for (String st : s) {
//
//        }

        // Print the inverted index
//        printInvertedIndex(root, "");

//        reader.close();
    }

    public List<Integer> search(String searchTerm){
        List<SearchResult> searchResults = searchInvertedIndex(root, searchTerm);

        // Example: Printing search results
        System.out.println("Search results for \"" + searchTerm + "\":");
        List<Integer> docIds = new ArrayList<>();
        for (SearchResult result : searchResults) {
            System.out.println("Document ID: " + result.getDocumentId() + ", Frequency: " + result.getFrequency());
            System.out.println("Hotel Details:");
            docIds.add(result.getDocumentId() -1 );
            result.getJsonObjectDetails();
        }
        if(docIds.isEmpty()){
            System.out.println("No results found !!");
        }
        return docIds;
    }

    private void insertIntoTrie(TrieNode root, String word, int lineNumber, JsonObject e) {
//private static void insertIntoTrie(TrieNode root, String word, int lineNumber) {

        TrieNode current = root;

        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }

        current.documentFrequency.put(lineNumber, current.documentFrequency.getOrDefault(lineNumber, 0) + 1);
        current.keys.add(e);
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
//                System.out.println(node.keys);
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

    static class SearchResult {
        private final int documentId;
        private final int frequency;

        private final List<JsonObject> keys;


        public SearchResult(int documentId, int frequency, List keys) {
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

        public void getJsonObjectDetails(){
            for(JsonObject e : this.keys) {
                System.out.println(e.get("name").getAsString());
            }
        }
    }
}
