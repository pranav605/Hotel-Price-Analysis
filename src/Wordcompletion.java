import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;
    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
    }
}
public class Wordcompletion {
    public static TrieNode root = new TrieNode();
    public static void insertIntoTrie(String word) {
        TrieNode current = root;

        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
    }
    public static List<String> getWordCompletions(String prefix) {
        List<String> completions = new ArrayList<>();
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            if (current.children.containsKey(c)) {
                current = current.children.get(c);
            } else {
                return completions;
            }
        }
        collectCompletions(current, prefix, completions);
        return completions;
    }
    private static void collectCompletions(TrieNode node, String prefix, List<String> completions) {
        if (node.isEndOfWord) {
            completions.add(prefix);
        }
        for (char c : node.children.keySet()) {
            collectCompletions(node.children.get(c), prefix + c, completions);
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
            String[] cityAdminCountry = line.split(","); // Assuming CSV file contains city, admin_name, country
            if (cityAdminCountry.length >= 3) {
                String city = cityAdminCountry[0].trim();
                String admin = cityAdminCountry[1].trim();
                String country = cityAdminCountry[2].trim();
                String fullLocation = city + ", " + admin + ", " + country;
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
