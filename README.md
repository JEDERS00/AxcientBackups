Hi, this project is working with **Spring Boot v2.7.0** and **Java 8**

The purpose of this project is to make backups of gmail accounts.
I developed the application with NetBeans 13 working in Windows 11 Pro.

***STEP 1:***  **You need to do next settings in your gmail account**

- [Create the Two Factor Authentication](https://myaccount.google.com/security?rapt=AEjHL4OeXjzcPKbzDF0wcM3mg4Uayhl6iWJoAtjZPdOIIhHCj8xTh73lnv5jZe0Rr5mDZNchsgZUEuVADd72mfyHTXRg3xPpYw)
- Create an application password to use in this application

***STEP 2:***  **Release port 8080**

The application is working in port 8080
http://localhost:8080/


***STEP 3:***  **Create the AxcientBackups-0.1.0.jar**

Doing a Cleand and Build from NetBeans you can get the .jar in ...\AxcientBackups\target


***STEP 4:***  **Run the AxcientBackups-0.1.0.jar with next command**
```
java -jar AxcientBackups-0.1.0.jar
```
Or in the project path directory run 
```
mvnw spring-boot:run
```

***Note:***
If you are getting next error:

Error geme: javax.mail.MessagingException: sun.security.validator.ValidatorException: 
PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: 
unable to find valid certification path to requested target;
  nested exception is:
        javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: 
		PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: 
		unable to find valid certification path to requested target
		
Please disable your antivirus
