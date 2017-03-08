package com.tazzie02.tazbotdiscordlib.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.tazzie02.tazbotdiscordlib.MessageEditedLogger;
import com.tazzie02.tazbotdiscordlib.MessageReceivedLogger;
import com.tazzie02.tazbotdiscordlib.MessageSentLogger;
import com.tazzie02.tazbotdiscordlib.filehandling.FileLogger;
import com.tazzie02.tazbotdiscordlib.impl.MessageSenderImpl.SendMessageFailed;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class MessageLoggerImpl implements MessageReceivedLogger, MessageSentLogger, MessageEditedLogger {
	
	private final boolean LOG_COMMANDS = true;
	private final boolean CONSOLE_OUTPUT_COMMANDS = true;
	private final boolean LOG_NON_COMMANDS = true;
	private final boolean CONSOLE_OUTPUT_NON_COMMANDS = false;
	
	private final boolean LOG_SENT = true;
	private final boolean CONSOLE_OUTPUT_SENT = true;
	private final boolean LOG_SEND_FAILED = true;
	private final boolean CONSOLE_OUTPUT_SEND_FAILED = true;
	
	private final boolean LOG_EDITED = true;
	private final boolean CONSOLE_OUTPUT_EDITED = true;
	private final boolean LOG_DELETED = true;
	private final boolean CONSOLE_OUTPUT_DELETED = true;
	
	private final DateFormat dateTimeFormat;
	
	{
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}

	// Ignored because messageReceivedCommand and messageReceivedNotCommand cover this anyway
	@Override
	public void messageReceived(MessageReceivedEvent e) {
		return;
	}

	@Override
	public void messageReceivedCommand(MessageReceivedEvent e) {
		logMessage(e, LOG_COMMANDS, CONSOLE_OUTPUT_COMMANDS);
	}

	@Override
	public void messageReceivedNotCommand(MessageReceivedEvent e) {
		logMessage(e, LOG_NON_COMMANDS, CONSOLE_OUTPUT_NON_COMMANDS);
	}
	
	// Careful that Message will most likely have a null author
	@Override
	public void messageSent(TextChannel c, Message message) {
		logMessage(c, message, LOG_SENT, CONSOLE_OUTPUT_SENT);
	}

	// Careful that Message will most likely have a null author
	@Override
	public void messageSendFailed(TextChannel c, Message message, SendMessageFailed error) {
		logMessage(c, message, error, LOG_SEND_FAILED, CONSOLE_OUTPUT_SEND_FAILED);
	}

	// Careful that Message will most likely have a null author
	@Override
	public void messageSent(PrivateChannel c, Message message) {
		logMessage(c, message, LOG_SENT, CONSOLE_OUTPUT_SENT);
	}
	
	@Override
	public void messageEdited(Message message, String oldContent) {
		logEdited(message, oldContent, LOG_EDITED, CONSOLE_OUTPUT_EDITED);
	}

	@Override
	public void messageDeleted(Message message) {
		logDeleted(message, LOG_DELETED, CONSOLE_OUTPUT_DELETED);
	}
	
	private void logMessage(MessageReceivedEvent e, boolean log, boolean printToConsole) {
		if (e.isFromType(ChannelType.PRIVATE)) {
			logMessage(e.getPrivateChannel(), e.getMessage(), log, printToConsole);
		}
		else {
			logMessage(e.getTextChannel(), e.getMessage(), log, printToConsole);
		}
	}
	
	private void logMessage(TextChannel c, Message message, boolean log, boolean printToConsole) {
		User author = message.getAuthor();
		if (author == null) {
			author = c.getJDA().getSelfUser();
		}
		
		if (log) {
			// HH:mm:ss [#CHANNEL] Username: Message
			String s = String.format("[#%s] %s: %s", c.getName(), author.getName(), getSafeMessageContent(message));
			try {
				FileLogger.log(s, c.getGuild(), c.getJDA());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (printToConsole) {
			// yyyy-MM-dd HH:mm:ss.SSS [GUILD][#CHANNEL] Username: Message
			String s = String.format("[%s][#%s] %s: %s", c.getGuild().getName(), c.getName(), 
														author.getName(), getSafeMessageContent(message));
			sendConsoleOutput(s);
		}
	}
	
	private void logMessage(TextChannel c, Message message, SendMessageFailed error, boolean log, boolean printToConsole) {
		User author = message.getAuthor();
		if (author == null) {
			author = c.getJDA().getSelfUser();
		}
		
		if (log) {
			// HH:mm:ss FAILED_REASON [#CHANNEL] Username: Message
			String s = String.format("%s [#%s] %s: %s", error.toString(), c.getName(), author.getName(), getSafeMessageContent(message));
			try {
				FileLogger.log(s, c.getGuild(), c.getJDA());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (printToConsole) {
			// yyyy-MM-dd HH:mm:ss.SSS FAILED_REASON [GUILD][#CHANNEL] Username: Message
			String s = String.format("%s [%s][#%s] %s: %s", error.toString(), c.getGuild().getName(), c.getName(), 
															author.getName(), getSafeMessageContent(message));
			sendConsoleOutput(s);
		}
	}
	
	private void logMessage(PrivateChannel c, Message message, boolean log, boolean printToConsole) {
		User author = message.getAuthor();
		if (author == null) {
			author = c.getJDA().getSelfUser();
		}
		
		if (log) {
			// HH:mm:ss Username: Message
			String s = String.format("%s: %s", author.getName(), getSafeMessageContent(message));
			try {
				FileLogger.log(s, null, c.getJDA());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (printToConsole) {
			// yyyy-MM-dd HH:mm:ss.SSS [PRIVATE] Username: Message
			String s = String.format("[PRIVATE] %s: %s", author.getName(), getSafeMessageContent(message));
			sendConsoleOutput(s);
		}
	}

	private void logEdited(Message message, String oldContent, boolean log, boolean printToConsole) {
		if (log) {
			// HH:mm:ss [#CHANNEL][EDITED] Username: Message [OLD-CONTENT] Message
			String s = String.format("[#%s][EDITED] %s: %s [OLD-CONTENT] %s", message.getChannel().getName(),
					message.getAuthor().getName(), getSafeMessageContent(message), getSafeMessageContent(oldContent));
			try {
				FileLogger.log(s, message.getGuild(), message.getJDA());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (printToConsole) {
			// yyyy-MM-dd HH:mm:ss.SSS [GUILD][#CHANNEL][EDITED] Username: Message [OLD-CONTENT] Message
			String s = String.format("[%s][#%s][EDITED] %s: %s [OLD-CONTENT] %s", message.getGuild().getName(),
					message.getChannel().getName(), message.getAuthor().getName(), getSafeMessageContent(message), getSafeMessageContent(oldContent));
			sendConsoleOutput(s);
		}
	}
	
	private void logDeleted(Message message, boolean log, boolean printToConsole) {
		if (log) {
			// HH:mm:ss [#CHANNEL][DELETED] Username: Message
			String s = String.format("[#%s][DELETED] %s: %s", message.getChannel().getName(),
					message.getAuthor().getName(), getSafeMessageContent(message));
			try {
				FileLogger.log(s, message.getGuild(), message.getJDA());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (printToConsole) {
			// yyyy-MM-dd HH:mm:ss.SSS [GUILD][#CHANNEL][DELETED] Username: Message
			String s = String.format("[%s][#%s][DELETED] %s: %s", message.getGuild().getName(),
					message.getChannel().getName(), message.getAuthor().getName(), getSafeMessageContent(message));
			sendConsoleOutput(s);
		}
	}
	
	protected void sendConsoleOutput(String s) {
		System.out.println(getDateTimeString() + " " + s);
	}
	
	protected String getDateTimeString() {
		return dateTimeFormat.format(new Date());
	}
	
	protected String getSafeMessageContent(Message message) {
		// TODO getContent doesn't seem to work, but rawContent will show mentions as <@1234567890>
		return getSafeMessageContent(message.getRawContent());
	}
	
	protected String getSafeMessageContent(String message) {
		return message.replaceAll("\n", " \\n ");
	}

}
