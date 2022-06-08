package improving.axcientbackups;

import improving.beans.BodyMail;
import improving.beans.CustomMessage;
import improving.options.Gmail;
import improving.options.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
@SpringBootApplication
@RestController
public class AxcientBackupsApplication {

    //Non-Functional Requirements The application does not require persistent storage. 
    public static HashMap<String, BodyMail> processStatus = new HashMap();
    public static HashMap<String, List<CustomMessage>> objectsByKey = new HashMap();

    public static void main(String[] args) {
        SpringApplication.run(AxcientBackupsApplication.class, args);
        new Menu().print();
    }

    @PostMapping("/backups")
    @ResponseBody
    public ResponseEntity<?> postBackups(@RequestParam(value="X_MAIL") String mail, @RequestParam(value="X_PASS") String pass) {
        try {
            Optional<String> optionalMail = Optional.ofNullable(mail);
            Optional<String> optionalPass = Optional.ofNullable(pass);
            if (optionalMail.isPresent() && optionalPass.isPresent()) {
                
                mail = optionalMail.get();
                String password = optionalPass.get();

                BodyMail bodyMail = new BodyMail(mail);
                processStatus.put(mail, bodyMail);

                Gmail gmail = new Gmail();
                gmail.setSettings(mail, password);

                Thread thread = new Thread(gmail);
                thread.start();

                return ResponseEntity.ok(bodyMail);
            } else {
                return ResponseEntity.badRequest().body("Credentials are required.");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error in backup: " + ex.getMessage());
        }
    }

    @GetMapping("/backups")
    @ResponseBody
    public ResponseEntity<?> getBackups() {
        List<BodyMail> listBodyMail = new ArrayList(processStatus.size());
        for (String key : processStatus.keySet()) {
            listBodyMail.add(processStatus.get(key));
        }
        return ResponseEntity.ok(listBodyMail);
    }

    @GetMapping("/exports/{backupId}")
    @ResponseBody
    public ResponseEntity<?> getEmployeesByIdAndNameWithMapVariable(@PathVariable Map<String, String> pathVarsMap) {
        Optional<String> optionalLabel = Optional.ofNullable(pathVarsMap.get("label"));
        if (optionalLabel.isPresent()) {
            return ResponseEntity.ok().body("This option is going to be available in version 0.2.0");
        }
        Optional<String> optionalID = Optional.ofNullable(pathVarsMap.get("backupId"));
        if (optionalID.isPresent()) {
            String id = optionalID.get();
            List<CustomMessage> customMessages = objectsByKey.get(id);
            if (customMessages == null) {
                return ResponseEntity.badRequest().body("Backup Id [" + id +"] not found.");
            } else {
                Gmail gmail = new Gmail();
                String message = gmail.exportMails(customMessages, id);
                return ResponseEntity.ok().body(message);
            }
        } else {
            return ResponseEntity.badRequest().body("Backup Id is required");
        }
    }
}
