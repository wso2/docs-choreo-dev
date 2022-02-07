/*
 *
 *  Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *  This software is the property of WSO2 Inc. and its suppliers, if any.
 *  Dissemination of any information or reproduction of any material contained
 *  herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 *  You may not alter or remove any copyright or other notice from copies of this content.
 *
 */

package com.wso2.choreo.integration.common.email;

import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import java.util.Properties;

/**
 * Base class for email related tests.
 */
public class EmailUtils extends TestNGCitrusSpringSupport {
    private final static Logger log = LoggerFactory.getLogger(EmailUtils.class);

    public static boolean checkForMail(final String searchText) throws Exception {

        Properties properties = new Properties();
        properties.put("mail.imap.host", Constant.ALERT.MAIL_IMAP_HOST);
        properties.put("mail.imap.port", Constant.ALERT.MAIL_IMAP_PORT);
        properties.put("mail.imap.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        try (Store store = emailSession.getStore("imaps")) {
            store.connect(Constant.ALERT.MAIL_IMAP_HOST, Constant.ALERT.MAIL_IMAP_USER,
                    Configuration.ALERT.MAIL_IMAP_PASS);
            try (Folder emailFolder = store.getFolder("INBOX")) {
                emailFolder.open(Folder.READ_ONLY);
                return search(emailFolder, searchText);
            }
        }
    }

    private static boolean search(Folder emailFolder, String searchText) throws Exception {
        int i = 0;
        while (i++ < 300) {
            Message[] messages = emailFolder.search(new EmailSearchCondition(searchText));
            if (messages.length > 0) {
                log.info("Found '{}' emails for the given criteria", messages.length);
                for (Message message : messages) {
                    log.info("Found mail with subject '{}'", message.getSubject());
                }
                return true;
            }
            if (i % 10 == 0) {
                log.info("Searching emails with search text '{}' in the subject.", searchText);
            }
            Thread.sleep(1000);
        }
        return false;
    }

    static class EmailSearchCondition extends SearchTerm {
        private String searchText;

        public EmailSearchCondition(String searchText) {
            this.searchText = searchText;
        }

        @Override
        public boolean match(Message message) {
            try {
                if (message.getSubject().contains(this.searchText)) {
                    return true;
                }
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }
}
