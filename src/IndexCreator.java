package cs172;

import java.util.ArrayList;
/**
 * @author David Swanson
 */
public class IndexCreator {
    protected FileUT doc;
    protected ArrayList<String> stoplist;
    protected InvertedIndex index;
    protected int numDocsTotal;
    protected int numTermsTotal;
    
    public IndexCreator(){
        /* Constructor sets up index */
        FileUT F=new FileUT("stoplist.txt");    //get stoplist from file
        stoplist= F.readAll();
        index=new InvertedIndex();
        numDocsTotal=0;                         //init counters
        numTermsTotal=0;
        /*  To parse all files in local directory, need to generate file names
            of the form file01.txt
            parseDoc counts terms and docs and returns false when all files 
            have been read.
            (This assumes files have consecutive names starting with 01)*/
        int i=1;
        while( parseDoc( i++ ) ){}
        index.setNumTermsTotal( numTermsTotal );
        index.setNumDocsTotal( numDocsTotal );
        index.calc();
    }
    public boolean isStopWord( String word ){
        return (stoplist.stream().anyMatch((stopWord) -> ( word.equals( stopWord ))));
    }
    protected final boolean parseDoc( int docNum_int ){
        String docNum = ( docNum_int>9 )? ""+docNum_int : "0"+docNum_int;
        String fileName="file"+docNum+".txt";
        FileUT F=new FileUT( fileName );
        if(!F.good()){//return false on the first bad file name
            return false;
        }
        //System.out.println( docNum+" good");
        String dWord;
        Integer count = 0;
        while(!F.done()){
            dWord=F.nextWord();
            if( !dWord.isEmpty() && !isStopWord( dWord ) ){
                index.inc( dWord, docNum );
                count++;
            }
        }
        numTermsTotal+=count;           //total n terms in collection
        index.setDocSize( docNum, count ); //n terms by document
        numDocsTotal++;
        return true;
    }
    public InvertedIndex getObj_InvertedIndex(){
        return index;
    }
}
