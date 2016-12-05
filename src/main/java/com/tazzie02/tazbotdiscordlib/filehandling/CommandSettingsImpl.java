package com.tazzie02.tazbotdiscordlib.filehandling;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.CommandOverrides;
import com.tazzie02.tazbotdiscordlib.CommandSettings;
import com.tazzie02.tazbotdiscordlib.impl.CommandOverridesImpl;

public class CommandSettingsImpl implements CommandSettings {
	
	private String prefix;
	private Set<String> moderators = new HashSet<>();
	private CommandOverridesImpl commandOverrides = new CommandOverridesImpl();
	
	public void setPrefix(String prefix) {
		if (prefix == null) {
			throw new NullPointerException();
		}
		this.prefix = prefix;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}
	
	public boolean addModerator(String id) {
		return moderators.add(id);
	}
	
	public boolean removeModerator(String id) {
		return moderators.remove(id);
	}

	@Override
	public Set<String> getModerators() {
		return Collections.unmodifiableSet(moderators);
	}
	
	public void addCommandOverride(Command command, String prefix) {
		commandOverrides.addOverride(command, prefix);
	}
	
	public boolean removeCommandOverride(Command command) {
		return commandOverrides.removeOverride(command);
	}

	@Override
	public CommandOverrides getCommandOverrides() {
		return commandOverrides;
	}

}
