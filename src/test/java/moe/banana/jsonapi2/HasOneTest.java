package moe.banana.jsonapi2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Person;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static org.junit.Assert.*;

public class HasOneTest {

    private HasOne<Person> createHasOne() {
        return new HasOne<>("people", "5");
    }

    @Test
    public void equality() throws Exception {
        assertEquals(createHasOne(), createHasOne());
    }

    @Test
    public void hashcode_equality() throws Exception {
        assertEquals(createHasOne().hashCode(), createHasOne().hashCode());
    }

    @Test
    public void resolution() throws Exception {
        Document document = new Document();
        assertNull(createHasOne().get(document));
        Person holder = new Person();
        assertEquals(createHasOne().get(document, holder), holder);
        Person person = new Person();
        person.setId("5");
        document.include(person);
        assertEquals(createHasOne().get(document), person);
    }

    @Test
    public void serialization() throws Exception {
        throw new NotImplementedException();
    }

    @Test
    public void deserialization() throws Exception {
        throw new NotImplementedException();
    }

}
