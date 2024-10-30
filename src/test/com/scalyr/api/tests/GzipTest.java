package com.scalyr.api.tests;

import boss.scalyr.TuningConstants;
import boss.scalyr.knobs.ConfigurationFile;
import boss.scalyr.knobs.Knob;
import boss.scalyr.logs.EventAttributes;
import boss.scalyr.logs.Events;
import boss.scalyr.logs.LogService;


import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for Gzip compression. These are marked as @Ignore b/c they are dependent on a local server instance
 * or a remote Scalyr environment. You must manually check that the test logs are received and decompressed
 * correctly at the destination.
 */
@Ignore public class GzipTest extends LogsTestBase {

  // Put appropriate log write token here.
  String apiLogWriteKey = "";

  // Either put localhost here, or a particular Scalyr environment. Remember to use "https" for staging and prod.
  String serverAddress = "https://scalyr.com";

  @Test public void testGzipOnJavaNetHttpClient() {
    LogService testService = new LogService(apiLogWriteKey);
    testService = testService.setServerAddress(serverAddress);

    Events._reset("testGzipSession", testService, 999999, false, true);
    // Enable Gzip in case it's not enabled by default
    Events.enableGzip();

    Events.info(new EventAttributes("tag", "testWithGzipJava", "foo1", "bla1", "foo2", "bla2"));
    Events.flush();
  }

  @Test public void testGzipOnApacheHttpClient() {
    Knob.setDefaultFiles(new ConfigurationFile[0]);

    LogService testService = new LogService(apiLogWriteKey);
    testService = testService.setServerAddress(serverAddress);

    Events._reset("testGzipSession", testService, 999999, false, true);
    // Enable Gzip in case it's not enabled by default
    Events.enableGzip();

    // Turn on usage of ApacheHTTPClient
    TuningConstants.useApacheHttpClientForEventUploader = new Knob.Boolean("foo", true);

    Events.info(new EventAttributes("tag", "testWithGzipApache", "foo1", "bla1", "foo2", "bla2"));
    Events.flush();

    TuningConstants.useApacheHttpClientForEventUploader = null; // Set it back to original value of null
  }
}
