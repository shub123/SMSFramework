# SMS Engine

Utility to send SMS to a mobile number

# Dependencies
 Apache Commons Framwork
 Java Mail API
 
 # Usage
 
    // Create a property file containing the SMTP properties
    Properties smtpProps = new Properties();
		smtpProps.load(SmsFTest.class.getResourceAsStream("./smtp.properties"));

    // create an object of SMSUtil passing the SMTP properties object as a dependency
		SmsUtil smsUtil = new SmsUtil(smtpProps);
    
    // invoke sendSMS method with the mobile number,  Cell phone provider identifier , and the sms message 
		smsUtil.sendSMS("1234567788", "Cricket", "hello user! ");

# Notes 

 the cell provider list is encoded in providers.xml file. Following providers and their codes  are valid-
 
|  PROVIDER           |   CODE               | 
| --------------------|----------------------|
| AT & T              |              ATT     |
| Verizon Wireless    |              Verizon |
| T Mobile            |              TMobile |
|Pinger               |              Pinger  |
| Sprint              |              Sprint  |
| Cricket Wireless    |              Cricket |

   
