package com.tazzie02.tazbotdiscordlib;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;

public class EditMessage {
	
	private static Map<JDA, MessageEditor> messageEditors = new HashMap<>();
	
	public static void addMessageEditor(MessageEditor messageEditor, JDA jda) {
		messageEditors.put(jda, messageEditor);
	}
	
	public static void removeMessageEditor(JDA jda) {
		messageEditors.remove(jda);
	}
	
	public static Message editMessageSync(Message message, String content) {
		MessageEditor messageEditor = messageEditors.get(message.getJDA());
		if (messageEditor == null) {
			throw new NullPointerException("MessageEditor is not set for this JDA.");
		}
		return messageEditor.editMessageSync(message, content);
	}
	
	public static void editMessageAsync(Message message, String content) {
		MessageEditor messageEditor = messageEditors.get(message.getJDA());
		if (messageEditor == null) {
			throw new NullPointerException("MessageEditor is not set for this JDA.");
		}
		messageEditor.editMessageAsync(message, content);
	}
	
	public static void deleteMessageSync(Message message) {
		MessageEditor messageEditor = messageEditors.get(message.getJDA());
		if (messageEditor == null) {
			throw new NullPointerException("MessageEditor is not set for this JDA.");
		}
		messageEditor.deleteMessageSync(message);
	}
	
	public static void deleteMessageAsync(Message message) {
		MessageEditor messageEditor = messageEditors.get(message.getJDA());
		if (messageEditor == null) {
			throw new NullPointerException("MessageEditor is not set for this JDA.");
		}
		messageEditor.deleteMessageAsync(message);
	}
	
}
