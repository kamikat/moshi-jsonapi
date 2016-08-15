package moe.banana.jsonapi2;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonApi {

    /**
     * Specify type name of resource (typically, a kebab-cased plural noun)
     */
    String type();

    /**
     * Priority to determine the class to de-serializing a generic type of
     * resource across classes of same resource type name.
     * Class with smaller priority value should be chosen by the conflict
     * resolving function.
     */
    int priority() default 0;
}
