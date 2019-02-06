package com.cafe.crm.services.impl;

import com.cafe.crm.configs.inteface.InfoConfig;
import com.cafe.crm.models.call.ClientCall;
import com.cafe.crm.repositories.call.ClientCallRepository;
import com.cafe.crm.services.interfaces.call.ClientCallService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;
import java.io.IOException;
import java.util.Properties;

@Service
public class ClientCallServiceImpl implements ClientCallService {

    private final ClientCallRepository clientCallRepository;

    private static Logger logger = LoggerFactory.getLogger(ClientCallServiceImpl.class);

    private String mailYandex;

    private String mailPassword;

    public ClientCallServiceImpl(ClientCallRepository clientCallRepository, InfoConfig infoConfig) {
        this.clientCallRepository = clientCallRepository;
        mailYandex = infoConfig.getMailYandex();
        mailPassword = infoConfig.getMailPassword();
    }

    public ClientCall checkClientRequest() {
        ClientCall clientCall;
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");
        props.setProperty("mail.imaps.ssl.trust", "imap.yandex.ru");
        Session session = Session.getDefaultInstance(props, null);
        try {
            Store store;
            store = session.getStore();
            store.connect("imap.yandex.ru", 993, mailYandex, mailPassword);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            SearchTerm sender = new FromTerm(new InternetAddress("noreply@flexbe.com"));
            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            SearchTerm searchTerm = new AndTerm(unseenFlagTerm, sender);
            Message[] message = inbox.search(searchTerm);
            if (message.length > 0) {
                clientCall = getClientCall(message[0]);
                inbox.setFlags(new Message[]{message[0]}, new Flags(Flags.Flag.SEEN), true);
                clientCallRepository.save(clientCall);
            } else {
                return null;
            }
        } catch (MessagingException e) {
            logger.error("Can't get message", e);
            return null;
        } catch (IOException e) {
            logger.error("Don't get info about client", e);
            return null;
        }
        return clientCall;
    }

    private ClientCall getClientCall(Message message) throws IOException, MessagingException {
        ClientCall clientCall = new ClientCall();
        Document doc = Jsoup.parseBodyFragment(message.getContent().toString());
        Element table = doc.select("table").get(2); //select the first table.
        Elements rows = table.select("tr");
        Element row;
        Elements cols;
        for (int i = 1; i < rows.size(); i++) {
            if (rows.get(i).text().equalsIgnoreCase("Телефон")) {
                row = rows.get(i + 1);
                cols = row.select("td");
                clientCall.setClientNumber(cols.text());
            }

            if (rows.get(i).text().equalsIgnoreCase("Введите Ваше имя")) {
                row = rows.get(i + 1);
                cols = row.select("td");
                clientCall.setClientName(cols.text());
            }

            if (rows.get(i).text().equalsIgnoreCase("Введите количество человек")) {
                row = rows.get(i + 1);
                cols = row.select("td");
                clientCall.setNumPerson(Integer.parseInt(cols.text()));
            }

            }
        return clientCall;
    }

    public ClientCall getById(Long id) {
        return clientCallRepository.getOne(id);
    }

    @Override
    public void update(ClientCall clientCall) {
        clientCallRepository.saveAndFlush(clientCall);
    }

    public String getMailYandex() {
        return mailYandex;
    }

    public String getMailPassword() {
        return mailPassword;
    }
}
