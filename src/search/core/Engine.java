package search.core;

import search.holder.Document;
import search.holder.TermFrequency;
import search.utils.ArrayUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Dmitry [ ______ ]
 * @version 07.10.2018 19:53
 */
public class Engine {
	/**
	 * Comparator...
	 */
	private static final Comparator<Map.Entry<Integer, TermFrequency>> TERM_FREQUENCY_COMPARATOR = (o1, o2) -> Double.compare(o2.getValue().getFrequency(), o1.getValue().getFrequency());
	/**
	 * List of all documents.
	 */
	private static final Map<Integer, Document> documentMap = new LinkedHashMap<>();
	/**
	 * A list of all the terms that are in all documents.
	 */
	private static final Map<String, Integer> termIdMap = new LinkedHashMap<>();
	/**
	 * The list of frequencies of all terms along with documents.
	 * Map<TermId, Map<DocumentId, TermFrequency>>
	 */
	private static final Map<Integer, Map<Integer, TermFrequency>> termDocumentMap = new HashMap<>();

	public Document getDocument(int documentId) {
		return documentMap.get(documentId);
	}

	public void addDocument(int id, Document document) {
		documentMap.put(id, document);
	}

	public Map<Integer, Document> getDocumentMap() {
		return documentMap;
	}

	public int getTermId(String term) {
		return hasTerm(term) ? termIdMap.get(term) : -1;
	}

	public int getNewTermId(String term) {
		if (hasTerm(term)) {
			return termIdMap.get(term);
		} else {
			int newId = termIdMap.size() + 1;
			addTerm(newId, term);
			return newId;
		}
	}

	public boolean hasTerm(String term) {
		return termIdMap.containsKey(term);
	}

	public void addTerm(int id, String term) {
		termIdMap.put(term, id);
	}

	public Map<String, Integer> getTermMap() {
		return termIdMap;
	}

	public Map<Integer, TermFrequency> getDocumentListByTermId(int id) {
		if (termDocumentMap.get(id) == null) {
			termDocumentMap.put(id, new LinkedHashMap<>());
		}

		return termDocumentMap.get(id);
	}

	public boolean hasTermFrequency(int id) {
		return termDocumentMap.containsKey(id);
	}

	public void addTermFrequency(int id, Map<Integer, TermFrequency> termFrequency) {
		termDocumentMap.put(id, termFrequency);
	}

	public Map<Integer, Map<Integer, TermFrequency>> getTermDocumentMap() {
		return termDocumentMap;
	}

	public void calculateTermFrequency() {
		for (Map<Integer, TermFrequency> termDocuments : termDocumentMap.values()) {
			for (TermFrequency termDocument : termDocuments.values()) {
				Document document = getDocument(termDocument.getDocumentId());

				double tf = (double) termDocument.getCount() / (double) document.getTermCount();
				double idf = Math.log((double) documentMap.size() / termDocuments.size());
				termDocument.setFrequency(tf * idf);
			}
		}
	}

	public void sortDocumentInIndexByFrequency() {
		for (Map.Entry<Integer, Map<Integer, TermFrequency>> termDocuments : termDocumentMap.entrySet()) {
			termDocuments.setValue(ArrayUtil.sort(termDocuments.getValue(), TERM_FREQUENCY_COMPARATOR));
		}
	}

	public void clear() {
		termDocumentMap.clear();
		termIdMap.clear();
		documentMap.clear();
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		private static final Engine INSTANCE = new Engine();
	}

	public static Engine getInstance() {
		return SingletonHolder.INSTANCE;
	}
}
