package com.tazzie02.tazbotdiscordlib;

import com.tazzie02.tazbotdiscordlib.impl.MessageSenderImpl;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.junit.*;

import javax.security.auth.login.LoginException;

import java.time.Instant;

import static org.junit.Assert.*;

public class TazbotDiscordLibTest {

    private static final String BOT_TOKEN;

    private static TazbotDiscordLib tdl;

    static {
        BOT_TOKEN = System.getProperty("BOT_TOKEN");
    }

    @BeforeClass
    public static void login() {
        TazbotDiscordLibBuilder builder = new TazbotDiscordLibBuilder(BOT_TOKEN);
        try {
            builder.setMessageSender(new MessageSenderImpl());
            tdl = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void logout() {
        if (tdl != null) {
            tdl.shutdown();
            tdl = null;
        }
    }

    @Test
    public void tazbotDiscordLibTest() {
        assertNotNull(tdl.getJDA());
    }

    @Test
    public void botNameTest() {
        String name = tdl.getJDA().getSelfUser().getName();
        System.out.println(name);
        assertNotNull(name);
    }

}