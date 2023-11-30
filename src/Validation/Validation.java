package Validation;

import java.util.regex.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Validation {

	public static boolean isValidURL(String url) {
	    String regex = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
	    Pattern p = Pattern.compile(regex);
	    if (url == null) {
	        return false;
	    }
	    Matcher m = p.matcher(url);
	    return m.matches();
	}
	
public static boolean isAccessibleURL(String urlStr) {
	    try {
	        URL url = new URL(urlStr);
	        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
	        httpURLConnection.setRequestMethod("HEAD");
	        httpURLConnection.setConnectTimeout(5000); // Set timeout as per your need
	        httpURLConnection.setReadTimeout(5000); // Set timeout as per your need

	        int responseCode = httpURLConnection.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            return true;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}


public static final String DATE_PATTERN = "\\d{4}-\\d{1,2}-\\d{1,2}"; // Regex for YYYY-MM-DD

public static LocalDate parseDate(String dateStr) throws Exception {
    return LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
}

public static boolean isValidDateFormat(String dateStr) {
    return dateStr.matches(DATE_PATTERN);
}

public static LocalDate getValidatedDate(Scanner scannerObj, String message) {
    LocalDate currentDate = LocalDate.now();
    LocalDate checkInDate = null;

    while (checkInDate == null || checkInDate.isBefore(currentDate)) {
        System.out.print(message);
        String dateInput = scannerObj.nextLine();

        if (isValidDateFormat(dateInput)) {
            try {
            	checkInDate = parseDate(dateInput);
                if (checkInDate.isBefore(currentDate)) {
                    System.out.println("Entered Date is in the past. Please enter again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid date. Please enter a valid date in the format YYYY-MM-DD.");
            }
        } else {
            System.out.println("Invalid format. Please enter a date in the format YYYY-MM-DD.");
        }
    }

    return checkInDate;
}

public static LocalDate getCheckoutDate(Scanner scanner, String prompt, LocalDate checkInDate) {
    LocalDate checkOutDate = null;
    do {
    	checkOutDate = getValidatedDate(scanner, prompt);
        if (checkOutDate.isBefore(checkInDate)) {
            System.out.println("Entered Date is before the start date. Please enter a valid check out date.");
        }
    } while (checkOutDate.isBefore(checkInDate));

    return checkOutDate;
}

public static int extractNumber(String str) {
	   String numberStr = str.replaceAll("[^\\d]", "");
       return numberStr.isEmpty() ? 0 : Integer.parseInt(numberStr);
}
}
