import java.io.*;
import java.util.*;
class MyTrieNode {
    Map<Character, MyTrieNode> children;
    boolean isWord;
    public MyTrieNode() {
        children = new HashMap<>();
        isWord = false;
    }
}
class HybridTrie {
    MyTrieNode root;
    public HybridTrie() {
        root = new MyTrieNode();
    }
    public void insert(String word) {
        MyTrieNode current = root;
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new MyTrieNode());
            current = current.children.get(c);
        }
        current.isWord = true;
    }
    public boolean search(String word) {
        MyTrieNode current = root;
        for (char c : word.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return false;
            }
            current = current.children.get(c);
        }
        return current.isWord;
    }
    public List<String> suggestWords(String word, int threshold) {
        List<String> suggestions = new ArrayList<>();
        suggestWordsHelper(root, word, new StringBuilder(), 0, threshold, suggestions);
        return suggestions;
    }
    private void suggestWordsHelper(MyTrieNode node, String word, StringBuilder currentWord, int index, int threshold, List<String> suggestions) {
        if (currentWord.length() > word.length() + threshold) {
            return;
        }
        if (node.isWord && Math.abs(currentWord.length() - word.length()) <= threshold) {
            int editDistance = calculateEditDistance(currentWord.toString(), word);
            if (editDistance <= threshold) {
                suggestions.add(currentWord.toString());
            }
        }
        if (index >= word.length()) {
            return;
        }
        char currentChar = word.charAt(index);
        for (Map.Entry<Character, MyTrieNode> entry : node.children.entrySet()) {
            char nextChar = entry.getKey();
            MyTrieNode nextNode = entry.getValue();
            StringBuilder newWord = new StringBuilder(currentWord);
            newWord.append(nextChar);
            int newThreshold = threshold;
            if (nextChar != currentChar) {
                newThreshold--;
            }
            suggestWordsHelper(nextNode, word, newWord, index + 1, newThreshold, suggestions);
        }
    }
    private int calculateEditDistance(String word1, String word2) {
        int l = word1.length();
        int m = word2.length();
        int[][] dp = new int[l + 1][m + 1];
        for (int a = 0; a <= l; a++) {
            for (int b = 0; b <= m; b++) {
                if (a == 0) {
                    dp[a][b] = b;
                } else if (b == 0) {
                    dp[a][b] = a;
                } else if (word1.charAt(a - 1) == word2.charAt(b - 1)) {
                    dp[a][b] = dp[a - 1][b - 1];
                } else {
                    dp[a][b] = 1 + Math.min(dp[a - 1][b], Math.min(dp[a][b - 1], dp[a - 1][b - 1]));
                }
            }
        }
        return dp[l][m];
    }
}
public class SpellCheck {
    private HybridTrie dictionary;
    public SpellCheck() {
        dictionary = new HybridTrie();
    }
    public void loadDictionary(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                for (String word : parts) {
                    dictionary.insert(word.toLowerCase().trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> getSuggestions(String word, int threshold) {
        List<String> s = new ArrayList<>();
        if(dictionary.search(word)){
            return s;
        }else{
            return dictionary.suggestWords(word.toLowerCase().trim(), threshold);
        }

    }
    public static void main(String[] args) {
        SpellCheck spellChecker = new SpellCheck();
        spellChecker.loadDictionary("dictionary.csv");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a word for spell check: ");
        String userInput = scanner.nextLine();
        int threshold = 2; // Adjust threshold as desired
        List<String> suggestions = spellChecker.getSuggestions(userInput, threshold);
        if (suggestions.isEmpty()) {
            System.out.println("No suggestions found.");
        } else {
            System.out.println("Did you mean:");
            for (String suggestion : suggestions) {
                System.out.println(suggestion);
            }
        }
        scanner.close();
    }
}
