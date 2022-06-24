package ru.netology.server.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// Класс запроса
public class Request {
    // метод запроса
    private final String method;
    // путь запроса
    private final String path;
    // для хранения header'ов воспользуемся Map
    private final Map<String, String> headers;
    // тело запроса
    private final InputStream body;

    // Создадим приватный конструктор, что бы никто не мог вызвать его конструктор
    private Request(String method, String path, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    // геттеры для всех полей
    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }

    // метод что бы парсить запрос
    // в нём работа только с валидными запросами, поэтому метод просто выбрасывает исключение IOException если что то не так
    public static Request fromInputStream(InputStream inputStream) throws IOException {
        // обёртка на InputStream
        final var in = new BufferedReader(new InputStreamReader(inputStream));
        // read only request line for simplicity
        // must be in form GET /path HTTP/1.1
        final var requestLine = in.readLine();
        final var parts = requestLine.split(" ");

        if (parts.length != 3) {
            // если не так, то будем бросать исключение
            throw new IOException("Invalid request!!!");
        }

        // Получим метод и путь запроса
        String method = parts[0];
        String path = parts[1];

        // Вытащим все заголовки
        // Map для хранения заголовков
        Map<String, String> headers = new HashMap<>();
        // переменная для считывания промежуточного значения
        String intermediateValue;

        // читать в переменную из нашего InputStream
        // и сразу же проверять на то, что следующее значение не Empty
        // т.к. разделитель между header'ами и body пустая строка перед

        while (!(intermediateValue = in.readLine()).isEmpty()) {
            // распарсим значение каждого headera
            // надо вычленить двоеточие
            int index = intermediateValue.indexOf(":");
            // название
            String name = intermediateValue.substring(0, index);
            // и всё остальное с index + 2
            String value = intermediateValue.substring(index + 2);
            // добавим в Map
            headers.put(name, value);
        }
        // создаём объект класса Request
        Request request = new Request(method, path, headers, inputStream); // body после пустой строки, поэтому в качестве body InputStream
        // т.е. отдаём всё что осталось

        // возвращаем объект класса Request
        return request;
    }

    // и метод toString
    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                '}';
    }
}
