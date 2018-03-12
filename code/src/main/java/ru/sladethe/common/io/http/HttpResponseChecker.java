package ru.sladethe.common.io.http;

/**
 * @author Maxim Shipko (sladethe@gmail.com)
 */
public interface HttpResponseChecker {
    boolean check(HttpResponse response);
}
