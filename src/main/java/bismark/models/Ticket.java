package bismark.models;

public class Ticket {

    private static final String REPORT_LINE_TEMPLATE = "<a href=\"%s\" target=\"_blank\" rel=\"noopener noreferrer\" class=\"x_OWAAutoLink\">%s</a> - %s - <b><i style=\"color:%s;\">%s</i></b> <br />";

    private String link;

    private String linkTitle;

    private String title;

    private String status;

    private String color;

    private boolean normalTicket;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isNormalTicket() {
        return normalTicket;
    }

    public void setNormalTicket(boolean normalTicket) {
        this.normalTicket = normalTicket;
    }

    @Override
    public String toString() {
        return normalTicket ? String.format(REPORT_LINE_TEMPLATE, link, linkTitle, title, color, status) : title;
    }
}
