package edu.sjsu.cmpe275.aop;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class AppTest {

	App app ;
    
    /**
     * Default constructor for test class AppTest
     */
    public AppTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        app = new App() ;
    }


    

}
