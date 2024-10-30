package boss.scalyr;

import boss.scalyr.logs.EventAttributes;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import boss.scalyr.logs.Events;

public class ScalyrAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private String apiKey = "";
    private String serverHost = "";
    private String env = "";
    private Integer maxBufferRam;
    private String logfile = "logback";
    private String parser = "logback";
    private String extraAttributes = "";
    private Layout<ILoggingEvent> layout;
    // The address of the Scalyr servers to use.  Null will force us to use the default server address.
    private String scalyrServerAddress = null;

    @Override protected void append(ILoggingEvent event) {
        int level = event.getLevel().toInt();
        String message = layout.doLayout(event);

        if (level >= Level.ERROR_INT) {
            Events.error(new EventAttributes("message", message));
        } else if (level >= Level.WARN_INT) {
            Events.warning(new EventAttributes("message", message));
        } else if (level >= Level.INFO_INT) {
            Events.info(new EventAttributes("message", message));
        } else if (level >= Level.DEBUG_INT) {
            Events.fine(new EventAttributes("message", message));
        } else if (level >= Level.TRACE_INT) {
            Events.finer(new EventAttributes("message", message));
        } else {
            Events.finest(new EventAttributes("message", message));
        }
    }

    public String getApiKey() { return this.apiKey; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getServerHost() { return this.serverHost == null ? "" : this.serverHost.trim(); }

    public void setServerHost(String serverHost) { this.serverHost = serverHost; }

    public Integer getMaxBufferRam() { return maxBufferRam; }

    public void setMaxBufferRam(String maxBufferRam) { this.maxBufferRam = Util.stringToIntMemory(maxBufferRam); }

    public void setLogfile(String logfile) { this.logfile = logfile; }

    public String getLogfile() { return logfile; }

    public String getParser() { return parser; }

    public void setEnv(String env) { this.env = env; }

    public String getEnv() { return env; }

    public String getScalyrServerAddress() { return this.scalyrServerAddress; }

    public void setScalyrServerAddress(String scalyrServerAddress) {
        this.scalyrServerAddress = Util.canonicalizeScalyrServerAddress(scalyrServerAddress);
    }

    /**
     * Use this to describe any additional server attributes. Takes a string that is kv pairs separated by a comma e.g.:
     * <pre>
     *   appName=appofdoom,zodiac=rooster
     * </pre>
     * An example of what to put in your logback.xml:
     <pre><code>
     &lt;configuration&gt;
     &lt;appender name="scalyr" class="com.scalyr.logback.ScalyrAppender"&gt;
     &lt;apiKey&gt;YOUR KEY&lt;/apiKey&gt;
     &lt;extraAttributes&gt;appName=appofdoom,zodiac=rooster&lt;/extraAttributes&gt;
     ...
     &lt;/appender&gt;
     &lt;/configuration&gt;
     </code></pre>
     * <p>
     * The extraAttributes will be appended to the serverAttributes list and will be searchable in scalyr by a query like:
     * <pre>
     *   $zodiac == "rooster" "hello world"
     * </pre>
     *
     * <p>
     * If your account is hosted on https://eu.scalyr.com, then you should override the <code>scalyrServerAddress</code>
     * value with <code>https://upload.eu.scalyr.com</code>.  For example,
     <pre><code>
     &lt;configuration&gt;
     &lt;appender name="scalyr" class="com.scalyr.logback.ScalyrAppender"&gt;
     &lt;apiKey&gt;YOUR KEY&lt;/apiKey&gt;
     &lt;scalyrServerAddress&gt;https://upload.eu.scalyr.com&lt;/scalyrServerAddress&gt;
     ...
     &lt;/appender&gt;
     &lt;/configuration&gt;
     </code></pre>
     * <p>
     * @param extraAttributes String of key-value pairs
     */
    public void setExtraAttributes(String extraAttributes) { this.extraAttributes = extraAttributes; }

    public String getExtraAttributes() { return extraAttributes; }

    public void setParser(String parser) { this.parser = parser; }

    public Layout<ILoggingEvent> getLayout() { return layout; }

    /**
     * Sets the Logback layout to use for this appender.  The default layout
     * consists of the first character of the level name (E, W, I, D, T for error,
     * warning, info, debug, and trace, respectively) followed by the message.
     *
     * The Layout should have the logger context set and be started.
     *
     * @param layout the Layout to use
     */
    public void setLayout(Layout<ILoggingEvent> layout) { this.layout = layout; }

    @Override
    public void start() {
        if (layout == null) {
            //default layout
            layout = new PatternLayout();
            ((PatternLayout) layout).setPattern("%.-1level %msg");
            layout.setContext(context);
            layout.start();
        }

        final EventAttributes serverAttributes = new EventAttributes();
        if (getServerHost().length() > 0)
            serverAttributes.put("serverHost", getServerHost());
        serverAttributes.put("logfile", getLogfile());
        serverAttributes.put("parser", getParser());
        serverAttributes.put("env", getEnv());
        serverAttributes.addAll(Util.makeEventAttributesFromString(getExtraAttributes()));

        if(this.apiKey != null && !this.apiKey.trim().isEmpty()) {
            // default to 4MB if not set.
            int maxBufferRam = (this.maxBufferRam != null) ? this.maxBufferRam : 4194304;
            Events.init(this.apiKey.trim(), maxBufferRam, scalyrServerAddress, serverAttributes);
            super.start();
        } else {
            addError("Cannot initialize logging.  No Scalyr API Key has been set.");
        }
    }

    @Override public void stop() {
        Events.flush();
        super.stop();
    }
}