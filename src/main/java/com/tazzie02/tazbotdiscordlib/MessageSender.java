package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;

public interface MessageSender {
	
	public void sendMessage(TextChannel c, Message message);
	public void sendMessage(TextChannel c, String message);
	public void sendPrivate(PrivateChannel c, Message message);
	public void sendPrivate(PrivateChannel c, String message);
	
}
