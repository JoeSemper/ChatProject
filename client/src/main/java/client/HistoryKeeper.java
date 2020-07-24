package client;

import java.io.*;
import java.util.Arrays;

public class HistoryKeeper {


    /* создает файл сохранения если его нет */
    public static void createFile(String nick) throws IOException {

        String fileName = String.format("history_%s.txt", nick);
        File fileList = new File("clientHistory");
        String[] fileListArr = fileList.list();

        for (String x : fileListArr) {
            if (x.equals(fileName)) {
                return;
            }
        }

        File file = new File(String.format("clientHistory/%s", fileName));
        file.createNewFile();
    }


    /* записывает данные в файл */
    public static void writeToFile (String nick, String data) throws IOException {

        createFile(nick);

        String fileName = String.format("history_%s.txt", nick);

        try (FileOutputStream fos = new FileOutputStream(String.format("clientHistory/%s", fileName))) {
            fos.write(data.getBytes());
        }

    }


    /* считывает данные из фала */
    public static String readFromFile (String nick) throws IOException {

        createFile(nick);

        String fileName = String.format("history_%s.txt", nick);

        String data;

        StringBuilder sb = new StringBuilder();

        try(FileInputStream fis = new FileInputStream(String.format("clientHistory/%s", fileName))){
            int x;
            while ((x = fis.read()) != -1){
                sb.append((char) x);
            }
        }

        return data = sb.toString();
    }


    /* возвращает последние 100 сообщений */
    public static String readLast100 (String nick) throws IOException {
        String data = readFromFile(nick);

        StringBuilder sb = new StringBuilder();

        String[] tempData = data.split("\\n");

        if(tempData.length <= 100){
            for (int i = 0; i <tempData.length; i++) {
                sb.append(tempData[i] + "\n");
            }
            return data = sb.toString();
        }

        for (int i = tempData.length-100; i<tempData.length; i++) {
            sb.append(tempData[i] + "\n");
        }
        return data = sb.toString();
    }
}
