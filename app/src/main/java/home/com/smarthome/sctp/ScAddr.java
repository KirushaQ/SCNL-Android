package home.com.smarthome.sctp;

public class ScAddr {
    protected int Value;
    final static public int SIZE_BYTES = 4;

    public ScAddr() {
        Value = 0;
    }

    public ScAddr(int InValue) {
        Value = InValue;
    }

    public int getValue() {
        return Value;
    }

    public boolean isValid() {
        return (Value != 0);
    }

    public boolean isEqual(ScAddr other) {
        return (Value == other.Value);
    }
}