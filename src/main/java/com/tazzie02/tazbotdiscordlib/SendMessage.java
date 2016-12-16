package com.tazzie02.tazbotdiscordlib;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class SendMessage {
	
	private static Map<JDA, MessageSender> messageSenders = new HashMap<>();
	
	// TODO Should only be used by TazbotDiscordLibImpl
	public static void addMessageSender(MessageSender messageSender, JDA jda) {
		messageSenders.put(jda, messageSender);
	}
	
	// TODO Should only be used by TazbotDiscordLibImpl
	public static void removeMessageSender(JDA jda) {
		messageSenders.remove(jda);
	}
	
	public static void sendMessage(MessageChannel c, Message message) {
		if (c == null) {
			throw new NullPointerException();
		}
		
		if (c instanceof TextChannel) {
			sendMessage((TextChannel) c, message);
			return;
		}
		else if (c instanceof PrivateChannel) {
			sendMessage((PrivateChannel) c, message);
			return;
		}
		
		TextChannel tc = c.getJDA().getTextChannelById(c.getId());
		if (tc != null) {
			sendMessage(tc, message);
			return;
		}
		
		PrivateChannel pc = c.getJDA().getPrivateChannelById(c.getId());
		if (pc != null) {
			sendMessage(pc, message);
			return;
		}
		
		throw new NullPointerException("This shouldn't happen...");
	}
	
	public static void sendMessage(MessageChannel c, String message) {
		if (c == null) {
			throw new NullPointerException();
		}
		
		if (c instanceof TextChannel) {
			sendMessage((TextChannel) c, message);
			return;
		}
		else if (c instanceof PrivateChannel) {
			sendMessage((PrivateChannel) c, message);
			return;
		}

		TextChannel tc = c.getJDA().getTextChannelById(c.getId());
		if (tc != null) {
			sendMessage(tc, message);
			return;
		}
		
		PrivateChannel pc = c.getJDA().getPrivateChannelById(c.getId());
		if (pc != null) {
			sendMessage(pc, message);
			return;
		}
		
		throw new NullPointerException("This shouldn't happen...");
	}
	
	public static void sendMessage(TextChannel c, Message message) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this TazbotDiscordLib.");
		}
		messageSender.sendMessage(c, message);
	}
	
	public static void sendMessage(TextChannel c, String message) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this TazbotDiscordLib.");
		}
		messageSender.sendMessage(c, message);
	}
	
	public static void sendMessage(MessageReceivedEvent e, Message message) {
		if (e == null) {
			throw new NullPointerException();
		}
		
		if (e.isPrivate()) {
			sendMessage(e.getPrivateChannel(), message);
		}
		else {
			sendMessage(e.getTextChannel(), message);
		}
	}
	
	public static void sendMessage(MessageReceivedEvent e, String message) {
		if (e == null) {
			throw new NullPointerException();
		}
		
		if (e.isPrivate()) {
			sendMessage(e.getPrivateChannel(), message);
		}
		else {
			sendMessage(e.getTextChannel(), message);
		}
	}
	
	public static void sendPrivate(PrivateChannel c, Message message) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this TazbotDiscordLib.");
		}
		messageSender.sendPrivate(c, message);
	}
	
	public static void sendPrivate(PrivateChannel c, String message) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this TazbotDiscordLib.");
		}
		messageSender.sendPrivate(c, message);
	}
	
	public static void sendPrivate(User u, Message message) {
		if (u == null) {
			throw new NullPointerException();
		}
		
		sendMessage(u.getPrivateChannel(), message);
	}
	
	public static void sendPrivate(User u, String message) {
		if (u == null) {
			throw new NullPointerException();
		}
		
		sendMessage(u.getPrivateChannel(), message);
	}
	
	public static void sendPrivate(MessageReceivedEvent e, Message message) {
		if (e == null) {
			throw new NullPointerException();
		}
		
		PrivateChannel c;
		if (e.isPrivate()) {
			c = e.getPrivateChannel();
		}
		else {
			c = e.getAuthor().getPrivateChannel();
		}
		
		sendPrivate(c, message);
	}
	
	public static void sendPrivate(MessageReceivedEvent e, String message) {
		if (e == null) {
			throw new NullPointerException();
		}
		
		PrivateChannel c;
		if (e.isPrivate()) {
			c = e.getPrivateChannel();
		}
		else {
			c = e.getAuthor().getPrivateChannel();
		}
		
		sendPrivate(c, message);
	}

}
