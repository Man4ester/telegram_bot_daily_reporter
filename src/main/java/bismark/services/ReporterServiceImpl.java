package bismark.services;

import bismark.models.Message;
import bismark.models.Ticket;
import bismark.services.interfaces.IReporterService;
import bismark.utils.ReporterHolder;
import bismark.workers.ReminderWorker;
import bismark.workers.TelegramConfirmationWorker;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
@Qualifier("reporterServiceImpl")
public class ReporterServiceImpl implements IReporterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReporterServiceImpl.class);

    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    @Value("${mongo.host}")
    private String dbHost;

    @Value("${mongo.port}")
    private int port;

    @Value("${mongo.db}")
    private String dbName;

    @Value("${mongo.collection}")
    private String dbCollection;

    @Value("${report.path}")
    private String reportPath;


    @Override
    public void storeReportFromMessages(List<Message> messages, Map<Long, String> username) {

        LOGGER.info("storeReportFromMessages");

        if (messages.isEmpty()) {
            LOGGER.warn("No messages");
            return;
        }

        try (MongoClient mongoClient = new MongoClient(dbHost, port)) {
            List<Document> documents = new ArrayList<>();
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection collection = database.getCollection(dbCollection);

            for (Message m : messages) {
                String[] lines = m.getText().split(ReporterHolder.NEW_LINE);
                for (int i = 0; i < lines.length; i++) {
                    Ticket ticket = getTicketFromString(lines[i]);
                    documents.add(createDocumentFromMessageTicket(m, ticket, username));
                }
            }

            collection.insertMany(documents);


        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }


    }

    @Override
    public Ticket getTicketFromString(String line) {
        String[] parts = line.split(ReporterHolder.PART_SPLITTER);
        Ticket ticket = new Ticket();
        if (parts.length == 3) {
            ticket.setNormalTicket(true);

            ticket.setLink(parts[0]);
            ticket.setTitle(parts[1]);
            ticket.setStatus(parts[2]);

            ticket.setLinkTitle(getTicketTitleFromLink(ticket.getLink()));
            ticket.setColor(ticket.getLinkTitle().contains(ReporterHolder.CMC) ? ReporterHolder.COLOR_CMC : ReporterHolder.COLOR_NOT_CMC);
        } else {
            ticket.setNormalTicket(false);
            ticket.setTitle(line);
        }
        return ticket;
    }

    @Override
    public String getTicketTitleFromLink(String link) {
        String[] parts = link.split(ReporterHolder.URL_SPLITTER);
        return parts[parts.length - 1];
    }

    @Override
    public List<String> generateHTMLReport() {
        LOGGER.info("generateHTMLReport");
        Set<String> userNames = new HashSet<>();
        Map<String, List<String>> row = readRowForTodayFromDB();
        Path path = Paths.get(reportPath + generateReportFileName());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Map.Entry<String, List<String>> r : row.entrySet()) {
                String assignee = r.getKey();
                userNames.add(assignee);
                List<String> tickets = r.getValue();

                writer.write(String.format(ReporterHolder.ASSIGNEE_TEMPLATE, assignee));
                for (String ticket : tickets) {
                    writer.write(ticket);
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return new ArrayList<>(userNames);
    }

    private String generateReportFileName() {
        DateTime today = new DateTime();
        return String.format(ReporterHolder.REPORT_NAME, DATE_FORMAT.format(today.toDate()));
    }

    private Map<String, List<String>> readRowForTodayFromDB() {
        Map<String, List<String>> row = new HashMap<>();
        try (MongoClient mongoClient = new MongoClient(dbHost, port)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection collection = database.getCollection(dbCollection);

            DateTime today = new DateTime();
            today = today.withMinuteOfHour(0);
            today = today.withSecondOfMinute(0);
            today = today.withMillisOfDay(0);


            collection.find(Filters.gt(ReporterHolder.DATE, today.toDate())).forEach((Consumer<? super Document>) (Document doc) -> {
                if (row.get(doc.getString(ReporterHolder.ASSIGNEE)) != null) {
                    row.get(doc.getString(ReporterHolder.ASSIGNEE)).add(doc.getString(ReporterHolder.TICKET));
                } else {
                    row.put(doc.getString(ReporterHolder.ASSIGNEE), new ArrayList<>());
                    row.get(doc.getString(ReporterHolder.ASSIGNEE)).add(doc.getString(ReporterHolder.TICKET));
                }

            });

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return row;
    }

    private Document createDocumentFromMessageTicket(Message message, Ticket ticket, Map<Long, String> username) {
        Document document = new Document();

        String userName = username.get(message.getSender().getId());
        document.put(ReporterHolder.ASSIGNEE, null != userName ? userName : "");
        document.put(ReporterHolder.TICKET, ticket.toString());
        document.put(ReporterHolder.DATE, new Date());

        return document;
    }


}
