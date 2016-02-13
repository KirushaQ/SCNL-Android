package home.com.smarthome.sctp;

import java.nio.ByteBuffer;

public class SctpCmdFindLinksByContent extends SctpCmd {
    private ByteBuffer data = null;
    private ScAddr []results = null;

    SctpCmdFindLinksByContent(int inFlags, int inCmdId, ByteBuffer inData) {
        super(SctpCmd.FindLinksByContent, inFlags, inCmdId, 4 + inData.capacity());
        data = inData;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putInt(data.capacity());
        bb.put(data);
    }

    @Override
    protected boolean parseResultData(SctpResult inResult) {

        if (!inResult.isResultOk() || (inResult.getResultSize() != ScAddr.SIZE_BYTES))
            return false;

        ByteBuffer bb = inResult.getResultData();
        int count = bb.getInt();

        results = new ScAddr[count];
        for (int i = 0; i < results.length; ++i) {
            results[i] = new ScAddr(bb.getInt());
        }

        return true;
    }

    public ScAddr[] getResult() {
        return results;
    }

}


