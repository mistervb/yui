package br.com.blackhunter.bots.yui.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class YuiLogger {
    private static final Logger logger = Logger.getLogger(YuiLogger.class.getName());
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE_PREFIX = "log_";
    private static final String LOG_FILE_EXTENSION = ".log";
    private static FileHandler fileHandler;

    static {
        setupLogger();
    }

    private static void setupLogger() {
        try {
            // Cria o diretório "logs" se não existir
            Path baseLogDir = Paths.get(LOG_DIRECTORY);
            if (!Files.exists(baseLogDir)) {
                Files.createDirectories(baseLogDir);
            }

            // Cria o diretório com o nome da data atual (exemplo: "logs/2024_12_27")
            String currentDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
            Path dateLogDir = baseLogDir.resolve(currentDate);
            if (!Files.exists(dateLogDir)) {
                Files.createDirectories(dateLogDir);
            }

            // Define o nome do arquivo de log dentro da pasta com a data
            String logFileName = getLogFileName();
            Path logFilePath = dateLogDir.resolve(logFileName);

            // Cria o FileHandler para o log dentro da pasta com data
            fileHandler = new FileHandler(logFilePath.toString(), true); // Define append = true
            fileHandler.setFormatter(new CustomLogFormatter());
            fileHandler.setEncoding("UTF-8");

            // Adiciona o FileHandler ao logger
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Erro ao configurar o logger: " + e.getMessage());
        }
    }

    private static String getLogFileName() {
        String currentDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        return LOG_FILE_PREFIX + currentDate + LOG_FILE_EXTENSION;
    }

    private static void refreshFileHandler() {
        try {
            // Cria o diretório de logs com base na data atual
            String currentDate = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
            Path baseLogDir = Paths.get(LOG_DIRECTORY);
            Path dateLogDir = baseLogDir.resolve(currentDate);

            // Cria o diretório com a data, caso não exista
            if (!Files.exists(dateLogDir)) {
                Files.createDirectories(dateLogDir);
            }

            // Define o caminho do arquivo de log para a data atual
            String logFileName = getLogFileName();
            Path logFilePath = dateLogDir.resolve(logFileName);

            // Remover o FileHandler atual se ele existir
            if (fileHandler != null) {
                logger.removeHandler(fileHandler);
                fileHandler.close();
            }

            // Cria um novo FileHandler com o novo caminho
            fileHandler = new FileHandler(logFilePath.toString(), true); // Define append = true
            fileHandler.setFormatter(new CustomLogFormatter());
            fileHandler.setEncoding("UTF-8");

            // Adiciona o FileHandler ao logger
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Erro ao atualizar o arquivo de log: " + e.getMessage());
        }
    }

    public static void log(String message) {
        refreshFileHandler();
        logger.log(Level.INFO, message);
    }

    public static void info(String message) {
        refreshFileHandler();
        logger.log(Level.INFO, message);
    }

    public static void warn(String message) {
        refreshFileHandler();
        logger.log(Level.WARNING, message);
    }

    public static void error(String message) {
        refreshFileHandler();
        logger.log(Level.SEVERE, message);
    }

    public static void debug(String message) {
        refreshFileHandler();
        logger.log(Level.FINE, message);
    }

    private static class CustomLogFormatter extends Formatter {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append("[")
                    .append(dateFormat.format(new Date(record.getMillis())))
                    .append("] [")
                    .append(record.getLevel().getName())
                    .append("]: ")
                    .append(formatMessage(record))
                    .append(System.lineSeparator());
            return sb.toString();
        }
    }
}
