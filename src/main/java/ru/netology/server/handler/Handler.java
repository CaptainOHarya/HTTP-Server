package ru.netology.server.handler;

import ru.netology.server.request.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;

// Функциональный Интерфейс обработчик
@FunctionalInterface
public interface Handler {
    // метод
    void handle(Request request, BufferedOutputStream out) throws IOException;// BufferedOutputStream - для ответа
}
