package home.com.smarthome.sctp.tasks;


import android.os.AsyncTask;
import android.os.SystemClock;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import home.com.smarthome.MainActivity;
import home.com.smarthome.sctp.ScAddr;
import home.com.smarthome.sctp.ScKeynodes;
import home.com.smarthome.sctp.ScType;
import home.com.smarthome.sctp.SctpClient;
import home.com.smarthome.sctp.SctpIterator;

public class RequestCommand extends AsyncTask<Void, Void, Void> {

    public SctpClient client = null;
    public MainActivity mainActivity = null;
    public String json = null;
    private String speechText = null;

    private boolean isDone = false;

    private ScAddr getByIdentifier(String idtf) {
        ScAddr []links = client.findLinksByContent(SctpClient.ByteBufferFromString(idtf));
        if (links != null) {
            for (int i = 0; i < links.length; ++i) {
                SctpIterator it = client.iterate5(SctpIterator.Iterator5A_A_F_A_F,
                        new ScType(),
                        new ScType(ScType.ArcCommon | ScType.Const),
                        links[i],
                        new ScType(ScType.ArcPosConstPerm),
                        mainActivity.getKeynodes().nrel_main_idtf);
                if (it.next()) {
                    return it.value(0);
                }
            }
        }
        return client.findElementBySystemIdentifier(idtf);
    }

    private void generateSpeechText(ScAddr inCmdAddr) {

        ScKeynodes keynodes = mainActivity.getKeynodes();

        ScType arcPermType = new ScType(ScType.ArcPosConstPerm);

        ScAddr cmdAddr = client.createNode(new ScType(ScType.Const));
        client.createArc(keynodes.command_generate_text_from_template, cmdAddr, arcPermType);

        /// TODO: language selection
        /// TODO: correct command result
        ScAddr params[] = {inCmdAddr, keynodes.lang_ru, keynodes.rrel_success};

        for (int i = 0; i < params.length; ++i) {
            ScAddr arc = client.createArc(cmdAddr, params[i], arcPermType);
            client.createArc(keynodes.getRrelOrder(i), arc, arcPermType);
        }

        // initialize command
        client.createArc(keynodes.command_initiated, cmdAddr, arcPermType);

        /// TODO: Asynchronous wait
        // wait for result
        ScAddr resultAddr = waitResult(cmdAddr, 1000);

        if (resultAddr != null) {
            SctpIterator it = client.iterate3(SctpIterator.Iterator3F_A_A,
                    resultAddr,
                    arcPermType,
                    new ScType(ScType.Link));

            if (it.next()) {
                speechText = SctpClient.ByteBufferToString(client.getLinkContent(it.value(2)));
            }
        }
    }

    private ScAddr runCommand(ScAddr cmdAddr, List<ScAddr> params) {

        ScKeynodes keynodes = mainActivity.getKeynodes();

        // generate command instance in memory
        ScAddr instCmdAddr = client.createNode(new ScType(ScType.Node | ScType.Const));
        ScAddr arc = client.createArc(keynodes.ui_command_generate_instance, instCmdAddr, new ScType(ScType.ArcPosConstPerm));

        ScAddr instCmdArc = client.createArc(instCmdAddr, cmdAddr, new ScType(ScType.ArcPosConstPerm));
        arc = client.createArc(keynodes.ui_rrel_commnad, instCmdArc, new ScType(ScType.ArcPosConstPerm));

        // create arguments
        ScAddr argsAddr = client.createNode(new ScType(ScType.Node | ScType.Const));
        ScAddr argsArc = client.createArc(instCmdAddr, argsAddr, new ScType(ScType.ArcPosConstPerm));
        arc = client.createArc(keynodes.ui_rrel_command_arguments, argsArc, new ScType(ScType.ArcPosConstPerm));

        for (int i = 0; i < params.size(); ++i) {
            ScAddr argArc = client.createArc(argsAddr, params.get(i), new ScType(ScType.ArcPosConstPerm));
            arc = client.createArc(keynodes.getRrelOrder(i), argArc, new ScType(ScType.ArcPosConstPerm));
        }

        // initialize command
        arc = client.createArc(keynodes.ui_command_initiated, instCmdAddr, new ScType(ScType.ArcPosConstPerm));

        boolean isCmdFinished = false;
        long waitTimeMs = 5000;
        long waitTickMs = 100;
        while (waitTimeMs > 0 && !isCmdFinished) {

            SctpIterator it = client.iterate3(SctpIterator.Iterator3F_A_F,
                    keynodes.ui_command_finished,
                    new ScType(ScType.ArcPosConstPerm),
                    instCmdAddr);

            isCmdFinished = it.next();

            if (isCmdFinished)
                break;

            waitTimeMs -= waitTickMs;
            SystemClock.sleep(waitTickMs);
        }

        if (!isCmdFinished)
            return null;

        // get command result
        SctpIterator it5 = client.iterate5(SctpIterator.Iterator5F_A_A_A_F,
                instCmdAddr,
                new ScType(ScType.ArcCommon |ScType.Const),
                new ScType(ScType.Node | ScType.Const),
                new ScType(ScType.ArcPosConstPerm),
                keynodes.ui_nrel_command_result);

        if (!it5.next())
            return null;

        ScAddr cmdResult = it5.value(2);
        ScAddr instanceNode = null;
        ScAddr initSet = null;

        // check if it's a question
        it5 = client.iterate5(SctpIterator.Iterator5F_A_A_A_F,
                keynodes.question,
                new ScType(ScType.ArcPosConstPerm),
                new ScType(ScType.Node | ScType.Const),
                new ScType(ScType.ArcPosConstPerm),
                cmdResult);

        if (it5.next()) {
            ScAddr questionAddr = it5.value(2);
            /// TODO: implement question logic

            return null;
        } else {

            it5 = client.iterate5(SctpIterator.Iterator5F_A_A_A_F,
                    keynodes.command,
                    new ScType(ScType.ArcPosConstPerm),
                    new ScType(ScType.Node | ScType.Const),
                    new ScType(ScType.ArcPosConstPerm),
                    cmdResult);

            if (!it5.next())
                return null;

            instanceNode = it5.value(2);
            initSet = keynodes.command_initiated;
        }

        // initiate command instance
        client.createArc(initSet, instanceNode, new ScType(ScType.ArcPosConstPerm));

        return instanceNode;
    }

