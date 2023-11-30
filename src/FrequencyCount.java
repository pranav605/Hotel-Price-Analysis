import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FrequencyCount {

    // Inner class to handle the structure of Hotel data.
    public static class HotelData {
        String name, location, reviewMessage;
        List<Room> rooms;
        List<String> amenities;
    }

    // Inner class to handle the structure of Room data within each Hotel.
    public static class Room {
        String roomDesc;
        List<String> roomFacilities;
    }

    // Below function returns an array which shows frequency count for a search word in various files
    public static int[] searchSpecificWordFrequencies(String searchWord) {
        String[] fileArr = {"src/data/BookingCA.json", "src/data/HotelsCA.json", "src/data/ExpediaCA.json"};
        int[] wordFrequencyArrayHotelData = new int[fileArr.length];
        Gson gsonObj = new Gson();
        // Updated TypeToken for JSON deserialization with Google's Gson library
        Type typeObjGson = new TypeToken<List<Map<String, List<HotelData>>>>(){}.getType();

        int fileIndex = 0;
        for (String hotelDataFileName : fileArr) {
            TreeMap<String, Integer> wordCountTM = new TreeMap<>();

            try (FileReader fReader = new FileReader(hotelDataFileName)) {
                // Deserialize JSON to the new structure
                List<Map<String, List<HotelData>>> listOfHotelDataMaps = gsonObj.fromJson(fReader, typeObjGson);

                // Iterate through the list and then through each map
                for (Map<String, List<HotelData>> hotelsData : listOfHotelDataMaps) {
                    processHotelData(hotelsData, wordCountTM);
                }

                wordFrequencyArrayHotelData[fileIndex++] = wordCountTM.getOrDefault(searchWord, 0);
            } catch (Exception e) {
                System.out.println("Error processing file: " + hotelDataFileName);
            }
        }
        return wordFrequencyArrayHotelData;
    }

    private static void processHotelData(Map<String, List<HotelData>> hotelsData, TreeMap<String, Integer> wordCountTM) {
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
    }

    private static void processText(String text, TreeMap<String, Integer> wordCountTM) {
        if (text != null) {
            // Split the text into words using non-word characters as delimiters
            String[] words = text.toLowerCase().split("\\W+");
            for (String word : words) {
                // Check if the word is non-empty and not just a number
                if (!word.isEmpty() && !word.matches("\\d+")) {
                    // Increment the word count in the TreeMap
                    wordCountTM.put(word, wordCountTM.getOrDefault(word, 0) + 1);
                }
            }
        }
    }

    public static void main(String[] args) {
        String searchWord = "breakfast"; // This is an example keyword
        int[] frequencies = FrequencyCount.searchSpecificWordFrequencies(searchWord);
        System.out.println("Frequencies of '" + searchWord + "': " + Arrays.toString(frequencies));
    }
}