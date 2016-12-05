package com.tazzie02.tazbotdiscordlib;

import com.tazzie02.tazbotdiscordlib.impl.MessageSenderImpl.SendMessageFailed;

import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.TextChannel;

public interface MessageSentLogger {
	
	public void messageSent(TextChannel c, Message message);
	public void messageSendFailed(TextChannel c, Message message, SendMessageFailed error);
	public void messageSent(PrivateChannel c, Message message);
//	public void messageSendFailed(PrivateChannel c, Message message, SendMessageFailed error);

}
