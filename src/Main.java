import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import HotelPriceAnalysis.InvertedIndex;
import HotelPriceAnalysis.PageRank;
import Validation.Validation;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //User input for web crawling with Pattern check, word completion and spell checking
        //crawling and HTML parsing
        HotelsCrawler hc = new HotelsCrawler();

        Scanner s = new Scanner(System.in);
        LocalDate currentDate = LocalDate.now();

        // Validate and input City 
        String cityName;
        while (true) {
            System.out.print("Enter city name: ");
            cityName = s.nextLine().trim();
            if (!cityName.isEmpty() && cityName.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.println("Invalid city name. Please enter a valid name.");
        }
        
     // Validate and Input Province/State
        String provinceState;
        while (true) {
            System.out.print("Enter province/state: ");
            provinceState = s.nextLine().trim();
            if (!provinceState.isEmpty() && provinceState.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.println("Invalid province/state. Please enter a valid name.");
        }

        // Validate and Input Country
        String country;
        while (true) {
            System.out.print("Enter country: ");
            country = s.nextLine().trim();
            if (!country.isEmpty() && country.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.println("Invalid country. Please enter a valid name.");
        }
        
        // Validate and Input Check-in Date
        LocalDate checkInDate = Validation.getValidatedDate(s, "Enter start date (YYYY-MM-DD): ");
        String startDate = checkInDate.format(DateTimeFormatter.ISO_DATE);
        
        // Validate and Input Check-Out Date
        LocalDate checkOutDate = Validation.getCheckoutDate(s, "Enter end date (YYYY-MM-DD): ", checkInDate);
        String endDate = checkOutDate.format(DateTimeFormatter.ISO_DATE);      

        System.out.print("Enter number of rooms: ");
        int numberOfRooms = 0;
        while (numberOfRooms < 1) {
            System.out.print("Enter number of rooms: ");
            String roomInput = s.nextLine();

            // Check if the input is a positive integer (excluding zero)
            if (roomInput.matches("\\d+") && !roomInput.equals("0")) {
                numberOfRooms = Integer.parseInt(roomInput);
            } else {
                System.out.println("Number of rooms cannot be less than 1. Please enter a valid positive integer.");
            }
        } 
        
        int[] adults = new int[numberOfRooms];
        int totalAdults = 0;
        String roomAdultsQuery = "adults=";
        for (int i = 0; i < numberOfRooms; i++) {
            System.out.print("Adults for room number "+(i+1)+": ");
            int noOfAdults = s.nextInt();
            while (noOfAdults < 1) {
                System.out.print("Number of adults can not be less than 1. Enter again (Room "+(i+1)+"): ");
                noOfAdults = s.nextInt();
            }
            adults[i] = noOfAdults;
            totalAdults += noOfAdults;
            if (i == 0) {
                roomAdultsQuery += noOfAdults;
            } else {
                roomAdultsQuery += "%2C"+noOfAdults;
            }
        }

        HotelsCrawler.Parameters p = new HotelsCrawler.Parameters(cityName, provinceState, country, startDate, endDate, numberOfRooms, adults, totalAdults);
        String parameterJsonFile = "parameters.json";
        hc.writeParametersToJson(p, parameterJsonFile);

        String cityInJSON = cityName + ", " + provinceState + ", " + country;
        String filePath = "HotelsCA.json";
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String baseURL = "https://ca.hotels.com/Hotel-Search?"+roomAdultsQuery+"&destination="+cityName+"%2C"+provinceState+"%2C"+country+"&endDate="+endDate+"&sort=RECOMMENDED&startDate="+startDate;
        hc.crawl(baseURL, cityInJSON, filePath, "hotels");

        filePath = "ExpediaCA.json";
        file = new File(filePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        baseURL = "https://www.expedia.ca/Hotel-Search?"+roomAdultsQuery+"&destination="+cityName+"%2C"+provinceState+"%2C"+country+"&endDate="+endDate+"&sort=RECOMMENDED&startDate="+startDate;
        hc.crawl(baseURL, cityInJSON, filePath, "expedia");

        filePath = "BookingCA.json";
        file = new File(filePath);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("File created: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        baseURL = "https://www.booking.com/searchresults.html?ss="+cityName+"%2C"+provinceState+"%2C"+country+"&checkin="+startDate+"&checkout="+endDate+"&group_adults="+totalAdults+"&no_rooms="+numberOfRooms+"&group_children=0";
        hc.crawl(baseURL, cityInJSON, filePath, "booking");
        //Display the top search results based on suggestion criteria
        //Prompt the user to search by hotel name


        String[] files = {"D:\\ACC Project\\ACCProject\\HotelsCA.json","D:\\ACC Project\\ACCProject\\BookingCA.json","D:\\ACC Project\\ACCProject\\ExpediaCA.json"};
        try{

            InvertedIndex i = new InvertedIndex();
            PageRank pr = new PageRank();
            i.index(files);
            System.out.println("Enter a hotel name to search : ");
            Scanner sn = new Scanner(System.in);
            String searchElement = sn.nextLine();
            List<Integer> docIdsForPageRank = i.search(searchElement);
            //Check page rank if the hotel name is found in more than one document
            if(docIdsForPageRank.size() > 1){
                pr.getPageRank(searchElement, files);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        
        // Integrate search frequency
        try {
            Searchfrequency.updateSearches();
            Map<String, Integer> searchFrequency = Searchfrequency.indexMultipleFiles();
            System.out.println("Do you want to see the most searched destinations? (yes/no): ");
            String choice = s.next();
            if (choice.equalsIgnoreCase("yes")) {
                Searchfrequency.displaySearchFrequency(searchFrequency);
            } else {
                System.out.println("Thank you!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
