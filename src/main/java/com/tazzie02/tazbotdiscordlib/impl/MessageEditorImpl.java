package com.tazzie02.tazbotdiscordlib.impl;

import com.tazzie02.tazbotdiscordlib.MessageEditedLogger;
import com.tazzie02.tazbotdiscordlib.MessageEditor;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.requests.RestAction;

public class MessageEditorImpl implements MessageEditor {
	
	private final int MAX_MESSAGE_SIZE = 2000;
	private MessageEditedLogger messageEditedLogger;
	
	public void setMessageEditedLogger(MessageEditedLogger messageEditedLogger) {
		this.messageEditedLogger = messageEditedLogger;
	}

	private RestAction<Message> editMessage(Message message, String content) {
		if (message == null) {
			throw new NullPointerException();
		}
		if (content != null && content.length() > MAX_MESSAGE_SIZE) {
			throw new IllegalStateException("Content must not be longer than " + MAX_MESSAGE_SIZE);
		}
		if (!message.getAuthor().getId().equals(message.getJDA().getSelfUser().getId())) {
			throw new IllegalStateException();
		}
		
		return message.editMessage(content);
	}
	
	@Override
	public Message editMessageSync(Message message, String content) {
		String oldContent = message.getContentRaw();
		Message newMessage;
		
		try {
			newMessage = editMessage(message, content).complete();
			logMessageEdited(newMessage, oldContent);
		}
		catch (NullPointerException | IllegalStateException e) {
			throw e;
		}
		
		return newMessage;
	}
	
	@Override
	public void editMessageAsync(Message message, String content) {
		String oldContent = message.getContentRaw();
		
		try {
			editMessage(message, content).queue(m -> 
					logMessageEdited(m, oldContent));
		}
		catch (NullPointerException | IllegalStateException e) {
			throw e;
		}
	}
	
	private RestAction<Void> deleteMessage(Message message) {
		if (message == null) {
			throw new NullPointerException();
		}
		// JDA bug with messages created with MessageBuilder.
		if (message.getAuthor() == null) {
			throw new NullPointerException();
		}
		// Not by self
		if (!message.getAuthor().getId().equals(message.getJDA().getSelfUser().getId())) {
			if (message.isFromType(ChannelType.PRIVATE) || message.isFromType(ChannelType.GROUP)) {
				throw new IllegalStateException("Cannot delete other users messages from PRIVATE or GROUP");
			}
			if (message.isFromType(ChannelType.TEXT) && !message.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
				throw new PermissionException("Missing MESSAGE_MANAGE permission");
			}
		}
		
		return message.delete();
	}
	
	@Override
	public void deleteMessageSync(Message message) {
		try {
			deleteMessage(message).complete();
			logMessageDeleted(message);
		}
		catch (NullPointerException | IllegalStateException | PermissionException e) {
			throw e;
		}
	}
	
	@Override
	public void deleteMessageAsync(Message message) {
		try {
			deleteMessage(message).queue(Void ->
					logMessageDeleted(message));
		}
		catch (NullPointerException | IllegalStateException | PermissionException e) {
			throw e;
		}
	}
	
	private void logMessageEdited(Message message, String oldContent) {
		if (messageEditedLogger != null) {
			messageEditedLogger.messageEdited(message, oldContent);
		}
	}
	
	private void logMessageDeleted(Message message) {
		if (messageEditedLogger != null) {
			messageEditedLogger.messageDeleted(message);
		}
	}

}
