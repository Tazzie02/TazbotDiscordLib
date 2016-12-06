# TazbotDiscordLib using [JDA](https://github.com/DV8FromTheWorld/JDA)
[![Build Status](https://travis-ci.org/Tazzie02/TazbotDiscordLib.svg?branch=master)](https://travis-ci.org/Tazzie02/TazbotDiscordLib)

###Basic example using default implementers.
```java
// Create the builder with the bot token
String token = "MY_TOKEN";
TazbotDiscordLibBuilder builder = new TazbotDiscordLibBuilder(token);
// Set the location files will be stored
builder.setFilePath(Paths.get(""));

try {
    TazbotDiscordLib tdl = builder.build();

    // Create the default MessageSender and add the default MessageLogger to it
    MessageLoggerImpl logger = new MessageLoggerImpl();
    MessageSenderImpl sender = new MessageSenderImpl();
    sender.setMessageSentLogger(logger);
    tdl.setMessageSender(sender);

    LocalFiles filesInstance = LocalFiles.getInstance(tdl.getJDA());

    // Create a CommandRegistry to manage Commands
    CommandRegistry registry = new CommandRegistry();
	// Register the basic included Commands to the CommandRegistry
    registry.registerCommand(new HelpCommand(registry));
    registry.registerCommand(new PingCommand());
    registry.registerCommand(new ShutdownCommand());
    registry.setCaseSensitiveCommands(false);
	// Set the owners as per the config file
    registry.setOwners(filesInstance.getConfig());
	// Set the CommandSettings for all as well as guild overrides
    registry.setDefaultCommandSettings(filesInstance);
    registry.setGuildCommandSettings(filesInstance);
	// Use the MessageLogger to log received messages
    registry.setMessageReceivedLogger(logger);

    // Add the CommandRegistry to the TazbotDiscordLib object
    tdl.addListener(registry);
} catch (LoginException e) {
    e.printStackTrace();
} catch (IllegalArgumentException e) {
    e.printStackTrace();
} catch (InterruptedException e) {
    e.printStackTrace();
}
```
