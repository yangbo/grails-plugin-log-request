package logrequest

import groovy.json.JsonOutput
import org.apache.groovy.io.StringBuilderWriter

class JsonUtils {
    /**
     * Pretty print json string and show Chinese character correctly without escape it.
     */
    static String prettyPrintJson(String json) {
        unescapeUnicode(JsonOutput.prettyPrint(json))
    }

    /**
     * Unescape unicode like '\u7d2e' to Unicode character.
     * Copy and modified from groovy.json.StringEscapeUtils#unescapeJava(java.lang.String)
     */
    static String unescapeUnicode(String str) {
        if (str == null) {
            return null;
        }
        try {
            Writer writer = new StringBuilderWriter(str.length());
            unescape(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringBuilderWriter
            throw new RuntimeException(ioe);
        }
    }

    static void unescape(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode character
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char) value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case 'u':
                        // uh-oh, we're in unicode country....
                        inUnicode = true;
                        break;
                    default:
                        out.write(ch);
                        break;
                }
                continue;
            } else if (ch == (char) '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }
}
