package com.tazzie02.tazbotdiscordlib.filehandling;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.tazzie02.tazbotdiscordlib.Owners;

public class Config extends CommandSettingsImpl implements Owners {
	
	private String botToken;
	private Set<String> owners = new HashSet<>();
	
	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}
	
	public String getBotToken() {
		return botToken;
	}
	
	public boolean addOwner(String id) {
		return owners.add(id);
	}
	
	public boolean removeOwner(String id) {
		return owners.remove(id);
	}
	
	@Override
	public Set<String> getOwners() {
		return Collections.unmodifiableSet(owners);
	}

}
