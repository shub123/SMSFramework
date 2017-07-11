package org.sms.framework.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmsUtil {
	private static final String PROVIDER_LIST_PATH = "./providers.xml";
	private static Document providerRef;
	private Properties _smtpProps;

	static {
		try {
			providerRef = _initializeProviderList(PROVIDER_LIST_PATH);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

	}

	private static Document _initializeProviderList(String path)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputStream is = SmsUtil.class.getResourceAsStream(path);

		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();
		return doc;
	}

	public SmsUtil(Properties _smtpProps) throws IllegalArgumentException {

		if (providerRef == null) {
			throw new IllegalArgumentException();
		}
		setSmtpProps(_smtpProps);
	}

	public Properties getSmtpProps() {
		return _smtpProps;
	}

	private void setSmtpProps(Properties _smtpProps) {
		if (_smtpProps == null || _smtpProps.isEmpty())
			throw new IllegalArgumentException(
					"ERR101: no smtp properties were passed");

		this._smtpProps = _smtpProps;
	}

	public boolean sendSMS(String number, String provider, String msg)
			throws IllegalArgumentException {

		if (StringUtils.isEmpty(number) || StringUtils.isEmpty(provider)
				|| StringUtils.isEmpty(msg)) {
			throw new IllegalArgumentException(
					"ERR102: cell number, cell provider and sms message must be provided ");
		}

		String toAddr = getEmailAddr(number, provider);
		EmailVO emailVO = new EmailVO(new String[] { toAddr }, new String[] {},
				new String[] {}, _smtpProps.getProperty("smtp.from.addr"), msg);
		return sendEmail(emailVO);
	}

	private String getEmailAddr(String number, String provider) {

		String emailAddr = "";

		if (StringUtils.isEmpty(number) || StringUtils.isEmpty(provider)) {
			return emailAddr;
		}

		Element el = providerRef.getDocumentElement();
		// .getElementById(provider);
		if (el == null) {
			return emailAddr;
		}

		NodeList pp = el.getElementsByTagName("provider");
		if (pp == null || pp.getLength() == 0) {
			return emailAddr;
		}

		Node provNode = null;
		for (int x = 0; (x < pp.getLength() && provNode == null); x++) {

			Node n = pp.item(x);
			if (!"provider".equals(n.getNodeName())) {
				continue;
			}

			NamedNodeMap attrMap = n.getAttributes();
			if (attrMap == null || attrMap.getLength() == 0
					|| attrMap.getNamedItem("id") == null) {
				continue;
			}

			if (provider.equalsIgnoreCase(attrMap.getNamedItem("id")
					.getNodeValue())) {
				provNode = n;
			}

		}

		if (provNode == null || !provNode.hasChildNodes()) {
			return emailAddr;
		}

		Node domainNode = null;
		NodeList nl = provNode.getChildNodes();
		for (int x = 0; (x < nl.getLength() && domainNode == null); x++) {

			Node n = nl.item(x);
			if ("gateway-domain".equals(n.getNodeName())) {
				domainNode = n;
			}
		}

		if (domainNode == null) {
			return emailAddr;
		}

		String domain = domainNode.getTextContent();
		if (StringUtils.isEmpty(domain)) {
			return emailAddr;
		}

		emailAddr = StringUtils.trim(number) + "@" + StringUtils.trim(domain);

		return emailAddr;
	}

	private boolean sendEmail(EmailVO emailVO) {
		// TODO Auto-generated method stub

		if (emailVO == null) {
			return false;
		}

		final String uid = _smtpProps.getProperty("smtp.from.addr");
		final String pwd = _smtpProps.getProperty("smtp.from.addr.pwd");

		Properties mailProps = getSmtpConfig(_smtpProps);

		Session session = Session.getInstance(mailProps,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(uid, pwd);
					}
				});

		session.setDebug(true);

		javax.mail.Message mailMsg = constructMimeMessage(emailVO, session);

		if (mailMsg == null) {
			return false;
		}

		boolean emailSent = false;
		try {
			Transport.send(mailMsg);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return emailSent;
	}

	private Properties getSmtpConfig(Properties smtpPropSource) {
		// TODO Auto-generated method stub
		Properties mailProps = new Properties();

		mailProps.put("mail.smtp.auth",
				smtpPropSource.getProperty("mail.smtp.auth"));
		mailProps.put("mail.smtp.host",
				smtpPropSource.getProperty("mail.smtp.host"));
		mailProps.put("mail.smtp.port",
				smtpPropSource.getProperty("mail.smtp.port"));
		mailProps.put("mail.smtp.starttls.enable",
				smtpPropSource.getProperty("mail.smtp.starttls.enable"));
		return mailProps;
	}

	private Address[] getInetAddress(String[] addresses)
			throws AddressException {

		if (addresses == null || addresses.length == 0) {
			return new Address[0];
		}

		Address[] addr = new InternetAddress[addresses.length];
		int x = 0;
		for (String address : addresses) {

			addr[x] = new InternetAddress(address);
			x++;
		}

		return addr;
	}

	private Message constructMimeMessage(EmailVO msg, Session session) {
		if (msg == null || session == null) {
			return null;
		}

		Message mimeMsg = new MimeMessage(session);
		try {
			mimeMsg.setFrom(new InternetAddress(msg.getFromAddress()));
			mimeMsg.addRecipients(Message.RecipientType.TO,
					getInetAddress(msg.getToAddresses()));
			if (msg.getCcAddresses() != null && msg.getCcAddresses().length > 0) {
				mimeMsg.addRecipients(Message.RecipientType.CC,
						getInetAddress(msg.getCcAddresses()));
			}

			if (msg.getBccAddresses() != null
					&& msg.getBccAddresses().length > 0) {
				mimeMsg.addRecipients(Message.RecipientType.BCC,
						getInetAddress(msg.getBccAddresses()));
			}
			if (msg.getSubject() != null
					&& msg.getSubject().trim().length() > 0) {
				mimeMsg.setSubject(msg.getSubject());
			}
			StringBuffer content = new StringBuffer();
			if (msg.getMsg() != null) {
				content.append(msg.getMsg());
			}
			mimeMsg.setText(content.toString());
		} catch (Exception e) {
			mimeMsg = null;
		}

		return mimeMsg;
	}
	
	public void hello(){
		
	}

}
