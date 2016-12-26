package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * A Command for Discord text chat.
 */
public interface Command extends CommandInformation {
	
	/**
	 * Called when an alias for this {@link Command} is used.
	 * 
	 * @param   e
	 *          The event associated with the message.
	 * @param   args
	 *          Additional arguments accompanying the alias in the message string.
	 *          The alias will not be in this array. Possibly empty.
	 */
	public void onCommand(MessageReceivedEvent e, String[] args);
	
}
