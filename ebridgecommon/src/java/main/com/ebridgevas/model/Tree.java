package com.ebridgevas.model;

import java.io.Serializable;
import java.util.List;

/**
 * david@ebridgevas.com
 *
 */
public interface Tree <N extends Serializable> extends Serializable {
    public List<N> getRoots();
    public N getParent( N node );
    public List<N> getChildren( N node);
}
