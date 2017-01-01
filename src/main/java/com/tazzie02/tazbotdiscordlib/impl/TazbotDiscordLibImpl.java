package com.tazzie02.tazbotdiscordlib.impl;

import java.nio.file.Path;
import java.util.Set;

import javax.security.auth.login.LoginException;

import com.tazzie02.tazbotdiscordlib.MessageSender;
import com.tazzie02.tazbotdiscordlib.SendMessage;
import com.tazzie02.tazbotdiscordlib.TazbotDiscordLib;
import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TazbotDiscordLibImpl implements TazbotDiscordLib {
	
	private JDA jda;
	private boolean shutdown;
	
	public TazbotDiscordLibImpl(String botToken, Set<ListenerAdapter> listeners, boolean audioEnabled) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		jda = buildJDA(botToken, listeners, audioEnabled);
	}
	
	private JDA buildJDA(String botToken, Set<ListenerAdapter> listeners, boolean audioEnabled) throws LoginException, IllegalArgumentException, InterruptedException, RateLimitedException {
		JDABuilder builder = new JDABuilder(AccountType.BOT);
		builder.setToken(botToken);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setAudioEnabled(audioEnabled);
		listeners.forEach(builder::addListener);
		
		return builder.buildBlocking();
	}

	@Override
	public JDA getJDA() {
		if (shutdown) {
			throw new IllegalStateException("This instance has been shutdown and cannot be used.");
		}
		
		return jda;
	}
	
	@Override
	public TazbotDiscordLib addListener(ListenerAdapter listener) {
		if (shutdown) {
			throw new IllegalStateException("This instance has been shutdown and cannot be used.");
		}
		
		jda.addEventListener(listener);
		return this;
	}
	
	@Override
	public TazbotDiscordLib removeListener(ListenerAdapter listener) {
		if (shutdown) {
			throw new IllegalStateException("This instance has been shutdown and cannot be used.");
		}
		
		jda.removeEventListener(listener);
		return this;
	}

	@Override
	public TazbotDiscordLib setMessageSender(MessageSender messageSender) {
		if (shutdown) {
			throw new IllegalStateException("This instance has been shutdown and cannot be used.");
		}
		
		if (messageSender == null) {
			SendMessage.removeMessageSender(jda);
		}
		else {
			SendMessage.addMessageSender(messageSender, jda);
		}
		return this;
	}
	
	@Override
	public TazbotDiscordLib setFilePath(Path path) {
		if (shutdown) {
			throw new IllegalStateException("This instance has been shutdown and cannot be used.");
		}
		
		// addInstance will remove entry if path is null
		LocalFiles.addInstance(jda, path);
		
		return this;
	}
	
	@Override
	public void shutdown(boolean free) {
		jda.shutdown(free);
		shutdown = true;
	}
	
	@Override
	public boolean isShutdown() {
		return shutdown;
	}
	
}
