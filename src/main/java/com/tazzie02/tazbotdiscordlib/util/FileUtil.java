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

	// Some file lock issue is preventing folders from being deleted properly
//	public static void moveFilesFromDirectory(Path srcDir, Path destDir) throws IOException {
//		System.out.println("START " + srcDir.toAbsolutePath() + " to " + destDir.toAbsolutePath());
//		if (Files.exists(destDir) && !Files.isDirectory(destDir)) {
//			return;
//		}
//		if (!Files.isDirectory(destDir)) {
//			if (Files.exists(destDir)) {
//				return;
//			}
//			Files.createDirectories(destDir);
//		}
//		
//		for (Path p : Files.list(srcDir).toArray(length -> new Path[length])) {
//			if (Files.isDirectory(p)) {
//				moveFilesFromDirectory(p, destDir.resolve(p.getFileName()));
//			}
//			else {
//				String fileString = p.getFileName().toString();
//				int lastIndex = fileString.lastIndexOf('.');
//				String fileName = lastIndex != -1 ? fileString.substring(0, lastIndex) : fileString;
//				String fileExt = lastIndex != -1 ? fileString.substring(lastIndex, fileString.length()) : null;
//				String unique = "";
//				
//				int i = 0;
//				while (Files.exists(destDir.resolve(fileName + unique + fileExt))) {
//					unique = " (" + i++ + ")"; 
//				}
//				
//				System.out.println("MOVING: " + p.toAbsolutePath() + " to " + destDir.resolve(fileName + unique + fileExt).toAbsolutePath());
//				Files.move(p, destDir.resolve(fileName + unique + fileExt));
//				System.out.println("MOVED: " + destDir.resolve(fileName + unique + fileExt).toAbsolutePath());
//			}
//		}
//		
//		Files.list(srcDir).forEach(p -> System.out.println("DIRECTORY STILL CONTAINS " + p.toAbsolutePath()));
//		if (Files.list(srcDir).count() == 0) {
//			System.out.println("DELETING " + srcDir.toAbsolutePath());
//			Files.delete(srcDir);
//		}
//		System.out.println("END");
//	}
	
}
