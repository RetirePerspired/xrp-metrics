

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class RippledInterface {

    public static void main(String[] args) throws Exception {

        Rippled server = new Rippled();
        Utilities.printObject(server.getServerInfo());
        DataAPI data = new DataAPI();
        data.loadFileWithAccountCreationDatesFromLastDate();
        System.out.println(server.getServerHash());
        emptyXRPWallets.emptyXRPWalletStats(server);
//        DataAPI test = new DataAPI();
//        test.loadFileWithAccountCreationDatesFromInception("");
//        test.loadFileWithAccountCreationDatesFromLastDate();
    }


}
