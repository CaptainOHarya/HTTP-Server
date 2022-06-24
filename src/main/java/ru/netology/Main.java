package ru.netology;

import ru.netology.server.Server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;


public class Main {
    final static int PORT = 11111;
    final static int PULL_SIZE = 64;

    public static void main(String[] args) {
        // Запуск сервера
        Server server = new Server(PULL_SIZE);
// обработчики на определённые шаблоны путей
        server.addHandler("GET", "/classic.html", (request, out) -> {
            final var filePath = Path.of(".", "public", request.getPath());
            final var mimeType = Files.probeContentType(filePath);
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(content);
            out.flush();


        });
        // вызовем метод listen
        server.listen(PORT);

    }
}