    private ScAddr waitResult(ScAddr cmdInstance, long waitTimeMs) {
        ScKeynodes keynodes = mainActivity.getKeynodes();
        // determine type of result
        ScAddr resultRel = keynodes.nrel_result;
        if (client.iterate3(SctpIterator.Iterator3F_A_F,
                keynodes.question,
                new ScType(ScType.ArcPosConstPerm),
                cmdInstance).next()) {
            resultRel = keynodes.nrel_answer;
        }

        ScAddr resultAddr  = null;
        long waitTickMs = 10;
        while (waitTimeMs > 0) {

            SystemClock.sleep(waitTickMs);

            SctpIterator it = client.iterate5(SctpIterator.Iterator5F_A_A_A_F,
                    cmdInstance,
                    new ScType(ScType.ArcCommon | ScType.Const),
                    new ScType(ScType.Node | ScType.NodeStruct | ScType.Const),
                    new ScType(ScType.ArcPosConstPerm),
                    resultRel);

            if (it.next()) {
                resultAddr = it.value(2);
                break;
            }


            waitTimeMs -= waitTickMs;
        }

        return resultAddr;
    }

    protected Void doInBackground(Void... arg0) {

        String actionIdtf = null;
        List<ScAddr> params = new ArrayList<ScAddr>();
        List<String> paramIdtfs = new ArrayList<String>();

        try {
            JSONObject resultObj = new JSONObject(json);
            JSONObject paramsObj = resultObj.getJSONObject("params");

            actionIdtf = resultObj.getString("action");

            boolean hasParam = false;
            int idx = 1;
            do {
                String idtf = String.format("ui_arg_%d", idx++);
                hasParam = paramsObj.has(idtf);

                if (hasParam) {
                    String paramValue = paramsObj.getString(idtf);
                    paramIdtfs.add(paramValue);
                }

            } while (hasParam);

        } catch (JSONException e) {
            e.printStackTrace();
        };

        if (actionIdtf == null)
            return null;

        for (int i = 0; i < paramIdtfs.size(); ++i) {
            ScAddr el = getByIdentifier(paramIdtfs.get(i));
            if (el != null && el.isValid()) {
                params.add(el);
            }
        }

        ScAddr cmdAddr = client.findElementBySystemIdentifier(actionIdtf);
        if (cmdAddr == null || !cmdAddr.isValid())
            return null;

        /// TODO: Move logic to server agent. There just wait result and text to speech
        ScAddr cmdInstance = runCommand(cmdAddr, params);
        if (cmdInstance != null) {
            ScAddr resultAddr = waitResult(cmdInstance, 2000);

            generateSpeechText(cmdInstance);
            isDone = true;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (isDone)
            mainActivity.printResponse("done: \n" + json);
        else
            mainActivity.printResponse("failed: \n" + json);

        if (speechText != null)
            mainActivity.speak(speechText);
    }
}
