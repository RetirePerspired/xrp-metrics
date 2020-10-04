import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class emptyXRPWallets {

    public static void emptyXRPWalletStats(Rippled server) throws Exception {

        long accountsHoldingXRP = 0;
        long accountCount = 0;
        long csvMatchCount = 0;
        long totalCount = 0;
        long preTwentySevenTeen = 0;
        long TwentySeventeen = 0;
        long TwentyEighteen = 0;
        long TwentyNineteen = 0;
        long TwentyTwenty = 0;
        int totalCountInForLoop = 0;
        long totalBalance = 0;
        ArrayList<String> emptyAccounts = new ArrayList<String>();

        while (server.getServerMarker() != null) {

            server.setLedgerData(server.getServerHash(), server.getServerMarker());
            JSONArray stateArray = server.getStateData();
            System.out.println(server.getServerMarker());


            for (int i = 0; i < stateArray.size(); i++) {
                JSONObject accountEntry = (JSONObject) stateArray.get(i);

                try {
                    String accountType = (String) accountEntry.get("LedgerEntryType");
                    if (accountType.equals("AccountRoot")) {
                        Long balance = Long.parseLong((String) accountEntry.get("Balance"));
                        if (balance <= 21000000) {
                            totalBalance += balance;
                            emptyAccounts.add((String) accountEntry.get("Account"));
                            accountCount++;
//                            String walletId = (String) accountEntry.get("Account");
//                            System.out.println("I made it to the csv call");
                        }
                        accountsHoldingXRP++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalCountInForLoop++;
            }
            totalCount += 5000;

            if (totalCount % 100000 == 0 || server.getServerMarker() == null) {

                System.out.println("Churned through another 5000 Accounts. Total tally is " + totalCount);
                System.out.println("I've found " + accountCount + " accounts with 20 XRP or less");
                System.out.println("Accounts holding XRP total is " + accountsHoldingXRP);
            }
        }

        Map<String, String> accountsByCreatedDate = Utilities.loadFromCSV();
        for (int i = 0; i < emptyAccounts.size(); i++) {
            String inceptionDate = accountsByCreatedDate.get(emptyAccounts.get(i));
            if (!inceptionDate.equals("No match")) {
                Date date1 = new SimpleDateFormat("yyyy").parse(inceptionDate);
                if (date1.getYear() < 117) {
                    preTwentySevenTeen++;
                } else if (date1.getYear() == 117) {
                    TwentySeventeen++;
                } else if (date1.getYear() == 118) {
                    TwentyEighteen++;
                } else if (date1.getYear() == 119) {
                    TwentyNineteen++;
                } else if (date1.getYear() == 120) {
                    TwentyTwenty++;
                }
            }
        }
        System.out.println("Total balance of 'empty XRP accounts " + totalBalance/1000000)
        ;
        System.out.println("Pre 2017 count is " + preTwentySevenTeen);
        System.out.println("2017 count is " + TwentySeventeen);
        System.out.println("2018 count is " + TwentyEighteen);
        System.out.println("2019 count is " + TwentyNineteen);
        System.out.println("2020 count is " + TwentyTwenty);
    }
}
