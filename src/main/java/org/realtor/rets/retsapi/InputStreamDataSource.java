package org.realtor.rets.retsapi;

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class to provide a {@link javax.activation.DataSource} interface to
 * an input stream of unknown characteristics. The <code>DataSource</cdoe>
 * interface requires that its implementor be able to repeatedly restart
 * the read from the beginning. This isn't guaranteed by InputStream, so
 * we encapsulate the InputStream with an object that will buffer the
 * data coming from it. (We can't use <code>mark</code>/<code>reset</code>
 * because the eventual data source consumer might use those methods,
 * which would override use here.
 */
public class InputStreamDataSource implements DataSource
{
    private byte fStreamBytes[];
    private String fContentType;
    private static final int quantum = 4096;

    public InputStreamDataSource(InputStream baseStream, String contentType) throws IOException {
        fContentType = contentType;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte temporaryByteArray[] = new byte[quantum];
        int readCount = 0;

        while (readCount != -1) {
            out.write(temporaryByteArray, 0, readCount);
            readCount = baseStream.read(temporaryByteArray);
        }
        fStreamBytes = out.toByteArray();
    }

    /**
     * Returns the Content-Type header value for the encapsulated content.
     */
    public String getContentType() {
        return fContentType;
    }

    /**
     * Returns an input stream that may be used to access the content of this
     * <code>DataSource</code> A new input stream, set at the beginning of the
     * stream, is returned each time you call this method.
     *
     * @return An {@link InputStream} that will furnish the
     *         associated data.
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(fStreamBytes);
    }

    /**
     * Returns the name of this data source. This class does not provide named data
     * sources; the string "Untitled" is returned.
     *
     * @return The string "Untitled".
     */
    public String getName() {
        return "Untitled";
    }

    /**
     * Conformance to <code>javax.activation.DataSource</code> Throws an
     * {@link IOException} since this DataSource is read-only.
     */
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("InputStreamDataSource is read-only.");
    }

    /**
     * Return the content of the input stream as a full byte array.
     */
    public byte[] contentAsByteArray() {
        return fStreamBytes;
    }

    /**
     * Returns the loaded data as a string. This is primarily for diagnostic
     * purposes, as there are other ways of turning an InputStream into a String.
     *
     * @return A <code>String</code> containing the input data.
     */
    public String bufferedDataAsString() {
        return new String(fStreamBytes);
    }
}

