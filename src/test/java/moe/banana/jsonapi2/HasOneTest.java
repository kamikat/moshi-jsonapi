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
    public void hashcodes_equal() throws Exception {
        assertEquals(createHasOne().hashCode(), createHasOne().hashCode());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void when_resourcenotfound_then_throw() throws Exception {
        createHasOne().get();
    }

    @Test
    public void when_resourceavailable_then_return() throws Exception {
        Article article = new Article();
        Document.of(article);
        Person person = new Person();
        person._id = "personId";
        article.author = HasOne.create(article, person);
        assertNotNull(article.author.get());
    }

    private HasOne<Person> createHasOne() {
        Article article = new Article();
        Document.of(article);
        return HasOne.create(article, ResourceLinkage.of("people", "personId"));
    }

}
