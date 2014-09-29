package com.selesse.gitwrapper;

import org.eclipse.jgit.lib.PersonIdent;

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

        return emailAddress.equals(author.emailAddress) && name.equals(author.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + emailAddress.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getName() + " - " + getEmailAddress();
    }
}
