package bismark.services;

import bismark.models.Message;
import bismark.models.Ticket;
import bismark.services.interfaces.IReporterService;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.joda.time.DateTime;
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
import java.util.function.Consumer;

@Service
@Qualifier("reporterServiceImpl")
public class ReporterServiceImpl implements IReporterService {

    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public final static String REPORT_NAME = "%s.html";

    public final static String ASSIGNEE = "<br><br><b>%s</b></br>";


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

        if (messages.isEmpty()) {
            return;
        }

        try (MongoClient mongoClient = new MongoClient(dbHost, port)) {
            List<Document> documents = new ArrayList<>();
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection collection = database.getCollection(dbCollection);

            for (Message m : messages) {
                String[] lines = m.getText().split("\n");
                for (int i = 0; i < lines.length; i++) {
                    Ticket ticket = getTicketFromString(lines[i]);
                    documents.add(createDocumentFromMessageTicket(m, ticket, username));
                }
            }

            collection.insertMany(documents);


        } catch (Exception e) {
            System.out.print(e);
        }


    }

    @Override
    public Ticket getTicketFromString(String line) {
        String[] parts = line.split(" - ");
        Ticket ticket = new Ticket();
        if (parts.length == 3) {
            ticket.setNormalTicket(true);

            ticket.setLink(parts[0]);
            ticket.setTitle(parts[1]);
            ticket.setStatus(parts[2]);

            ticket.setLinkTitle(getTicketTitleFromLink(ticket.getLink()));
            ticket.setColor(ticket.getLinkTitle().contains("CMC") ? "red" : "black");
        } else {
            ticket.setNormalTicket(false);
            ticket.setTitle(line);
        }
        return ticket;
    }

    @Override
    public String getTicketTitleFromLink(String link) {
        String[] parts = link.split("/");
        return parts[parts.length - 1];
    }

    @Override
    public void generateHTMLReport() {
        Map<String, List<String>> row = readRowForTodayFromDB();
        Path path = Paths.get(reportPath + generateReportFileName());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Map.Entry<String, List<String>> r : row.entrySet()) {
                String assignee = r.getKey();
                List<String> tickets = r.getValue();

                writer.write(String.format(ASSIGNEE, assignee));
                for (String ticket : tickets) {
                    writer.write(ticket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateReportFileName() {
        DateTime today = new DateTime();
        return String.format(REPORT_NAME, DATE_FORMAT.format(today.toDate()));
    }

    private Map<String, List<String>> readRowForTodayFromDB() {
        Map<String, List<String>> row = new HashMap<>();
        try (MongoClient mongoClient = new MongoClient(dbHost, port)) {
            List<Document> documents = new ArrayList<>();
            MongoDatabase database = mongoClient.getDatabase(dbName);
            MongoCollection collection = database.getCollection(dbCollection);

            DateTime today = new DateTime();
            today = today.withMinuteOfHour(0);
            today = today.withSecondOfMinute(0);
            today = today.withMillisOfDay(0);


            collection.find(Filters.gt("date", today.toDate())).forEach((Consumer<? super Document>) (Document doc) -> {
                if (row.get(doc.getString("assignee")) != null) {
                    row.get(doc.getString("assignee")).add(doc.getString("ticket"));
                } else {
                    row.put(doc.getString("assignee"), new ArrayList<>());
                    row.get(doc.getString("assignee")).add(doc.getString("ticket"));
                }

            });

        } catch (Exception e) {
            System.out.print(e);
        }
        return row;
    }

    private Document createDocumentFromMessageTicket(Message message, Ticket ticket, Map<Long, String> username) {
        Document document = new Document();

        String userName = username.get(message.getSender().getId());
        document.put("assignee", null != userName ? userName : "");
        document.put("ticket", ticket.toString());
        document.put("date", new Date());

        return document;
    }


}
