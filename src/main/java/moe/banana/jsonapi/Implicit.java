package moe.banana.jsonapi;

import com.squareup.moshi.JsonQualifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * the value supports implicit construction of object
 *
 * Deprecated for implementation issue:
 *  https://github.com/rharter/auto-value-moshi/issues/32
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@JsonQualifier
@interface Implicit {
}
