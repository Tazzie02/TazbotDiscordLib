package com.tazzie02.tazbotdiscordlib.impl;

import java.nio.file.Path;
import java.util.Set;

import javax.security.auth.login.LoginException;

import com.tazzie02.tazbotdiscordlib.MessageSender;
import com.tazzie02.tazbotdiscordlib.SendMessage;
import com.tazzie02.tazbotdiscordlib.TazbotDiscordLib;
import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class TazbotDiscordLibImpl implements TazbotDiscordLib {
	
	private JDA jda;
	
	public TazbotDiscordLibImpl(String botToken, Set<ListenerAdapter> listeners, boolean audioEnabled) throws LoginException, IllegalArgumentException, InterruptedException {
		jda = buildJDA(botToken, listeners, audioEnabled);
	}
	
	private JDA buildJDA(String botToken, Set<ListenerAdapter> listeners, boolean audioEnabled) throws LoginException, IllegalArgumentException, InterruptedException {
		JDABuilder builder = new JDABuilder();
		builder.setBotToken(botToken);
		builder.setBulkDeleteSplittingEnabled(false);
		builder.setAudioEnabled(audioEnabled);
		listeners.forEach(builder::addListener);
		
		return builder.buildBlocking();
	}

	@Override
	public JDA getJDA() {
		return jda;
	}
	
	@Override
	public TazbotDiscordLib addListener(ListenerAdapter listener) {
		jda.addEventListener(listener);
		return this;
	}
	
	@Override
	public TazbotDiscordLib removeListener(ListenerAdapter listener) {
		jda.removeEventListener(listener);
		return this;
	}

	@Override
	public TazbotDiscordLib setMessageSender(MessageSender messageSender) {
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
		// addInstance will remove entry if path is null
		LocalFiles.addInstance(jda, path);
		
		return this;
	}

}
