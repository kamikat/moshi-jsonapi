package moe.banana.jsonapi2.model;

import moe.banana.jsonapi2.HasMany;
import moe.banana.jsonapi2.HasOne;
import moe.banana.jsonapi2.JsonApi;
import moe.banana.jsonapi2.Resource;

@JsonApi(type = "private")
public class Private extends Resource {

    private String a;
    private Double b;
    private Integer c;
    private Boolean d;
    private HasOne<Person> author;
    private HasMany<Person> readers;
    private transient String ignored;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public Double getB() {
        return b;
    }

    public void setB(Double b) {
        this.b = b;
    }

    public Integer getC() {
        return c;
    }

    public void setC(Integer c) {
        this.c = c;
    }

    public Boolean getD() {
        return d;
    }

    public void setD(Boolean d) {
        this.d = d;
    }

    public HasOne<Person> getAuthor() {
        return author;
    }

    public void setAuthor(HasOne<Person> author) {
        this.author = author;
    }

    public HasMany<Person> getReaders() {
        return readers;
    }

    public void setReaders(HasMany<Person> readers) {
        this.readers = readers;
    }

    public String getIgnored() {
        return ignored;
    }

    public void setIgnored(String ignored) {
        this.ignored = ignored;
    }

}
