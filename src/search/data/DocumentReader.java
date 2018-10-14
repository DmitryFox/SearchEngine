package search.data;

import search.core.Engine;
import search.holder.Document;
import search.holder.TermFrequency;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry [ _____ ]
 * @version 07.10.2018 19:54
 */
public class DocumentReader {
	private static final Pattern TOKEN_PATTERN = Pattern.compile("\\b([А-Яа-яA-Za-z]+)\\b");

	private final Document document;

	public DocumentReader(File file, Document document) {
		this.document = document;

		parseFile(file);
	}

	private void parseFile(File file) {
		Engine engine = Engine.getInstance();

		int termCount = 0;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = TOKEN_PATTERN.matcher(line);
				while (matcher.find()) {
					String term = matcher.group(1).toLowerCase();
					term = termStemming(term); // TODO: Not Implemented.
					int termId = engine.getNewTermId(term);

					Map<Integer, TermFrequency> termFrequencyMap = engine.getTermDocumentMap(termId);
					if (termFrequencyMap.get(document.getId()) == null)
						termFrequencyMap.put(document.getId(), new TermFrequency(termId, 0, document.getId()));

					termFrequencyMap.get(document.getId()).increaseCounter();
					++termCount;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		document.setTermCount(termCount);
	}

	private String termStemming(String term) {
		return term;
	}
}
