package home.com.smarthome.sctp;

public class ScType {

    static public int Node = 0x1;
    static public int Link = 0x2;
    static public int EdgeCommon = 0x4;
    static public int ArcCommon = 0x8;
    static public int ArcAccess = 0x10;

    static public int Const = 0x20;
    static public int Var = 0x40;

    static public int ArcPos = 0x80;
    static public int ArcNeg = 0x100;
    static public int ArcFuz = 0x200;

    static public int ArcTemp = 0x400;
    static public int ArcPerm = 0x800;

    static public int NodeTuple = 0x80;
    static public int NodeStruct = 0x100;
    static public int NodeRole = 0x200;
    static public int NodeNoRole = 0x400;
    static public int NodeClass = 0x800;
    static public int NodeAbstract = 0x1000;
    static public int NodeMaterial = 0x2000;

    static public int ArcPosConstPerm = ArcAccess | Const | ArcPos | ArcPerm;

    final static public int SIZE_BYTES = 2;

    private int Value;

    public ScType() {
        Value = 0;
    }

    public ScType(int inValue) {
        Value = inValue;
    }

    public boolean isValid() {
        return (Value != 0);
    }

    public int getValue() {
        return Value;
    }

    public boolean isEqual(ScType inType) {
        return Value == inType.Value;
    }
}
