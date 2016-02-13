package home.com.smarthome.sctp.tasks;

import android.os.AsyncTask;

import home.com.smarthome.MainActivity;
import home.com.smarthome.sctp.SctpClient;

public class ConnectTask extends AsyncTask<Void, Void, Void> {

    private boolean isConnected = false;
    private SctpClient client = null;
    public MainActivity mainActivity = null;

    protected Void doInBackground(Void... arg0) {
        client = new SctpClient();
        isConnected = client.connect("192.168.0.100", 55770);

        if (isConnected) {
            isConnected = mainActivity.getKeynodes().resolve(client);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mainActivity.sctpConnectResult(isConnected ? client : null);
    }
}