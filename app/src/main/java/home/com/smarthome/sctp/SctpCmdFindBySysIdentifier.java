package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdFindBySysIdentifier extends SctpCmd {

    private ByteBuffer data = null;
    private ScAddr result = null;

    SctpCmdFindBySysIdentifier(int inFlags, int inCmdId, ByteBuffer inData) {
        super(SctpCmd.FindElementBySysIdtf, inFlags, inCmdId, 4 + inData.capacity());
        data = inData;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putInt(data.capacity());
        bb.put(data);
    }

    @Override
    protected boolean parseResultData(SctpResult inResult) {

        if (inResult.getResultSize() != ScAddr.SIZE_BYTES)
            return false;

        ByteBuffer bb = inResult.getResultData();
        result = new ScAddr(bb.getInt());

        return true;
    }

    public ScAddr getResult() {
        return result;
    }
}
