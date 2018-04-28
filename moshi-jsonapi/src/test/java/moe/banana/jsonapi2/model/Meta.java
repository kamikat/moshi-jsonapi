package moe.banana.jsonapi2.model;

import java.util.List;

@SuppressWarnings("all")
public class Meta {
    public String copyright;
    public List<String> authors;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meta meta = (Meta) o;

        if (copyright != null ? !copyright.equals(meta.copyright) : meta.copyright != null) return false;
        return authors != null ? authors.equals(meta.authors) : meta.authors == null;

    }

    @Override
    public int hashCode() {
        int result = copyright != null ? copyright.hashCode() : 0;
        result = 31 * result + (authors != null ? authors.hashCode() : 0);
        return result;
    }
}
