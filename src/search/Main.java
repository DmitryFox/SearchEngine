package search;

import search.core.Engine;
import search.data.DocumentReader;
import search.holder.Document;
import search.holder.Result;
import search.holder.TermFrequency;
import search.utils.ArrayUtil;
import search.utils.FileUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dmitry [ ______ ]
 * @version 07.10.2018 19:31
 */
public class Main {
	private static final Comparator<Map.Entry<Integer, Result>> TERM_FREQUENCY_COMPARATOR = (o1, o2) -> Double.compare(o2.getValue().getFrequency(), o1.getValue().getFrequency());

	private static final Pattern TOKEN_PATTERN = Pattern.compile("\\b([А-Яа-яA-Za-z]+)\\b");

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0000000");

	private static final String HELP_MESSAGE = "Available commands:" + "\r\n"
			+ "-help [To show help message]" + "\r\n"
			+ "-index ./directory [Indexing files in directory]" + "\r\n"
			+ "-load ./directory [Load indexed files]" + "\r\n"
			+ "-find query [Search message in documents]" + "\r\n"
			+ "-exit [Terminate program]" + "\r\n";

	public static void main(String[] args) {
		log("Start.");
		log("Enter --help to show available commands.");

		try (Scanner scanner = new Scanner(System.in)) {
			System.out.print(">");
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] command = line.split(" ");
				switch (command[0]) {
					case "--help":
						log(HELP_MESSAGE);
						break;
					case "--index":
						indexedFileCommand(command[1]);
						saveIndexToFile(command[1] + ".idx");
						break;
					case "--load":
						loadIndexedFileCommand(command[1]);
						break;
					case "--find":
						String query = line.substring(command[0].length() + 1).toLowerCase();
						searchInIndexCommand(query);
						break;
					case "--exit":
						System.exit(0);
						break;
				}

				System.out.print(">");
			}
		}

		log("Program exit...");
	}

	private static void loadIndexedFileCommand(String file) {
		Engine engine = Engine.getInstance();
		engine.clear();

		long timeStart = System.currentTimeMillis();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
			int documentSize = Integer.parseInt(reader.readLine());
			for (int i = 0; i < documentSize; ++i) {
				String part[] = reader.readLine().split("\t");
				int documentId = Integer.parseInt(part[0]);
				String documentName = part[1];
				int documentTermCount = Integer.parseInt(part[2]);
				long documentLength = Long.parseLong(part[3]);
				long documentCRC32 = Long.parseLong(part[4]);

				Document document = new Document(documentId, documentName, documentLength, documentCRC32);
				document.setTermCount(documentTermCount);
				engine.addDocument(documentId, document);
			}

			int termSize = Integer.parseInt(reader.readLine());
			for (int i = 0; i < termSize; ++i) {
				String part[] = reader.readLine().split("\t");
				String term = part[0];
				int termId = Integer.parseInt(part[1]);

				engine.addTerm(termId, term);
			}

			int termDocumentSize = Integer.parseInt(reader.readLine());
			for (int i = 0; i < termDocumentSize; ++i) {
				String part[] = reader.readLine().split("\t");
				int termId = Integer.parseInt(part[0]);
				int termFrequencySize = Integer.parseInt(part[1]);

				for (int j = 0; j < termFrequencySize; ++j) {
					String partFrequency[] = reader.readLine().split("\t");
					int documentId = Integer.parseInt(partFrequency[0]);
					int countTerm = Integer.parseInt(partFrequency[1]);

					Map<Integer, TermFrequency> termFrequencyMap = engine.getDocumentListByTermId(termId);
					if (termFrequencyMap.get(documentId) == null)
						termFrequencyMap.put(documentId, new TermFrequency(termId, countTerm, documentId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		engine.calculateTermFrequency();

		long timeEnd = System.currentTimeMillis();
		log("Load index time spent: " + (timeEnd - timeStart) + "ms.");
	}

	private static void indexedFileCommand(String inputDirectory) {
		log("Start indexing *.txt files...");
		Engine engine = Engine.getInstance();
		engine.clear();

		long timeStart = System.currentTimeMillis();
		int documentId = 1;
		for (File file : FileUtil.getFileList(new File(inputDirectory), ".txt", true)) {
			String fileName = file.getName();
			long fileLength = file.length();
			long crc32 = FileUtil.getFileCRC32(file);
			if (fileLength <= 0 || crc32 == -1) {
				log("Incorrect file: " + fileName);
				continue;
			}

			log("Parse file: " + fileName);

			Document document = new Document(documentId, fileName, fileLength, crc32);
			new DocumentReader(file, document);

			engine.addDocument(documentId, document);
			++documentId;
		}

		engine.calculateTermFrequency();
		engine.sortDocumentInIndexByFrequency();

		long timeEnd = System.currentTimeMillis();

		log("Indexing time: " + (timeEnd - timeStart) + "ms.");
	}

	private static void saveIndexToFile(String file) {
		Engine engine = Engine.getInstance();

		long timeStart = System.currentTimeMillis();
		try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(file)), StandardCharsets.UTF_8))) {
			writer.write(engine.getDocumentMap().size() + "\r\n");
			for (Document document : engine.getDocumentMap().values()) {
				writer.write(document.getId() + "\t" + document.getName() + "\t" + document.getTermCount() + "\t" + document.getLength() + "\t" + document.getCRC32() + "\r\n");
			}

			writer.write(engine.getTermMap().size() + "\r\n");
			for (Map.Entry<String, Integer> term : engine.getTermMap().entrySet()) {
				writer.write(term.getKey() + "\t" + term.getValue() + "\r\n");
			}

			writer.write(engine.getTermDocumentMap().size() + "\r\n");
			for (Map.Entry<Integer, Map<Integer, TermFrequency>> term : engine.getTermDocumentMap().entrySet()) {
				writer.write(term.getKey() + "\t" + term.getValue().size() + "\r\n");
				for (Map.Entry<Integer, TermFrequency> document : term.getValue().entrySet()) {
					writer.write(document.getValue().getDocumentId() + "\t" + document.getValue().getCount() + "\r\n");
				}
			}
		}
		catch (Exception e) {
			log("Error while save index to file.");
			e.printStackTrace();
		}

		long timeEnd = System.currentTimeMillis();

		log("Save index to file time: " + (timeEnd - timeStart) + "ms.");
	}

	private static void searchInIndexCommand(String query) {
		long timeStart = System.nanoTime();

		Engine engine = Engine.getInstance();

		Map<Integer, Result> resultMap = new LinkedHashMap<>();

		Matcher matcher = TOKEN_PATTERN.matcher(query);
		while (matcher.find()) {
			String term = matcher.group(1).toLowerCase();
			int termId = engine.getTermId(term);
			if (termId == -1)
				continue;

			Map<Integer, TermFrequency> termFrequencyMap = engine.getDocumentListByTermId(termId);
			if (termFrequencyMap == null)
				continue;

			for (Map.Entry<Integer, TermFrequency> fileIndex : termFrequencyMap.entrySet()) {
				TermFrequency termFrequency = fileIndex.getValue();

				if (resultMap.get(fileIndex.getKey()) == null)
					resultMap.put(fileIndex.getKey(), new Result(termFrequency.getFrequency()));

				resultMap.get(fileIndex.getKey()).addFrequency(termFrequency.getFrequency());
			}
		}

		resultMap = ArrayUtil.sort(resultMap, TERM_FREQUENCY_COMPARATOR);

		long timeEnd = System.nanoTime();

		if (resultMap.isEmpty()) {
			log("Documents by query \"" + query + "\" does not found.");
			return;
		}

		for (Map.Entry<Integer, Result> result : resultMap.entrySet())
			log("Score: [" + DECIMAL_FORMAT.format(result.getValue().getFrequency()) + "]\tname: [" + engine.getDocument(result.getKey()).getName() + "]");

		log("Time spent: " + (timeEnd - timeStart) / 1000 + "µs, found: " + resultMap.size() + " documents.");
	}

	private static void log(Object msg) {
		System.out.println(msg);
	}
}
