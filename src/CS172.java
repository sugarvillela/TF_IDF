package cs172;

import java.util.Scanner;
/**
 * @author David Swanson
 */
public class CS172 {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner keyIn = new Scanner (System.in);
        IndexCreator I=new IndexCreator();
        InvertedIndex index=I.getObj_InvertedIndex();
        
        while( true ){
            System.out.println("Enter a word to search or Q/q to quit");
            String userin = keyIn.nextLine().trim().toLowerCase();
            if( userin.isEmpty() ){
                continue;
            }
            if( userin.equals("q") ){
                break;
            }
            index.search( userin );
            index.dispSearchResult();
        }
    }
    static void testInvertedIndex(){
        InvertedIndex index=new InvertedIndex();
        index.inc("Hello", "02");
        index.inc("Hello", "02");
        index.inc("Hello", "02");
        index.inc("Hello", "02");
        index.inc("Hello", "14");
        index.inc("Hello", "06");
        index.inc("Hello", "14");
        index.inc("Hello", "06");
        index.dispPretty();
        System.out.println("Hello 02="+ index.getCount("Hello", "02"));
        System.out.println("Hello 14="+ index.getCount("Hello", "14"));
        System.out.println("Hello 06="+ index.getCount("Hello", "06"));
        System.out.println("donkey="+ index.getCount("donkey", "14"));
        System.out.println("wow="+ index.getCount("wow", "02"));
    }
}

