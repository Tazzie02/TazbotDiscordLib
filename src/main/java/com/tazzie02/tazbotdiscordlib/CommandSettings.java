package com.tazzie02.tazbotdiscordlib;

import java.util.Set;

public interface CommandSettings {
	
	public Set<String> getModerators();
	public String getPrefix();
	public CommandOverrides getCommandOverrides();
	

}
