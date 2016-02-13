package home.com.smarthome.sctp;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SctpCmd {

    final static public int CheckElement         = 0x01; // check if specified sc-element exist
    final static public int GetElementType       = 0x02; // return sc-element type
    final static public int EraseElement         = 0x03; // erase specified sc-element
    final static public int CreateNode           = 0x04; // create new sc-node
    final static public int CreateLink           = 0x05; // create new sc-link
    final static public int CreateArc            = 0x06; // create new sc-arc
    final static public int GetArc               = 0x07; // return begin element of sc-arc

    final static public int GetLinkContent       = 0x09; // return content of sc-link
    final static public int FindLinksByContent = 0x0a; // return sc-links with specified content
    final static public int SetLinkContent       = 0x0b; // setup new content for the link
    final static public int IterateElements      = 0x0c; // return base template iteration results
    final static public int IterateConstruction  = 0x0d; // return advanced template iteration results
    final static public int EventCreate          = 0x0e; // create subscription to specified event
    final static public  int EventDestroy         = 0x0f; // destroys specified event subscription
    final static public int EventEmit            = 0x10; // emits events to client

    final static public int FindElementBySysIdtf = 0xa0; // return sc-element by it system identifier
    final static public int SetSystemIdentifier  = 0xa1;   // setup new system identifier for sc-element
    final static public int Statistics           = 0xa2; // return usage statistics from server
    final static public int Version              = 0xa3;  // return version of used sctp protocol

    final static public int HEADER_SIZE_BYTES   = 10;

    protected int cmdCode = 0;
    protected int flags = 0;
    protected int cmdId = 0;
    protected int argsSize = 0;
    protected SctpResult result = null;

    public SctpCmd() {
    }

    public SctpCmd(int inCmdCode, int inFlags, int inCmdId, int inArgsSize) {
        cmdCode = inCmdCode;
        flags = inFlags;
        cmdId = inCmdId;
        argsSize = inArgsSize;
    }

    public boolean isValid() {
        return (cmdCode != 0);
    }

    protected boolean writeCommandHeader(DataOutputStream outputStream) {

        ByteBuffer bb = allocateBuffer(HEADER_SIZE_BYTES);
        bb.put((byte)cmdCode);
        bb.put((byte) flags);
        bb.putInt(cmdId);
        bb.putInt(argsSize);

        return writeBufferToOutputStream(outputStream, bb);
    }

    protected void putParamsBuffer(ByteBuffer bb) {
    }

    public boolean writeToStream(DataOutputStream outputStream) {
        ByteBuffer params = allocateBuffer(argsSize);
        putParamsBuffer(params);
        return (writeCommandHeader(outputStream) && writeBufferToOutputStream(outputStream, params));
    }

    private boolean writeBufferToOutputStream(DataOutputStream outputStream, ByteBuffer bb) {
        try {
            outputStream.write(bb.array());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean readFromStream(DataInputStream inputStream) {
        result = new SctpResult();
        if (!result.readFromStream(inputStream))
            return false;
        return parseResultData(result);
    }

    protected boolean parseResultData(SctpResult inResult) {
        return inResult.isResultOk();
    }

    static public ByteBuffer allocateBuffer(int size) {
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb;
    }
}
