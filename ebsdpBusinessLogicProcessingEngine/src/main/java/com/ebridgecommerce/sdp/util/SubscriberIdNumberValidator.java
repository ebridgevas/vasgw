package com.ebridgecommerce.sdp.util;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: David
 * Date: 6/25/12
 * Time: 11:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class SubscriberIdNumberValidator {

    public static String editIdNumber(String idNumber){
        if (idNumber == null || idNumber.trim().length() == 0) {
            return idNumber;
        }

        StringBuffer result = new StringBuffer();
        for ( Character c : idNumber.trim().toCharArray()) {
            if (Character.isLetter(c) || Character.isDigit(c) ) {
                result.append(c);
            }
        }
        return result.toString();
    }
    public static boolean isValid(String id) throws BlankNameException, InvalidNameException {
        if (id == null || id.trim().length() == 0) {
            throw new BlankNameException("Empty name");
        }

        if (id.length() < 10 || id.length() > 12) {
            throw new InvalidNameException("Invalid Id");
        }

        if (! DISTRICTS.contains(id.substring(0,2))) {
            throw new InvalidNameException("Invalid registration district");
        }
        // regDist 5,6,7 checkLetter origDist
        // 08 2026803 S 75

        if (! DISTRICTS.contains(id.substring(id.length() - 2, id.length()))) {
            throw new InvalidNameException("Invalid origin district");
        }

        if (!Character.isLetter(id.charAt(id.length() - 3))) {
            throw new InvalidNameException("No check letter found");
        }

        return true;
    }

    private static final List<String> DISTRICTS;

    static {
        DISTRICTS = Arrays.asList(new String[] {
                "02",
                "03",
                "04",
                "05",
                "06",
                "07",
                "08",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "18",
                "19",
                "21",
                "22",
                "23",
                "24",
                "25",
                "26",
                "27",
                "28",
                "29",
                "32",
                "34",
                "35",
                "37",
                "38",
                "39",
                "41",
                "42",
                "43",
                "44",
                "45",
                "46",
                "47",
                "48",
                "49",
                "50",
                "53",
                "54",
                "56",
                "58",
                "59",
                "61",
                "63",
                "66",
                "67",
                "68",
                "69",
                "70",
                "71",
                "73",
                "75",
                "77",
                "79",
                "80",
                "81",
                "83",
                "84",
                "85",
                "86",
                "86",
                "00" });
    }

    public static void main(String[] args) {
        try {
            System.out.println("##### " + isValid(editIdNumber("63-788429-6-50")));
        } catch (BlankNameException e) {
            System.out.println("##### " + e.getMessage());
        } catch (InvalidNameException e) {
            System.out.println("##### " + e.getMessage());
        }
    }
}
