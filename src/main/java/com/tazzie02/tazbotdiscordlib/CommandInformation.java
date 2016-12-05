package com.tazzie02.tazbotdiscordlib;

import java.util.List;

public interface CommandInformation {
	
	public CommandAccess getAccess();
	public List<String> getAliases();
	public String getDescription();
	public String getName();
	public String getDetails();
	public boolean isHidden();
	
	public enum CommandAccess {
		OWNER,
		MODERATOR,
		ALL
		;
	}

}
