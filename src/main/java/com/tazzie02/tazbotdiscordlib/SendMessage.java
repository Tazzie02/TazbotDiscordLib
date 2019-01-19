package com.tazzie02.tazbotdiscordlib;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SendMessage {
	
	private static Map<JDA, MessageSender> messageSenders = new HashMap<>();
	
	public static void addMessageSender(MessageSender messageSender, JDA jda) {
		messageSenders.put(jda, messageSender);
	}
	
	public static void removeMessageSender(JDA jda) {
		messageSenders.remove(jda);
	}
	
	public static void sendMessage(MessageChannel c, Message message, MessageCallback callback) {
		if (c == null) {
			throw new NullPointerException();
		}
		
		if (c instanceof TextChannel) {
			sendMessage((TextChannel) c, message);
			return;
		}
		else if (c instanceof PrivateChannel) {
			sendPrivate((PrivateChannel) c, message);
			return;
		}
		
		TextChannel tc = c.getJDA().getTextChannelById(c.getId());
		if (tc != null) {
			sendMessage(tc, message, callback);
			return;
		}
		
		PrivateChannel pc = c.getJDA().getPrivateChannelById(c.getId());
		if (pc != null) {
			sendPrivate(pc, message, callback);
			return;
		}
		
		throw new NullPointerException("This shouldn't happen...");
	}

	public static void sendMessage(MessageChannel c, Message message) {
	    sendMessage(c, message, null);
	}

	public static void sendMessage(MessageChannel c, String message, MessageCallback callback) {
		if (c == null) {
			throw new NullPointerException();
		}
		
		if (c instanceof TextChannel) {
			sendMessage((TextChannel) c, message);
			return;
		}
		else if (c instanceof PrivateChannel) {
			sendPrivate((PrivateChannel) c, message);
			return;
		}

		TextChannel tc = c.getJDA().getTextChannelById(c.getId());
		if (tc != null) {
			sendMessage(tc, message, callback);
			return;
		}
		
		PrivateChannel pc = c.getJDA().getPrivateChannelById(c.getId());
		if (pc != null) {
			sendPrivate(pc, message, callback);
			return;
		}
		
		throw new NullPointerException("This shouldn't happen...");
	}

	public static void sendMessage(MessageChannel c, String message) {
		sendMessage(c, message, null);
	}

	public static void sendMessage(TextChannel c, Message message, MessageCallback callback) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this JDA.");
		}
		messageSender.sendMessage(c, message, callback);
	}

	public static void sendMessage(TextChannel c, Message message) {
	    sendMessage(c, message, null);
	}

	public static void sendMessage(TextChannel c, String message, MessageCallback callback) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this JDA.");
		}
		messageSender.sendMessage(c, message, callback);
	}

	public static void sendMessage(TextChannel c, String message) {
	    sendMessage(c, message, null);
	}


	public static void sendMessage(MessageReceivedEvent e, Message message, MessageCallback callback) {
		if (e == null) {
			throw new NullPointerException();
		}
		
		if (e.isFromType(ChannelType.PRIVATE)) {
			sendPrivate(e.getPrivateChannel(), message, callback);
		}
		else {
			sendMessage(e.getTextChannel(), message, callback);
		}
	}

	public static void sendMessage(MessageReceivedEvent e, Message message) {
	    sendMessage(e, message, null);
	}

	public static void sendMessage(MessageReceivedEvent e, String message, MessageCallback callback) {
		if (e == null) {
			throw new NullPointerException();
		}

		if (e.isFromType(ChannelType.PRIVATE)) {
			sendPrivate(e.getPrivateChannel(), message, callback);
		}
		else {
			sendMessage(e.getTextChannel(), message, callback);
		}
	}

	public static void sendMessage(MessageReceivedEvent e, String message) {
	    sendMessage(e, message, null);
	}

	public static void sendPrivate(PrivateChannel c, Message message, MessageCallback callback) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this JDA.");
		}
		messageSender.sendPrivate(c, message, callback);
	}

	public static void sendPrivate(PrivateChannel c, Message message) {
	    sendPrivate(c, message, null);
	}

	public static void sendPrivate(PrivateChannel c, String message, MessageCallback callback) {
		MessageSender messageSender = messageSenders.get(c.getJDA());
		if (messageSender == null) {
			throw new NullPointerException("MessageSender is not set for this JDA.");
		}
		messageSender.sendPrivate(c, message, callback);
	}

	public static void sendPrivate(PrivateChannel c, String message) {
	    sendPrivate(c, message, null);
	}

	public static void sendPrivate(User u, Message message, MessageCallback callback) {
		if (u == null) {
			throw new NullPointerException();
		}
		
		sendPrivate(u.openPrivateChannel().complete(), message, callback);
	}

	public static void sendPrivate(User u, Message message) {
	    sendPrivate(u, message, null);
	}

	public static void sendPrivate(User u, String message, MessageCallback callback) {
		if (u == null) {
			throw new NullPointerException();
		}
		
		sendPrivate(u.openPrivateChannel().complete(), message, callback);
	}

	public static void sendPrivate(User u, String message) {
	    sendPrivate(u, message, null);
	}

	public static void sendPrivate(MessageReceivedEvent e, Message message, MessageCallback callback) {
		if (e == null) {
			throw new NullPointerException();
		}
		
		PrivateChannel c;
		if (e.isFromType(ChannelType.PRIVATE)) {
			c = e.getPrivateChannel();
		}
		else {
			c = e.getAuthor().openPrivateChannel().complete();
		}
		
		sendPrivate(c, message, callback);
	}

	public static void sendPrivate(MessageReceivedEvent e, Message message) {
		sendPrivate(e, message, null);
	}

	public static void sendPrivate(MessageReceivedEvent e, String message, MessageCallback callback) {
		if (e == null) {
			throw new NullPointerException();
		}
		
		PrivateChannel c;
		if (e.isFromType(ChannelType.PRIVATE)) {
			c = e.getPrivateChannel();
		}
		else {
			c = e.getAuthor().openPrivateChannel().complete();
		}
		
		sendPrivate(c, message, callback);
	}

	public static void sendPrivate(MessageReceivedEvent e, String message) {
	    sendPrivate(e, message, null);
	}
}
