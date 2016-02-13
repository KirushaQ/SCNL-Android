package home.com.smarthome.sctp;

import org.apache.commons.io.IOExceptionWithCause;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SctpResult {

    final static public int HEADER_SIZE = 10;

    final static public int ResultOk        = 0x00;
    final static public int ResultFail      = 0x01;
    final static public int ErrorNoElement  = 0x02;
    final static public int ErrorNoRights   = 0x03;

    protected int cmdCode;
    protected int cmdId;
    protected int cmdReturnCode;
    protected int resultSize;
    protected ByteBuffer resultData;

    SctpResult() {
        cmdCode = 0;
        cmdId = 0;
        cmdReturnCode = 0;
        resultSize = 0;
        resultData = null;
    }

    public boolean isResultOk() {
        return (cmdReturnCode == ResultOk);
    }

    public boolean readFromStream(DataInputStream inputStream) {
        byte [] data = new byte[HEADER_SIZE];

        if (!readIntoBuffer(inputStream, data))
            return false;

        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        cmdCode = bb.get();
        cmdId = bb.getInt();
        cmdReturnCode = bb.get();
        resultSize = bb.getInt();

        if (resultSize > 0) {
            byte [] resultBuff = new byte[resultSize];
            if (!readIntoBuffer(inputStream, resultBuff))
                return false;

            resultData = ByteBuffer.wrap(resultBuff);
            resultData.order(ByteOrder.LITTLE_ENDIAN);
        }

        return true;
    }

    public ByteBuffer getResultData() {
        return resultData;
    }

    public int getResultSize() {
        return resultSize;
    }

    public int getReturnCode() {
        return cmdReturnCode;
    }

    public int getId() {
        return cmdId;
    }

    private boolean readIntoBuffer(DataInputStream inputStream, byte [] buffer) {
        try {
            int readBytes = inputStream.read(buffer);
            return (buffer.length == readBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
