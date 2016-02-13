package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdElementType extends SctpCmd {

    private ScAddr addr = null;
    private ScType resultType = null;

    SctpCmdElementType(int inFlags, int inCmdId, ScAddr inAddr) {
        super(SctpCmd.GetElementType, inFlags, inCmdId, ScAddr.SIZE_BYTES);
        addr = inAddr;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putInt(addr.getValue());
    }

    @Override
    protected boolean parseResultData(SctpResult inResult) {

        if (inResult.getResultSize() != ScType.SIZE_BYTES)
            return false;

        ByteBuffer bb = inResult.getResultData();
        resultType = new ScType(bb.getChar());

        return true;
    }

    public ScType getResult() {
        return resultType;
    }
}
