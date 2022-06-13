package ru.netology.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // Используем ExecutorService
    private final ExecutorService executorService;

    // Размер пула потоков зададим через параметры конструктора
    public Server(int pullSize) {
        this.executorService = Executors.newFixedThreadPool(pullSize);
    }

    // В классе будет метод - сервер слушает по определённому порту
    public void listen(int port) {

        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                // Принимать socket в самом цикле
                final Socket socket = serverSocket.accept();
                MyRunnable myRunnable = new MyRunnable(socket);
                // Поставим задачу на выполнение, передадим экземпляр нашего класса MyRunnable
                executorService.submit(myRunnable);

            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


}

