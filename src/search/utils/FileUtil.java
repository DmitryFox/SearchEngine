package search.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.CRC32C;

/**
 * @author Dmitry [ ______ ]
 * @version 07.10.2018 19:33
 */
public class FileUtil {
	private static final int CRC_BUFFER = 4096;

	/**
	 * The method returns the list of files in a directory.
	 *
	 * @param dir The directory.
	 * @param suffix The file name suffix.
	 * @param isRecursive Is include dirs.
	 * @return List of files.
	 */
	public static List<File> getFileList(File dir, String suffix, boolean isRecursive) {
		File[] files = dir.listFiles();
		if (files != null && files.length != 0) {
			List<File> fileList = new ArrayList<>(files.length);
			for (File file : files) {
				if (file.isDirectory() && isRecursive)
					fileList.addAll(getFileList(file, suffix, true));
				else if (file.isFile() && file.getName().endsWith(suffix))
					fileList.add(file);
			}

			return fileList;
		}

		return Collections.emptyList();
	}

	/**
	 * The method return CRC32 hash-number.
	 *
	 * @param file The file.
	 * @return CRC32 hash-number of file.
	 */
	public static long getFileCRC32(File file) {
		CRC32C crc = new CRC32C();
		byte[] data = new byte[CRC_BUFFER];
		try (FileInputStream fis = new FileInputStream(file)) {
			int bytesRead = fis.read(data);
			crc.update(data, 0, bytesRead);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}

		return crc.getValue();
	}
}
