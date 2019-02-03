package cs172;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
/**
 * @author David Swanson
 */
public class InvertedIndex {
    protected HashMap<String, HashMap<String, Node>> map;
    protected HashMap<String, Integer> docSizes;
    protected ArrayList<Node> result;
    protected int numDocsTotal;
    protected int numTermsTotal;
    protected String lastSearch;
    
    public InvertedIndex(){
        map = new HashMap<>();      //word->docNum->Node
        docSizes=new HashMap<>();   //docNum->numTerms
        numDocsTotal=0;             //collection size
        numTermsTotal=0;            
        result=null;                //save and sort search results
        lastSearch="";              //search text
    }
    public void inc( String word, String docNum ){
        /*  If first time saving word, create and set count to 1
            If word already exists in map, increment count */
        if( map.containsKey( word )){
            HashMap<String, Node> docMap = map.get(word);
            if( docMap.containsKey( docNum )){
                docMap.get(docNum).inc();
            }
            else{
                docMap.put( docNum, new Node( docNum ) );
            }
        }
        else{
            map.put(word, new HashMap<>() );
            map.get(word).put( docNum, new Node( docNum ) );
        }
    }
    public void setDocSize( String docNum, int count ){
        /* IndexCreator counts and sets this when known */
        docSizes.put( docNum, count ); //n terms by document
    }
    public void setNumTermsTotal( int n ){
        numTermsTotal=n;
    }
    public void setNumDocsTotal( int n ){
        numDocsTotal=n;
    }
    public void calc(){
        /*  IndexCreator calls after map is filled.
            Iterate all nodes to set termFreq and TFIDF */
        map.keySet().forEach((word) -> {
            float IDF=(float)Math.log( numDocsTotal/docFreq( word ) ); 
            HashMap<String, Node> docMap = map.get(word);
            docMap.keySet().forEach((docNum) -> {
                Node node=docMap.get( docNum );
                node.setTermFreq(
                    (float)node.count/numTerms( docNum )
                );
                node.setTFIDF(
                    node.termFreq*IDF
                );
            });
        });
    }
    public void search( String word ){
        /*  Search already exists in map, if word is there
            Sort if found, else set result null */
        lastSearch=word;
        if( map.containsKey( word ) ){
            result=new ArrayList<>( map.get(word).values() );
            Collections.sort(result);
        }
        else{
            result=null;
        }
    }
    public ArrayList<Node> getSearchResult(){
        return result;
    }
    public int getCount( String word, String docNum ){
        /* Number of occurrences of a particular term in a particular doc */
        if( map.containsKey( word ) && map.get(word).containsKey( docNum )){
            return map.get(word).get(docNum).count;
        }
        return 0;
    }
    public float termFreq( String word, String docNum ){
        /* Number of occurrences normalized for doc size */
        return map.get(word).get(docNum).termFreq;
    }
    public int docFreq( String word ){
        /* Number of docs in which a word occurs */
        return( map.containsKey( word ))? map.get(word).size() : 0;
    }
    public int numTerms( String docNum ){
        /* number of words in a particular doc */
        return docSizes.get( docNum );
    }
    public HashMap<String, Integer> getObj_docSizes(){
        return docSizes;
    }
    public void dispPretty(){//print map for debug
        System.out.println( 
            "Found "+numTermsTotal+" terms in "+
            numDocsTotal+" documents"
        );
        System.out.println("TERM                 DOC   COUNT   FREQ         TFIDF");
        map.keySet().forEach((word) -> {
            System.out.println("==========================================================");
            String fWord=String.format( "%-20s", word );
            HashMap<String, Node> docMap = map.get(word);
            docMap.keySet().forEach((docNum) -> {
                Node node=docMap.get( docNum );
                String fString=String.format( 
                    "%-20s %-4s  %-6s  %.6f     %.6f", 
                    word, node.text, node.count, node.termFreq, node.tfidf 
                );
                System.out.println( fString );
            });
        });
    }
    public void dispSearchResult(){
        if( result==null ){
            System.out.println( 
                "Your search - "+lastSearch+
                " - did not match any documents."
            );
            return;
        }
        System.out.println();
        System.out.println("'"+lastSearch+"' returned "+result.size()+" results");
        System.out.println( "(Results in descending order of relevance)"); 
        System.out.println();
        System.out.println("DOC   COUNT   FREQ         TFIDF");
        System.out.println("================================================");
        result.forEach((node) -> {
            String fString=String.format( 
                "%-4s  %-6s  %.6f     %.6f", 
                node.text, node.count, node.termFreq, node.tfidf 
            );
            System.out.println( fString );
        });
        System.out.println("================================================");
        System.out.println();
    }
    public void dispDocSizes(){
        docSizes.keySet().forEach((docNum) -> {
            System.out.println(
                "DocNum " + docNum +
                ": Count=" + docSizes.get( docNum )
            );
        });
    }
}

