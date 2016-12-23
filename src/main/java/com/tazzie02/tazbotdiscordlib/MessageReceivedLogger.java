package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface MessageReceivedLogger {
	
	public void messageReceived(MessageReceivedEvent e); 
	public void messageReceivedCommand(MessageReceivedEvent e);
	public void messageReceivedNotCommand(MessageReceivedEvent e);

}
