package com.tazzie02.tazbotdiscordlib;

import java.util.Map;
import java.util.Set;

public interface CommandOverrides {
	
	public Map<String, String> getCommandOverrides();
	public String getOverridePrefix(Command command);
	public Set<String> getAllPrefixes();
	public Set<String> getAliasesWithPrefix(String prefix);

}
