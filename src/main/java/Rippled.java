
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class Rippled {

    private String serverHash;
    private String marker;
    public String serverInfo;
    private String ledgerData;
    private JSONArray ledgerDataState;

    public Rippled() throws Exception {

        setServerInfo();
        setServerHash(serverInfo);
        setServerMarker("");
    }

    public void setServerInfo() throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String json = "{\n" +
                "    \"method\": \"server_info\",\n" +
                "    \"params\": [\n" +
                "        {}\n" +
                "    ]\n" +
                "}";

        RequestBody body = RequestBody.create(
                  MediaType.parse("application/json"), json
        );

        Request request = new Request.Builder()
                .url("http://localhost:5005/")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        serverInfo = client.newCall(request).execute().body().string();

    }

    public void setServerHash(String serverInfoResponse) throws IOException, ParseException {

        JSONParser parser = new JSONParser();
        JSONObject serverInfoObject = (JSONObject) parser.parse(serverInfoResponse);
        JSONObject resultObject = new JSONObject();
        JSONObject infoObject = new JSONObject();
        JSONObject validatedLedgerObject = new JSONObject();

        resultObject = (JSONObject) serverInfoObject.get("result");
        infoObject = (JSONObject) resultObject.get("info");
        validatedLedgerObject = (JSONObject) infoObject.get("validated_ledger");

        serverHash = (String) validatedLedgerObject.get("hash");
    }

    public String getServerInfo() throws IOException {

        return serverInfo;
    }

    public String getServerHash() {

        return serverHash;
    }

    public String getServerMarker() {

        return marker;
    }

    public void setServerMarker(String marker) {

        this.marker = marker;
    }

    public void setLedgerData(String hash, String markers) throws IOException, ParseException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String json = "{\n" +
                "    \"method\": \"ledger_data\",\n" +
                "    \"params\": [\n" +
                "        {\n" +
                "            \"binary\": false,\n" +
                "            \"ledger_hash\": \"" + hash + "\",\n" +
                "            \"limit\": 5000,\n" +
                "            \"marker\":\"" + markers + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), json
        );

        Request request = new Request.Builder()
                .url("http://localhost:5005/")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();

        ledgerData = client.newCall(request).execute().body().string();
        setStateData(ledgerData);
    }

    public String getLedgerData() {

        return ledgerData;
    }

    public JSONArray getStateData() {

        return ledgerDataState;
    }

    public void setStateData(String ledgerData) throws ParseException {

        JSONParser parser = new JSONParser();
        JSONObject ledgerObject = (JSONObject) parser.parse(ledgerData);
        JSONObject resultObject = new JSONObject();
        resultObject = (JSONObject) ledgerObject.get("result");
        setServerMarker((String) resultObject.get("marker"));
        ledgerDataState = (JSONArray) resultObject.get("state");

    }
    
}
