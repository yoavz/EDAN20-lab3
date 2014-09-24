package format;

import java.io.*;
import java.util.*;
import mlchunker.MLChunker;
import mlchunker.Features;

/**
 *
 * @author pierre
 */
// The class to load a CoNLL 2000 corpus.
public class ReaderWriterCoNLL2000 {

    private List<List<WordCoNLL2000>> sentenceList;

    public List<List<WordCoNLL2000>> loadFile(File file) throws IOException {
        sentenceList = new ArrayList<List<WordCoNLL2000>>();
        List<WordCoNLL2000> sentence = new ArrayList<WordCoNLL2000>();

        WordCoNLL2000 curWord = null;

        int sentenceCount = 0;
        int lineCount = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            curWord = makeWord(line);
            if (curWord != null) { // A word
                sentence.add(curWord);
            } else { // An empty line denoting a new sentence
                sentenceList.add(sentence);
                sentence = new ArrayList<WordCoNLL2000>();
                sentenceCount++;
            }
            if ((lineCount % 50000) == 0) {
                System.out.println("Read: " + lineCount + " lines, " + sentenceCount + " sentences.");
            }
        }
        reader.close();
        System.out.println("Read: " + lineCount + " lines, " + sentenceCount + " sentences.");
        return sentenceList;
    }

    // Splits the input line of the CoNLL 2000 file into three fields: word, part of speech, and chunk tag.
    private WordCoNLL2000 makeWord(String line) {
        String[] wordD = line.split(" ");
        WordCoNLL2000 word = null;
        // Both the training and test sets have the three fields.
        if (wordD.length == 3) {
            word = new WordCoNLL2000(wordD[0], wordD[1], wordD[2]);
        }
        return word;
    }

    public void printFile(List<List<WordCoNLL2000>> sentenceList) {
        for (List<WordCoNLL2000> sentence : sentenceList) {
            for (WordCoNLL2000 word : sentence) {
                System.out.print(word.getForm() + " ");
            }
            System.out.println("");
        }
    }

    public void saveFile(File file, List<List<WordCoNLL2000>> sentenceList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

        for (int i = 0; i < sentenceList.size(); i++) {
            for (int j = 0; j < (sentenceList.get(i)).size(); j++) {
                writer.write((sentenceList.get(i)).get(j).getForm() + " ");
                writer.write((sentenceList.get(i)).get(j).getPpos() + " ");
                writer.write((sentenceList.get(i)).get(j).getChunk() + "\n");
            }
            if (i != sentenceList.size()) {
                writer.write("\n");
            }
        }
        writer.close();
    }

    String escape(String s) {
        if (s.equals(","))
            return "\",\"" ;
        else if (s.equals("''"))
            return "\"''\"";
        else 
            return s;
    }

    // This method saves the training data in the ARFF format. It adds the header.
    public void saveARFF(File file, List<List<WordCoNLL2000>> sentenceList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        writer.write(Constants.ARFF_HEADER);
        for (List<WordCoNLL2000> sentence : sentenceList) {
            String chunk = null;
            String ppos = null;
            for (WordCoNLL2000 word : sentence) {
                ppos = escape(word.getPpos()); 
                writer.write(ppos + ", ");
                writer.write(word.getChunk() + "\n");
            }
        }
    }

    public void saveARFFMulti(File file, List<Features> featureList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        String f_1, f, f1, chunk;
        System.out.println(Constants.ARFF_HEADER_MULTI);
        for (Features features : featureList) {
            f_1 = escape(features.getPpos_1()); 
            f = escape(features.getPpos()); 
            f1 = escape(features.getPpos1());
            chunk = features.getChunk();
            System.out.println(f_1 + ", " + f + ", " + f1 + ", " + chunk);
        }
    }

    List<WordCoNLL2000> getSentence(int index) {
        return sentenceList.get(index);
    }

    List<List<WordCoNLL2000>> getSentenceList() {
        return sentenceList;
    }

    int getSize() {
        return sentenceList.size();
    }
}
