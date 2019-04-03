package org.linagora.intentDetection.common;

import java.util.Date;
import org.joda.time.DateTime;

import org.apache.commons.lang3.time.DateUtils;

public class DateTimeUtil {
	
	public static DateTime parseDate(String strDate) {

        Date date = null;
        String[] possibleDateFormats =
              {

            	      "yyyy-MM",
            		  "yyyy-MM-dd'T'HH:mm",
                      "yyyyMMdd",
                      "dd/MM/yy",
                      "dd/MM/yyyy",
                      "MM/yyyy",
                      "yyyy",
                      "yyyy-MM-dd",
                      "yyyy/MM/dd"
                    
              };

        try {

            date = DateUtils.parseDate(strDate, possibleDateFormats);
            //System.out.println("inputDate ==> " + strDate + ", outputDate ==> " + date);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return new DateTime(date.getTime());

    }
	
	public static void main(String [] args) {
		System.out.println(parseDate("mon"));
	}

}
