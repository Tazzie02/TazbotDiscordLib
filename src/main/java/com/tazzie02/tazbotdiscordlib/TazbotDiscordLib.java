package com.tazzie02.tazbotdiscordlib;

import java.nio.file.Path;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public interface TazbotDiscordLib {

	public JDA getJDA();
	public TazbotDiscordLib addListener(ListenerAdapter listener);
	public TazbotDiscordLib removeListener(ListenerAdapter listener);
	public TazbotDiscordLib setMessageSender(MessageSender messageSender);
	public TazbotDiscordLib setFilePath(Path path);
	public void shutdown();
	public void shutdownNow();
	public boolean isShutdown();
	
}
