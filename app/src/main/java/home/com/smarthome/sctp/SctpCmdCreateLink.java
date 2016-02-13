package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdCreateLink extends SctpCmd {

    private ScAddr resultAddr = null;

    SctpCmdCreateLink(int inFlags, int inCmdId) {
        super(SctpCmd.CreateLink, inFlags, inCmdId, 0);
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
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
