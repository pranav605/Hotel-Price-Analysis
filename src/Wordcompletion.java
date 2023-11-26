import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class TrieNode {
    TrieNode[] children;
    boolean isEndOfWord;

    public TrieNode() {
        this.children = new TrieNode[256]; // Support for ASCII characters
        this.isEndOfWord = false;
    }
}

public class Wordcompletion {
    public static TrieNode root = new TrieNode();

    public static void insertIntoTrie(String word) {
        TrieNode current = root;

        for (char c : word.toCharArray()) {
            int index = c;
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }

    public static List<String> getWordCompletions(String prefix) {
        List<String> completions = new ArrayList<>();
        TrieNode current = root;

        for (char c : prefix.toCharArray()) {
            int index = c;
            if (current.children[index] == null) {
                return completions; // Prefix not found in the trie
            }
            current = current.children[index];
        }

        collectCompletions(current, prefix, completions);
        return completions;
    }

    private static void collectCompletions(TrieNode node, String prefix, List<String> completions) {
        if (node.isEndOfWord) {
            completions.add(prefix);
        }

        for (int i = 0; i < 256; i++) {
            if (node.children[i] != null) {
                collectCompletions(node.children[i], prefix + (char) i, completions);
            }
        }
    }
    public static void main(String[] args) {
        try {
            String[] fileNames = {"HotelsExpediaBooking.json"}; // Add all your file names

            for (String fileName : fileNames) {
                processFile(fileName);
            }
            System.out.println("------Word Completion------");
            // Taking user input for prefix
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a prefix: ");
            String prefix = scanner.nextLine().toLowerCase(); // User inputs a prefix
            scanner.close();

            // Getting word completions for the input prefix
            List<String> suggestions = getWordCompletions(prefix);

            if (!suggestions.isEmpty()) {
                System.out.print("Related Words: {" + String.join(",", suggestions) + "}");
            } else {
                System.out.println("No suggestions available.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public static void processFile(String fileName) throws IOException {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(new FileReader(fileName)).getAsJsonArray().get(0).getAsJsonObject();

            for (JsonElement element : jsonObject.entrySet().iterator().next().getValue().getAsJsonArray()) {
                JsonObject hotel = element.getAsJsonObject();
                String name = hotel.get("name").getAsString().toLowerCase(); // Extracting hotel names
                String[] words = name.split("[^a-zA-Z]"); // Splitting based on non-alphabetic characters

                for (String word : words) {
                    if (!word.isEmpty()) {
                        insertIntoTrie(word);
                    }
                }
            }
        }
}

