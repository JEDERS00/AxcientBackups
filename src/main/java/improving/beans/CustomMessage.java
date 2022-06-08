package improving.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
public class CustomMessage implements Serializable {

    private Date receivedDate;
    private String subject;
    private String from;
    private List<String> allRecipients = new ArrayList();
    private String text;
    private List<HashMap> multipart = new ArrayList();

    public CustomMessage(Message message) {
        if (message == null) {
            System.out.println("Message is required");
            return;
        }
        try {
            this.receivedDate = message.getReceivedDate();
            this.subject = StringUtils.trimToEmpty(message.getSubject());
            this.from = message.getFrom()[0].toString();
            for (int position = 0; position < message.getAllRecipients().length; position++) {
                allRecipients.add(message.getAllRecipients()[position].toString());
            }
            this.text = getTextMessage(message);
            setMultipart(message);
        } catch (MessagingException messagingException) {
            System.out.println("MessagingException: " + messagingException.getMessage());
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.getMessage());
        }
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<String> getAllRecipients() {
        return allRecipients;
    }

    public void setAllRecipients(List<String> allRecipients) {
        this.allRecipients = allRecipients;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String getTextMessage(Message message) throws MessagingException, IOException {
        String textStr = "";
        if (message.isMimeType("text/plain")) {
            textStr = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            textStr = getTextMessageFromMultipart(mimeMultipart);
        }
        return textStr;
    }

    private String getTextMessageFromMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder textSB = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int position = 0; position < count; position++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(position);
            if (bodyPart.isMimeType("text/plain")) {
                textSB.append("\n").append(bodyPart.getContent());
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                textSB.append("\n").append(org.jsoup.Jsoup.parse((String) bodyPart.getContent()).text());
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                textSB.append(getTextMessageFromMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        return textSB.toString();
    }

    private void setMultipart(Message message) throws MessagingException, IOException {
        String contentType = message.getContentType();
        if (contentType.contains("multipart")) {
            Multipart multiPart = (Multipart) message.getContent();
            MimeBodyPart part;
            String fileName;
            HashMap data = new HashMap();
            for (int count = 0; count < multiPart.getCount(); count++) {
                part = (MimeBodyPart) multiPart.getBodyPart(count);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    fileName = part.getFileName();
                    data.put("FILE_NAME", fileName);
                    data.put("INPUT_STREAM", getStringFromInputStream(part.getInputStream()));
                    this.multipart.add(data);
                }
            }
        }
    }

    private String getStringFromInputStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
    }

    public List<HashMap> getMultipart() {
        return this.multipart;
    }

}
