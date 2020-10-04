import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataAPI {

    private String marker;

    public DataAPI() {


    }

    public String getMarker() {

        return marker;
    }

    public void setMarker(String marker) {

        this.marker = marker;
    }

    public void loadFileWithAccountCreationDatesFromInception(String marker) throws Exception {

        List<String[]> dataLines = new ArrayList<>();
        long count = 0;

        while (marker != null) {

            Request request = new Request.Builder()
                    .url("http://data.ripple.com/v2/accounts/?descending=false&limit=1000&marker=" + marker)
                    .method("GET", (RequestBody) null)
                    .addHeader("Content-Type", "application/json")
                    .build();


            try {
                String accountDataResponse = executeRequest(request);
                JSONParser parser = new JSONParser();
                JSONObject accountDataResponseJson = (JSONObject) parser.parse(accountDataResponse);
                System.out.println("I made it here");
                while (accountDataResponseJson.get("error") != null) {
                    String errormessage = (String) accountDataResponseJson.get("error");
                    System.out.println(errormessage);
                    errormessage = errormessage.replace("Rate limit exceeded, retry in ", "");
                    errormessage = errormessage.replace("sec", "");
                    System.out.println("I'm inside the if!");
                    System.out.println(errormessage);
                    waitForTimeout((int) (Math.round(Double.parseDouble(errormessage)) + 20));
                    accountDataResponse = executeRequest(request);
                    accountDataResponseJson = (JSONObject) parser.parse(accountDataResponse);
                }
                System.out.println(accountDataResponse);
                parser = new JSONParser();
                accountDataResponseJson = (JSONObject) parser.parse(accountDataResponse);

                JSONArray accountsArray = (JSONArray) accountDataResponseJson.get("accounts");
                marker = (String) accountDataResponseJson.get("marker");

                for (int i = 0; i < accountsArray.size(); i++) {

                    JSONObject accountEntry = (JSONObject) accountsArray.get(i);
                    String accountId = (String) accountEntry.get("account");
                    String createdDate = (String) accountEntry.get("inception");
//                System.out.println(accountId);
//                System.out.println(createdDate);

                    //Write entry to new line of file
                    dataLines.add(new String[]{accountId, createdDate});
                }
                count += 1000;
                System.out.println(count);
            } catch (Exception ignored) {
                throw (ignored);
            }
        }
        writeToCSVFile(dataLines);
    }

    public void loadFileWithAccountCreationDatesFromLastDate() throws Exception {

        BufferedReader br = null;
        String line = "";
        String csvSplitBy = ",";
        String[] country = new String[2];
        String marker = "";

        try {

            br = new BufferedReader(new FileReader("Name"));
            while ((line = br.readLine()) != null) {

                country = line.split(csvSplitBy);
            }
            System.out.println(country[0] + " " + country[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        while (marker != null) {

            Request request = new Request.Builder()
                    .url("http://data.ripple.com/v2/accounts/?descending=false&start=" + country[1] + "&limit=1000&marker=" + marker)
                    .method("GET", (RequestBody) null)
                    .addHeader("Content-Type", "application/json")
                    .build();

            List<String[]> dataLines = new ArrayList<>();
            long count = 0;

            try {
                String accountDataResponse = executeRequest(request);
                JSONParser parser = new JSONParser();
                JSONObject accountDataResponseJson = (JSONObject) parser.parse(accountDataResponse);
                System.out.println("I made it here");
                while (accountDataResponseJson.get("error") != null) {
                    String errormessage = (String) accountDataResponseJson.get("error");
                    System.out.println(errormessage);
                    errormessage = errormessage.replace("Rate limit exceeded, retry in ", "");
                    errormessage = errormessage.replace("sec", "");
                    System.out.println("I'm inside the if!");
                    System.out.println(errormessage);
                    waitForTimeout((int) (Math.round(Double.parseDouble(errormessage)) + 20));
                    accountDataResponse = executeRequest(request);
                    accountDataResponseJson = (JSONObject) parser.parse(accountDataResponse);
                }
                System.out.println(accountDataResponse);
                parser = new JSONParser();
                accountDataResponseJson = (JSONObject) parser.parse(accountDataResponse);

                JSONArray accountsArray = (JSONArray) accountDataResponseJson.get("accounts");
                marker = (String) accountDataResponseJson.get("marker");

                for (int i = 0; i < accountsArray.size(); i++) {

                    JSONObject accountEntry = (JSONObject) accountsArray.get(i);
                    String accountId = (String) accountEntry.get("account");
                    String createdDate = (String) accountEntry.get("inception");
                    dataLines.add(new String[]{accountId, createdDate});
                }
                count += 1000;
                System.out.println(count);
            } catch (Exception ignored) {
                throw (ignored);
            }
            writeToCSVFile(dataLines);
        }
    }

    public void writeToCSVFile(List<String[]> dataLines) {
        File csvOutputFile = new File("Name");
        try (PrintWriter pw = new PrintWriter(new FileOutputStream
                (new File("Name"),true ))) {
            dataLines.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String convertToCSV(String[] data) {
        return Stream.of(data)
                .collect(Collectors.joining(","));
    }

    public void waitForTimeout(int time) throws InterruptedException {
        System.out.println("Meow");
        System.out.println("Waiting for " + Long.toString(time) + " seconds");
        TimeUnit.SECONDS.sleep(time);
    }

    public String executeRequest(Request request) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        return client.newCall(request).execute().body().string();
    }
}
