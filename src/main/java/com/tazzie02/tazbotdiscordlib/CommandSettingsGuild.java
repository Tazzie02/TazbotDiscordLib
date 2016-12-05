package com.tazzie02.tazbotdiscordlib;

import java.util.Set;

import net.dv8tion.jda.entities.Guild;

public interface CommandSettingsGuild {
	
	public Set<String> getModerators(Guild guild);
	public String getPrefix(Guild guild);
	public CommandOverrides getCommandOverrides(Guild guild);
	
}
