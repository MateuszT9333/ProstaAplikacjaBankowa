package pl.mateusz.springdemo.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class UserLogPass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String login;
    private String haslo;

    public UserLogPass() {
    }

    public UserLogPass(String login, String haslo) {
        this.login = login;
        this.haslo = haslo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserLogPass that = (UserLogPass) o;
        return Objects.equals(login, that.login) &&
                Objects.equals(haslo, that.haslo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(login, haslo);
    }
}
