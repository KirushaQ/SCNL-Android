package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdSetSysIdentifier extends SctpCmd {

    private ScAddr addr = null;
    private ByteBuffer data = null;
    private boolean result = false;

    SctpCmdSetSysIdentifier(int inFlags, int inCmdId, ScAddr inAddr, ByteBuffer inData) {
        super(SctpCmd.SetSystemIdentifier, inFlags, inCmdId, ScAddr.SIZE_BYTES + 4 + inData.capacity());
        addr = inAddr;
        data = inData;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putInt(addr.getValue());
        bb.putInt(data.capacity());
        bb.put(data);
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
