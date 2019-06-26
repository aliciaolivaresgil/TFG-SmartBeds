package com.example.smartbeds;

import com.example.smartbeds.bedMonitoring.Bed;

import org.junit.Test;

import static org.junit.Assert.*;

public class BedUnitTest {
    @Test
    public void sessionIsSingleton() {
        Bed bed = new Bed("Cama 1", "dormido");

        assertEquals(bed.getBedName(), "Cama 1");
        assertEquals(bed.getBedState(), "dormido");

        bed.setBedState("crisis epiléptica");

        assertEquals(bed.getBedState(), "crisis epiléptica");
        assertNotEquals(bed.getBedState(), "dormido");
    }
}