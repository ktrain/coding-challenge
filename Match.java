/* Entry for sortable.com coding challenge
 * by Kellen Steffen
 */

import java.util.Stack;
import java.util.Iterator;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.*;

public class Match
{
  public static void main( String[] args )
  {
    String learnFilename = "products.txt";
    String matchFilename = "listings.txt";
    String resultFilename = "results.txt";
    FileReader learnReader;
    FileWriter resultWriter;
    JSONTokener toke;
    Stack<JSONObject> s = new Stack<JSONObject>();

    try {
      learnReader = new FileReader( learnFilename );
      resultWriter = new FileWriter( resultFilename );
      toke = new JSONTokener( learnReader );
      while ( toke.more() ) {
        //s.push( new JSONObject( toke ) );
        resultWriter.write( new JSONObject( toke ).toString() );
      }
    } catch ( Exception e ) {
      System.err.println( e );
      /*Iterator i = s.iterator();
      while ( i.hasNext() ) {
        System.err.println( i.next() );
      }*/
      System.exit( 1 );
    }

    try {
      learnReader.close();
      resultWriter.flush();
      resultWriter.close();
    } catch ( Exception e ) {
    }

  }

}
