package ru.netology.server;

import ru.netology.server.handler.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    // Используем ExecutorService
    private final ExecutorService executorService;
    // Хранить наши handler'ы, через вложенные Map {method -> {path -> handler, path2 -> handler2,...}}
    private final Map<String, Map<String, Handler>> handlers;

    // Размер пула потоков зададим через параметры конструктора
    public Server(int pullSize) {
        this.executorService = Executors.newFixedThreadPool(pullSize);
        // Логично использовать ConcurrentHashMap для нашей реализации, т.к. она потокобезопасна
        // Мы не знаем как будут работать с нашим сервером
        this.handlers = new ConcurrentHashMap<>();

    }

    // метод добавления handler'a
    public void addHandler(String method, String path, Handler handler) {
        // проверка путей, т.к. каждый метод будет добавляться в 1ый раз
        if (this.handlers.get(method) == null) {
            this.handlers.put(method, new ConcurrentHashMap<>());

        }
        this.handlers.get(method).put(path, handler);
    }

    // В классе будет метод - сервер слушает по определённому порту
    public void listen(int port) {

        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                // Принимать socket в самом цикле
                final Socket socket = serverSocket.accept();
                MyRunnable myRunnable = new MyRunnable(socket, handlers);
                // Поставим задачу на выполнение, передадим экземпляр нашего класса MyRunnable
                executorService.submit(myRunnable);

            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }


}

