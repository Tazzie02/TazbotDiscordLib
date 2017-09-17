package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.entities.Message;

public interface MessageEditor {
	
	public Message editMessageSync(Message message, String content);
//	public Message editMessageSync(Message message, Message newMessage); // TODO
	public void editMessageAsync(Message message, String content);
//	public void editMessageAsync(Message message, Message newMessage); // TODO
	public void deleteMessageSync(Message message);
	public void deleteMessageAsync(Message message);

}
