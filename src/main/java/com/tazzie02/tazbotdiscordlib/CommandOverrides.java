package com.tazzie02.tazbotdiscordlib;

import java.util.Map;
import java.util.Set;

/**
 * Information about Commands with prefix overrides.
 */
public interface CommandOverrides {
	
	/**
	 * Get a Map of Commands with an override prefix.
	 * 
	 * @return  An unmodifiable Map with the key of the first alias name for a
	 * Command, and a value of the override prefix for that Command.
	 */
	public Map<String, String> getCommandOverrides();
	
	/**
	 * Get a possibly null override prefix for the Command.
	 * 
	 * @param   command
	 *          The Command to get the override prefix for.
	 * @return  The override prefix for the Command. Null if no override exists.
	 */
	public String getOverridePrefix(Command command);
	
	/**
	 * Get a Set containing all override prefixes.
	 * 
	 * @return  A Set of all override prefixes.
	 */
	public Set<String> getAllPrefixes();
	
	/**
	 * Get a Set containing all aliases that have the override prefix of prefix.
	 * 
	 * @param   prefix
	 *          The prefix to get aliases for.
	 * @return  A Set of all aliases with the override prefix of prefix.
	 */
	public Set<String> getAliasesWithPrefix(String prefix);

}
