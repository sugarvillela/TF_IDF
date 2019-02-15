import os
import os.path
import math

port = os.getenv('PORT', '8080')
host = os.getenv('IP', '0.0.0.0')

# Add absolute path to relative path from where code file resides
def fixPath( filename ):
    script_dir = os.path.dirname(__file__) #<-- absolute dir the script is in
    return os.path.join(script_dir, 'textFiles1/'+filename)

# Open file, dump to list (endlines removed) and close    
def readall( filename ):
    with open( fixPath( filename ) ) as f:
        out = [line.strip() for line in f]
        return out;
            
class FileUT:
    # Word iterator: abstracts all into simple interface: good, done, nextWord
    def __init__(self, filename ):
        filename=fixPath( filename );
        self.words=[];
        self.tokens=False;
        self.index=0;

        self.fin=None;
        s=self;
        try:
            s.fin=open( filename );
            s.isGood=True;
            s.isDone=False;
        except IOError:
            #print('File does not exist');
            self.isGood=False;
            self.isDone=True;

    def __del__(self):
        #print('FileUT destruct')
        if self.isGood:
            self.fin.close();
            #print('FileUT closed');
            
    def good(self):
        #print('isGood='+str(self.isGood))
        return self.isGood;
        
    def done(self):
        #print('isDone='+str(self.isDone))
        return self.isDone;
        
    def nextWord(self):     #return single word stripped and lowercase
        length=len(self.words);
        
        if not length or self.index>=length :
            line="\n";
            while line=='\n':
                line = self.fin.readline(); # Don't strip yet
                if not line:                # "" is eof, "\n" is empty line
                    self.isDone=True;
                    return "";
            self.words=line.split(' ');     #tokenize
            self.index=0;                   #reset line iteration
        self.index+=1;                      #inc for next iteration
        return self.words[self.index-1].strip().lower();

class Node:
    # Struct for holding values
    def __init__(self, setText ):
        self.text=setText;
        self.count=1;
        self.termFreq=1;
        self.tfidf=1;
    
    def inc( self ):
        self.count+=1;
        
    def setTermFreq( self, tf ):
        self.termFreq=tf;
        
    def setTFIDF( self, tfidf ):
        self.tfidf=tfidf;
        
    def __str__(self):
        return "Text={}, Count={:d}, TermFreq={:8.5f}, TFIDF={:8.5f}".format(
            self.text, self.count, self.termFreq, self.tfidf
        );

    
