package moe.banana.jsonapi;

import com.squareup.moshi.JsonQualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Qualifies a meta object that contains non-standard meta-information.
 *
 * Deprecated for implementation issue:
 *  https://github.com/rharter/auto-value-moshi/issues/32
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@JsonQualifier
public @interface Meta {
}
