import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
            processFile("dictionary.csv"); // Process the dictionary file
            System.out.println("------Word Completion------");
            // Taking user input for prefix
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a prefix: ");
            String prefix = scanner.nextLine().toLowerCase(); // User inputs a prefix

            // Getting word completions for the input prefix
            List<String> suggestions = getWordCompletions(prefix);

            prioritizeCountries(suggestions);

            if (!suggestions.isEmpty()) {
                System.out.println("Related Words:");
                int count = 0;
                for (String suggestion : suggestions) {
                    if (count >= 7) {
                        break;
                    }
                    System.out.println(suggestion);
                    count++;
                }
            } else {
                System.out.println("No suggestions available.");
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void processFile(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] cityCountry = line.split(","); // Assuming CSV file contains city, country
            if (cityCountry.length >= 2) {
                String city = cityCountry[0].trim();
                String country = cityCountry[1].trim();
                String fullLocation = city + ", " + country;
                insertIntoTrie(fullLocation.toLowerCase()); // Convert words to lowercase before inserting
            }
        }
        reader.close();
    }

    public static void prioritizeCountries(List<String> suggestions) {
        List<String> canadaCities = new ArrayList<>();
        List<String> ukCities = new ArrayList<>();
        List<String> usaCities = new ArrayList<>();
        List<String> otherCities = new ArrayList<>();

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().contains(", canada")) {
                canadaCities.add(suggestion);
            } else if (suggestion.toLowerCase().contains(", united kingdom")) {
                ukCities.add(suggestion);
            } else if (suggestion.toLowerCase().contains(", usa")) {
                usaCities.add(suggestion);
            } else {
                otherCities.add(suggestion);
            }
        }

        suggestions.clear();
        suggestions.addAll(canadaCities);
        suggestions.addAll(ukCities);
        suggestions.addAll(usaCities);
        suggestions.addAll(otherCities);
    }
}
