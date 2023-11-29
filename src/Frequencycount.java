import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FrequencyCount {

    public static class HotelData {
        String name, location, reviewMessage;
        List<Room> rooms;
        List<String> amenities;
    }

    public static class Room {
        String roomDesc;
        List<String> roomFacilities;
    }

    public static void main(String[] args) {
        // Array of file names
        String[] fileArr = {"src/data/BookingCA.json", "src/data/HotelsCA.json", "src/data/ExpediaCA.json"};

        Gson gsonObj = new Gson();
        Type type = new TypeToken<Map<String, List<HotelData>>>(){}.getType();

        // Process each file
        for (String fileName : fileArr) {
            // TreeMap to store the frequency count of words for the current file
            TreeMap<String, Integer> wordCountTM = new TreeMap<>();

            try (FileReader fReader = new FileReader(fileName)) {
                // Load the JSON data from file
                Map<String, List<HotelData>> hotelsData = gsonObj.fromJson(fReader, type);

                // Process each hotel data
                for (List<HotelData> hotels : hotelsData.values()) {
                    for (HotelData hotel : hotels) {
                        processText(hotel.name, wordCountTM);
                        processText(hotel.location, wordCountTM);
                        processText(hotel.reviewMessage, wordCountTM);

                        if (hotel.rooms != null) {
                            for (Room room : hotel.rooms) {
                                processText(room.roomDesc, wordCountTM);
                                if (room.roomFacilities != null) {
                                    for (String facility : room.roomFacilities) {
                                        processText(facility, wordCountTM);
                                    }
                                }
                            }
                        }

                        if (hotel.amenities != null) {
                            for (String amenity : hotel.amenities) {
                                processText(amenity, wordCountTM);
                            }
                        }
                    }
                }

                // Print the word frequencies for the current file
                System.out.println("Word frequencies for " + fileName + ":");
                wordCountTM.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

            } catch (Exception e) {
                System.out.println("Error processing file: " + fileName);
                e.printStackTrace();
            }
        }
    }

    // Helper method to process text and update word frequency
    private static void processText(String text, TreeMap<String, Integer> wordCountTM) {
        if (text != null) {
            String[] words = text.toLowerCase().split("\\W+"); // Splitting text into words
            for (String word : words) {
                if (!word.isEmpty() && !word.matches("\\d+")) { // Exclude numbers
                    wordCountTM.put(word, wordCountTM.getOrDefault(word, 0) + 1);
                }
            }
        }
    }
}
