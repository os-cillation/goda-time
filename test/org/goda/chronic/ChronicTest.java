/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.goda.chronic;

import java.util.Date;
import junit.framework.TestCase;
import org.goda.time.MutableInterval;

/**
 *
 * @author kebernet
 */
public class ChronicTest extends TestCase {
    
    public ChronicTest(String testName) {
        super(testName);
    }

    /**
     * Test of parse method, of class Chronic.
     */
    public void testParse_String() {
        System.out.println("parse");
        Options o = new Options();
        o.setDebug(true);
        MutableInterval expResult = null;
        MutableInterval result = Chronic.parse("this tuesday 5:00", o);
        System.out.println(result);
        result = Chronic.parse("1979-05-27", o);
        System.out.println(result);

        result = Chronic.parse("Aug 23, 2010", o);
        System.out.println(result);

        result = Chronic.parse("1st monday in sept", o);
        System.out.println(result);

        result = Chronic.parse("september 4th", o);
        System.out.println(result);

        result = Chronic.parse("1st of september", o);
        System.out.println(result);

        result = Chronic.parse("three hundred twenty-one and a half days ago", o);
        System.out.println(result);

        o = new Options();
        o.setDebug(true);
        
         System.out.println("1.25 hr ago");
        result = Chronic.parse("1.25 hr ago", o);
        System.out.println(new Date(result.getStartMillis()));
        
        
        System.out.println("17:50");
        result = Chronic.parse("17:50", o);
        System.out.println(new Date(result.getStartMillis()));
        
        System.out.println("3 am");
        result = Chronic.parse("3 am", o);
        System.out.println(result.getStart());
        
       
        
        System.out.println("3a");
        result = Chronic.parse("3a", o);
        System.out.println(new Date(result.getStartMillis()));
        
        System.out.println("10021974");
        result = Chronic.parse("10021974", o);
        System.out.println(result.getStart());
        
        
        System.out.println("023");
        result = Chronic.parse("023", o);
        System.out.println(result.getStart());
        
        System.out.println("1630");
        result = Chronic.parse("1630", o);
        System.out.println(result.getStart());
        
        System.out.println("16:50 10/2/1974");
        result = Chronic.parse("16:50 10/2/1974 ", o);
        System.out.println(result.getStart());
        
        
        System.out.println("16:50 10021974");
        result = Chronic.parse("16:50 10021974 ", o);
        System.out.println(result.getStart());
        
        
        
        System.out.println("10:00am");
        result = Chronic.parse("10:00am");
        System.out.println(result.getStart());
        
        
        System.out.println("10:00a 9/23/2011");
        result = Chronic.parse("10:00a 9/23/2011");
        System.out.println(result.getStart());
    
        System.out.println("sept 11 2001");
        result = Chronic.parse("sept 11 2001");
        System.out.println(result.getStart());
        
        
        
        
        System.out.println("10:00am sept 11 2001");
        result = Chronic.parse("10:00am sept 11 2001");
        System.out.println(result.getStart());
    
    }
    
    
    
    
    
    public void testRegExTemp(){
        
        String s = "1650 10/2/1974";
        s = s.replaceAll("(^| )([01]\\d)(\\d\\d)(pm|am|p|a|\\W)","$1$2:$3$4");
        System.out.println(s);
        
        s = "4:50pm 10/2/74";
        System.out.println(s.replaceAll( "(\\d?\\d:\\d\\d[p|a]m)\\W(\\d?\\d/\\d?\\d/\\d\\d\\d?\\d?)", "$2 $1"));
    }

    /**
     * Test of parse method, of class Chronic.
     */
    public void xtestParse_String_Options() {
        System.out.println("parse");
        String text = "";
        Options options = null;
        MutableInterval expResult = null;
        MutableInterval result = Chronic.parse(text, options);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
