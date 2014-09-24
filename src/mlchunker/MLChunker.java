package mlchunker;

import java.io.*;
import java.util.*;
import wekaglue.WekaGlue;
import format.Constants;
import format.ReaderWriterCoNLL2000;
import format.WordCoNLL2000;
import format.Corpus;

/**
 *
 * @author pierre
 */
public class MLChunker extends Corpus {

    WekaGlue wekaGlue;

    public MLChunker() {
    }

    public void writeARFF(String file, int multi) throws IOException {
        ReaderWriterCoNLL2000 reader = new ReaderWriterCoNLL2000();
        if (multi != 0)
            reader.saveARFFMulti(new File(file), featureList);
        else
            reader.saveARFF(new File(file), sentenceList);
    }

    public void tag() {
        wekaGlue = new WekaGlue();
        wekaGlue.create(Constants.ARFF_MODEL, Constants.ARFF_DATA);
        String prevWord = null, currWord = null, nextWord = null;
        String[] features = new String[3];
        WordCoNLL2000 currWordStruct = null;
        for (List<WordCoNLL2000> sent : sentenceList) {
            for (WordCoNLL2000 word : sent) {
                nextWord = word.getPpos();
                if (currWord != null && prevWord != null && nextWord != null) {
                    features[0] = prevWord;
                    features[1] = currWord;
                    features[2] = nextWord;
                    currWordStruct.setChunk(wekaGlue.classify(features));
                }
                prevWord = currWord;
                currWord = nextWord;
                currWordStruct = word;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        MLChunker chunker = new MLChunker();
        if (args.length < 1) {
            System.out.println("Usage: java mlchunker.MLChunker (-train|-tag)");
            System.exit(0);
        }
        if (args[0].equals("-trainold")) {
            chunker.load(Constants.TRAINING_SET_2000);
            chunker.extractBaselineFeatures();
            chunker.writeARFF(Constants.ARFF_DATA, 0);
        } else if (args[0].equals("-train")) {
            chunker.load(Constants.TRAINING_SET_2000);
            chunker.extractFeatures();
            chunker.writeARFF(Constants.ARFF_DATA, 1);
        }else if (args[0].equals("-tag")) {
            chunker.load(Constants.TEST_SET_2000);
            chunker.tag();
            chunker.save(Constants.TEST_SET_PREDICTED_2000);
        } else {
            System.out.println("Usage: java mlchunker.MLChunker (-train|-tag)");
        }
    }
}
