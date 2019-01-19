package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

public interface MessageSender {
	
	public void sendMessage(TextChannel c, Message message, MessageCallback callback);
	public void sendMessage(TextChannel c, String message, MessageCallback callback);
	public void sendPrivate(PrivateChannel c, Message message, MessageCallback callback);
	public void sendPrivate(PrivateChannel c, String message, MessageCallback callback);
	
}
