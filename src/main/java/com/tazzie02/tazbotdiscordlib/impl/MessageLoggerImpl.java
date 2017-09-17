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

// TODO Allow custom output messages
public class MessageLoggerImpl implements MessageReceivedLogger, MessageSentLogger, MessageEditedLogger {
	
	// Received messages
	private boolean logCommands = true;
	private boolean consoleOutputCommands = true;
	private boolean logNonCommands = true;
	private boolean consoleOutputNonCommands = false;
	
	// Sent messages
	private boolean logSent = true;
	private boolean consoleOutputSent = true;
	private boolean logSendFailed = true;
	private boolean consoleOutputSendFailed = true;
	
	// Changed messages
	private boolean logEdited = true;
	private boolean consoleOutputEdited = true;
	private boolean logDeleted = true;
	private boolean consoleOutputDeleted = true;
	
	private final DateFormat dateTimeFormat;
	
	{
		dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	public void setAllLogging(boolean allLogging) {
		logCommands = allLogging;
		logNonCommands = allLogging;
		logSent = allLogging;
		logSendFailed = allLogging;
		logEdited = allLogging;
		logDeleted = allLogging;
	}
	
	public void setAllConsoleOutput(boolean allConsoleOutput) {
		consoleOutputCommands = allConsoleOutput;
		consoleOutputNonCommands = allConsoleOutput;
		consoleOutputSent = allConsoleOutput;
		consoleOutputSendFailed = allConsoleOutput;
		consoleOutputEdited = allConsoleOutput;
		consoleOutputDeleted = allConsoleOutput;
	}

	public boolean isLogCommands() {
		return logCommands;
	}

	public void setLogCommands(boolean logCommands) {
		this.logCommands = logCommands;
	}

	public boolean isConsoleOutputCommands() {
		return consoleOutputCommands;
	}

	public void setConsoleOutputCommands(boolean consoleOutputCommands) {
		this.consoleOutputCommands = consoleOutputCommands;
	}

	public boolean isLogNonCommands() {
		return logNonCommands;
	}

	public void setLogNonCommands(boolean logNonCommands) {
		this.logNonCommands = logNonCommands;
	}

	public boolean isConsoleOutputNonCommands() {
		return consoleOutputNonCommands;
	}

	public void setConsoleOutputNonCommands(boolean consoleOutputNonCommands) {
		this.consoleOutputNonCommands = consoleOutputNonCommands;
	}

	public boolean isLogSent() {
		return logSent;
	}

	public void setLogSent(boolean logSent) {
		this.logSent = logSent;
	}

	public boolean isConsoleOutputSent() {
		return consoleOutputSent;
	}

	public void setConsoleOutputSent(boolean consoleOutputSent) {
		this.consoleOutputSent = consoleOutputSent;
	}

	public boolean isLogSendFailed() {
		return logSendFailed;
	}

	public void setLogSendFailed(boolean logSendFailed) {
		this.logSendFailed = logSendFailed;
	}

	public boolean isConsoleOutputSendFailed() {
		return consoleOutputSendFailed;
	}

	public void setConsoleOutputSendFailed(boolean consoleOutputSendFailed) {
		this.consoleOutputSendFailed = consoleOutputSendFailed;
	}

	public boolean isLogEdited() {
		return logEdited;
	}

	public void setLogEdited(boolean logEdited) {
		this.logEdited = logEdited;
	}

	public boolean isConsoleOutputEdited() {
		return consoleOutputEdited;
	}

	public void setConsoleOutputEdited(boolean consoleOutputEdited) {
		this.consoleOutputEdited = consoleOutputEdited;
	}

	public boolean isLogDeleted() {
		return logDeleted;
	}

	public void setLogDeleted(boolean logDeleted) {
		this.logDeleted = logDeleted;
	}

	public boolean isConsoleOutputDeleted() {
		return consoleOutputDeleted;
	}

	public void setConsoleOutputDeleted(boolean consoleOutputDeleted) {
		this.consoleOutputDeleted = consoleOutputDeleted;
	}

	public DateFormat getDateTimeFormat() {
		return dateTimeFormat;
	}

	// Ignored because messageReceivedCommand and messageReceivedNotCommand cover this anyway
	@Override
	public void messageReceived(MessageReceivedEvent e) {
		return;
	}

	@Override
	public void messageReceivedCommand(MessageReceivedEvent e) {
		logMessage(e, logCommands, consoleOutputCommands);
	}

	@Override
	public void messageReceivedNotCommand(MessageReceivedEvent e) {
		logMessage(e, logNonCommands, consoleOutputNonCommands);
	}
	
	// Careful that Message will most likely have a null author
	@Override
	public void messageSent(TextChannel c, Message message) {
		logMessage(c, message, logSent, consoleOutputSent);
	}

	// Careful that Message will most likely have a null author
	@Override
	public void messageSendFailed(TextChannel c, Message message, SendMessageFailed error) {
		logMessage(c, message, error, logSendFailed, consoleOutputSendFailed);
	}

	// Careful that Message will most likely have a null author
	@Override
	public void messageSent(PrivateChannel c, Message message) {
		logMessage(c, message, logSent, consoleOutputSent);
	}
	
	@Override
	public void messageEdited(Message message, String oldContent) {
		logEdited(message, oldContent, logEdited, consoleOutputEdited);
	}

	@Override
	public void messageDeleted(Message message) {
		logDeleted(message, logDeleted, consoleOutputDeleted);
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
