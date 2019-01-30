package cs172;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.ArrayList;
/**
 * @author David Swanson
 */
public class FileUT {
    /*  For text file input;
        good() is true if file opened
        readAll dumps file to array list
        nextWord is an iterator that tokenizes file at spaces.
        Returns one word with each call.
        done() is true when iterator runs out of words 
    */
    public Scanner input;
    protected String line;
    protected String[] tokens;
    protected int index;
    protected boolean isGood;
    protected boolean isDone;
    public FileUT( String fileName ){
        line="";
        index=0;
        tokens=null;
        isGood=false;
        try{
            input = new Scanner( new File(fileName) );
            isGood=true;
        }
        catch ( FileNotFoundException fileNotFoundException ){}
    }
    public void change( String fileName ){
        line="";
        index=0;
        tokens=null;
        try{
            input = new Scanner( new File(fileName) );
        }
        catch ( FileNotFoundException fileNotFoundException ){
            System.err.println( "Error opening file." );
            isGood=false;
        }
        isGood=true;
    }
    public void end(){
        input.close();
    }
    public ArrayList<String> readAll(){//return array of file contents
        ArrayList<String> arr;
        arr = new ArrayList<>();
        try{
            while ( ( line = input.nextLine() )!=null ){
                if( !line.isEmpty()){
                    arr.add(line.trim());
                }
            }
        }
        catch ( NoSuchElementException | IllegalStateException elementException ){
        }
        return arr;
    }
    public String nextWord(){
        if( tokens==null || index >= tokens.length ){
            try{
                line="";
                while( line.isEmpty() ){
                    line = input.nextLine();
                }
                tokens=line.split(" ");
                index=0;
            }
            catch ( NoSuchElementException | IllegalStateException elementException ){
                isDone=true;
                return "";
            }
        }
        return tokens[index++].trim().toLowerCase();
    }
    public void reset(){
        isGood=false;
        isDone=false;
    }
    public boolean good(){ return isGood; }
    public boolean done(){ return isDone; }
}
