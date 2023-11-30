import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

public class DataExtractor {

	// Below function extracts mobile numbers from the given text.
    public static List<String> mobileNumsExtractor(String messageText) {
        List<String> phoneNumbers = new ArrayList<>();
     // Regex pattern for matching phone numbers
        Pattern patternObjP = Pattern.compile("\\(?(\\d{3})\\)?[-\\s]?(\\d{3})[-\\s]?(\\d{4})");
        Matcher matcherObjV = patternObjP.matcher(messageText);

        while (matcherObjV.find()) {
            phoneNumbers.add(matcherObjV.group());
        }
        return phoneNumbers;
    }
    
    // Below function extracts email addresses from the given text.
    public static List<String> emailExtractor (String text) {
        List<String> emails = new ArrayList<>();
      //Regex pattern for matching emails
        Pattern patternObjP = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcherObjV = patternObjP.matcher(text);

        while (matcherObjV.find()) {
            emails.add(matcherObjV.group());
        }

        return emails;
    }
    
    // Below function extracts prices from the given text.
    public static List<String> extractPrices(String text) {
        List<String> prices = new ArrayList<>();
        // Regex pattern for matching prices
        Pattern patternObjP = Pattern.compile("\\p{Sc}\\d+(\\.\\d{1,2})?");
        Matcher matcherObjV = patternObjP.matcher(text);

        while (matcherObjV.find()) {
            prices.add(matcherObjV.group());
        }

        return prices;
    }

    public static void main(String[] args) {
    	
    	 // Example for extracting mobile numbers
        String mobileSampleMessage = "Contact us at (413) 955-7890 or 226-961-4567 for more information.";
        List<String> phoneNumbers = mobileNumsExtractor(mobileSampleMessage);
        for (String number : phoneNumbers) {
            System.out.println(number);
        }
        
        // Example for extracting emails
        String emailSamplesMessage = "For inquiries, please contact korat1@uwindsor.ca or Vaibhav1@work.net.";
        List<String> emails = emailExtractor(emailSamplesMessage);
        for (String email : emails) {
            System.out.println(email);
        }
        
     // Example for extracting prices
        String priceSampleText = "The total cost is $15.99, whereas the discounted price is £12.50.";
        List<String> prices = extractPrices(priceSampleText);
        for (String price : prices) {
            System.out.println(price);
        }
    }
}
