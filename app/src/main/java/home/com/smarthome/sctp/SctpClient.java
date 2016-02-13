package home.com.smarthome.sctp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SctpClient {

    private Socket socket;
    private BufferedReader reader;

    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    public boolean connect(String host, int port) {

        try {
            socket = new Socket(host, port);
            if (socket.isConnected()) {
                outputStream = new DataOutputStream(socket.getOutputStream());
                inputStream = new DataInputStream(socket.getInputStream());
                return true;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ScAddr createNode(ScType scType) {
        SctpCmdCreateNode command = new SctpCmdCreateNode((byte)0, 0, scType);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream()))
                return command.getResult();
        }
        return null;
    }

    public ScAddr createArc(ScAddr beginAddr, ScAddr endAddr, ScType scType) {
        SctpCmdCreateArc command = new SctpCmdCreateArc((byte)0, 0, scType, beginAddr, endAddr);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream()))
                return command.getResult();
        }
        return null;
    }

    public ScAddr createLink() {
        SctpCmdCreateLink command = new SctpCmdCreateLink((byte)0, 0);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream()))
                return command.getResult();
        }
        return null;
    }

    public boolean isElement(ScAddr addr) {
        SctpCmdIsElement command = new SctpCmdIsElement((byte)0, 0, addr);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream()))
                return command.getResult();
        }
        return false;
    }

    public boolean eraseElement(ScAddr addr) {
        SctpCmdEraseElement command = new SctpCmdEraseElement((byte)0, 0, addr);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream()))
                return command.getResult();
        }
        return false;
    }

    public ScType getElementType(ScAddr addr) {
        SctpCmdElementType command = new SctpCmdElementType((byte)0, 0, addr);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }
        return null;
    }

    public boolean setElementSubtype(ScAddr addr, ScType type) {
        return false;
    }

    public ScArcInfo getArcInfo(ScAddr arcAddr) {
        SctpCmdArcInfo command = new SctpCmdArcInfo((byte)0, 0, arcAddr);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }
        return null;
    }

    public boolean setLinkContent(ScAddr addr, ByteBuffer data) {
        SctpCmdSetLinkContent command = new SctpCmdSetLinkContent((byte)0, 0, addr, data);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }

        return false;
    }

    public ByteBuffer getLinkContent(ScAddr addr) {

        SctpCmdGetLinkContent command = new SctpCmdGetLinkContent((byte)0, 0, addr);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }

        return null;
    }

    public ScAddr[] findLinksByContent(ByteBuffer data) {

        SctpCmdFindLinksByContent command = new SctpCmdFindLinksByContent((byte)0, 0, data);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }

        return null;
    }

    public ScAddr findElementBySystemIdentifier(String sysIdtf) {
        SctpCmdFindBySysIdentifier command = new SctpCmdFindBySysIdentifier((byte)0, 0, ByteBufferFromString(sysIdtf));
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }
        return null;
    }

    public boolean setSystemIdentifier(ScAddr addr, String sysIdtf) {
        SctpCmdSetSysIdentifier command = new SctpCmdSetSysIdentifier((byte)0, 0, addr, ByteBufferFromString(sysIdtf));
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }
        return false;
    }

    public SctpIterator iterate3(int iteratorType, Object param1, Object param2, Object param3) {
        SctpCmdIterator command = new SctpCmdIterator((byte)0, 0, iteratorType, param1, param2, param3);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }

        return null;
    }

    public SctpIterator iterate5(int iteratorType, Object param1, Object param2, Object param3, Object param4, Object param5) {
        SctpCmdIterator command = new SctpCmdIterator((byte)0, 0, iteratorType, param1, param2, param3, param4, param5);
        if (command.writeToStream(getOutputStream())) {
            if (command.readFromStream(getInputStream())) {
                return command.getResult();
            }
        }

        return null;
    }

    private DataInputStream getInputStream() {
        return inputStream;
    }

    private DataOutputStream getOutputStream() {
        return outputStream;
    }

    static public ByteBuffer ByteBufferFromString(String str) {
        ByteBuffer result = null;
        try {
            byte[] bytes = str.getBytes("UTF-8");
            result = ByteBuffer.wrap(bytes);
            result.order(ByteOrder.LITTLE_ENDIAN);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    static public String ByteBufferToString(ByteBuffer bb) {

        try {
            return new String(bb.array(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
