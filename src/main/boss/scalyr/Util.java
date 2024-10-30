package boss.scalyr;

import boss.scalyr.logs.EventAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Util {
    public static Integer stringToIntMemory(final String maxBufferRam) {
        if (maxBufferRam != null && !"".equals(maxBufferRam)) {
            String cleanMaxBufferRam = maxBufferRam.toLowerCase().trim();
            if (cleanMaxBufferRam.contains("m")) {
                return Integer.valueOf(cleanMaxBufferRam.substring(0, cleanMaxBufferRam.indexOf("m"))) * 1048576;
            } else if (cleanMaxBufferRam.contains("k")) {
                return Integer.valueOf(cleanMaxBufferRam.substring(0, cleanMaxBufferRam.indexOf("k"))) * 1024;
            } else {
                return Integer.valueOf(cleanMaxBufferRam);
            }
        }
        return null;
    }

    /**
     * Takes a string and returns an EventAttributes full of the attributes. Combine with other EventAttributes using EventAttributes#addAll method
     * @param extraAttributes kv string to separate
     * @return EventAttributes full of split attributes from passed string
     */
    public static EventAttributes makeEventAttributesFromString(String extraAttributes) {
        EventAttributes attributes = new EventAttributes();
        for (Map.Entry<String, String> e : Util.kvStringToMap(extraAttributes).entrySet()) {
            attributes.put(e.getKey(), e.getValue());
        }
        return attributes;
    }

    public static Map<String, String> kvStringToMap(String str) {
        return kvStringToMap(str, ",", "=");
    }

    public static Map<String, String> kvStringToMap(String str, String pairSeparator, String kvSeparator) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> kvMap = new HashMap<String, String>();
        String[] pairs = str.split(pairSeparator);
        for (String pair : pairs) {
            String[] kv = pair.split(kvSeparator);
            if (kv.length != 2 || kv[0].isEmpty() || kv[1].isEmpty()) {
                System.out.println("Ignoring pair " + pair);
                continue;
            }
            kvMap.put(kv[0].trim(), kv[1].trim());
        }
        return kvMap;
    }

    /**
     * Transforms a user-provided value for the Scalyr server address if necessary.
     *
     * @param address The user-provided value
     * @return  The value to use for the Scalyr server address.
     */
    public static String canonicalizeScalyrServerAddress(String address) {
        if (address == null) {
            return null;
        }

        address = address.trim();
        return address.isEmpty() ? null : address;
    }
}

