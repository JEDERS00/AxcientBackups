package improving.options;

import improving.utils.Utils;
import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
public class Menu {
    
    private final String HTTPS_DOMAIN = "http://localhost:8080/";

    public void print() {
        String breakText = "----------------------------------------------------------------------------------------------";
        System.out.println("\n\n");
        System.out.println(breakText);
        System.out.println("--------------------------------------- AXCIENT BACKUPS --------------------------------------");
        System.out.println();
        System.out.println("Hi, welcome to the 1st version of Axcient backups.");
        System.out.println("The '@gmail.com' extension is not necessary, the system will add it automatically");
        System.out.println(breakText);

        MenuOptionsEnum moe = getMenuOption();
        while (MenuOptionsEnum.EXIT != moe) {
            switch (moe) {
                case INIT_COMPLETE_BACKUP:
                    executeBackup();
                    break;
                case LIST_COMPLETE_INITIATED:
                    listCompleteInitiated();
                    break;
                case EXPORT_BY_ID:
//                case EXPORT_BY_ID_AND_LABEL:
                    exportMail();
                    break;
            }
            moe = getMenuOption();
        }
        System.exit(0);
    }

    private MenuOptionsEnum getMenuOption() {
        String openingParentheses = "(";
        String closingParentheses = ") ";
        String lineBreak = "\n";
        String pleaseSelectOption = "Please select an option:";

        StringBuilder optionsSB = new StringBuilder(304);
        //This loop is to add all the options to show later
        for (MenuOptionsEnum menuOptionsEnum : MenuOptionsEnum.values()) {
            optionsSB.append(openingParentheses).append(menuOptionsEnum.getOption()).append(closingParentheses)
                    .append(menuOptionsEnum.getDescription()).append(lineBreak);
        }
        String options = optionsSB.toString();

        Optional<MenuOptionsEnum> optional = Optional.empty();
        Utils utils = new Utils();
        while (!optional.isPresent()) {
            System.out.println(lineBreak);
            System.out.println(pleaseSelectOption);
            System.out.println(options);
            optional = utils.getUserMenuOption();
        }
        return optional.get();
    }

    private void executeBackup() {
        Console console = System.console();

        Utils utils = new Utils();
        String mail = utils.getGmail(console);
        String password = utils.getPassword(console);

        List nameValuePairs = new ArrayList();
        nameValuePairs.add(new BasicNameValuePair("X_MAIL", mail));
        nameValuePairs.add(new BasicNameValuePair("X_PASS", password));
        HttpPost httpPost = new HttpPost(HTTPS_DOMAIN + "backups");
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  
                CloseableHttpResponse chr = httpClient.execute(httpPost);) {
            System.out.println(EntityUtils.toString(chr.getEntity()));
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe.getMessage());
        }
    }

    private void listCompleteInitiated() {
        HttpGet httpGet = new HttpGet(HTTPS_DOMAIN + "backups");
        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  
                CloseableHttpResponse chr = httpClient.execute(httpGet);) {
            System.out.println(EntityUtils.toString(chr.getEntity()));
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe.getMessage());
        }
    }

    private void exportMail() {
        Utils utils = new Utils();
        String backupId = utils.getValueFromInput("Backup Id");
        backupId = utils.addGmailExtension(backupId);
        
        HttpGet httpGet = new HttpGet(HTTPS_DOMAIN + "exports/" + backupId);
        try ( CloseableHttpClient httpClient = HttpClients.createDefault();  
                CloseableHttpResponse chr = httpClient.execute(httpGet);) {
            System.out.println(EntityUtils.toString(chr.getEntity()));
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe.getMessage());
        }
    }

}
