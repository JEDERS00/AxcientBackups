package improving.utils;

import improving.options.MenuOptionsEnum;
import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
public class Utils {

    public Optional<MenuOptionsEnum> getUserMenuOption() {
        Optional<MenuOptionsEnum> optional = Optional.empty();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String optionString = StringUtils.trimToEmpty(bufferedReader.readLine());
            int intOption = Integer.parseInt(optionString);
            optional = MenuOptionsEnum.getByOption(intOption);
        } catch (NumberFormatException nfe) {
            System.out.println("Option is not valid: " + nfe.getMessage());
        } catch (IOException ioe) {
            System.out.println("Error: " + ioe.getMessage());
            optional = Optional.ofNullable(MenuOptionsEnum.EXIT);
        }
        return optional;
    }

    public String getGmail(Console console) {
        String gmail = "";
        if (console != null) {
            boolean isValidMail = false;
            String gmailMessage = "Please enter the mail: ";
            String errorEmail = "is not a valid email.";
            while (!isValidMail) {
                gmail = console.readLine(gmailMessage);
                gmail = addGmailExtension(gmail);
                isValidMail = isValidGmail(gmail);
                if (!isValidMail) {
                    System.out.println(gmail + errorEmail);
                }
            }
        }
        return gmail;
    }

    public String addGmailExtension(String gmail) {
        gmail = StringUtils.trimToEmpty(gmail);
        if (StringUtils.isNotBlank(gmail)) {
            return gmail.contains("@gmail.com") ? gmail : gmail + "@gmail.com";
        }
        return gmail;
    }

    public boolean isValidGmail(String gmail) {
        if (StringUtils.isBlank(gmail)) {
            return false;
        }
        String regex = "^(.+)@gmail.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(gmail);
        return matcher.matches();
    }

    public String getPassword(Console console) {
        String password = "";
        if (console != null) {
            boolean isValidPassword = false;
            String passwordMessage = "Please enter the password: ";
            while (!isValidPassword) {
                password = new String(console.readPassword(passwordMessage));
                if (StringUtils.isNotBlank(password)) {
                    isValidPassword = true;
                }
            }
        }
        return password;
    }

    public String dateToString(Date date) {
        if (date != null) {
            return dateToString(date, "yyyy/MM/dd HH:mm:ss");
        }
        return "";
    }

    public String dateToString(Date date, String format) {
        if (date != null && StringUtils.isNotBlank(format)) {
            DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
        return "";
    }

    public String getValueFromInput(String name) {
        name = StringUtils.trimToEmpty(name);
        String value = "";
        if (StringUtils.isNotBlank(name)) {
            String valueName = String.format("Please enter %s: ", name);
            BufferedReader bufferedReader;
            try {
                while (StringUtils.isBlank(value)) {
                    System.out.println(valueName);
                    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
                    value = StringUtils.trimToEmpty(bufferedReader.readLine());
                }
            } catch (IOException ioe) {
                System.out.println("Error: " + ioe.getMessage());
            }
        }
        return value;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        return dateFormat.format(new Date());
    }

    public void createZipFolder(String path) {
        if (StringUtils.isNotBlank(path)) {
            try (FileOutputStream fos = new FileOutputStream(path + ".zip");
                ZipOutputStream zipOut = new ZipOutputStream(fos);) {
                File fileToZip = new File(path);
                zipFile(fileToZip, fileToZip.getName(), zipOut);
                
                File filePath = new File(path);
                deleteDirectory(filePath);
            } catch (FileNotFoundException fnfe) {
                System.out.println("File not found: " + fnfe.getMessage());
            } catch (IOException ioe) {
                System.out.println("Problem to zip folder: " + ioe.getMessage());
            }
        }
    }
    
    private void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }
    
    private void deleteDirectory(File filePath) {
        File[] allContents = filePath.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        filePath.delete();
    }

}
