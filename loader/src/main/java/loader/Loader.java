package loader;

/**
 * Common functionality shared by all java based loaders
 */
public interface Loader {
    void init();

    void down();

    void reset();

    boolean canLoad(String... files);

    boolean canLoad(String filename);

    void load(String... files);

    void load(String filename);
}
