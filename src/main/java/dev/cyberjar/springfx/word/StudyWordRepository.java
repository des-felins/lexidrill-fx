package dev.cyberjar.springfx.word;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyWordRepository extends JpaRepository<StudyWord, Long> {

    List<StudyWord> findByUserIdAndActiveTrueOrderByTermAsc(Long userId);

    Optional<StudyWord> findByUserIdAndTermIgnoreCaseAndActiveTrue(
            Long userId,
            String term
    );
}
