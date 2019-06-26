package com.example.smartbeds;

import com.example.smartbeds.session.Session;

import org.junit.Test;

import static org.junit.Assert.*;

public class SessionUnitTest {
    @Test
    public void sessionIsSingleton() {
        Session session1 = Session.getInstance();
        Session session2 = Session.getInstance();

        assertEquals(session1, session2);
    }

    @Test
    public void resetSession() {
        Session session = Session.getInstance();
        session.setToken("XXX");
        session.setUsername("alicia");
        session.setRole("user");

        assertEquals(session.getToken(), "XXX");
        assertEquals(session.getUsername(), "alicia");
        assertEquals(session.getRole(), "user");

        Session.resetSession();

        assertNotEquals(session.getToken(), "XXX");
        assertNotEquals(session.getUsername(), "alicia");
        assertNotEquals(session.getRole(), "user");
    }
}