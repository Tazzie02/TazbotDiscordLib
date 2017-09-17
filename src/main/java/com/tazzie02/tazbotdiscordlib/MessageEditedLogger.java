package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.entities.Message;

public interface MessageEditedLogger {
	
	public void messageEdited(Message message, String oldContent);
	public void messageDeleted(Message message);

}
