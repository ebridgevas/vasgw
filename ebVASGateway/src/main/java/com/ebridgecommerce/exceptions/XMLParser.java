package com.ebridgecommerce.exceptions;

/**
*
* @author DaTekeshe
*/
import java.util.StringTokenizer;

public class XMLParser {

   public static String getError(String raw) {
       StringTokenizer st = new StringTokenizer(raw, ";");
       int i = 0;
       while (st.hasMoreElements()) {
           raw = st.nextToken();
           if ((++i) == 7) {
               raw = raw.replaceAll("&lt", "");
               return raw;
           }
       }
       return raw;
   }
   // &lt;ErrorCode&gt;5000&lt;/ErrorCode&gt;&lt;ErrorDescription&gt;Voucher Not Found&lt;/ErrorDescription&gt;
   //

   public static String getErrorCode(String raw) {
       StringTokenizer st = new StringTokenizer(raw, ";");
       int i = 0;
       while (st.hasMoreElements()) {
           raw = st.nextToken();
           if ((++i) == 3) {
               raw = raw.replaceAll("&lt", "");
               return raw;
           }
       }
       return raw;
   }
}