class InvertedIndex:
    def __init__(self):
        self.index=dict();              # 2-d map: 
        self.docSizes=dict();           # 1-d map: docNum,size
        self.numDocsTotal=0;            # collection size
        self.numTermsTotal=0;           # words in collection   
        self.result=None;               # save and sort search results
        self.lastSearch="";             # search text
        
    def inc( self, word, docNum ):
        #   If first time saving word, create and set count to 1
        #   If word already exists in map, increment count 
        if word in self.index:                          # word yes
            if docNum in self.index[word]:              # doc yes: increment
                self.index[word][docNum].inc();
            else:                                       # doc no: create doc
                self.index[word][docNum]=Node( docNum );
        else:                                           # word no, doc no
            self.index[word]={ docNum : Node( docNum )};# create word, create doc
            
    def setDocSize( self, docNum, n ):
        #  IndexCreator counts and sets this when known 
        self.docSizes[docNum]=n; #//n terms by document

    def setNumTermsTotal( self, n ):
        self.numTermsTotal=n;

    def setNumDocsTotal( self, n ):
        self.numDocsTotal=n;
        
    def getCount( self, word, docNum ):
        # Number of occurrences of a particular term in a particular doc 
        if word in self.index and docNum in self.index[word]:
            return self.index[word][docNum].count;
        return 0;

    def getTermFreq( self, word, docNum ):# doesn't check if index exists
        # Number of occurrences normalized for doc size
        return self.index[word][docNum].termFreq;
        # if word in self.index and docNum in self.index[word]:
        # return self.index[word][docNum].termFreq;
        # return 0;

    def getDocFreq( self, word ):
        # Number of docs in which a word occurs 
        if word in self.index:
            return len( self.index[word] );
        return 0;

    def getNumTerms( self, docNum ):
        # number of words in a particular doc 
        if docNum in self.docSizes:
            return self.docSizes[docNum];
        return 0;
        
    def calc( self ):
        # IndexCreator calls after map is filled.
        # Iterate all nodes to set termFreq and TFIDF 
        for word, docList in self.index.items():
            IDF=math.log10( self.numDocsTotal/self.getDocFreq( word ) );
            for docNum, node in docList.items():
                node.setTermFreq( node.count/self.getNumTerms( docNum ) );
                node.setTFIDF( node.termFreq*IDF );
                
        
    def disp( self ):
        for word, docList in self.index.items():
            print("%s:" % word );
            for docNum, node in docList.items():
                print(
                    ".....%s: %d, %f, %f" % 
                    (docNum, node.count, node.termFreq, node.tfidf) 
                );
                
    def search( self, word ):
        # Search already exists in map, if word is there
        # Sort if found, else set result null
        self.lastSearch=word;
        if word in self.index:
            self.result=list( self.index[word].values() );
            self.result.sort(key=lambda x: x.tfidf, reverse=True);
            return True;
        self.result=None;
        return False;

    def getSearchResult( self ):
        return self.result;
        
    def dispSearchResult(self):
        if self.result==None:
            print("Your search - "+self.lastSearch+" - did not match any documents.");
            return;
        print();
        print("'"+self.lastSearch+"' returned "+str(len(self.result))+" results");
        print( "(Results in descending order of relevance)"); 
        print();
        print("DOC   COUNT   FREQ         TFIDF");
        print("================================================");
        for node in self.result:
            print( "{:4s} {:6d}  {:9.6f}   {:9.6f}".format(
                node.text, node.count, node.termFreq, node.tfidf
            ));
        print("================================================");
        print();
    
class IndexCreator:
    def __init__(self):
        self.stops=readall('stoplist.txt');
        self.index=InvertedIndex();
        self.numDocsTotal=0;
        self.numTermsTotal=0;
            # To parse all files in local directory, need to generate file names
            # of the form file01.txt
            # parseDoc counts terms and docs and returns false when all files 
            # have been read.
            # (This assumes files have consecutive names starting with 01)
        i=1;
        while self.parseDoc( i ):
            i+=1;
            #break;
        self.index.setNumTermsTotal( self.numTermsTotal );
        self.index.setNumDocsTotal( self.numDocsTotal );
        self.index.calc();
    
    def isStopWord( self, word ):
        if word in self.stops:
            #print( word+" is stop word" );
            return True;
        else:
            return False;
            
        #return word in self.stops;
        
    def parseDoc(self, docNum_int ):
        # Format two digit string as 02 or 12 etc
        docNum = str( docNum_int ) if docNum_int>9 else "0"+str( docNum_int );
        fileName="file"+docNum+".txt";
        F=FileUT( fileName );
        if not F.good():                        #Return false when out of files
            return False;
        count=0;                                #to count terms
        while True:
            dWord=F.nextWord();                 #nextWord sets done on eof
            if F.done():
                break;
            if not self.isStopWord( dWord ):    #skip stopword
                count+=1;
                self.index.inc( dWord, docNum );#add or increment inverted index
                
        self.numTermsTotal+=count;
        self.index.setDocSize( docNum, count );
        self.numDocsTotal+=1;
        return True;
        
    def getObj_InvertedIndex(self):
        # For getting the index after initialization
        return self.index;
        
    def disp( self ):
        print( 
            "numTermsTotal="+
            str( self.numTermsTotal )+
            ", numDocsTotal="+
            str(self.numDocsTotal) 
        );
        self.index.disp();

def main():
    print("hello world");
    I=IndexCreator();
    index=I.getObj_InvertedIndex();
    while True:
        text = input("Enter a word to search or 'q' to quit  : ").strip().lower();
        if not text:
            continue;
        if text=='q':
            break;
        if text=='disp':
            index.disp();
        else:
            index.search( text );
            index.dispSearchResult();


if __name__ == '__main__':
	main();


