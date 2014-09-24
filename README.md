Lab 3: Extracting noun groups using machine learning techniques
===============================================================
Report by ***Yoav Zimmerman*** for EDAN20 Fall 2014

Introduction
------------

Last week, we used manual rules to attempt to chunk a CoNLL 2000 dataset. Although this enabled us to achieve a reasonable F1 score of approximately 78, we can do better. This week, we will use machine learning techniques to chunk the same dataset and achieve a higher F1 score.

Baseline
--------

The first task whenever working with a machine learning is to identify a **baseline**, or the application of a minimal techinque. The baseline can then be used as a relative measure to compare with further experiments. For this particular problem, CoNLL2000 obtained a baseline result by selecting the chunk tag which was most frequently associated with the part of speech tag for every word. This is the absolute minimal technique we can use as a chunk classifier, so it seems reasonable to use as a baseline.

For the implementation, a `BLChunkerInstance` skeleton class was supplied. The code written to complete it iterated through every word in the dataset counted the frequency of each PPoS-Chunk pair in a hashmap. Then, all that is left to do is iterate through every unique PPoS and find the chunk that corresponds to the highest occurence together with the PPoS. The following are the results of the baseline experiment:

```bash
$ ./eval.sh test.txt test_pred.txt
processed 47377 tokens with 23852 phrases; found: 26992 phrases; correct: 19592.
accuracy:  77.29%; precision:  72.58%; recall:  82.14%; FB1:  77.07
             ADJP: precision:   0.00%; recall:   0.00%; FB1:   0.00  0
             ADVP: precision:  44.33%; recall:  77.71%; FB1:  56.46  1518
            CONJP: precision:   0.00%; recall:   0.00%; FB1:   0.00  0
             INTJ: precision:  50.00%; recall:  50.00%; FB1:  50.00  2
              LST: precision:   0.00%; recall:   0.00%; FB1:   0.00  0
               NP: precision:  79.87%; recall:  86.80%; FB1:  83.19  13500
               PP: precision:  74.73%; recall:  97.07%; FB1:  84.45  6249
              PRT: precision:  75.00%; recall:   8.49%; FB1:  15.25  12
             SBAR: precision:   0.00%; recall:   0.00%; FB1:   0.00  0
               VP: precision:  60.53%; recall:  74.22%; FB1:  66.68  5711
```

As we can see, we have already achieved a modest 77.01 F1 score from the most minimal technique. Not bad!

Replicating Baseline
--------------------

The above was done purely through manual calculations in Java, but we may replicate the same implementation and results in Weka. To do this, we use a header file in ARFF (Attribute Relation File Format) that looks something like this:

```
@relation chunk

@attribute pos { #, $, "''", (, ), ",", ., :, CC, CD, DT, EX, FW, IN, JJ, JJR, JJS, MD, NN, NNP, NNPS, NNS, PDT, POS, PRP, PRP$, RB, RBR, RBS, RP, SYM, TO, UH, VB, VBD, VBG, VBN, VBP, VBZ, WDT, WP, WP$, WRB, `` }

@attribute chunk { B-ADJP, B-ADVP, B-CONJP, B-INTJ, B-LST, B-NP, B-PP, B-PRT, B-SBAR, B-UCP, B-VP, I-ADJP, I-ADVP, I-CONJP, I-INTJ, I-NP, I-PP, I-PRT, I-SBAR, I-UCP, I-VP, O }

@data
"''", B-NP
., B-VP
```

The ARFF file above defines two attributes to our dataset. One is POS and has a domain of all the possible CoNLL2000 part of speech tags. Another is chunk and has a domain of a the possible CoNLL2000 chunk tags. In addition, the actual datapoints are defined under the `@data` directive. In the sample dataset above, only two words and their subsequent parts of speech and chunk tags are defined. Note that the actual string of the word is not used as a feature for supervised learning and therefore _not required as an attribute_.

To replicate the baseline classifier in Weka, the above header and java program `MLChunker.java` was used to systematically generate an ARFF file with all of the words from the training set. It's `@data` section looked something like this:

```
@data
NN, B-NP
IN, B-PP
DT, B-NP
NN, I-NP
...
```

The Weka GUI interface was then used to generate a decision tree model using the built-in J48 algorithm. Now that a model file was generated, the `MLChunker.java` and `WekaGlue.java` programs were run to tag the test set according to the model. The eval script was run once more and returned an F1 score of 77.07, the exact same as when we tagged the test set in Java manually. We may conclude that we have successfully replicated the baseline in Weka.

