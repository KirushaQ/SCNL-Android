package home.com.smarthome.sctp;

import android.os.AsyncTask;

import java.nio.ByteBuffer;

import home.com.smarthome.MainActivity;

public class TestTask extends AsyncTask<Void, Void, Void> {

    public MainActivity mainActivity = null;
    boolean isDone = false;

    protected Void doInBackground(Void... params) {
        SctpClient client = mainActivity.getSctpClient();
        ScType type = new ScType(ScType.NodeClass | ScType.Node);
        ScAddr addr = client.createNode(type);
        isDone = (addr != null) && addr.isValid();

        isDone = (isDone && client.isElement(addr));
        isDone = (isDone && (client.getElementType(addr).isEqual(type)));
        isDone = (isDone && (client.eraseElement(addr) && !client.isElement(addr)));

        ScAddr addrLink = client.createLink();
        isDone = (isDone && addrLink.isValid());
        addr = client.createNode(new ScType(ScType.NodeAbstract));
        isDone = (isDone && addr.isValid());

        type = new ScType(ScType.ArcPosConstPerm);
        ScAddr arcAddr = client.createArc(addr, addrLink, type);
        isDone = (isDone && arcAddr.isValid());
        isDone = (isDone && client.getElementType(arcAddr).isEqual(type));

        ScArcInfo arcInfo = client.getArcInfo(arcAddr);
        isDone = (isDone && (arcInfo != null));
        isDone = (isDone && arcInfo.beginAddr.isValid() && arcInfo.beginAddr.isEqual(addr));
        isDone = (isDone && arcInfo.endAddr.isValid() && arcInfo.endAddr.isEqual(addrLink));

        String s = new String("Test link content");
        ByteBuffer bb = SctpClient.ByteBufferFromString(s);
        isDone = (isDone && client.setLinkContent(addrLink, bb));

        ByteBuffer bb2 = client.getLinkContent(addrLink);
        isDone = (isDone && bb2 != null);
        String s2 = SctpClient.ByteBufferToString(bb2);
        isDone = (isDone && s.equals(s2));

        SctpIterator it = client.iterate3(SctpIterator.Iterator3F_A_A, addr, type, new ScType(ScType.Link));
        isDone = (isDone && (it != null));
        isDone = (isDone && it.next());
        isDone = (isDone && it.value(0).isEqual(addr));
        isDone = (isDone && it.value(1).isEqual(arcAddr));
        isDone = (isDone && it.value(2).isEqual(addrLink));
        isDone = (isDone && !it.next());

        ScAddr addr3 = client.findElementBySystemIdentifier("device");
        isDone = (isDone && addr3.isValid());
        String idtf = new String("Test idtf" + addr.toString());
        isDone = (isDone && client.setSystemIdentifier(addr, idtf));
        isDone = (isDone && client.findElementBySystemIdentifier(idtf).isEqual(addr));

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (isDone) {
            mainActivity.printResponse(new String("Done"));
        } else {
            mainActivity.printResponse(new String("Failed"));
        }
    }
}