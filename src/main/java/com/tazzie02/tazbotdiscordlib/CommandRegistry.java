package com.tazzie02.tazbotdiscordlib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.tazzie02.tazbotdiscordlib.CommandInformation.CommandAccess;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandRegistry extends ListenerAdapter {
	
	private final boolean ALLOW_MENTION_PREFIX = true;
	private final String EMPTY_PREFIX = "";
	private List<Command> commands;
	private Owners owners;
	private CommandSettings defaultSettings;
	private CommandSettingsGuild guildSettings;
	private MessageReceivedLogger messageReceivedLogger;
	private boolean caseSensitive = false;
	private boolean ignoreMessagesFromSelf = true;
	
	public CommandRegistry() {
		commands = new ArrayList<>();
	}
	
	public void setOwners(Owners owners) {
		this.owners = owners;
	}
	
	public void setDefaultCommandSettings(CommandSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}
	
	public void setGuildCommandSettings(CommandSettingsGuild guildSettings) {
		this.guildSettings = guildSettings;
	}
	
	public void setMessageReceivedLogger(MessageReceivedLogger messageReceivedLogger) {
		this.messageReceivedLogger = messageReceivedLogger;
	}
	
	public void setCaseSensitiveCommands(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	// Default to true
	// If false, messages from self will show as messages received meaning messages could appear twice if sent is logged.
	public void setIgnoreMessagesFromSelf(boolean ignoreMessagesFromSelf) {
		this.ignoreMessagesFromSelf = ignoreMessagesFromSelf;
	}
	
	public CommandRegistry registerCommand(Command command) {
		if (command == null) {
			 throw new NullPointerException("Command must not be null.");
		}
		if (command.getAliases() == null) {
			throw new UnsupportedOperationException("Command aliases must not be null.");
		}
		if (command.getAliases().isEmpty()) {
			throw new UnsupportedOperationException("Command aliases must not be empty.");
		}
		if (command.getAliases().contains(null)) {
			throw new UnsupportedOperationException("Command aliases must not contain null.");
		}
		if (command.getAccess() == null) {
			throw new UnsupportedOperationException("Command access must not be null.");
		}
		
		commands.add(command);
		return this;
	}
	
	public boolean unregisterCommand(Command command) {
		if (command == null) {
			throw new NullPointerException();
		}
		
		return commands.remove(command);
	}
	
	public boolean unregisterCommand(String alias) {
		if (alias == null) {
			throw new NullPointerException();
		}
		
		Command command = findCommand(alias);
		if (command != null) {
			return unregisterCommand(command);
		}
		return false;
	}
	
	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Ignore messages from self
		if (e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
			if (ignoreMessagesFromSelf) {
				return;
			}
		}
		
		logMessageReceived(e);
		
		String message = e.getMessage().getRawContent();
		
		if (ALLOW_MENTION_PREFIX) {
			// If first argument is mention
			message = removeStartMention(message, e.getJDA().getSelfUser());
			// Message the contains only bot mention should be handled elsewhere
			if (message.length() == 0) {
				return;
			}
		}
		
		Guild guild = !e.isFromType(ChannelType.PRIVATE) ? e.getGuild() : null;
		Command command = null;
		
		StringBuilder messageBuilder = new StringBuilder(message);
		command = getAsOverrideCommand(guild, messageBuilder);
		
		if (command == null) {
			command = getAsCommand(guild, messageBuilder);
		}
		
		if (command == null || !hasAccess(guild, command, e.getAuthor())) {
			logMessageReceivedNotCommand(e);
			return;
		}
		
		logMessageReceivedCommand(e);
		
		// Send to command
		command.onCommand(e, splitIntoArgs(messageBuilder.toString()));
	}
	
	protected String[] splitIntoArgs(String s) {
		String[] args = s.trim().split(" ");
		if (args[0].equals("")) {
			args = new String[0];
		}
		return args;
	}
	
	protected String getPrefix(Guild guild) {
		String prefix = null;
		if (guildSettings != null) {
			prefix = guildSettings.getPrefix(guild);
		}
		if (prefix == null) {
			if (defaultSettings != null) {
				prefix = defaultSettings.getPrefix();
			}
		}
		if (prefix == null) {
			prefix = EMPTY_PREFIX;
		}
		return prefix;
	}
	
	protected String getPrefix(Guild guild, Command command) {
		String prefix = null;
		if (guildSettings != null) {
			if (guildSettings.getCommandOverrides(guild) != null) {
				prefix = guildSettings.getCommandOverrides(guild).getOverridePrefix(command);
			}
		}
		if (prefix == null) {
			if (defaultSettings != null) {
				if (defaultSettings.getCommandOverrides() != null) {
					prefix = defaultSettings.getCommandOverrides().getOverridePrefix(command);
				}
			}
		}
		if (prefix == null) {
			prefix = getPrefix(guild);
		}
		return prefix;
	}
	
	protected boolean overrideExists(Guild guild, Command command) {
		if (guildSettings != null) {
			if (guildSettings.getCommandOverrides(guild) != null) {
				if (guildSettings.getCommandOverrides(guild).getOverridePrefix(command) != null) {
					return true;
				}
			}
		}
		if (defaultSettings != null) {
			if (defaultSettings.getCommandOverrides() != null) {
				if (defaultSettings.getCommandOverrides().getOverridePrefix(command) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean startsWithMention(String text, User user) {
		if (text.startsWith("<@" + user.getId() + ">")) {
			return true;
		}
		return false;
	}
	
	protected String removeStartMention(String text, User user) {
		if (startsWithMention(text, user)) {
			int length = 3 + user.getId().length(); // <@ + id + >
//			if (text.length() == length) {
//				return "";
//			}
			text = text.substring(length);
			if (text.startsWith(" ")) {
				text = text.substring(1);
			}
		}
		return text;
	}
	
	protected Command startsWithAlias(String text) {
		for (Command command : commands) {
			for (String alias : command.getAliases()) {
				if (text.startsWith(alias)) {
					return command;
				}
			}
		}
		return null;
	}
	
	protected String removeAlias(String text, List<String> aliases) {
		String longestAlias = "";
		for (String alias : aliases) {
			if (text.startsWith(alias) && alias.length() > longestAlias.length()) {
				// Enforce text to only be alias or a space to directly follow alias
				if (text.length() == alias.length() || text.charAt(alias.length()) == ' ') {
					// TODO Break when text.length == alias.length
					longestAlias = alias;
				}
			}
		}
		if (!longestAlias.isEmpty()) {
			return text.substring(longestAlias.length());
		}
		return null;
	}
	
	protected Command getAsOverrideCommand(Guild guild, StringBuilder messageBuilder) {
		if (guildSettings == null) {
			return null;
		}
		CommandOverrides overrides = guildSettings.getCommandOverrides(guild);
		if (overrides == null) {
			return null;
		}
		
		String message = messageBuilder.toString();
		for (String prefix : overrides.getAllPrefixes()) {
			if (message.startsWith(prefix) && message.length() != prefix.length()) {
				String messageNoPrefix = message.substring(prefix.length());
				
				for (String alias : overrides.getAliasesWithPrefix(prefix)) {
					Command command = findCommand(alias);
					String tempMessage = removeAlias(messageNoPrefix, command.getAliases());
					if (tempMessage != null) {
						messageBuilder.replace(0, messageBuilder.length(), tempMessage);
						return command;
					}
				}
			}
		}
		
		return null;
	}
	
	protected Command getAsCommand(Guild guild, StringBuilder messageBuilder) {
		String prefix = getPrefix(guild);
		
		String message = messageBuilder.toString();
		if (message.startsWith(prefix) && message.length() != prefix.length()) {
			String messageNoPrefix = message.substring(prefix.length());
			
			for (Command command : commands) {
				if (!overrideExists(guild, command)) {
					String tempMessage = removeAlias(messageNoPrefix, command.getAliases());
					if (tempMessage != null) {
						messageBuilder.replace(0, messageBuilder.length(), tempMessage);
						return command;
					}
				}
			}
		}
		return null;
	}
	
	protected String[] commandArgs(String message) {
		return message.split(" ");
	}
	
	protected Command findCommand(String alias) {
		if (alias == null) {
			throw new NullPointerException();
		}
		
		Optional<Command> found = null;
		if (caseSensitive) {
			found = commands.parallelStream()
					.filter(c -> c.getAliases().stream()
							.anyMatch(a -> a.equals(alias)))
					.findFirst();
		}
		else {
			found = commands.parallelStream()
					.filter(c -> c.getAliases().stream()
							.anyMatch(a -> a.equalsIgnoreCase(alias)))
					.findFirst();
		}
		
		if (found.isPresent()) {
			return found.get();
		}
		return null;
	}
	
	protected boolean hasAccess(Guild guild, Command command, User user) {
		CommandAccess access = command.getAccess();
		
		if (access.equals(CommandAccess.MODERATOR)) {
			if (isModerator(user, guild) || isOwner(user, guild)) {
				return true;
			}
		}
		else if (access.equals(CommandAccess.OWNER)) {
			if (isOwner(user, guild)) {
				return true;
			}
		}
		else {
			return true;
		}
		
		return false;
	}
	
	protected void logMessageReceived(MessageReceivedEvent e) {
		if (messageReceivedLogger != null) {
			messageReceivedLogger.messageReceived(e);
		}
	}
	
	protected void logMessageReceivedNotCommand(MessageReceivedEvent e) {
		if (messageReceivedLogger != null) {
			messageReceivedLogger.messageReceivedNotCommand(e);
		}
	}
	
	protected void logMessageReceivedCommand(MessageReceivedEvent e) {
		if (messageReceivedLogger != null) {
			messageReceivedLogger.messageReceivedCommand(e);
		}
	}
	
	// TODO Shouldn't be in CommandRegistry
	public boolean isModerator(User user, Guild guild) {
		String id = user.getId();
		
		if (guildSettings != null) {
			if (guildSettings.getModerators(guild) != null) {
				if (guildSettings.getModerators(guild).contains(id)) {
					return true;
				}
			}
		}
		if (defaultSettings != null) {
			if (defaultSettings.getModerators() != null) {
				if (defaultSettings.getModerators().contains(id)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// TODO Shouldn't be in CommandRegistry
	public boolean isOwner(User user, Guild guild) {
		if (owners != null) {
			if (owners.getOwners() != null) {
				if (owners.getOwners().contains(user.getId())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public CommandInformation findCommandInformation(String alias) {
		return findCommand(alias);
	}
	
}
