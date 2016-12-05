# TazbotDiscordLib using [JDA](https://github.com/DV8FromTheWorld/JDA)

###Basic example using default implementers.
```
String token = "MY_TOKEN";
TazbotDiscordLibBuilder builder = new TazbotDiscordLibBuilder(token);
builder.setFilePath(Paths.get(""));

try {
    TazbotDiscordLib tdl = builder.build();

    MessageLoggerImpl logger = new MessageLoggerImpl();
    MessageSenderImpl sender = new MessageSenderImpl();
    sender.setMessageSentLogger(logger);
    tdl.setMessageSender(sender);

    LocalFiles filesInstance = LocalFiles.getInstance(tdl.getJDA());

    CommandRegistry registry = new CommandRegistry();
    registry.registerCommand(new HelpCommand(registry));
    registry.registerCommand(new PingCommand());
    registry.registerCommand(new ShutdownCommand());
    registry.setCaseSensitiveCommands(false);
    registry.setOwners(filesInstance.getConfig());
    registry.setDefaultCommandSettings(filesInstance);
    registry.setGuildCommandSettings(filesInstance);
    registry.setMessageReceivedLogger(logger);

    tdl.addListener(registry);
} catch (LoginException e) {
    e.printStackTrace();
} catch (IllegalArgumentException e) {
    e.printStackTrace();
} catch (InterruptedException e) {
    e.printStackTrace();
}
```
