package com.tazzie02.tazbotdiscordlib.commands;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class PingCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, "Pong!");
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("ping");
	}

	@Override
	public String getDescription() {
		return "Peform a Ping Pong event.";
	}

	@Override
	public String getName() {
		return "Ping Command";
	}

	@Override
	public String getDetails() {
		return "ping - Reply with Pong!";
	}

	@Override
	public boolean isHidden() {
		return true;
	}

}
