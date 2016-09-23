package moe.banana.jsonapi2;

/**
 * Resource reference object to regulate {@link Resource} and {@link ResourceLinkage}
 */
public interface ResourceRef {

    /**
     * @return resource type
     */
    String getType();

    /**
     * @return resource identifier
     */
    String getId();
}
