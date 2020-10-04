import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Utilities {

    public static void printPrettyJson(JSONObject obj) {
        //JsonObject myjson = response.body().string();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(String.valueOf((JSONObject) obj));
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);
    }

    public static void printObject(String object) throws Exception {

        JSONParser parser = new JSONParser();
        JSONObject serverObject = (JSONObject) parser.parse(object);
        Utilities.printPrettyJson(serverObject);
    }

    public static String readCSVFile(String walletId) throws IOException {

        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        String[] country = new String[2];

        br = new BufferedReader(new FileReader("Name"));
        while ((line = br.readLine()) != null) {

            country = line.split(csvSplitBy);
            if (country[0].equals(walletId)) {
//                System.out.println("I made a match");
                br.close();
                return country[1];
            }
        }
        br.close();
        return "No Match";
    }
    public static Map<String, String> loadFromCSV() throws IOException {

        Map<String, String> accountIdsByCreationDate = new HashMap<String, String>();
        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        String[] country = new String[2];

        br = new BufferedReader(new FileReader("Name"));
        while ((line = br.readLine()) != null) {

            country = line.split(csvSplitBy);
            accountIdsByCreationDate.put(country[0], country[1]);
        }
        System.out.println("Loaded csv elements");
        br.close();
        return accountIdsByCreationDate;
    }
}
