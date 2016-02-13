package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdCreateArc extends SctpCmd {

    private ScType arcType = null;
    private ScAddr addrBegin = null;
    private ScAddr addrEnd = null;
    private ScAddr resultAddr = null;

    static private int PARAMS_BYTE_SIZE = ScType.SIZE_BYTES + 2 * ScAddr.SIZE_BYTES;

    SctpCmdCreateArc(int inFlags, int inCmdId, ScType inArcType, ScAddr inAddrBegin, ScAddr inAddrEnd) {
        super(SctpCmd.CreateArc, inFlags, inCmdId, PARAMS_BYTE_SIZE);
        arcType = inArcType;
        addrBegin = inAddrBegin;
        addrEnd = inAddrEnd;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putChar((char)arcType.getValue());
        bb.putInt(addrBegin.getValue());
        bb.putInt(addrEnd.getValue());
    }
    @Override
    protected boolean parseResultData(SctpResult inResult) {

        if (inResult.getResultSize() != ScAddr.SIZE_BYTES)
            return false;

        ByteBuffer bb = inResult.getResultData();
        resultAddr = new ScAddr(bb.getInt());

        return true;
    }

    public ScAddr getResult() {
        return resultAddr;
    }
}
