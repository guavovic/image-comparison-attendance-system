package io.github.guavovic.pontofacial.recognition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.guavovic.pontofacial.TestSupport;

class SamplePhotosTest {

    private static final List<String> REGISTERED = List.of("ana-souza", "bruno-lima", "carla-mendes", "diego-rocha",
            "elisa-martins");

    private final ImageComparator comparator = new ImageComparator();

    @ParameterizedTest(name = "{0} é reconhecido(a) como ela mesma")
    @ValueSource(strings = { "ana-souza", "bruno-lima", "carla-mendes", "diego-rocha", "elisa-martins" })
    @DisplayName("as fotos de teste dos cadastrados passam do limite, só na própria pessoa")
    void registeredPeopleAreRecognized(String person) throws IOException {
        String best = bestMatch(person + ".jpg");

        assertEquals(person, best);
        assertTrue(score(person + ".jpg", person) >= ImageComparator.THRESHOLD, "nota abaixo do limite");
    }

    @Test
    @DisplayName("o rosto desconhecido não passa do limite com ninguém")
    void unknownFaceIsNotRecognized() throws IOException {
        for (String person : REGISTERED) {
            double score = score("desconhecido.jpg", person);

            assertTrue(score < ImageComparator.THRESHOLD, person + " tirou " + score);
        }
    }

    @Test
    @DisplayName("nenhum cadastrado passa do limite com a foto de outro")
    void nobodyIsRecognizedAsSomeoneElse() throws IOException {
        for (String probe : REGISTERED) {
            for (String person : REGISTERED) {
                if (!probe.equals(person)) {
                    double score = score(probe + ".jpg", person);

                    assertTrue(score < ImageComparator.THRESHOLD, probe + " contra " + person + ": " + score);
                }
            }
        }
    }

    private String bestMatch(String testPhoto) throws IOException {
        String best = null;
        double bestScore = -1;
        for (String person : REGISTERED) {
            double score = score(testPhoto, person);
            if (score > bestScore) {
                best = person;
                bestScore = score;
            }
        }
        return best;
    }

    private double score(String testPhoto, String person) throws IOException {
        return comparator.compare(TestSupport.sampleImage("test-photos", testPhoto),
                TestSupport.sampleImage("photos", person + ".png")).score();
    }
}
