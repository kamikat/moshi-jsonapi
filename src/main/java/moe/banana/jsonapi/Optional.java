package moe.banana.jsonapi;

import java.lang.annotation.*;

/**
 * The annotated field is optional in JSON API specification
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Optional {
}
