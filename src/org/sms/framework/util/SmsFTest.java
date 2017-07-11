package org.sms.framework.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class SmsFTest {

	public static void main(String[] args) throws IOException {

		Properties smtpProps = new Properties();
		smtpProps.load(SmsFTest.class.getResourceAsStream("./smtp.properties"));

		SmsUtil smsUtil = new SmsUtil(smtpProps);
		smsUtil.sendSMS("6035025577", "Cricket", "hello user! ");
	}

}
