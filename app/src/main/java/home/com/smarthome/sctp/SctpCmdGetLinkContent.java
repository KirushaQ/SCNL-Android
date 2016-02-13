package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdGetLinkContent extends SctpCmd {

    private ScAddr addr = null;
    private ByteBuffer result = null;

    SctpCmdGetLinkContent(int inFlags, int inCmdId, ScAddr inAddr) {
        super(SctpCmd.GetLinkContent, inFlags, inCmdId, ScAddr.SIZE_BYTES);
        addr = inAddr;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putInt(addr.getValue());
    }

    @Override
    protected boolean parseResultData(SctpResult inResult) {

        if (inResult.getResultSize() == 0)
            return false;

        result = inResult.getResultData();

        return true;
    }

    public ByteBuffer getResult() {
        return result;
    }
}
