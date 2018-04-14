package bismark.services.interfaces;

import bismark.models.Message;
import bismark.models.Ticket;

import java.util.List;
import java.util.Map;

public interface IReporterService {

    /**
     * MEthod to store report in FS.
     * @param messages
     * @param username
     */
    void storeReportFromMessages(List<Message> messages, Map<Long, String> username);

    /**
     * Parse txt message to {@link Ticket}
     * @param line
     * @return ticket
     */
    Ticket getTicketFromString(String line);

    /**
     * To read title of ticket from String
     * @param link
     * @return correct title for ticket
     */
    String getTicketTitleFromLink(String link);

    /**
     * Method to generate HTML report
     */
    List<String> generateHTMLReport();

}
