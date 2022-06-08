package improving.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author EduardoGallegos<JuanGallegos at improving.axcient>
 */
@JsonInclude(Include.NON_NULL)
public class BodyMail implements Serializable {

    @Value("#{props.string.backupId:test}")
    private String backupId;
    @Value("#{props.string.date:test}")
    private String date;
    @Value("#{props.string.status:test}")
    private String status;

    public BodyMail(String backupID) {
        this.backupId = backupID;
    }

    public String getBackupId() {
        return backupId;
    }

    public void setBackupId(String backupId) {
        this.backupId = backupId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
