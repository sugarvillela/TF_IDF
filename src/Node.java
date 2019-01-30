package cs172;
/**
 *
 * @author newAdmin
 */
public class Node  implements Comparable<Node> {
    /*  Struct for holding values
        Can be sorted by TFIDF 
    */
    public int count;
    public float termFreq, tfidf;
    public String text;
    Node( String setText ){
        text=setText;
        count=1;
        termFreq=1;
        tfidf=1;
    }
    public void inc(){
        count++;
    }
    public void setTermFreq( float tf ){
        termFreq=tf;
    }
    public void setTFIDF( float tfidfSet ){
        tfidf=tfidfSet;
    }
    @Override
    public int compareTo(Node node) {
        int thisone=(int)(this.tfidf*10000);
        int otherone=(int)(((Node)node).tfidf*10000);
        return otherone-thisone;//sorts high to low
    }
    @Override
    public String toString(){
        return String.format( 
            "%-4s  %-6s  %.6f     %.6f", 
            text, count, termFreq, tfidf 
        );
    }
}