Using more features
-------------------

To do better, we increase the complexity of our model by adding features. The baseline implementation corresponds to a length one feature vector for each data point, the part of speech tag: 

Θ = _p<sub>i</sub>_

The program that won the CoNLL2000 conference used a complicated feature vector of length twelve. We shall use a feature vector of length three, so that feature vector includes the part of speech tag for the word, the part of speech tag before it, and the one after it:

Θ = _p<sub>i-1</sub>, p<sub>i</sub>, p<sub>i+1</sub>_

To implement this, several of given java programs had to be modified. The ARFF header was changed to include two additional attributes, pos_1 and pos1, representing the part of speech tags before and after the word respectively. `ReaderWriterCoNLL2000.java` was changed to spit out the ARFF with the extra attributes. After the training program was run, the new ARFF file looked like this:

```
@relation chunk

@attribute pos_1 {#, $, "''", (, ), ",", ., :, CC, CD, DT, EX, FW, IN, JJ, JJR, JJS, MD, NN, NNP, NNPS, NNS, PDT, POS, PRP, PRP$, RB, RBR, RBS, RP, SYM, TO, UH, VB, VBD, VBG, VBN, VBP, VBZ, WDT, WP, WP$, WRB, ``}
@attribute pos {#, $, "''", (, ), ",", ., :, CC, CD, DT, EX, FW, IN, JJ, JJR, JJS, MD, NN, NNP, NNPS, NNS, PDT, POS, PRP, PRP$, RB, RBR, RBS, RP, SYM, TO, UH, VB, VBD, VBG, VBN, VBP, VBZ, WDT, WP, WP$, WRB, ``}
@attribute pos1 {#, $, "''", (, ), ",", ., :, CC, CD, DT, EX, FW, IN, JJ, JJR, JJS, MD, NN, NNP, NNPS, NNS, PDT, POS, PRP, PRP$, RB, RBR, RBS, RP, SYM, TO, UH, VB, VBD, VBG, VBN, VBP, VBZ, WDT, WP, WP$, WRB, ``}
@attribute chunk {B-ADJP, B-ADVP, B-CONJP, B-INTJ, B-LST, B-NP, B-PP, B-PRT, B-SBAR, B-UCP, B-VP, I-ADJP, I-ADVP, I-CONJP, I-INTJ, I-NP, I-PP, I-PRT, I-SBAR, I-UCP, I-VP, O}

@data
NN, IN, DT, B-PP
IN, DT, NN, B-NP
DT, NN, VBZ, I-NP
NN, VBZ, RB, B-VP
...
```

To simplify things, the first and last "border" words the dataset were left out of the ARFF file. Similarly to the process above, the Weka GUI was used to generate a decision tree model using the J48 algorithm. Finally, after slightly modifying the `MLChunker.java` program to tag each word on all three features, the test set was tagged using the generated model. The results are displayed below:

```bash
$ ./eval.sh test.txt test_pred.txt
processed 47377 tokens with 23852 phrases; found: 24673 phrases; correct: 21151.
accuracy:  92.43%; precision:  85.73%; recall:  88.68%; FB1:  87.18
             ADJP: precision:  58.78%; recall:  57.31%; FB1:  58.03  427
             ADVP: precision:  68.34%; recall:  73.79%; FB1:  70.96  935
            CONJP: precision:   0.00%; recall:   0.00%; FB1:   0.00  11
             INTJ: precision:  50.00%; recall:  50.00%; FB1:  50.00  2
              LST: precision:   0.00%; recall:   0.00%; FB1:   0.00  0
               NP: precision:  87.24%; recall:  90.02%; FB1:  88.61  12818
               PP: precision:  84.94%; recall:  96.51%; FB1:  90.36  5466
              PRT: precision:  58.82%; recall:  18.87%; FB1:  28.57  34
             SBAR: precision:  77.30%; recall:  23.55%; FB1:  36.10  163
               VP: precision:  89.04%; recall:  92.08%; FB1:  90.53  4817
```

Using an only slightly more complex model, an excellent F1 score of 87.18 was achieved. These results are better than could likely ever be achieved using the manual grammar rules method of last week. Furthermore, they could be improved with only a little bit more work. The feature vector can be expanded to include more parts of speech, previous chunk tags, and even the word strings themselves.
