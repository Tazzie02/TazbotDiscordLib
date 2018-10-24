package com.tazzie02.tazbotdiscordlib;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.login.LoginException;

import static org.junit.Assert.*;

public class TazbotDiscordLibTest {

    private static final String BOT_TOKEN;

    private TazbotDiscordLib tdl;

    static {
        BOT_TOKEN = System.getProperty("BotToken");
    }

    @Before
    public void login() {
        TazbotDiscordLibBuilder builder = new TazbotDiscordLibBuilder(BOT_TOKEN);
        try {
            this.tdl = builder.build();
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void logout() {
        if (tdl != null) {
            tdl.shutdown();
            tdl = null;
        }
    }

    @Test
    public void tazbotDiscordLibTest() {
        assertNotNull(tdl.getJDA());
    }

}