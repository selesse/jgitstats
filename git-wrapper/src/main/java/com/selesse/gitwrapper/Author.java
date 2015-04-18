package com.selesse.gitwrapper;

import org.eclipse.jgit.lib.PersonIdent;

import java.util.Objects;

public class Author {
    private String name;
    private String emailAddress;

    public Author(PersonIdent authorIndent) {
        this.name = authorIndent.getName();
        this.emailAddress = authorIndent.getEmailAddress();
    }

    public String getName() {
        return name;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Author author = (Author) o;

        return Objects.equals(getName(), author.getName()) &&
                Objects.equals(getEmailAddress(), author.getEmailAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, emailAddress);
    }

    @Override
    public String toString() {
        return getName() + " <" + getEmailAddress() + ">";
    }
}
