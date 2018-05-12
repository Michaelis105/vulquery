package xyz.vulquery.util;

public class HTTPResponse {

    // TODO: What about status code?

    private String message;

    public HTTPResponse() {}

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Response [ message: " + message + " ]";
    }
}
