package moe.banana.jsonapi2;

public enum Policy {

    /**
     * Registered class is intended to be used in both serialization and deserialization process.
     * Other class with same type name can only be registered with {@link #SERIALIZATION_ONLY} policy.
     */
    SERIALIZATION_AND_DESERIALIZATION,

    /**
     * Registered class is available only when doing serialization.
     */
    SERIALIZATION_ONLY,

    /**
     * Registered class is available only when doing de-serialization.
     * Other class with same type name can only be registered with {@link #SERIALIZATION_ONLY} policy.
     */
    DESERIALIZATION_ONLY

}
