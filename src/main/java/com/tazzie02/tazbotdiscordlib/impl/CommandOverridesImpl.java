package com.tazzie02.tazbotdiscordlib.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.CommandOverrides;

public class CommandOverridesImpl implements CommandOverrides {
	
	private Map<String, String> commandOverrides = new HashMap<>();

	@Override
	public Map<String, String> getCommandOverrides() {
		return Collections.unmodifiableMap(commandOverrides);
	}

	@Override
	public String getOverridePrefix(Command command) {
		return commandOverrides.get(getAlias(command));
	}

	@Override
	public Set<String> getAllPrefixes() {
		return new HashSet<>(commandOverrides.values());
	}

	@Override
	public Set<String> getAliasesWithPrefix(String prefix) {
		Set<String> commands = new HashSet<>();
		for (Map.Entry<String, String> entry : commandOverrides.entrySet()) {
			if (entry.getValue().equals(prefix)) {
				commands.add(entry.getKey());
			}
		}
		return commands;
	}
	
	public void addOverride(Command command, String prefix) {
		if (prefix == null) {
			removeOverride(command);
		}
		else {
			commandOverrides.put(getAlias(command), prefix);
		}
	}
	
	public boolean removeOverride(Command command) {
		String result = commandOverrides.remove(getAlias(command)); 
		if (result == null) {
			return false;
		}
		return true;
	}
	
	private String getAlias(Command command) {
		if (command == null) {
			throw new NullPointerException();
		}
		if (command.getAliases().isEmpty()) {
			throw new UnsupportedOperationException();
		}
		
		return command.getAliases().get(0).toLowerCase();
	}
	
	
	
}
