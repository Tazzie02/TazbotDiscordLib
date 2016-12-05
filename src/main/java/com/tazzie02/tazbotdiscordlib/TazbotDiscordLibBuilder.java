package com.tazzie02.tazbotdiscordlib;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginException;

import com.tazzie02.tazbotdiscordlib.impl.TazbotDiscordLibImpl;

import net.dv8tion.jda.hooks.ListenerAdapter;

public class TazbotDiscordLibBuilder {
	
	private String botToken;
	private Set<ListenerAdapter> listeners = new HashSet<>();
	private boolean audioEnabled = false;
	private MessageSender messageSender = null;
	private Path path = null; 
	
	public TazbotDiscordLibBuilder(String botToken) {
		this.botToken = botToken;
	}
	
	public TazbotDiscordLib build() throws LoginException, IllegalArgumentException, InterruptedException {
		TazbotDiscordLibImpl tdl = new TazbotDiscordLibImpl(botToken, listeners, audioEnabled);
		tdl.setMessageSender(messageSender);
		tdl.setFilePath(path);
		
		return tdl;
	}
	
	public TazbotDiscordLibBuilder addListener(ListenerAdapter listener) {
		listeners.add(listener);
		return this;
	}
	
	public TazbotDiscordLibBuilder removeListener(ListenerAdapter listener) {
		listeners.remove(listener);
		return this;
	}
	
	public TazbotDiscordLibBuilder setAudioEnabled(boolean audioEnabled) {
		this.audioEnabled = audioEnabled;
		return this;
	}
	
	public TazbotDiscordLibBuilder setMessageSender(MessageSender messageSender) {
		this.messageSender = messageSender;
		return this;
	}
	
	public TazbotDiscordLibBuilder setFilePath(Path path) {
		this.path = path;
		return this;
	}
	
}
