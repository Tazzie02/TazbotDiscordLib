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

/**
 * Reads all text messages sent through Discord and triggers a {@link Command Command}'s
 * {@link Command#onCommand(MessageReceivedEvent, String[]) onCommand()} method if the
 * message starts with the correct prefix followed by an {@link Command#getAliases() alias}.
 */
public class CommandRegistry extends ListenerAdapter {
	
	private final boolean ALLOW_MENTION_PREFIX = true;
	private final String EMPTY_PREFIX = "";
	private List<Command> commands;
	private Owners owners;
	private CommandSettings defaultSettings;
	private CommandSettingsGuild guildSettings;
	private MessageReceivedLogger messageReceivedLogger;
	private boolean caseSensitive = false;
	
	public CommandRegistry() {
		commands = new ArrayList<>();
	}
	
	/**
	 * Set the Owners. The Owners will have full access to Commands.
	 * 
	 * @param owners
	 *        Owners to set.
	 */
	public void setOwners(Owners owners) {
		this.owners = owners;
	}
	
	/**
	 * Set the default CommandSettings. These are the base settings which exist for
	 * all guilds, but are overwritten if guild specific settings exist.
	 * 
	 * @param defaultSettings
	 *        The default CommandSettings to set.
	 */
	public void setDefaultCommandSettings(CommandSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
	}
	
	/**
	 * Set the guild specific CommandSettings. If settings exist for a guild, they
	 * will overwrite the default settings.
	 * 
	 * @param guildSettings
	 *        The CommandSettingsGuild to set.
	 */
	public void setGuildCommandSettings(CommandSettingsGuild guildSettings) {
		this.guildSettings = guildSettings;
	}
	
	/**
	 * Set a logger for messages received.
	 * 
	 * @param messageReceivedLogger
	 *        The MessageReceivedLogger to set.
	 */
	public void setMessageReceivedLogger(MessageReceivedLogger messageReceivedLogger) {
		this.messageReceivedLogger = messageReceivedLogger;
	}
	
