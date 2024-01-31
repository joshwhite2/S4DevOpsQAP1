import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//THis is just to test git
//how much needs to be added to change?
public class SuggestionEngineTest {
    private SuggestionEngine suggestionEngine;

    @BeforeEach
    public void setup() {
        suggestionEngine = new SuggestionEngine();
    }

    @Test
    public void testLoadDictionaryData() throws Exception {
        Path tempFile = Files.createTempFile("temp-dictionary", ".txt");
        Files.write(tempFile, "word1\nword2\nword3".getBytes());

        suggestionEngine.loadDictionaryData(tempFile);
        Map<String, Integer> wordMap = suggestionEngine.getWordSuggestionDB();

        assertEquals(1, wordMap.get("word1"));
        assertEquals(1, wordMap.get("word2"));
        assertEquals(1, wordMap.get("word3"));

        // Clean up: Delete the temporary file
        Files.delete(tempFile);
    }
    @Test
    public void testDeletions() {
        String word = "example";
        List<String> deletions = new SuggestionEngine().testWordEdits(word).collect(Collectors.toList());

        assertTrue(deletions.contains("example"));
        assertTrue(deletions.contains("example"));
        assertTrue(deletions.contains("exmple"));
        // ... other assertions for deletions
    }
    @Test
    public void testReplaces() {
        String word2 = "fast";
        List<String> replacements = new SuggestionEngine().testWordEdits(word2).collect(Collectors.toList());

        assertTrue(replacements.contains("east"));
        assertTrue(replacements.contains("fass"));
        assertTrue(replacements.contains("fatt"));
    }
    @Test
    public void testInsert(){
        String word3 = "elementary";
        List<String> inserts = new SuggestionEngine().testWordEdits(word3).collect(Collectors.toList());

        assertTrue(inserts.contains("felementary"));
        assertTrue(inserts.contains("elementaryd"));
        assertTrue(inserts.contains("eleementary"));
    }
    @Test
    public void testTanspose(){
        String word4 = "ridiculous";
        List<String> transposes = new SuggestionEngine().testWordEdits(word4).collect(Collectors.toList());

        assertTrue(transposes.contains("irdiculous"));
        assertTrue(transposes.contains("ridiuclous"));
        assertTrue(transposes.contains("ridiculuos"));

    }
    @Test
    public void testGenerateSuggestions()throws IOException {

        Path tempFile = Files.createTempFile("temp-dictionary", ".txt");
        Files.write(tempFile, "existing\nnonexisting\nwords\nhello\nworld".getBytes());
        String existingWord = "existing";

        SuggestionEngine suggestionEngine = new SuggestionEngine();
        suggestionEngine.loadDictionaryData(tempFile);

        suggestionEngine.getWordSuggestionDB().put(existingWord, 1);
        assertEquals("", suggestionEngine.generateSuggestions(existingWord));

        String wordToGenerateSuggestionsFor = "hellol";
        String suggestions = suggestionEngine.generateSuggestions(wordToGenerateSuggestionsFor);
        assertTrue(suggestions.contains("hello"));

        String wordToGenerateSuggestionFor2 = "worl";
        String suggestions2 = suggestionEngine.generateSuggestions(wordToGenerateSuggestionFor2);
        assertTrue(suggestions2.contains("world"));

        String wordToGenerateSuggestionFor3 = "nnnexisting";
        String suggestions3 = suggestionEngine.generateSuggestions(wordToGenerateSuggestionFor3);
        assertTrue(suggestions3.contains("nonexisting"));



        Files.delete(tempFile);

    }
}

