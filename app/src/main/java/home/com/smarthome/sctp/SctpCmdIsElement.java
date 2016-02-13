package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdIsElement extends SctpCmd {

    private ScAddr addr = null;
    private boolean result = false;

    SctpCmdIsElement(int inFlags, int inCmdId, ScAddr inAddr) {
        super(SctpCmd.CheckElement, inFlags, inCmdId, ScAddr.SIZE_BYTES);
        addr = inAddr;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putInt(addr.getValue());
    }
    @Override
    protected boolean parseResultData(SctpResult inResult) {
        if (inResult.getResultSize() != 0)
            return false;

        result = inResult.isResultOk();
        return true;
    }

    public boolean getResult() {
        return result;
    }
}
