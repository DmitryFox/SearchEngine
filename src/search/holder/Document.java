package search.holder;

/**
 * @author Dmitry [ ______ ]
 * @version 07.10.2018 20:17
 */
public class Document {
	private final int id;
	private final String name;
	private long length;
	private long crc32;
	private int termCount;

	public Document(int id, String name, long length, long crc32) {
		this.id = id;
		this.name = name;
		this.length = length;
		this.crc32 = crc32;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getTermCount() {
		return termCount;
	}

	public void setTermCount(int termCount) {
		this.termCount = termCount;
	}

	public long getLength() {
		return length;
	}

	public long getCRC32() {
		return crc32;
	}
}
