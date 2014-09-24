package mlchunker;

import format.WordCoNLL2000;

/**
 *
 * @author pierre
 */
// This class extracts the baseline feature: the part of speech and the class, the chunk tag. 
public class Features {

    String ppos;
    String ppos_1;
    String ppos1;
    String chunk;
    String form;

    public Features(WordCoNLL2000 word) {
        form = word.getForm();
        ppos = word.getPpos();
        chunk = word.getChunk();
    }

    public Features(String ppos, String chunk) {
        this.ppos = ppos;
        this.chunk = chunk;
    }

    public Features(String ppos, String chunk, String ppos_1, String ppos1) {
        this.ppos = ppos;
        this.chunk = chunk;
        this.ppos_1 = ppos_1;
        this.ppos1 = ppos1;
    }

    public Features(String ppos, String chunk, String ppos_1, String ppos1, String form) {
        this.ppos = ppos;
        this.chunk = chunk;
        this.ppos_1 = ppos_1;
        this.ppos1 = ppos1;
        this.form = form;
    }

    public boolean equals(Object features) {
        String signature1 = getPpos() + getChunk();
        String signature2 = ((Features) features).getPpos() + ((Features) features).getChunk();
        return signature1.equals(signature2);
    }

    public int hashCode() {
        return (getPpos() + getChunk()).hashCode();
    }

    public String getPpos() {
        return ppos;
    }

    public String getPpos1() {
        return ppos1;
    }

    public String getPpos_1() {
        return ppos_1;
    }

    public String getForm() {
        return form;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }
}
