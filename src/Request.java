public class Request {
    String date;
    String timestamp;
    String threadID;
    String userContext;
    String URI_query;
    String URI;
    String resource;
    String dataPayload;
    int duration;

    public Request(String date, String timestamp, String threadID, String userContext, String URI_query, int duration) {
        this.date = date;
        this.timestamp = timestamp;
        this.threadID = threadID;
        this.userContext = userContext;
        this.URI_query = URI_query;
        this.duration = duration;
    }

    public Request(String date, String timestamp, String threadID, String userContext, String resource, String dataPayload, int duration) {
        this.date = date;
        this.timestamp = timestamp;
        this.threadID = threadID;
        this.userContext = userContext;
        this.resource = resource;
        this.dataPayload = dataPayload;
        this.duration = duration;
    }

    @Override
    public String toString() {
        if (URI_query == null) {
            return "Request{" +
                    date + " " +
                    timestamp + " " +
                    threadID + " " +
                    userContext + " " +
                    resource + " " +
                    dataPayload + " " +
                    "in " + duration +
                    '}';
        } else {
            return "Request{" +
                    date + " " +
                    timestamp + " " +
                    threadID + " " +
                    userContext + " " +
                    URI_query + " " +
                    "in " + duration +
                    '}';
        }

    }

    public String getDate() {
        return date;
    }
}
