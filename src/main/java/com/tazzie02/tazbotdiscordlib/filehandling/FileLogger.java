package com.tazzie02.tazbotdiscordlib.filehandling;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;

public class FileLogger {
	
	private final static String NAME_FORMAT = "yyyyMMdd";
	private final static String EXTENSION = "log";
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat(NAME_FORMAT);
	private final static String TIME_FORMAT = "HH:mm:ss.SSS";
	private final static SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
	
	{
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public static void log(String s, Guild guild, JDA jda) throws IOException {
		s = String.format("%s %s\n", getTime(), s);
		
		Path file = getLogFile(jda, guild);
		Files.write(file, s.getBytes(), StandardOpenOption.APPEND);
	}
	
	private static Path getLogFile(JDA jda, Guild guild) throws IOException {
		Path path = LocalFiles.getInstance(jda).getLogPath(guild);
		Path file =  path.resolve(getLogFileName());
		
		if (!Files.exists(file)) {
			Files.createFile(file);
		}
		
		return file;
	}
	
	private static String getLogFileName() {
		return dateFormat.format(new Date()) + "." + EXTENSION;
	}
	
	private static String getTime() {
		return timeFormat.format(new Date());
	}

}
