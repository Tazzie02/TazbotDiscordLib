package com.tazzie02.tazbotdiscordlib.commands;

import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.CommandInformation;
import com.tazzie02.tazbotdiscordlib.CommandRegistry;
import com.tazzie02.tazbotdiscordlib.SendMessage;

public class HelpCommand implements Command {
	
	private CommandRegistry commandRegistry;
	
	public HelpCommand(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if(!e.isPrivate()) {
			SendMessage.sendMessage(e, new MessageBuilder()
					.appendMention(e.getAuthor())
					.appendString(": Sending help information as a private message.")
					.build());
		}
		System.out.println("help length: " + args.length);
		if (args.length == 0) {
			sendHelp(e);
		}
		else {
			sendHelp(e, args);
		}
	}
	
	private void sendHelp(MessageReceivedEvent e) {
		User author = e.getAuthor();
		Guild guild = e.getGuild();
		List<Command> commands = commandRegistry.getCommands();

		StringBuilder all = new StringBuilder();
		StringBuilder mod = new StringBuilder();
		StringBuilder owner = new StringBuilder();
		
		for (Command c : commands) {
			String description = c.getDescription();
			if (description == null || description.isEmpty()) {
				description = "This command does not have a description.";
			}
			
			String s = "**" + c.getAliases().get(0) + "** - " + description;
			
			if (c.isHidden()) {
				if (commandRegistry.isOwner(author, guild)) {
					s += " *(hidden)*";
				}
				else {
					continue;
				}
			}
			
			s += "\n";
			
			CommandAccess access = c.getAccess();
			if (access.equals(CommandAccess.MODERATOR)) {
				// If moderator or owner
				if (commandRegistry.isModerator(author, guild) || commandRegistry.isOwner(author, guild)) {
					mod.append(s);
				}
			}
			else if (access.equals(CommandAccess.OWNER)) {
				// If developer
				if (commandRegistry.isOwner(author, guild)) {
					owner.append(s);
				}
			}
			else {
				all.append(s);
			}
		}
		
		StringBuilder sb = new StringBuilder();
		if (all.length() != 0) {
			sb.append("*General Commands:*\n")
			.append(all.toString());
		}
		if (mod.length() != 0) {
			sb.append("\n*Moderator Commands:*\n")
			.append(mod.toString());
		}
		if (owner.length() != 0) {
			sb.append("\n*Owner Commands:*\n")
			.append(owner.toString());
		}
		
		SendMessage.sendPrivate(e, "Get more information for a command with \"help <command>\".\n"
				+ sb.toString());
	}
	
	private void sendHelp(MessageReceivedEvent e, String[] args) {
		User author = e.getAuthor();
		Guild guild = e.getGuild();
		String commandString = String.join(" ", args);
		CommandInformation command = commandRegistry.findCommandInformation(commandString);
		
		if (command == null) {
			SendMessage.sendMessage(e, "Error: '**" + commandString + "**' does not exist. Use help to list all commands.");
			return;
		}
		
		CommandAccess access = command.getAccess();
		
		// If moderator command AND user is not moderator
		if (access.equals(CommandAccess.MODERATOR) && !commandRegistry.isModerator(author, guild)) {
			return;
		}
		// If owner command AND user is not owner
		else if (access.equals(CommandAccess.OWNER) && !commandRegistry.isOwner(author, guild)) {
			return;
		}
		
		String name = command.getName();
		if (name == null || name.isEmpty()) {
			name = "This command does not have a name.";
		}
		String description = command.getDescription();
		if (description == null || description.isEmpty()) {
			description = "This command does not have a description.";
		}
		String details = command.getDetails();
		if (details == null || details.isEmpty()) {
			details = "This command does not have any details.";
		}
		
		SendMessage.sendPrivate(e, "**Name:** " + name + "\n"
				+ "**Description:** " + description + "\n"
				+ "**Aliases:** " + StringUtils.join(command.getAliases(), ", ") + "\n"
				+ "**Details:** " + details);
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("help", "command", "commands");
	}
	
	@Override
	public String getDescription() {
		return "Display help information for commands.";
	}
	
	@Override
	public String getName() {
		return "Help Command";
	}
	
	@Override
	public String getDetails() {
		return "help - Print a list of available commands with short descriptions.\n"
				+ "help <command> - Print detailed information about <command>.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
}
