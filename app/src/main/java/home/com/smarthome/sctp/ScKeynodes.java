package home.com.smarthome.sctp;

import java.util.ArrayList;

public class ScKeynodes {

    private ArrayList<ScAddr> _addrs = null;

    public ScAddr nrel_main_idtf = null;
    public ScAddr ui_command_generate_instance = null;
    public ScAddr ui_rrel_commnad = null;
    public ScAddr ui_rrel_command_arguments = null;
    public ScAddr ui_command_initiated = null;
    public ScAddr ui_command_finished = null;
    public ScAddr ui_nrel_command_result = null;
    public ScAddr question = null;
    public ScAddr command = null;
    public ScAddr command_initiated = null;
    public ScAddr nrel_result = null;
    public ScAddr nrel_answer = null;
    public ScAddr command_generate_text_from_template = null;
    public ScAddr lang_ru = null;
    public ScAddr lang_en = null;
    public ScAddr rrel_success = null;

    public ScAddr rrel_order[] = new ScAddr[10];

    private ScAddr _resolve(SctpClient client, String idtf) {
        ScAddr addr = client.findElementBySystemIdentifier(idtf);
        _addrs.add(addr);
        return addr;
    }

    public boolean resolve(SctpClient client) {

        _addrs = new ArrayList<ScAddr>();

        nrel_main_idtf = _resolve(client, "nrel_main_idtf");
        ui_command_generate_instance = _resolve(client, "ui_command_generate_instance");
        ui_rrel_commnad = _resolve(client, "ui_rrel_command");
        ui_rrel_command_arguments = _resolve(client, "ui_rrel_command_arguments");
        ui_command_initiated = _resolve(client, "ui_command_initiated");
        ui_command_finished = _resolve(client, "ui_command_finished");
        ui_nrel_command_result = _resolve(client, "ui_nrel_command_result");
        question = _resolve(client, "question");
        command = _resolve(client, "command");
        command_initiated = _resolve(client, "command_initiated");
        nrel_result = _resolve(client, "nrel_result");
        nrel_answer = _resolve(client, "nrel_answer");
        command_generate_text_from_template = _resolve(client, "command_generate_text_from_template");
        lang_ru = _resolve(client, "lang_ru");
        lang_en = _resolve(client, "lang_en");
        rrel_success = _resolve(client, "rrel_success");

        for (int i = 0; i < rrel_order.length; ++i) {
            rrel_order[i] = _resolve(client, String.format("rrel_%d", (i + 1)));
        }

        return _addrs.indexOf(null) == -1;
    }

    public ScAddr getRrelOrder(int idx) {
        return rrel_order[idx];
    }
}
