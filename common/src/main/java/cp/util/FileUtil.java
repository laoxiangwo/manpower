package cp.util;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

import javax.annotation.Nullable;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FileUtil {
    private static final int defaultBufferSize = 8192;

    private FileUtil() {
    }

    /**
     * The number of bytes in a kilobyte.
     */
    public static final long ONE_KB = 1024;

    /**
     * The number of bytes in a megabyte.
     */
    public static final long ONE_MB = ONE_KB * ONE_KB;

    /**
     * The number of bytes in a gigabyte.
     */
    public static final long ONE_GB = ONE_KB * ONE_MB;

    private static final MessageFormat FILE_SIZE_FMT = new MessageFormat("{0,number,#.#} {1}");

    enum FileSize {
        GB, MB, KB, bytes
    }

    /**
     * Returns a human-readable version of the file size, where the input represents a specific number of bytes.
     *
     * @param size the number of bytes
     *
     * @return a human-readable display value (includes units)
     */
    // Lifted from org.apache.commons.io.FileUtils with modifications
    // to make size string accurate to at least one decimal place
    public static String byteCountToDisplaySize(long size) {
        float displayValue;
        FileSize displaySize;

        if (size / ONE_GB > 0) {
            displayValue = (float) size / ONE_GB;
            displaySize = FileSize.GB;
        } else if (size / ONE_MB > 0) {
            displayValue = (float) size / ONE_MB;
            displaySize = FileSize.MB;
        } else if (size / ONE_KB > 0) {
            displayValue = (float) size / ONE_KB;
            displaySize = FileSize.KB;
        } else {
            displayValue = size;
            displaySize = FileSize.bytes;
        }

        return FILE_SIZE_FMT.format(new Object[]{displayValue, displaySize.name()});
    }

    private static final LoadingCache<Path, String> baseNameCache = CacheBuilder.newBuilder()
       .concurrencyLevel(4)
       .weakKeys()
       .expireAfterAccess(20, TimeUnit.MINUTES)
       .build(new CacheLoader<Path, String>() {
           @Override
           public String load(Path path)
              throws Exception {
               String fname = path.toFile().getName();
               int idx = fname.lastIndexOf('.');

               return idx < 0 ? fname : fname.substring(0, idx);
           }
       });

    /**
     * Returns the name of file with the given path, without the file extension or any directory
     * paths or anything else. Example:
     *
     * <p/>
     * "src/test/resources/dataStuff.idx" = "dataStuff"
     * "us/catalist/namesDict.txt" = "namesDict"
     *
     * @param path The path to get base file/resource name out of.
     * @return The base name of the given {@link Path}.
     */
    public static String getBaseName(Path path) {
        return baseNameCache.getUnchecked(path);
    }

    public static BufferedInputStream inputStream(File file)
       throws IOException {
        return new BufferedInputStream(new FileInputStream(file), defaultBufferSize);
    }

    public static BufferedReader resourceReader(String path) {
        try {
            return reader(Resources.getResource(path).openStream());
        } catch (Throwable t) {
            throw Throwables.propagate(t);
        }
    }

    public static BufferedReader resourceReader(Class<?> ctxClass, String path) {
        try {
            return reader(Resources.getResource(ctxClass, path).openStream());
        } catch (Throwable t) {
            throw Throwables.propagate(t);
        }
    }

    public static BufferedReader reader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is, Charsets.UTF_8), defaultBufferSize);
    }

    @SuppressWarnings("resource")
    public static BufferedInputStream inputStream(InputStream is) {
        return BufferedInputStream.class.isInstance(is)
           ? (BufferedInputStream) is
           : new BufferedInputStream(is, defaultBufferSize);
    }

    public static BufferedInputStream streamResource(String resource)
       throws IOException {
        return inputStream(Resources.getResource(resource).openStream());
    }

    public static BufferedInputStream streamResource(Class<?> contextClass, String resource)
       throws IOException {
        return inputStream(Resources.getResource(contextClass, resource).openStream());
    }

    public static OutputStream outputStream(File file)
       throws IOException {
        return new BufferedOutputStream(new FileOutputStream(file), defaultBufferSize);
    }

    @SuppressWarnings("resource")
    public static BufferedOutputStream outputStream(OutputStream os) {
        return BufferedOutputStream.class.isInstance(os)
           ? (BufferedOutputStream) os : new BufferedOutputStream(os, defaultBufferSize);
    }

    static final Splitter PROPS_SPLIT = Splitter.on('=').omitEmptyStrings().trimResults();

    public static Map<String, String> loadProperties(String classPath) {
        try {
            URL propUrl = Resources.getResource(classPath).toURI().toURL();

            return Resources.readLines(propUrl, Charsets.UTF_8, new LineProcessor<Map<String, String>>() {
                Map<String, String> props = Maps.newLinkedHashMap();

                @Override
                public boolean processLine(String line) {
                    if (line.startsWith("#"))
                        return true;

                    Iterable<String> it = PROPS_SPLIT.split(line);

                    if (Iterables.size(it) != 2)
                        return true;

                    props.put(Iterables.get(it, 0), Iterables.get(it, 1));
                    return true;
                }

                @Override
                public Map<String, String> getResult() {
                    return props;
                }
            });
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing
     * if {@code closeable} is null.
     */
    public static void closeQuietly(Closeable... resources) {
        for (Closeable c : resources) {
            closeQuietly(c);
        }
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing
     * if {@code closeable} is null.
     */
    @SuppressWarnings("ConstantConditions")
    public static void closeQuietly(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes {@code socket}, ignoring any checked exceptions. Does nothing if
     * {@code socket} is null.
     */
    @SuppressWarnings("ConstantConditions")
    public static void closeQuietly(@Nullable Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes {@code serverSocket}, ignoring any checked exceptions. Does nothing if
     * {@code serverSocket} is null.
     */
    @SuppressWarnings("ConstantConditions")
    public static void closeQuietly(@Nullable ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes {@code a} and {@code b}. If either close fails, this completes
     * the other close and rethrows the first encountered exception.
     */
    @SuppressWarnings("ConstantConditions")
    public static void closeAll(@Nullable Closeable a, @Nullable Closeable b)
       throws IOException {
        Throwable thrown = null;
        try {
            a.close();
        } catch (Throwable e) {
            thrown = e;
        }

        try {
            b.close();
        } catch (Throwable e) {
            if (thrown == null)
                thrown = e;
        }

        if (thrown == null)
            return;

        if (thrown instanceof IOException)
            throw (IOException) thrown;
        if (thrown instanceof RuntimeException)
            throw (RuntimeException) thrown;
        if (thrown instanceof Error)
            throw (Error) thrown;

        throw new AssertionError(thrown);
    }

    /**
     * Deletes the contents of {@code dir}. Throws an IOException if any file
     * could not be deleted, or if {@code dir} is not a readable directory.
     */
    public static void deleteContents(File dir)
       throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("not a readable directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }
}
