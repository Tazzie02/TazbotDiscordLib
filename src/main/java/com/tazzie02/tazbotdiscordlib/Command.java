package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command extends CommandInformation {
	
	public void onCommand(MessageReceivedEvent e, String[] args);
	
}
