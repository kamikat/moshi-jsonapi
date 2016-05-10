package moe.banana.jsonapi;

import com.squareup.moshi.JsonQualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * the field can be either single object or array of objects.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@JsonQualifier
public @interface OneOrMany {
}
