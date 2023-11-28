import com.google.gson.*;
import java.io.*;
import java.util.*;

public class Searchfrequency {

    public static void main(String[] args) {
        try {
            Map<String, Integer> destinationFrequency = indexMultipleFiles();
            displayMostSearchedDestinations(destinationFrequency);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, Integer> indexMultipleFiles() throws IOException {
        String[] files = {"HotelsExpediaBooking.json"};

        Map<String, Integer> destinationFrequency = new HashMap<>();

        for (String filePath : files) {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            processJsonArray(jsonArray, destinationFrequency);
            reader.close();
        }

        return destinationFrequency;
    }

    public static void processJsonArray(JsonArray jsonArray, Map<String, Integer> destinationFrequency) {
        for (JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String location = entry.getKey();
                JsonArray hotelsArray = entry.getValue().getAsJsonArray();

                for (JsonElement hotelElement : hotelsArray) {
                    JsonObject hotelObject = hotelElement.getAsJsonObject();
                    if (hotelObject.has("location")) {
                        String hotelLocation = hotelObject.get("location").getAsString();
                        int frequency = destinationFrequency.getOrDefault(hotelLocation, 0);
                        destinationFrequency.put(hotelLocation, frequency + 1);
                    }
                }
            }
        }
    }

    public static void displayMostSearchedDestinations(Map<String, Integer> destinationFrequency) {
        System.out.println("***********************************");
        System.out.println("***********************************");
        System.out.println("Most Searched Destinations this year:");

        destinationFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5) // Change this number to display more top destinations
                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + " time"));

        System.out.println("***********************************");
        System.out.println("***********************************");
    }
}
