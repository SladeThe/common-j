package ru.sladethe.common.io;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
@SuppressWarnings("unused")
public final class MimeType {
    public static final String TEXT_PLAIN = "text/plain";

    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_CSS = "text/css";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_JAVASCRIPT = "application/javascript";

    public static final String APPLICATION_PDF = "application/pdf";
    public static final String APPLICATION_X_TEX = "application/x-tex";

    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final String APPLICATION_ZIP = "application/zip";

    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private MimeType() {
        throw new UnsupportedOperationException();
    }
}
