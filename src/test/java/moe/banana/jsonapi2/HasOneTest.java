package moe.banana.jsonapi2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import moe.banana.jsonapi2.model.Article;
import moe.banana.jsonapi2.model.Person;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(JUnit4.class)
public class HasOneTest {

    @Test
    public void equality() throws Exception {
        assertEquals(createHasOne(), createHasOne());
    }

    @Test
    public void hashcode_equality() throws Exception {
        assertEquals(createHasOne().hashCode(), createHasOne().hashCode());
    }

    @Test
    public void resource_resolution() throws Exception {
        Document document = new Document();
        Person person = new Person();
        person.setId("personId");
        document.include(person);
        Article article = new Article();
        article.author = createHasOne();
        assertEquals(article.author.get(document), person);
    }

    private HasOne<Person> createHasOne() {
        return new HasOne<>("people", "personId");
    }

}
