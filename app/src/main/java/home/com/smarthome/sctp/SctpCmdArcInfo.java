package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdArcInfo extends SctpCmd {

    private ScAddr addr = null;
    private ScArcInfo result = null;

    SctpCmdArcInfo(int inFlags, int inCmdId, ScAddr inAddr) {
        super(SctpCmd.GetArc, inFlags, inCmdId, ScAddr.SIZE_BYTES);
        addr = inAddr;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putInt(addr.getValue());
    }

    @Override
    protected boolean parseResultData(SctpResult inResult) {

        if (inResult.getResultSize() != 2 * ScAddr.SIZE_BYTES)
            return false;

        ByteBuffer bb = inResult.getResultData();

        result = new ScArcInfo(new ScAddr(bb.getInt()), new ScAddr(bb.getInt()));

        return true;
    }

    public ScArcInfo getResult() {
        return result;
    }
}
