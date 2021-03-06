package com.tazzie02.tazbotdiscordlib.impl;

import java.util.ArrayList;
import java.util.List;

import com.tazzie02.tazbotdiscordlib.MessageCallback;
import com.tazzie02.tazbotdiscordlib.MessageSender;
import com.tazzie02.tazbotdiscordlib.MessageSentLogger;

import net.dv8tion.jda.client.exceptions.VerificationLevelException;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild.VerificationLevel;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

// NOTE THAT THERE IS A BUG WITH JDA THAT MEANS THESE CREATED MESSAGE OBJECTS HAVE NULL AUTHORS
public class MessageSenderImpl implements MessageSender {
	
	private final int MAX_MESSAGE_SIZE = 2000;
	private final boolean SEND_ALL_SPLITS = true; // If false, only send the first MAX_MESSAGE_SIZE characters
	private MessageSentLogger messageSentLogger;

	@Override
	public void sendMessage(TextChannel c, Message message, MessageCallback callback) {
		if (c == null || message == null) {
			throw new NullPointerException();
		}
		
		if (!c.canTalk()) {
			logMessageFailed(c, message, SendMessageFailed.NO_WRITE_PERMISSION);
			return;
		}
		if (c.getGuild().getVerificationLevel().equals(VerificationLevel.LOW) && !c.getJDA().getSelfUser().isVerified()) {
			logMessageFailed(c, message, SendMessageFailed.NO_EMAIL_VERIFICATION);
			return;
		}
		
		try {
			c.sendMessage(message).queue(m -> {
				logMessageSent(c, message);
				if (callback != null) {
					callback.callback(m);
				}
			});
		}
		catch (VerificationLevelException e) {
			logMessageFailed(c, message, SendMessageFailed.NO_TIME_VERIFICATION);
		}
	}

	@Override
	public void sendMessage(TextChannel c, String message, MessageCallback callback) {
		if (c == null || message == null || message.isEmpty()) {
			throw new NullPointerException();
		}
		
		// TODO Check message length before sending to split?
		List<Message> messages = splitMessage(message);
		int n = messages.size();
		if (!SEND_ALL_SPLITS) {
			n = 1;
		}
		
		for (int i = 0; i < n; i++) {
			sendMessage(c, messages.get(i), callback);
		}
	}

	@Override
	public void sendPrivate(PrivateChannel c, Message message, MessageCallback callback) {
		if (c == null || message == null) {
			throw new NullPointerException();
		}

		c.sendMessage(message).queue(m -> {
			logMessageSent(c, message);
			if (callback != null) {
				callback.callback(m);
			}
		});
	}

	@Override
	public void sendPrivate(PrivateChannel c, String message, MessageCallback callback) {
		if (c == null || message == null || message.isEmpty()) {
			throw new NullPointerException();
		}
		
		// TODO Check message length before sending to split?
		List<Message> messages = splitMessage(message);
		int n = messages.size();
		if (!SEND_ALL_SPLITS) {
			n = 1;
		}
		
		for (int i = 0; i < n; i++) {
			sendPrivate(c, messages.get(i), callback);
		}
		
	}
	
	public void setMessageSentLogger(MessageSentLogger messageSentLogger) {
		this.messageSentLogger = messageSentLogger;
	}
	
	private List<Message> splitMessage(String message) {
		if (message == null) {
			throw new NullPointerException();
		}
		
		List<Message> messages = new ArrayList<Message>();
		while (!message.isEmpty()) {
			if (message.length() > MAX_MESSAGE_SIZE) {
				String split = message.substring(0, MAX_MESSAGE_SIZE);
				message = message.substring(MAX_MESSAGE_SIZE);

				// Split on last new line
				int index = split.lastIndexOf("\n");

				// If no new line, split on space
				if (index == -1) {
					index = split.lastIndexOf(" ");
				}

				// Split if index found
				if (index != -1) {
					messages.add(new MessageBuilder().append(split.substring(0, index)).build());
					message = split.substring(index + 1) + message;
				}
				// Split on MAX_MESSAGE_SIZE if index found
				else {
					messages.add(new MessageBuilder().append(split).build());
				}
			}
			else {
				messages.add(new MessageBuilder().append(message).build());
				message = "";
			}
		}
		return messages;
	}
	
	private void logMessageSent(TextChannel c, Message message) {
		if (messageSentLogger != null) {
			messageSentLogger.messageSent(c, message);
		}
	}
	
	private void logMessageFailed(TextChannel c, Message message, SendMessageFailed error) {
		if (messageSentLogger != null) {
			messageSentLogger.messageSendFailed(c, message, error);
		}
	}
	
	private void logMessageSent(PrivateChannel c, Message message) {
		if (messageSentLogger != null) {
			messageSentLogger.messageSent(c, message);
		}
	}
	
//	private void logMessageFailed(PrivateChannel c, Message message, SendMessageFailed error) {
//		messageSentLogger.messageFailed(c, message, error);
//	}
	
	
	public enum SendMessageFailed {
		NO_WRITE_PERMISSION,
		NO_EMAIL_VERIFICATION,
		NO_TIME_VERIFICATION
	}
	
}


