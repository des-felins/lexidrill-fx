package dev.cyberjar.springfx.word;

import dev.cyberjar.springfx.user.User;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(
        name = "study_words",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "term"})
)
public class StudyWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String term;

    private String reading;

    @Column(nullable = false)
    private String translation;

    private boolean active;

    public StudyWord() {
    }

    public StudyWord(User user, String term, String reading, String translation) {
        this.user = user;
        this.term = term;
        this.reading = reading;
        this.translation = translation;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTerm() {
        return term;
    }

    public String getReading() {
        return reading;
    }

    public String getTranslation() {
        return translation;
    }


    public boolean isActive() {
        return active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StudyWord studyWord)) {
            return false;
        }
        return Objects.equals(id, studyWord.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
