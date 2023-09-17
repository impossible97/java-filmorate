package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;

@Slf4j
abstract class BasePseudoRepository {
    private static final Map<String, String> storedSqls = new HashMap<>();
    private static boolean isInitialized = false;
    private static final String SQL_REGISTRY_PATH = "src/main/resources/sql";

    protected static String getSql(String name) {
        load();
        return ofNullable(storedSqls.getOrDefault(name, null))
                .orElseThrow(() -> new RuntimeException(
                        String.format("Cant get stored SQL with name %s", name)));
    }

    private static void load() {
        if (isInitialized) return;
        try {
            storedSqls.putAll(
                    scanSQLs(SQL_REGISTRY_PATH)
                            .collect(Collectors.toMap(Path::getFileName, BasePseudoRepository::loadSQLByName))
                            .entrySet().stream()
                            .map(entry -> Pair.of(filenameToSQLName(entry.getKey()), entry.getValue()))
                            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            isInitialized = true;
        } catch (Exception e) {
            log.error("Cant load SQLs registry", e);
        }
    }

    private static String filenameToSQLName(Path filename) {
        return Objects.toString(filename).replaceFirst("\\.sql", "");
    }

    private static Stream<Path> scanSQLs(String path) throws IOException {
        return StreamSupport.stream(Files.newDirectoryStream(Path.of(path)).spliterator(), false);
    }

    private static String loadSQLByName(Path path) {
        try {
            return String.join(" ", Files.readAllLines(path));
        } catch (IOException e) {
            return null;
        }
    }
}
