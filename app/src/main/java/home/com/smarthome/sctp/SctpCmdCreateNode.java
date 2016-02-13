package home.com.smarthome.sctp;

import java.nio.ByteBuffer;


public class SctpCmdCreateNode extends SctpCmd {

    private ScType nodeType = null;
    private ScAddr resultAddr = null;

    SctpCmdCreateNode(int inFlags, int inCmdId, ScType inType) {
        super(SctpCmd.CreateNode, inFlags, inCmdId, ScType.SIZE_BYTES);
        nodeType = inType;
    }

    @Override
    protected void putParamsBuffer(ByteBuffer bb) {
        bb.putChar((char)nodeType.getValue());
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
