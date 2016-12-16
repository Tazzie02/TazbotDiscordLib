package com.tazzie02.tazbotdiscordlib.filehandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tazzie02.tazbotdiscordlib.CommandOverrides;
import com.tazzie02.tazbotdiscordlib.CommandSettings;
import com.tazzie02.tazbotdiscordlib.CommandSettingsGuild;
import com.tazzie02.tazbotdiscordlib.util.FileUtil;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;

public class LocalFiles implements CommandSettings, CommandSettingsGuild {
	
	private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static Map<JDA, LocalFiles> instances = new HashMap<>();
	private Path rootPath;
	private Path dataPath;
	private final String DATA_DIRECTORY_NAME = "data";
	private final String LOG_DIRECTORY_NAME = "logs";
	private Config config;
	private Map<Guild, CommandSettingsImpl> settings = new HashMap<>();;
	private final String CONFIG_FILE_NAME = "config.json";
	private final String SETTINGS_FILE_NAME = "command-settings.json";
	private final String DEFAULT_PREFIX = "!";
	
	private LocalFiles(JDA jda, Path path) {
		rootPath = path;
		dataPath = path.resolve(DATA_DIRECTORY_NAME);
	}
	
	public static LocalFiles getInstance(JDA jda) {
		return instances.get(jda);
	}
	
	public static LocalFiles addInstance(JDA jda, Path path) {
		if (path == null) {
			instances.remove(jda);
			return null;
		}
		
		LocalFiles files = new LocalFiles(jda, path);
		instances.put(jda, files);
		return files;
	}
	
	public Config getConfig() {
		if (config == null) {
			config = defaultConfig();
			saveConfig();
		}
		return config;
	}
	
	public boolean loadConfig() {
		try {
			Path file = getConfigFile();
			if (!Files.exists(file)) {
				saveConfig();
			}
			else {
				BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
				this.config = GSON.fromJson(reader, Config.class);
				reader.close();
			}
			System.out.println("Configuration loaded.");
			return true;
		} catch (IOException e) {
			System.out.println("Error loading configuration.");
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean saveConfig() {
		if (config == null) {
			config = defaultConfig();
		}
		String json = GSON.toJson(config);
		try {
			Path file = getConfigFile();
			if (!Files.exists(file)) {
				FileUtil.createFileAndPath(file);
			}
			FileUtil.writeToFile(json, file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private Config defaultConfig() {
		Config config = new Config();
		config.setPrefix(DEFAULT_PREFIX);
		return config;
	}
	
	private Path getConfigFile() throws IOException {
		Path file = rootPath.resolve(CONFIG_FILE_NAME);
		return file;
	}
	
	@Override
	public Set<String> getModerators() {
		return config.getModerators();
	}

	@Override
	public String getPrefix() {
		return config.getPrefix();
	}

	@Override
	public CommandOverrides getCommandOverrides() {
		return config.getCommandOverrides();
	}
	
	public CommandSettingsImpl getCommandSettings(Guild guild) {
		CommandSettingsImpl settings = this.settings.get(guild);
		if (settings == null) {
			settings = defaultCommandSettings();
			this.settings.put(guild, settings);
			saveCommandSettings(guild);
		}
		return settings;
	}
	
	public boolean loadCommandSettings(Guild guild) {
		try {
			Path file = getCommandSettingsFile(guild);
			if (!Files.exists(file)) {
				saveCommandSettings(guild);
			}
			else {
				BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
				this.settings.put(guild, GSON.fromJson(reader, CommandSettingsImpl.class));
				reader.close();
			}
			System.out.println("Command settings loaded for " + (guild != null ? guild.getId() : "private") + ".");
			return true;
		} catch (IOException e) {
			System.out.println("Error loading command settings for " + (guild != null ? guild.getId() : "private") + ".");
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean saveCommandSettings(Guild guild) {
		CommandSettingsImpl settings = this.settings.get(guild);
		if (settings == null) {
			settings = defaultCommandSettings();
			this.settings.put(guild, settings);
		}
		String json = GSON.toJson(settings);
		try {
			Path file = getCommandSettingsFile(guild);
			if (!Files.exists(file)) {
				FileUtil.createFileAndPath(file);
			}
			FileUtil.writeToFile(json, file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private CommandSettingsImpl defaultCommandSettings() {
		CommandSettingsImpl settings = new CommandSettingsImpl();
		return settings;
	}
	
	private Path getCommandSettingsFile(Guild guild) throws IOException {
		Path file = getGuildDirectory(guild).resolve(SETTINGS_FILE_NAME);
		return file;
	}
	
	@Override
	public Set<String> getModerators(Guild guild) {
		CommandSettingsImpl settings = this.settings.get(guild);
		if (settings == null) {
			loadCommandSettings(guild);
			settings = this.settings.get(guild);
		}
		return settings.getModerators();
	}

	@Override
	public String getPrefix(Guild guild) {
		CommandSettingsImpl settings = this.settings.get(guild);
		if (settings == null) {
			loadCommandSettings(guild);
			settings = this.settings.get(guild);
		}
		return settings.getPrefix();
	}

	@Override
	public CommandOverrides getCommandOverrides(Guild guild) {
		CommandSettingsImpl settings = this.settings.get(guild);
		if (settings == null) {
			loadCommandSettings(guild);
			settings = this.settings.get(guild);
		}
		return settings.getCommandOverrides();
	}
	
	public Path getLogPath(Guild guild) throws IOException {
		Path path = getGuildDirectory(guild).resolve(LOG_DIRECTORY_NAME);
		if (!Files.isDirectory(path)) {
			Files.createDirectories(path);
		}
		return path;
	}
	
	private Path getGuildDirectory(Guild guild) throws IOException {
		final String PRIVATE_DIRECTORY_NAME = "private";
		final String SEPARATOR = "-";
		
		String directoryName;
		if (guild == null) {
			directoryName = PRIVATE_DIRECTORY_NAME;
		}
		else {
			String guildName = guild.getName();
			String guildId = guild.getId();
			
			directoryName = String.format("%s%s%s", guildName.replaceAll("[^a-zA-Z0-9.\\-_]", SEPARATOR), SEPARATOR, guildId);
			
			// Guild names can change, decide on directory from guild id
			Path[] paths = Files.list(dataPath).toArray(length -> new Path[length]);
			for (Path path : paths) {
				String pathName = path.getFileName().toString();
				if (pathName.endsWith(SEPARATOR + guildId) && !pathName.equals(directoryName)) {
					Path destinationPath = dataPath.resolve(directoryName);
					if (!Files.isDirectory(destinationPath)) {
						Files.move(path, destinationPath);
					}
					else {
						// Hopefully the directory doesn't exist
					}
				}
			}
		}
		return dataPath.resolve(directoryName);
	}
	
}
