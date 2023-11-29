import com.google.gson.*;
import java.io.*;
import java.util.*;

public class Searchfrequency {

    public static void main(String[] args) {
        try {
            updateSearches();
            Map<String, Integer> searchFrequency = indexMultipleFiles();
            displaySearchFrequency(searchFrequency);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateSearches() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a destination:");
        String destination = scanner.nextLine();

        // Read existing data from the JSON file
        String filePath = "userSearches.json"; // Replace with your JSON file containing user search history
        JsonArray jsonArray;
        if (new File(filePath).exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            reader.close();
        } else {
            jsonArray = new JsonArray();
        }

        // Append the new destination to the JSON array
        JsonObject newSearch = new JsonObject();
        newSearch.addProperty("search_term", destination);
        jsonArray.add(newSearch);

        // Write the updated data back to the JSON file
        FileWriter writer = new FileWriter(filePath);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer.write(gson.toJson(jsonArray));
        writer.close();
    }

    public static Map<String, Integer> indexMultipleFiles() throws IOException {
        String filePath = "userSearches.json"; // Replace with your JSON file containing user search history

        Map<String, Integer> searchFrequency = new HashMap<>();

        if (new File(filePath).exists()) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            processJsonArray(jsonArray, searchFrequency);
            reader.close();
        }

        return searchFrequency;
    }

    public static void processJsonArray(JsonArray jsonArray, Map<String, Integer> searchFrequency) {
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();

            if (jsonObject.has("search_term")) {
                String searchTerm = jsonObject.get("search_term").getAsString();
                int frequency = searchFrequency.getOrDefault(searchTerm, 0);
                searchFrequency.put(searchTerm, frequency + 1);
            }
        }
    }

    public static void displaySearchFrequency(Map<String, Integer> searchFrequency) {
        System.out.println("***********************************");
        System.out.println("Search Frequency:");

        searchFrequency.entrySet().forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " times"));

        System.out.println("***********************************");
    }
}
