package home.com.smarthome.sctp;

import android.support.annotation.IntegerRes;

import java.nio.ByteBuffer;

public class SctpCmdSetLinkContent extends SctpCmd {

    private ScAddr addr = null;
    private ByteBuffer data = null;
    private boolean result = false;

    SctpCmdSetLinkContent(int inFlags, int inCmdId, ScAddr inAddr, ByteBuffer inData) {
        super(SctpCmd.SetLinkContent, inFlags, inCmdId, ScAddr.SIZE_BYTES + 4 + inData.capacity());
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
