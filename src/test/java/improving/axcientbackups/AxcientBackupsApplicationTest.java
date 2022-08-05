package improving.axcientbackups;

import improving.utils.Utils;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AxcientBackupsApplicationTest {

    @Autowired
    private AxcientBackupsApplication aba;

    @Test
    public void contextLoads() throws Exception {
        assertThat(aba).isNotNull();
    }

    @Test
    public void getUserMenuOption() throws Exception {
        String gmail = "theMail";
        String expResult = gmail + "@gmail.com";
        String result = new Utils().addGmailExtension(gmail);
        assertEquals(expResult, result);
    }
    
    @Test
    public void isValidGmail() throws Exception {
        boolean result = new Utils().isValidGmail("theMail@gmail.com");
        assertEquals(true, result);
    }
    
    @Test
    public void dateToString() throws Exception {
        String strDate = "2022/04/08 16:28:00";
        String result = new Utils().dateToString(new Date(strDate));
        assertEquals(strDate, result);
    }
    
    @Test
    public void dateToString2() throws Exception {
        String strDate = "2022/04/08 16:28:00";
        String format = "yyyy/MM/dd HH:mm:ss";
        Date date = new Date(strDate);
        
        String result = new Utils().dateToString(date, format);
        assertEquals(strDate, result);
    }
    
}
