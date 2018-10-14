package search.holder;

/**
 * @author Dmitry [ ______ ]
 * @version 07.10.2018 20:17
 */
public class TermFrequency {
	private final int termId;
    private int count;
    private double frequency;
	private final int documentId;

    public TermFrequency(int termId, int count, int documentId) {
	    this.termId = termId;
	    this.count = count;
	    this.documentId = documentId;
    }

	public int getTermId() {
		return termId;
	}

	public double getFrequency() {
		return frequency;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

    public int getCount() {
        return count;
    }

    public void increaseCounter() {
       ++count;
    }

    public void setCounter(int count) {
        this.count = count;
    }

	public int getDocumentId() {
		return documentId;
	}
}
