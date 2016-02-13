package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpIterator {

    static final public int Iterator3F_A_A      = 0;
    static final public int Iterator3A_A_F      = 1;
    static final public int Iterator3F_A_F      = 2;
    static final public int Iterator5F_A_A_A_F  = 3;
    static final public int Iterator5A_A_F_A_F  = 4;
    static final public int Iterator5F_A_F_A_F  = 5;
    static final public int Iterator5F_A_F_A_A  = 6;
    static final public int Iterator5F_A_A_A_A  = 7;
    static final public int Iterator5A_A_F_A_A  = 8;

    private ByteBuffer data = null;
    private int oneResultCount = 0;
    private int position = -1;
    private int resultsCount = 0;
    private ScAddr [] currentResult = null;

    SctpIterator(ByteBuffer inData, int inOneResultCount) {
        data = inData;
        oneResultCount = inOneResultCount;
        position = -1;

        resultsCount = data.getInt();
        currentResult = new ScAddr[oneResultCount];
    }

    public boolean next() {
        ++position;
        if (position < resultsCount) {
            for (int i = 0; i < oneResultCount; ++i) {
                currentResult[i] = new ScAddr(data.getInt());
            }
            return true;
        }
        return false;
    }

    public ScAddr value(int index) {
        if (index < currentResult.length)
            return currentResult[index];

        return null;
    }
}
