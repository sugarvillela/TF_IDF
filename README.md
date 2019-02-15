# TF_IDF
Implementation of Term_Frequency*Inverse_Document_Frequency for Information Retrieval

## Two Implementations
* I wrote this in Java, then I translated a Python version

## Instructions
* For Java version, make sure data files are in home directory. File open uses relative path.
* For Python version, just use relative path. My file utilities prepend the absolute path for you.
* Data files must be of the format 'file01.txt'.  (Mainly the numeric part is 2 digits starting at index 4)
* Stoplist.txt is a list of words to skip, like 'a' and 'the'
* To display inverted index, type 'disp'
* To quit, type 'q'
* A display of inverted index is in output.txt
* A sample of the search output is in sampleOutput.txt

## Overview
* The program uses nested Java hashmap datastructures to implement an inverted index
* The main hashmap uses found words as keys.
* Each inner hashmap uses document number as keys
* Each value of an inner hashmap is a node structure containing the count, term frequency and TF-IDF for a word-doc set of keys
* There is a separate hashmap that keeps track of document term counts, indexed by document number

## Document Parsing
* Class FileUT opens a text file and provides two utilities: readAll() and nextWord()
* readAll() dumps a file to a Java Arraylist. This is used to read the contents of stopword.txt
* nextWord is a word iterator. It abstracts the process of reading a file line-by-line. Use nextWord() with done() to iterate each word in the file.

## Inverted Index Initialization
* Class IndexCreator instantiates FileUT objects to parse files and create the index.  Use getObj_InvertedIndex() to get the finished index.
* IndexCreator opens any file of the form file01.txt by generating file names. This assumes that files are ordered, starting with 1. On generating a nonexistent file name, the creator finishes.
* IndexCreator calls inc(word,doc) to register a word.  If the word is already in the map, the count is increased.  If the word is not there yet, a new node object is initialized.
* After all files are parsed, IndexCreator runs the TFIDF calculations and saves the results to the nodes.

# Search function
* Search locates a word in the map, or returns false if the word is not found.  It sorts the submap by TFIDF and returns the results in descending order of relevance.
