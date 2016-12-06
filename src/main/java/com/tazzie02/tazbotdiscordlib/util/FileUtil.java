package com.tazzie02.tazbotdiscordlib.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
	
	public static Path createFileAndPath(Path file) throws IOException {
		if (Files.exists(file)) {
			return file;
		}
		
		// Does nothing if already exists
		Files.createDirectories(file.toAbsolutePath().getParent());
		
		Files.createFile(file);
		return file;
	}
	
	public static void writeToFile(String s, Path file) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
		writer.append(s);
		writer.close();
	}
	
}
