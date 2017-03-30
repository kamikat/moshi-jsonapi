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
     * @deprecated You can find {@link #policy()} more useful in most cases.
     */
    @Deprecated
    int priority() default 100;

    /**
     * Select policy applied to the resource class.
     * A type can have multiple classes to serialize but only one class to deserialize with.
     */
    Policy policy() default Policy.SERIALIZATION_AND_DESERIALIZATION;
}