	/**
	 * Set whether command aliases are case sensitive or case insensitive.
	 * Default false (insensitive).
	 * 
	 * @param caseSensitive
	 *        Set true for case sensitive.
	 */
	public void setCaseSensitiveCommands(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	/**
	 * Register a Command to the CommandRegistry.
	 * 
	 * @param  command
	 *         The Command to register.
	 * @return This CommandRegistry instance. Used for chaining.
	 * @throws UnsupportedOperationException
	 *         {@link CommandInformation} has one or more invalid fields.
	 *         The command will not be registered. Check the error message
	 *         for more information.
	 */
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
	
	/**
	 * Unregister a Command from the CommandRegistry.
	 * 
	 * @param  command
	 *         The Command to unregister.
	 * @return True if the Command was unregistered. May return false if the
	 *         Command was not registered.
	 */
	public boolean unregisterCommand(Command command) {
		if (command == null) {
			throw new NullPointerException();
		}
		
		return commands.remove(command);
	}
	
	/**
	 * Unregister a Command by one of it's aliases from the CommandRegistry.
	 * 
	 * @param  alias
	 *         An alias of the Command to unregister.
	 * @return True if the Command was unregistered. May return false if the
	 *         Command was not registered, or the alias did not match one of
	 *         the aliases of a registered Command.
	 */
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
	
	/**
	 * Get an unmodifiable list of the registered Commands.
	 * 
	 * @return An unmodifiable list of the registered Commands.
	 */
	public List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Ignore messages from self
		if (e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
			return;
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
	
	/**
	 * Split a String into an array of arguments using a space as a delimiter.
	 * The String is first trimmed removing leading and trailing whitespace.
	 * If the String is empty, or only contains whitespace, an empty array is
	 * returned.
	 * 
	 * @param  s
	 *         The String to split into an array of arguments.
	 * @return A String array containing the arguments of the String s. Possibly
	 *         empty if s is empty or only contains whitespace.
	 */
	protected String[] splitIntoArgs(String s) {
		String[] args = s.trim().split(" ");
		if (args[0].equals("")) {
			args = new String[0];
		}
		return args;
	}
	
	/**
	 * Get the prefix for the Guild. The first not-null prefix is used: 
	 * GuildSettings > DefaultSettings > EMPTY_PREFIX
	 * 
	 * @param  guild
	 *         The Guild to get a prefix for.
	 * @return The prefix for the Guild.
	 */
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
	
	/**
	 * Get the prefix for the Command in Guild. The first non-null prefix is used: 
	 * GuildOverrideCommand > GuildSettings > DefaultOverrideCommand > DefaultSettings > EMPTY_PREFIX
	 * 
	 * @param  guild
	 *         The Guild to get a prefix for Command.
	 * @param  command
	 *         The Command to get a prefix for.
	 * @return The prefix for the Command in Guild.
	 */
	protected String getPrefix(Guild guild, Command command) {
		String prefix = null;
		
		if (guildSettings != null) {
			if (guildSettings.getCommandOverrides(guild) != null) {
				if (guildSettings.getCommandOverrides(guild).getOverridePrefix(command) != null) {
					prefix = guildSettings.getCommandOverrides(guild).getOverridePrefix(command);
				}
			}
			if (prefix == null) {
				prefix = guildSettings.getPrefix(guild); 
			}
		}
		
		if (defaultSettings != null) {
			if (defaultSettings.getCommandOverrides() != null) {
				if (defaultSettings.getCommandOverrides().getOverridePrefix(command) != null) {
					prefix = defaultSettings.getCommandOverrides().getOverridePrefix(command);
				}
			}
			if (prefix == null) {
				prefix = defaultSettings.getPrefix();
			}
		}
		
		if (prefix == null) {
			prefix = EMPTY_PREFIX;
		}
		
		return prefix;
	}
	
	/**
	 * Check if an override exists for a Command. This is not necessarily a
	 * Guild override, it could be a default override.
	 * 
	 * @param  guild
	 *         The Guild to check for Command.
	 * @param  command
	 *         The Command to check for override.
	 * @return True if an override exists for Command.
	 */
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
	
	/**
	 * Check whether a String starts with a mention. A mention in String form
	 * looks like <@id> where id is replaced with an 18 digit snowflake.
	 * @param  text
	 *         The String to find the mention.
	 * @param  user
	 *         The User to find a mention for.
	 * @return True if the String starts with the User mention.
	 */
	protected boolean startsWithMention(String text, User user) {
		if (text.startsWith("<@" + user.getId() + ">")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return a String with the User's mention removed from the start of String.
	 * 
	 * @param  text
	 *         The String to remove the mention from.
	 * @param  user
	 *         The User who's mention should be removed.
	 * @return A String without User's mention at the start. Possibly the same
	 *         String from the text parameter if it did not start with User's mention.
	 */
	protected String removeStartMention(String text, User user) {
		if (startsWithMention(text, user)) {
			int length = 3 + user.getId().length(); // <@ + id + >
			text = text.substring(length);
			if (text.startsWith(" ")) {
				text = text.substring(1);
			}
		}
		return text;
	}
	
	/**
	 * Return a Command if the String starts with one of it's aliases, or null
	 * if a registered Command does have any aliases that match the start of String.
	 * 
	 * @param  text
	 *         The String to find an alias at the start of.
	 * @return The Command containing an alias that the String starts with.
	 *         Returns null if no such Command exists.
	 */
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
	
	/**
	 * Return a String that has the longest alias in the list of aliases removed
	 * from the start of the String. Only removes the alias if the String and the
	 * alias are the same length, or the alias is followed by a space. Returns
	 * null if the String was not changed.
	 * 
	 * @param  text
	 *         String to remove the alias from the start of.
	 * @param  aliases
	 *         List of Strings containing aliases to match and remove from the String.
	 * @return The String with an alias from the List removed. If the String did
	 *         not start with an alias in the List, returns null.
	 */
	protected String removeAlias(String text, List<String> aliases) {
		String longestAlias = "";
		for (String alias : aliases) {
			if (text.startsWith(alias) && alias.length() > longestAlias.length()) {
				// Enforce text to only be alias or a space to directly follow alias
				if (text.length() == alias.length()) {
					longestAlias = alias;
					break;
				}
				else if (text.charAt(alias.length()) == ' ') {
					longestAlias = alias;
				}
			}
		}
		if (!longestAlias.isEmpty()) {
			return text.substring(longestAlias.length());
		}
		return null;
	}
	
	/**
	 * 
	 * 
	 * @param guild
	 * @param messageBuilder
	 * @return
	 */
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
