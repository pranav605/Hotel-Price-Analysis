import HotelPriceAnalysis.InvertedIndex;
import HotelPriceAnalysis.PageRank;

import java.util.List;
import java.util.Scanner;



public class Main {
    public static void main(String[] args) {
        //User input for web crawling with Pattern check, word completion and spell checking
        //crawling and HTML parsing
        //Display the top search results based on suggestion criteria
        //Prompt the user to search by hotel name

        System.out.println("Hello world!");
        String[] files = {"D:\\ACC Project\\ACCProject\\hotels.json","D:\\ACC Project\\ACCProject\\airbnb.json","D:\\ACC Project\\ACCProject\\booking.json"};
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
                pr.getPageRank(searchElement);
            }
        }catch (Exception e){

        }

    }
}