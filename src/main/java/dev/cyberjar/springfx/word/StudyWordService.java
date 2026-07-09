package dev.cyberjar.springfx.word;

import dev.cyberjar.springfx.user.CurrentUser;
import dev.cyberjar.springfx.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyWordService {

    private final StudyWordRepository studyWordRepository;
    private final CurrentUser currentUser;

    public StudyWordService(
            StudyWordRepository studyWordRepository,
            CurrentUser currentUser
    ) {
        this.studyWordRepository = studyWordRepository;
        this.currentUser = currentUser;
    }

    public List<StudyWord> findWordsForCurrentUser() {
        return studyWordRepository.findByUserIdAndActiveTrueOrderByTermAsc(
                currentUser.getRequiredUserId()
        );
    }

    public StudyWord addWord(
            String rawTerm,
            String rawReading,
            String rawTranslation
    ) {
        String term = normalizeRequired(rawTerm, "Word must not be blank");
        String translation = normalizeRequired(rawTranslation, "Translation must not be blank");
        String reading = normalizeOptional(rawReading);

        Long userId = currentUser.getRequiredUserId();

        boolean alreadyExists = studyWordRepository
                .findByUserIdAndTermIgnoreCaseAndActiveTrue(userId, term)
                .isPresent();

        if (alreadyExists) {
            throw new IllegalArgumentException("This word already exists in your list");
        }

        User user = currentUser.getRequired();

        StudyWord word = new StudyWord(
                user,
                term,
                reading,
                translation
        );

        return studyWordRepository.save(word);
    }

    private String normalizeRequired(String value, String errorMessage) {
        String normalized = normalizeOptional(value);

        if (normalized.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }

        if (normalized.length() > 100) {
            throw new IllegalArgumentException("Value must not be longer than 100 characters");
        }

        return normalized;
    }

    private String normalizeOptional(String value) {
        return value == null ? "" : value.trim();
    }

}
