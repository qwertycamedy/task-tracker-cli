package io.github.qwertycamedy.tasktracker.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A small recursive-descent JSON parser written without third-party libraries.
 * <p>
 * Result types: {@code Map<String, Object>} for objects, {@code List<Object>} for arrays,
 * {@code String}, {@code Long} or {@code Double} for numbers, {@code Boolean}, and {@code null}.
 */
public final class JsonParser {

    private final String input;
    private int pos;

    private JsonParser(String input) {
        this.input = input;
    }

    public static Object parse(String json) {
        JsonParser parser = new JsonParser(json);
        parser.skipWhitespace();
        Object value = parser.readValue();
        parser.skipWhitespace();
        if (!parser.isEnd()) {
            throw parser.error("Unexpected trailing characters");
        }
        return value;
    }

    private Object readValue() {
        if (isEnd()) {
            throw error("Unexpected end of input");
        }
        char c = peek();
        return switch (c) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> readString();
            case 't' -> readLiteral("true", Boolean.TRUE);
            case 'f' -> readLiteral("false", Boolean.FALSE);
            case 'n' -> readLiteral("null", null);
            default -> {
                if (c == '-' || Character.isDigit(c)) {
                    yield readNumber();
                }
                throw error("Unexpected character '" + c + "'");
            }
        };
    }

    private Map<String, Object> readObject() {
        expect('{');
        Map<String, Object> result = new LinkedHashMap<>();
        skipWhitespace();
        if (tryConsume('}')) {
            return result;
        }
        do {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            result.put(key, readValue());
            skipWhitespace();
        } while (tryConsume(','));
        expect('}');
        return result;
    }

    private List<Object> readArray() {
        expect('[');
        List<Object> result = new ArrayList<>();
        skipWhitespace();
        if (tryConsume(']')) {
            return result;
        }
        do {
            skipWhitespace();
            result.add(readValue());
            skipWhitespace();
        } while (tryConsume(','));
        expect(']');
        return result;
    }

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (isEnd()) {
                throw error("Unterminated string");
            }
            char c = input.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c != '\\') {
                sb.append(c);
                continue;
            }
            if (isEnd()) {
                throw error("Unterminated escape sequence");
            }
            char escaped = input.charAt(pos++);
            switch (escaped) {
                case '"', '\\', '/' -> sb.append(escaped);
                case 'b' -> sb.append('\b');
                case 'f' -> sb.append('\f');
                case 'n' -> sb.append('\n');
                case 'r' -> sb.append('\r');
                case 't' -> sb.append('\t');
                case 'u' -> sb.append(readUnicodeEscape());
                default -> throw error("Invalid escape '\\" + escaped + "'");
            }
        }
    }

    private char readUnicodeEscape() {
        if (pos + 4 > input.length()) {
            throw error("Incomplete unicode escape");
        }
        String hex = input.substring(pos, pos + 4);
        pos += 4;
        try {
            return (char) Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            throw error("Invalid unicode escape '\\u" + hex + "'");
        }
    }

    private Number readNumber() {
        int start = pos;
        tryConsume('-');
        while (!isEnd() && (Character.isDigit(peek()) || "+-.eE".indexOf(peek()) >= 0)) {
            pos++;
        }
        String number = input.substring(start, pos);
        try {
            if (number.contains(".") || number.contains("e") || number.contains("E")) {
                return Double.parseDouble(number);
            }
            return Long.parseLong(number);
        } catch (NumberFormatException e) {
            throw error("Invalid number '" + number + "'");
        }
    }

    private Object readLiteral(String literal, Object value) {
        if (!input.startsWith(literal, pos)) {
            throw error("Expected '" + literal + "'");
        }
        pos += literal.length();
        return value;
    }

    private void skipWhitespace() {
        while (!isEnd() && Character.isWhitespace(peek())) {
            pos++;
        }
    }

    private void expect(char expected) {
        if (!tryConsume(expected)) {
            throw error("Expected '" + expected + "'");
        }
    }

    private boolean tryConsume(char c) {
        if (!isEnd() && peek() == c) {
            pos++;
            return true;
        }
        return false;
    }

    private char peek() {
        return input.charAt(pos);
    }

    private boolean isEnd() {
        return pos >= input.length();
    }

    private JsonException error(String message) {
        return new JsonException(message + " at position " + pos);
    }
}
