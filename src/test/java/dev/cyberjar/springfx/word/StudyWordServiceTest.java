package dev.cyberjar.springfx.word;

import dev.cyberjar.springfx.user.CurrentUser;
import dev.cyberjar.springfx.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudyWordServiceTest {

    private StudyWordRepository repository;
    private CurrentUser currentUser;
    private User user;
    private StudyWordService service;

    @BeforeEach
    void setUp() {
        repository = mock(StudyWordRepository.class);
        currentUser = new CurrentUser();
        user = new User(42L, "learner");
        currentUser.set(user);
        service = new StudyWordService(repository, currentUser);
    }

    @Test
    void addsNormalizedActiveWordForCurrentUserAndReturnsSavedWord() {
        StudyWord saved = new StudyWord(user, "猫", "ねこ", "cat");
        saved.setId(7L);
        when(repository.findByUserIdAndTermIgnoreCaseAndActiveTrue(42L, "猫"))
                .thenReturn(Optional.empty());
        when(repository.save(any(StudyWord.class))).thenReturn(saved);

        assertSame(saved, service.addWord("  猫  ", "  ねこ  ", "  cat  "));

        ArgumentCaptor<StudyWord> captor = ArgumentCaptor.forClass(StudyWord.class);
        verify(repository).findByUserIdAndTermIgnoreCaseAndActiveTrue(42L, "猫");
        verify(repository).save(captor.capture());
        StudyWord word = captor.getValue();
        assertSame(user, word.getUser());
        assertEquals("猫", word.getTerm());
        assertEquals("ねこ", word.getReading());
        assertEquals("cat", word.getTranslation());
        assertTrue(word.isActive());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t "})
    void normalizesMissingReadingToEmptyString(String reading) {
        when(repository.save(any(StudyWord.class))).thenAnswer(call -> call.getArgument(0));

        StudyWord word = service.addWord("cat", reading, "猫");

        assertEquals("", word.getReading());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t "})
    void rejectsBlankTermBeforeAccessingRepository(String term) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.addWord(term, null, "cat"));

        assertEquals("Word must not be blank", error.getMessage());
        verifyNoInteractions(repository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" \t "})
    void rejectsBlankTranslationBeforeAccessingRepository(String translation) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.addWord("cat", null, translation));

        assertEquals("Translation must not be blank", error.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void acceptsRequiredValuesAtHundredCharacterLimitAfterTrimming() {
        String value = "a".repeat(100);
        when(repository.save(any(StudyWord.class))).thenAnswer(call -> call.getArgument(0));

        StudyWord word = service.addWord(" " + value + " ", null, " " + value + " ");

        assertEquals(value, word.getTerm());
        assertEquals(value, word.getTranslation());
    }

    @Test
    void rejectsTermOverHundredCharacters() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.addWord("a".repeat(101), null, "cat"));

        assertEquals("Value must not be longer than 100 characters", error.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsTranslationOverHundredCharacters() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.addWord("cat", null, "a".repeat(101)));

        assertEquals("Value must not be longer than 100 characters", error.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsExistingActiveWordWithoutSaving() {
        when(repository.findByUserIdAndTermIgnoreCaseAndActiveTrue(42L, "cat"))
                .thenReturn(Optional.of(new StudyWord(user, "CAT", "", "猫")));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.addWord(" cat ", null, "猫"));

        assertEquals("This word already exists in your list", error.getMessage());
        verify(repository, never()).save(any(StudyWord.class));
    }

    @Test
    void rejectsAddingWordWhenSignedOut() {
        currentUser.clear();

        assertThrows(IllegalStateException.class, () -> service.addWord("cat", null, "猫"));

        verifyNoInteractions(repository);
    }
}
