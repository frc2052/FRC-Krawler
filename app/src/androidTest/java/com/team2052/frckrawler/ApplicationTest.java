package com.team2052.frckrawler;

import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<FRCKrawler> {
    public ApplicationTest() {
        super(FRCKrawler.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}