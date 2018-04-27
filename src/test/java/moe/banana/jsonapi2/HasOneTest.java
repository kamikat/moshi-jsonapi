package moe.banana.jsonapi2;

import moe.banana.jsonapi2.model.Person;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
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
        Document document = new ObjectDocument();
        assertNull(createHasOne().get(document));
        Person holder = new Person();
        assertEquals(createHasOne().get(document, holder), holder);
        Person person = new Person();
        person.setId("5");
        document.addInclude(person);
        assertEquals(createHasOne().get(document), person);
    }

    @Test
    public void serialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(HasOne.class).toJson(createHasOne()),
                equalTo("{\"data\":{\"type\":\"people\",\"id\":\"5\"}}"));
    }

    @Test
    public void serialization_null() throws Exception {
        assertThat(TestUtil.moshi().adapter(HasOne.class).toJson(new HasOne<>(null)),
                equalTo("{\"data\":null}"));
    }

    @Test
    public void deserialization() throws Exception {
        assertThat(TestUtil.moshi().adapter(HasOne.class).fromJson("{\"data\":{\"type\":\"people\",\"id\":\"5\"}}"),
                equalTo((HasOne) createHasOne()));
        assertNull(TestUtil.moshi().adapter(HasOne.class).fromJson("{\"data\":null}").get());
    }

}
