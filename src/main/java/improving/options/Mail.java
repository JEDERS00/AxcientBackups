package improving.options;

import improving.beans.CustomMessage;
import improving.utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
public abstract class Mail {

    @Value("#{props.string.host:test}")
    protected String host;
    @Value("#{props.string.port:test}")
    protected String port;
    @Value("#{props.string.enableTLS:test}")
    protected String enableTLS;
    @Value("#{props.string.storeProtocol:test}")
    protected String storeProtocol;
    @Value("#{props.string.user:test}")
    protected String user;
    @Value("#{props.string.password:test}")
    protected String password;

    protected String folderName = "Inbox";

    protected final String STATUS_IN_PROGRESS = "In progress";
    protected final String STATUS_COMPLETE = "OK";
    protected final String STATUS_ERROR = "Failed";

    protected abstract void setSettings(String user, String password);

    protected abstract Properties getProperties();

    protected abstract void executeBackup();

    public Optional<List<CustomMessage>> getMails() {
        Optional<List<CustomMessage>> optional = Optional.empty();
        Session emailSession = Session.getDefaultInstance(getProperties());
        try ( Store store = emailSession.getStore(this.storeProtocol)) {
            store.connect(this.host, this.user, this.password);
            Folder mailFolder = store.getFolder(this.folderName);
            mailFolder.open(Folder.READ_WRITE);
            Message[] messages = mailFolder.search(new FlagTerm(new Flags(Flag.USER), false));

            List<CustomMessage> customMessages = new ArrayList();
            for (int position = 0; position < messages.length; position++) {
                customMessages.add(new CustomMessage(messages[position]));
            }
            optional = Optional.ofNullable(customMessages);
            mailFolder.close(false);
        } catch (NoSuchProviderException nspe) {
            System.out.println("Error genspe: " + nspe);
        } catch (MessagingException me) {
            System.out.println("Error geme: " + me);
        } catch (Exception e) {
            System.out.println("Error gee: " + e);
        }
        return optional;
    }

    public String exportMails(List<CustomMessage> customMessages, String id) {
        id = StringUtils.trimToEmpty(id);
        if (StringUtils.isBlank(id) || customMessages == null) {
            return "Backup Id and information is required.";
        }
        
        String mainPath = getBackupPathToSave(id);
        boolean created = new File(mainPath).mkdirs();
        if (created) {
            for (int position = 0; position < customMessages.size(); position++) {
                try {
                    saveSubFolder(mainPath, customMessages.get(position));
                } catch (MessagingException messagingException) {
                    System.out.println("Error mem: " + messagingException.getMessage());
                } catch (IOException ioe) {
                    System.out.println("Error mie: " + ioe.getMessage());
                }
            }
            new Utils().createZipFolder(mainPath);
            return "Mail downloaded in: " + mainPath + ".zip";
        }
        return "There was a problem creating the folder to save the mail.";
    }

    private String getBackupPathToSave(String id) {
        String path = "";
        String slash = "/";
        String currentPath = StringUtils.trimToEmpty(System.getProperty("user.dir"));
        if (StringUtils.isNotBlank(currentPath)) {
            String[] paths = currentPath.split("\\\\");
            StringBuilder pathSB = new StringBuilder(15 + id.length() + currentPath.length() + paths.length);
            for (int position = 0; position < paths.length; position++) {
                pathSB.append(paths[position]).append(slash);
            }
            pathSB.append("AxcientBackup ").append(id).append(" ").append(new Utils().getDate());
            path = pathSB.toString();
        }
        return path;
    }

    private void saveSubFolder(String mainPath, CustomMessage customMessage) throws MessagingException, IOException {
        String subFolderName = getSubFolder(customMessage);
        String path = getFolderPathName(mainPath, subFolderName);
        boolean created = new File(path).mkdirs();
        if (created) {
            try ( FileOutputStream fos = new FileOutputStream(getFilePathName(path, subFolderName))) {
                byte[] array = getMessage(customMessage).getBytes();
                fos.write(array);
            } catch (Exception e) {
                System.out.println("Error ssfe: " + e.getMessage());
            }
            addAttachments(path, customMessage);
        } else {
            System.out.println("NO created");
        }
    }

    private String getSubFolder(CustomMessage customMessage) {
        Date mailDate = customMessage.getReceivedDate();
        return new Utils().dateToString(mailDate, "yyyy-MM-dd HHmmss");
    }

    private String getFolderPathName(String mainPath, String folderName) {
        StringBuilder pathSB = new StringBuilder(mainPath.length() + 1 + folderName.length());
        pathSB.append(mainPath).append("/").append(folderName);
        return pathSB.toString();
    }

    private String getFilePathName(String path, String folderName) {
        StringBuilder fileName = new StringBuilder(path.length() + folderName.length() + 5);
        fileName.append(path).append("/").append(folderName).append(".txt");
        return fileName.toString();
    }

    private String getMessage(CustomMessage customMessage) throws MessagingException, IOException {
        String from = customMessage.getFrom();
        String to = getAllRecipients(customMessage.getAllRecipients());
        String date = new Utils().dateToString(customMessage.getReceivedDate());
        String subject = customMessage.getSubject();
        String text = customMessage.getText();

        StringBuilder mesage = new StringBuilder(from.length() + to.length() + date.length() + subject.length() + text.length() + 41);
        mesage.append("FROM: ").append(from).append("\n")
                .append("TO: ").append(to).append("\n")
                .append("DATE: ").append(date).append("\n")
                .append("SUBJECT: ").append(subject).append("\n")
                .append("TEXT: ").append(text).append("\n");
        return mesage.toString();
    }

    private String getAllRecipients(List<String> recipients) throws MessagingException, IOException {
        StringBuilder allRecipients = new StringBuilder();
        for (int position = 0; position < recipients.size(); position++) {
            allRecipients.append(recipients.get(position)).append(" ");
        }
        return allRecipients.toString();
    }

    private void addAttachments(String path, CustomMessage customMessage) throws MessagingException, IOException {
        List<HashMap> multipart = customMessage.getMultipart();
        if (!multipart.isEmpty()) {
            HashMap data;
            String fileName;
            String inputStreamStr;
            StringBuilder pathSB;
            FileOutputStream output;
            for (int count = 0; count < multipart.size(); count++) {
                data = multipart.get(count);
                fileName = (String) data.get("FILE_NAME");
                inputStreamStr = (String) data.get("INPUT_STREAM");
                InputStream inputStream = new ByteArrayInputStream(inputStreamStr.getBytes());
                pathSB = new StringBuilder(path.length() + 1 + fileName.length());
                pathSB.append(path).append("/").append(fileName);
                output = new FileOutputStream(pathSB.toString());

                byte[] buffer = new byte[4096];
                int byteRead;
                while ((byteRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, byteRead);
                }
                output.close();
            }
        }
    }

}
