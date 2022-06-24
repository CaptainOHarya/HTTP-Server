package ru.netology.server;

import ru.netology.server.handler.Handler;
import ru.netology.server.request.Request;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Создадим собственный класс MyRunnable - наследник интерфейса Runnable, где реализуем собственный метод run
public class MyRunnable implements Runnable {
    // private static final List<String> VALID_PATHS = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final Socket socket;
    private final Map<String, Map<String, Handler>> handlers;
    private final Handler handlerNotFound = (request, out) -> {
        // А здесь укажем код, который отвечает за обработку 404 ошибки
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    };

    // конструктор, куда передадим сокет клиента и handler
    public MyRunnable(Socket socket, Map<String, Map<String, Handler>> handlers) {
        this.socket = socket;
        this.handlers = handlers;

    }

    // переопределим метод run для работы с клиентом, просто вызвав метод обработчика
    @Override
    public void run() {
        processConnection();
    }

    private void processConnection() {
        try (
                // закрыть сокет
                socket;
                final var in = socket.getInputStream();
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            Request request = Request.fromInputStream(in);

            // Работа с handler'ами
            // Получить Map по методу в запросе
            Map<String, Handler> handlerMap = handlers.get(request.getMethod());
            // если у данного метода нет handler
            if (handlerMap == null) {
                handlerNotFound.handle(request, out);
                return;//выход
            }
            // По пути
            Handler handler = handlerMap.get(request.getPath());
            if (handler == null) {
                handlerNotFound.handle(request, out);
                return;//выход
            }

            // если всё ок
            handler.handle(request, out);

        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }


}
