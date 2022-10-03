package com.example.coronaupdate1.utility;

public class StringNumber {

    public String bigNumberFormatting(String number){
        int count = 1;
        for (int i=number.length()-1; i>0; i--){
            //Log.d(TAG, "commaInsertion: " + number.charAt(i) + " count = " + count);
            if(count == 3){
                //Log.d(TAG, "commaInsertion: i = " + i);
                count = 0;
                number = new StringBuilder(number).insert(i, ",").toString();
            }
            count++;
        }
        return number;
    }
}
