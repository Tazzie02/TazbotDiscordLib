package com.tazzie02.tazbotdiscordlib.commands;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ShutdownCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, "Shutting down...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		e.getJDA().shutdown(true);
//		System.exit(0);
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.OWNER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("shutdown");
	}

	@Override
	public String getDescription() {
		return "Shutdown the bot.";
	}

	@Override
	public String getName() {
		return "Shutdown Command";
	}

	@Override
	public String getDetails() {
		return "shutdown - Shutdown the bot";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
