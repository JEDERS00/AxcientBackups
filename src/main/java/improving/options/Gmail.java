package improving.options;

import improving.axcientbackups.AxcientBackupsApplication;
import improving.beans.BodyMail;
import improving.beans.CustomMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
public class Gmail extends Mail implements Runnable {

    public Gmail() {

    }

    @Override
    public void setSettings(String user, String password) {
        this.host = "imap.gmail.com";
        this.port = "993";
        this.enableTLS = "true";
        this.storeProtocol = "imaps";

        this.user = StringUtils.trimToEmpty(user);
        this.password = StringUtils.trimToEmpty(password);
    }

    @Override
    protected Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.imap.host", this.host);
        properties.put("mail.imap.port", this.port);
        properties.put("mail.imap.starttls.enable", this.enableTLS);
        properties.put("mail.imap.ssl.trust", host);
        return properties;
    }

    @Override
    protected void executeBackup() {
        BodyMail bodyMail = AxcientBackupsApplication.processStatus.get(this.user);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        bodyMail.setDate(dtf.format(now));
        bodyMail.setStatus(STATUS_IN_PROGRESS);

        Optional<List<CustomMessage>> optionalMessages = getMails();
        if (optionalMessages.isPresent()) {
            AxcientBackupsApplication.objectsByKey.put(this.user, optionalMessages.get());
            bodyMail.setStatus(STATUS_COMPLETE);
        } else {
            bodyMail.setStatus(STATUS_ERROR);
            System.out.println("Error getting emails.");
        }
    }

    @Override
    public void run() {
        executeBackup();
    }

    public String getID() {
        return this.user;
    }

}
