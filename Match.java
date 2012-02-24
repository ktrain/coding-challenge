/* Entry for sortable.com coding challenge
 * by Kellen Steffen
 */

import java.util.Stack;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.Normalizer;

import org.json.*;

public class Match
{
  public static final String PRODUCT_NAME = "product_name";
  public static final String PRODUCT_MANUFACTURER = "manufacturer";
  public static final String PRODUCT_FAMILY = "family";
  public static final String PRODUCT_MODEL = "model";
  public static final String PRODUCT_DATE = "announced-date";

  public static final String LISTING_TITLE = "title";
  public static final String LISTING_MANUFACTURER = "manufacturer";
  public static final String LISTING_CURRENCY = "currency";
  public static final String LISTING_PRICE = "price";

  public static final String PRODUCT_FILE = "products.txt";
  public static final String LISTING_FILE = "listings.txt";
  public static final String RESULT_FILE = "results.txt";

  protected static HashMap<String,Stack<JSONObject>> resultsMap =
      new HashMap<String,Stack<JSONObject>>();
  protected static HashMap<String,Stack<JSONObject>> manufacturerMap =
      new HashMap<String,Stack<JSONObject>>();


  protected static void handleException( Exception e )
  {
    System.err.println( e );
    System.err.println( e.getMessage() );
    System.err.println( e.getStackTrace() );
    System.exit( 1 );
  }


  protected static String normalizeString( String s )
  {
    // replace non-english characters?
    /*String t = Normalizer.normalize( s, Normalizer.Form.NFD );
    // put the string to lowercase and remove excess whitespace
    t = t.toLowerCase().trim();
    // remove non-alphanumeric characters?
    //t = t.replaceAll( "[^a-z0-9\":{}, ]", "" );
    return t;*/
    return s.toLowerCase().trim();
  }


  /*
   * processProductFile()
   * Reads strings from r, expecting one JSON object per line.
   * Normalizes all strings and converts the strings to JSONObjects.
   * Fills the manufacturerMap, which maps a manufacturer name to
   * the list of products that the manufacturer produces.
   * Also initializes the resultsMap with each product name mapping to
   * an empty list.
   */
  protected static void processProductFile( FileReader r )
  {
    try {
      // create a JSONTokener to read r
      JSONTokener tokener = new JSONTokener( r );
      while ( tokener.more() ) {
        // get the next line
        String str = tokener.nextTo( '\n' );
        // discard the newline
        tokener.next();
        // normalize the string
        String con = normalizeString( str );

        // create JSONObjects from both the normalized and original strings
        JSONObject product = new JSONObject( con );
        // set the PRODUCT_NAME of the normalized object to the original name,
        // because we want the original, non-normalized name in the output and
        // it's not needed for processing
        String name = new JSONObject( str ).getString( PRODUCT_NAME );
        product.put( PRODUCT_NAME, name );
        resultsMap.put( name, new Stack<JSONObject>() );

        // put the product on the appropriate manufacturer stack
        String manufacturer = product.getString( PRODUCT_MANUFACTURER );
        if ( !manufacturerMap.containsKey( manufacturer ) ) {
          // first time encountering this manufacturer;
          // create a new stack containing the product
          // and map the manufacturer to it
          Stack<JSONObject> s = new Stack<JSONObject>();
          s.push( product );
          manufacturerMap.put( manufacturer, s );
        } else {
          // put the product in the manufacturer's map
          manufacturerMap.get( manufacturer ).push( product );
        }
      }
    } catch ( Exception e ) {
      handleException( e );
    }
  }


  /*
   * 
   */
  protected static boolean familyAndModelMatch( JSONObject product,
      String title )
  throws JSONException
  {
    boolean result = false;
    String model = product.getString( PRODUCT_MODEL );

    // check whether the product has a family
    if ( product.has( PRODUCT_FAMILY ) ) {
      String family = product.getString( PRODUCT_FAMILY );
      // since there is a family, the model must match with word boundaries
      String pattern = ".*\\b" + model + "\\b.*";
      if ( title.contains( family ) && title.matches( pattern ) ) {
        result = true;
      }
    // no family, so do a simple contains match
    } else if ( title.contains( model ) ) {
      result = true;
    }

    return result;
  }


  /*
   * processListingFile()
   * Reads strings from r, expecting one JSON object per line.
   * 
   * 
   */
  protected static void processListingFile( FileReader r )
  throws JSONException
  {
    // create a JSONTokener to read r
    JSONTokener tokener = new JSONTokener( r );
    while ( tokener.more() ) {
      // get the next line
      String str = tokener.nextTo( '\n' );
      // discard the newline
      tokener.next();

      // create a JSON object with the original string for result output
      JSONObject originalListing = new JSONObject( str );
      // normalize the string and create a JSON object
      String con = normalizeString( str );
      JSONObject listing = new JSONObject( con );

      // match manufacturers
      String title = listing.getString( LISTING_TITLE );
      String listingManufacturer = listing.getString( LISTING_MANUFACTURER );
      // loop through all manufacturers
      Iterator manufacturerIterator = manufacturerMap.keySet().iterator();
      while ( manufacturerIterator.hasNext() ) {
        String manufacturer = (String) manufacturerIterator.next();
        // check for a manufacturer match
        if ( listingManufacturer.contains( manufacturer ) ) {
          // manufacturer matches, so check family/model
          Stack<JSONObject> s = (Stack<JSONObject>)
              manufacturerMap.get( manufacturer );
          // loop through all products by that manufacturer
          Iterator productIterator = s.iterator();
          while ( productIterator.hasNext() ) {
            JSONObject product = (JSONObject) productIterator.next();
            if ( familyAndModelMatch( product, title ) ) {
              // it's a match!
              // put the (original) listing on the product's results list
              String name = product.getString( PRODUCT_NAME );
              resultsMap.get( name ).push( originalListing );
              // assume that a listing can only match one product;
              // we had a match, so stop looping through products
              break;
            }
          }
        }
      }
    }
  }


  /*
   * main()
   * reads and cross-matches the product and listing files,
   * writing the results to disk
   */
  public static void main( String[] args )
  {
    FileReader productReader;
    FileReader listingReader;
    FileWriter resultWriter;
    JSONWriter jsonWriter;

    try {
      // open up the product, listing, and result files
      productReader = new FileReader( PRODUCT_FILE );
      listingReader = new FileReader( LISTING_FILE );
      resultWriter = new FileWriter( RESULT_FILE );
      jsonWriter = new JSONWriter( resultWriter );

      // read the product file
      processProductFile( productReader );
      productReader.close();

      // read the listing file
      HashMap<String,Stack<JSONObject>> listingManufacturerMap =
          new HashMap<String,Stack<JSONObject>>();
      processListingFile( listingReader );
      listingReader.close();

      // compare products and listings
      
      // write the results to disk
      Iterator i = resultsMap.keySet().iterator();
      while ( i.hasNext() ) {
        String name = (String) i.next();
        Stack<JSONObject> listings = resultsMap.get( name );
        resultWriter.write( "{\"product_name\":\"" + name + "\",\"listings\":"
            + listings + "}\n");
      }

      System.out.println( "Results written to results.txt." );
      jsonWriter = null;
      resultWriter.close();

    } catch ( Exception e ) {
      handleException( e );
    }
  }
}

