package org.goda.chronic.numerizer;

import java.util.LinkedList;
import java.util.List;

public class Numerizer {
  protected static class DirectNum {
    private String _name;
    private String _number;
    
    public DirectNum(String name, String number) {
      _name = name;
      _number = number;
    }
    
    public String getName() {
      return _name;
    }
    
    public String getNumber() {
      return _number;
    }
  }
  
  protected static class Prefix {
    private String _name;
    private long _number;
    
    public Prefix(String name, long number) {
      _name = name;
      _number = number;
    }
    
    public String getName() {
      return _name;
    }
    
    public long getNumber() {
      return _number;
    }
  }

  protected static class TenPrefix extends Prefix {
    public TenPrefix(String name, long number) {
      super("(?:" + name + ")( *\\d(?=\\D|$))*", number);
    }
  }

  protected static class BigPrefix extends Prefix {
    public BigPrefix(String name, long number) {
      super("(\\d*) *" + name, number);
    }
  }
  
  protected static DirectNum[] DIRECT_NUMS;
  protected static TenPrefix[] TEN_PREFIXES;
  protected static BigPrefix[] BIG_PREFIXES;
   
  static {
    List<DirectNum> directNums = new LinkedList<DirectNum>();
    directNums.add(new DirectNum("eleven", "11"));
    directNums.add(new DirectNum("twelve", "12"));
    directNums.add(new DirectNum("thirteen", "13"));
    directNums.add(new DirectNum("fourteen", "14"));
    directNums.add(new DirectNum("fifteen", "15"));
    directNums.add(new DirectNum("sixteen", "16"));
    directNums.add(new DirectNum("seventeen", "17"));
    directNums.add(new DirectNum("eighteen", "18"));
    directNums.add(new DirectNum("nineteen", "19"));
    directNums.add(new DirectNum("ninteen", "19")); // Common mis-spelling
    directNums.add(new DirectNum("zero", "0"));
    directNums.add(new DirectNum("one", "1"));
    directNums.add(new DirectNum("two", "2"));
    directNums.add(new DirectNum("three", "3"));
    directNums.add(new DirectNum("four(\\W|$)", "4$1")); // The weird regex is so that it matches four but not fourty
    directNums.add(new DirectNum("five", "5"));
    directNums.add(new DirectNum("six(\\W|$)", "6$1"));
    directNums.add(new DirectNum("seven(\\W|$)", "7$1"));
    directNums.add(new DirectNum("eight(\\W|$)", "8$1"));
    directNums.add(new DirectNum("nine(\\W|$)", "9$1"));
    directNums.add(new DirectNum("ten", "10"));
    directNums.add(new DirectNum("\\ba\\b", "1"));
    Numerizer.DIRECT_NUMS = directNums.toArray(new DirectNum[directNums.size()]);
    
    List<TenPrefix> tenPrefixes = new LinkedList<TenPrefix>();
    tenPrefixes.add(new TenPrefix("twenty", 20));
    tenPrefixes.add(new TenPrefix("thirty", 30));
    tenPrefixes.add(new TenPrefix("fourty", 40));
    tenPrefixes.add(new TenPrefix("fifty", 50));
    tenPrefixes.add(new TenPrefix("sixty", 60));
    tenPrefixes.add(new TenPrefix("seventy", 70));
    tenPrefixes.add(new TenPrefix("eighty", 80));
    tenPrefixes.add(new TenPrefix("ninety", 90));
    tenPrefixes.add(new TenPrefix("ninty", 90)); // Common mis-spelling
    Numerizer.TEN_PREFIXES = tenPrefixes.toArray(new TenPrefix[tenPrefixes.size()]);
    
    List<BigPrefix> bigPrefixes = new LinkedList<BigPrefix>();
    bigPrefixes.add(new BigPrefix("hundred", 100L));
    bigPrefixes.add(new BigPrefix("thousand", 1000L));
    bigPrefixes.add(new BigPrefix("million", 1000000L));
    bigPrefixes.add(new BigPrefix("billion", 1000000000L));
    bigPrefixes.add(new BigPrefix("trillion", 1000000000000L));
    Numerizer.BIG_PREFIXES = bigPrefixes.toArray(new BigPrefix[bigPrefixes.size()]);
  }

  private static final String DEHYPHENATOR = " +|(\\D)-(\\D)";
  private static final String DEHALFER = "(a )?half";
  
  public static String numerize(String str) {
    String numerizedStr = str;
    
    // preprocess
    numerizedStr = numerizedStr.replaceAll(Numerizer.DEHYPHENATOR, "$1 $2"); // will mutilate hyphenated-words but shouldn't matter for date extraction
    numerizedStr = numerizedStr.replaceAll( Numerizer.DEHALFER, "0.5" );
    // easy/direct replacements
    for (DirectNum dn : Numerizer.DIRECT_NUMS) {
      numerizedStr = numerizedStr.replaceAll(dn.getName(), dn.getNumber());
    }
    
    // ten, twenty, etc.
    for (Prefix tp : Numerizer.TEN_PREFIXES) {
        numerizedStr = numerizedStr.replaceAll(tp.getName(), tp.getNumber()+"$1");
    }
    
    // hundreds, thousands, millions, etc.
    for (Prefix bp : Numerizer.BIG_PREFIXES) {
        numerizedStr = numerizedStr.replaceAll(bp.getName(),"$1 \\*"+ bp.getNumber());
    }
    numerizedStr = Numerizer.andition(numerizedStr);
    

    return numerizedStr;
  }

  public static String andition(String str) {
    //System.out.println("Andition "+ str);
    String[] toks = str.split(" ");
    for(int i= 0; i < toks.length; i++ ){
        if(toks[i].equalsIgnoreCase("and") ){
            try {
                double left = Double.parseDouble(toks[i-1]);
                double right = Double.parseDouble(toks[i+1]);
                double value = left + right;
                toks[i-1] = null;
                toks[i] = null;
                toks[i+1] = Double.toString(value);
            } catch(Exception e){
                e.printStackTrace();
            }
        } else if( toks[i].matches("[0-9\\.]*") && i+1 < toks.length && toks[i+1].startsWith("*") ){
            try {

                double left = Double.parseDouble(toks[i]);
                double right = Double.parseDouble(toks[i+1].substring(1));
                System.out.println( left +" * "+right );
                double value = left * right;
                toks[i] = null;
                toks[i+1] = Double.toString(value);
            } catch(Exception e){
                e.printStackTrace();
            }
        } else if ( toks[i].matches("[0-9\\.]*") ){
            try{
                double left = Double.parseDouble(toks[i]);
                double right = Double.parseDouble(toks[i+1]);
                double value = left + right;
                toks[i] = null;
                toks[i+1] = Double.toString(value);
            } catch(Exception e){
                //e.printStackTrace();
            }
        } 
    }
    StringBuffer sb = new StringBuffer();
    for(String s : toks ){
        if(s!= null ){
            sb.append(s);
            sb.append(" ");
        }
    }
    //System.out.println("Added "+sb.toString());
    return sb.toString();
  }

  
}
