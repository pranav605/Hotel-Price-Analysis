import com.google.gson.*;
import java.io.*;
import java.util.*;

public class Searchfrequency {
        public static void updateSearches() throws IOException {
        // Read data from parameters.json
        String parametersFilePath = "parameters.json"; 
        JsonObject parameters;
        try (BufferedReader reader = new BufferedReader(new FileReader(parametersFilePath))) {
            parameters = JsonParser.parseReader(reader).getAsJsonObject();
        }
        // Extract city, state/country information and convert to lowercase
        String enteredCity = parameters.get("enteredCity").getAsString().toLowerCase();
        String enteredState = parameters.has("enteredState") ? parameters.get("enteredState").getAsString().toLowerCase() : "";
        String enteredCountry = parameters.has("enteredCountry") ? parameters.get("enteredCountry").getAsString().toLowerCase() : "";
        // Create or read existing data from the userSearches.json file
        String userSearchesFilePath = "userSearches.json"; // Replace with your JSON file containing user search history
        JsonArray jsonArray;
        if (new File(userSearchesFilePath).exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(userSearchesFilePath))) {
                jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            }
        } else {
            jsonArray = new JsonArray();
        }
        // Create a new search object with the extracted lowercase information
        JsonObject newSearch = new JsonObject();
        newSearch.addProperty("city", enteredCity);
        newSearch.addProperty("state", enteredState);
        newSearch.addProperty("country", enteredCountry);
        // Add the new search object to the JSON array
        jsonArray.add(newSearch);
        // Write the updated data back to the userSearches.json file
        try (FileWriter writer = new FileWriter(userSearchesFilePath)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(jsonArray));
        }
    }
    public static Map<String, Integer> indexMultipleFiles() throws IOException {
        String filePath = "userSearches.json"; // Replace with your JSON file containing user search history
        Map<String, Integer> searchFrequency = new HashMap<>();
        if (new File(filePath).exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                processJsonArray(jsonArray, searchFrequency);
            }
        }
        return searchFrequency;
    }
    public static void processJsonArray(JsonArray jsonArray, Map<String, Integer> searchFrequency) {
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();

            if (jsonObject.has("city") && jsonObject.has("state") && jsonObject.has("country")) {
                String searchTerm = jsonObject.get("city").getAsString() + ", " +
                        jsonObject.get("state").getAsString() + ", " +
                        jsonObject.get("country").getAsString();
                int frequency = searchFrequency.getOrDefault(searchTerm, 0);
                searchFrequency.put(searchTerm, frequency + 1);
            }
        }
    }
    public static void displaySearchFrequency(Map<String, Integer> searchFrequency) {
        System.out.println("***********************************");
        System.out.println("***********************************");
        System.out.println("Most Searched Destinations:");
        searchFrequency.entrySet().forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " times"));
        System.out.println("***********************************");
        System.out.println("***********************************");
    }
}
