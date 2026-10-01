package io.github.guavovic.pontofacial.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.github.guavovic.pontofacial.TestSupport;
import io.github.guavovic.pontofacial.domain.Notice;

class NoticeRepositoryTest {

    @TempDir
    Path folder;

    @Test
    @DisplayName("lista do aviso mais novo para o mais antigo")
    void listsNewestFirst() {
        NoticeRepository notices = new NoticeRepository(TestSupport.database(folder));
        notices.add(LocalDateTime.of(2026, 10, 1, 8, 0), "primeiro");
        notices.add(LocalDateTime.of(2026, 10, 1, 10, 0), "terceiro");
        notices.add(LocalDateTime.of(2026, 10, 1, 9, 0), "segundo");

        List<String> messages = notices.findAll().stream().map(Notice::message).toList();

        assertEquals(List.of("terceiro", "segundo", "primeiro"), messages);
    }

    @Test
    @DisplayName("limpa todos os avisos")
    void clearsEverything() {
        NoticeRepository notices = new NoticeRepository(TestSupport.database(folder));
        notices.add(LocalDateTime.of(2026, 10, 1, 8, 0), "um");
        notices.add(LocalDateTime.of(2026, 10, 1, 9, 0), "dois");

        notices.deleteAll();

        assertTrue(notices.findAll().isEmpty());
    }
}
