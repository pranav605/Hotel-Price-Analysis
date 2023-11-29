import HotelPriceAnalysis.InvertedIndex;
import HotelPriceAnalysis.PageRank;

import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        //User input for web crawling with Pattern check, word completion and spell checking
        //crawling and HTML parsing
        HotelsCrawler hc = new HotelsCrawler();

        Scanner s = new Scanner(System.in);
        LocalDate currentDate = LocalDate.now();

        System.out.print("Enter city: ");
        String cityName = s.nextLine();
        System.out.print("Enter province/state: ");
        String provinceState = s.nextLine();
        System.out.print("Enter country: ");
        String country = s.nextLine();
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = s.next();
        LocalDate startEnteredDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        while (startEnteredDate.isBefore(currentDate)) {
            System.out.print("Entered Date is in the past. Enter again : ");
            startDate = s.next();
            startEnteredDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        }

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = s.next();
        LocalDate endEnteredDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        while (endEnteredDate.isBefore(startEnteredDate)) {
            System.out.print("Entered Date is before start date. Enter again : ");
            endDate = s.next();
            endEnteredDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        }

        System.out.print("Enter number of rooms: ");
        int numberOfRooms = s.nextInt();
        while (numberOfRooms < 1) {
            System.out.print("Number of rooms can not be less than 1. Enter again: ");
            numberOfRooms = s.nextInt();
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
