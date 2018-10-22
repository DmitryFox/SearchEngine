package search;

import search.core.Engine;
import search.data.DocumentReader;
import search.holder.Document;
import search.holder.TermFrequency;
import search.utils.FileUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Dmitry [ ______ ]
 * @version 07.10.2018 19:31
 */
public class Main {
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

	private static void loadIndexedFileCommand(String directory) {

	}

	private static void indexedFileCommand(String inputDirectory) {
		log("Start indexing *.txt files...");
		Engine engine = Engine.getInstance();

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

	private static void searchInIndexCommand(String query) {
		Engine engine = Engine.getInstance();

		int termId = engine.getTermId(query);
		if (termId != -1) {
			long timeStart = System.nanoTime();
			Map<Integer, TermFrequency> termFrequency = engine.getTermDocumentMap(termId);
			long timeEnd = System.nanoTime();

			int countDocument = 0;
			if (termFrequency != null) {
				for (Map.Entry<Integer, TermFrequency> fileIndex : termFrequency.entrySet()) {
					log("Score: [" + DECIMAL_FORMAT.format(fileIndex.getValue().getFrequency()) + "]\tname: [" + engine.getDocument(fileIndex.getValue().getDocumentId()).getName() + "]");
					++countDocument;
				}
			}

			log("Time spent: " + (timeEnd - timeStart) / 1000 + "µs, found: " + countDocument + " documents.");
		} else {
			log("Term \"" + query + "\" does not found.");
		}
	}

	private static void log(Object msg) {
		System.out.println(msg);
	}
}
