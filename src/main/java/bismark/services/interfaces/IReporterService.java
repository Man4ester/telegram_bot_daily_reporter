package bismark.services.interfaces;

import bismark.models.Message;
import bismark.models.Ticket;

import java.util.List;
import java.util.Map;

public interface IReporterService {

    void storeReportFromMessages(List<Message> messages, Map<Long, String> username);

    Ticket getTicketFromString(String line);

    String getTicketTitleFromLink(String link);

    void generateHTMLReport();

}
