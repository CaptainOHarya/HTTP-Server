package ru.netology;

import ru.netology.server.Server;


public class Main {
    final static int PORT = 11111;
    final static int PULL_SIZE = 64;

    public static void main(String[] args) {
        // Запуск сервера
        Server server = new Server(PULL_SIZE);
        // вызовем метод listen
        server.listen(PORT);

    }
}


