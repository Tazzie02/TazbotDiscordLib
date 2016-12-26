package com.tazzie02.tazbotdiscordlib;

import java.util.List;

/**
 * Information for a Command for Discord text chat. 
 */
public interface CommandInformation {
	
	/**
	 * Get the {@link CommandAccess}. This represents which types of user
	 * this Command can be used by.
	 * 
	 * @return  The types of user the Command can be used by.
	 */
	public CommandAccess getAccess();
	
	/**
	 * Get a list of aliases that can be used to trigger the Command.
	 * 
	 * @return  A list of aliases that can be used to trigger the Command.
	 */
	public List<String> getAliases();
	
	/**
	 * Get a description of the Command.
	 * 
	 * @return  Description of the Command.
	 */
	public String getDescription();
	
	/**
	 * Get the name of the Command.
	 * 
	 * @return  Name of the Command.
	 */
	public String getName();
	
	/**
	 * Get the details of the Command. This includes usage and examples.
	 * 
	 * @return  Details of the Command.
	 */
	public String getDetails();
	
	/**
	 * Get whether the Command is hidden from Help. If true, the Command is
	 * still shown in Help to {@link CommandAccess#OWNER Owners}.
	 * 
	 * @return  True if the Command is hidden from Help to all except 
	 *          {@link CommandAccess#OWNER Owners}.
	 */
	public boolean isHidden();
	
	/**
	 * Access levels representing different groups of users.
	 */
	public enum CommandAccess {
		/**
		 * Access to everything. Should be the only access level to potentially be
		 * able to break operation of the Bot. Owners are global, not per guild.
		 */
		OWNER,
		
		/**
		 * Elevated access. Should have access to modify per guild settings. Should
		 * generally be set per guild, but global is also possible.
		 */
		MODERATOR,
		
		/**
		 * All users can use this Command.
		 */
		ALL
		;
	}

}
