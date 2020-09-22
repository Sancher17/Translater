package com.test;

import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.open;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import io.github.bonigarcia.wdm.config.DriverManagerType;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import lombok.SneakyThrows;

public class Main {

    private static Map<String, String> dictonary = new HashMap<>();
    private static String pathToFile = "";
    private static String fileName = "";
    private static Properties appProps = new Properties();

    public static void main(String[] args) {
        ChromeDriverManager.getInstance(DriverManagerType.CHROME).browserVersion("85.0.4183.87").setup();
        Configuration.headless = true;
        open("https://translate.google.com/?hl=ru#view=home&op=translate&sl=en&tl=ru");
        scan();
    }

    private static void scan() {
        Scanner scan = null;
        try {
            scan = new Scanner(System.in);
            enterPathToFile(scan);
            enterFileName(scan);

            System.out.println("Enter the world for translate: ");
            String line = scan.nextLine();
            while (!line.equals("99")) {
                if (line.length() > 2) {
                    line = line.trim();
                    String transl = transl(line);
                    dictonary.put(line, transl);
                    writeToFile(line, transl);
                    System.out.println(line + " - " + transl);
                } else {
                    System.out.println("Too small the word");
                }

                line = scan.nextLine();
            }
        } finally {
            if (scan != null) {
                scan.close();
            }
        }
    }

    private static void enterFileName(Scanner scan) {
        System.out.println("Enter file name: ");
        fileName = scan.nextLine();
        if (fileName.length() < 1) {
            System.out.println("Enter file name: ");
            fileName = scan.nextLine();
        } else if (fileName.equals("1")) {
            fileName = getProperty("fileName");
            System.out.println("Loaded last fileName: " + fileName);
        }
        appProps.setProperty("fileName", fileName);
    }

    private static void enterPathToFile(Scanner scan) {
        System.out.println("Enter file path: ");
        pathToFile = scan.nextLine();
        if (pathToFile.length() < 1) {
            System.out.println("Enter file path: ");
            pathToFile = scan.nextLine();
        } else if (pathToFile.equals("1")) {
            pathToFile = getProperty("pathToFile");
            System.out.println("Loaded last path: " + pathToFile);
        }
        appProps.setProperty("pathToFile", pathToFile);
    }

    private static String transl(String string) {
        String text = "";
        try {
            $x("//textarea[@id='source']").clear();
            Selenide.sleep(1000);
            $x("//textarea[@id='source']").sendKeys(string);
            text = $x("//span[@class='tlid-translation translation']").getText();
        } catch (Exception e) {
            System.out.println("Some exception, try next");
        }
        return text;
    }

    @SneakyThrows
    private static String getProperty(String propertyName) {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";
        appProps.load(new FileInputStream(appConfigPath));
        return appProps.getProperty(propertyName);
    }

    private static void writeToFile(String word, String translate) {
        try {
            String fullPath = pathToFile + "/" + fileName + ".txt";
            File file = new File(fullPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, true);
            writer.write(word + " - " + translate + "\n");
            writer.flush();
//            for (Map.Entry<String, String> m : dictonary.entrySet()) {
//                String newRow = m.getKey() + " - " + m.getValue() + "\n";
//                writer.write(newRow);
//                writer.flush();
//            }

        } catch (Exception e) {
            System.out.println("Something goes wrong " + e);
        }
    }
}
