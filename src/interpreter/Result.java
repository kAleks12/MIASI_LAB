package interpreter;

enum Type {
    BOOLEAN,
    INTEGER,
    DOUBLE,
    ERROR
}
public record Result (String value, Type clazz) {
    Boolean getAsBoolean() {
        return switch (clazz) {
            case BOOLEAN -> Boolean.parseBoolean(value);
            case INTEGER -> Integer.parseInt(value) != 0;
            case DOUBLE -> Double.parseDouble(value) != 0;
            case ERROR -> null;
        };
    }

    Integer getAsInt() {
        return switch (clazz) {
            case BOOLEAN -> Boolean.parseBoolean(value) ? 1 : 0;
            case INTEGER -> Integer.parseInt(value);
            case DOUBLE -> (int) Double.parseDouble(value);
            case ERROR -> null;
        };
    }

    Double getAsDouble() {
        return switch (clazz) {
            case BOOLEAN -> Boolean.parseBoolean(value) ? 1.0 : 0.0;
            case INTEGER, DOUBLE -> Double.parseDouble(value);
            case ERROR -> null;
        };
    }
}
