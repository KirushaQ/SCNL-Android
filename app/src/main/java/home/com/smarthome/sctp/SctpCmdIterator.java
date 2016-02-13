package home.com.smarthome.sctp;

import java.nio.ByteBuffer;
import java.security.InvalidParameterException;
import java.util.Objects;

public class SctpCmdIterator extends SctpCmd {

    private Object [] params = null;
    private int iteratorType = -1;
    private SctpIterator result = null;

    SctpCmdIterator(int inFlags, int inCmdId, int inIteratorType, Object param1, Object param2, Object param3) {

        super(SctpCmd.IterateElements, inFlags, inCmdId, 0);

        iteratorType = inIteratorType;
        params = new Object[3];
        params[0] = param1;
        params[1] = param2;
        params[2] = param3;

        argsSize = calculateParamsSize();

        check();
    }

    SctpCmdIterator(int inFlags, int inCmdId, int inIteratorType, Object param1, Object param2, Object param3, Object param4, Object param5) {

        super(SctpCmd.IterateElements, inFlags, inCmdId, 0);

        iteratorType = inIteratorType;
        params = new Object[5];
        params[0] = param1;
        params[1] = param2;
        params[2] = param3;
        params[3] = param4;
        params[4] = param5;

        argsSize = calculateParamsSize();

        check();
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.put((byte) iteratorType);
        for (int i = 0; i < params.length; ++i) {
            if (params[i] instanceof ScAddr) {
                bb.putInt(((ScAddr)params[i]).getValue());
            } else if (params[i] instanceof ScType) {
                bb.putChar((char)((ScType)params[i]).getValue());
            }
        }
    }

    @Override
    protected boolean parseResultData(SctpResult inResult) {

        if (inResult.getResultSize() == 0 || !inResult.isResultOk())
            return false;

        result = new SctpIterator(inResult.getResultData(), params.length);

        return true;
    }

    public SctpIterator getResult() {
        return result;
    }

    private int calculateParamsSize() {
        int size = 1;

        for (int i = 0; i < params.length; ++i) {
            if (params[i] instanceof ScAddr) {
                size += ScAddr.SIZE_BYTES;
            } else if (params[i] instanceof ScType) {
                size += ScType.SIZE_BYTES;
            }
        }
        return size;
    }

    private void check() {
        boolean result = false;
        switch (iteratorType) {
            case SctpIterator.Iterator3A_A_F:
            case SctpIterator.Iterator3F_A_A:
            case SctpIterator.Iterator3F_A_F:
                result = (params.length == 3);
                break;

            case SctpIterator.Iterator5A_A_F_A_A:
            case SctpIterator.Iterator5A_A_F_A_F:
            case SctpIterator.Iterator5F_A_A_A_A:
            case SctpIterator.Iterator5F_A_A_A_F:
            case SctpIterator.Iterator5F_A_F_A_A:
            case SctpIterator.Iterator5F_A_F_A_F:
                result = (params.length == 5);
                break;
        }

        if (!result)
            throw new InvalidParameterException();
    }
}
