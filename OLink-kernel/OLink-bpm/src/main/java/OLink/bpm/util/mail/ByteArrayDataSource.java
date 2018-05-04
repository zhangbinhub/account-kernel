package OLink.bpm.util.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.activation.DataSource;

/**
 * A simple DataSource for demonstration purposes.
 * This class implements a DataSource from:
 * 	an InputStream
 *	a byte array
 * 	a String
 *
 * @author John Mani
 * @author Bill Shannon
 * @author Max Spivak
 */
public class ByteArrayDataSource implements DataSource {
    private byte[] data;	// data
    private String type;	// content-type

    /* Create a DataSource from an input stream */
    public ByteArrayDataSource(InputStream is, String type) {
        this.type = type;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
	    int ch;

	    while ((ch = is.read()) != -1)
                // XXX - must be made more efficient by
	        // doing buffered reads, rather than one byte reads
	        os.write(ch);
	    data = os.toByteArray();

        } catch (IOException ioex) { }
    }

    /* Create a DataSource from a byte array */
    public ByteArrayDataSource(byte[] data, String type) {
        this.data = data;
	this.type = type;
    }

    /* Create a DataSource from a String */
    public ByteArrayDataSource(String data, String type) {
	// Assumption that the string contains only ASCII
	// characters!  Otherwise just pass a charset into this
	// constructor and use it in getBytes()
	try {
		this.data = data.getBytes(Charset.forName("UTF-8").name());
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	this.type = type;
    }
    
    /**
     * Return an InputStream for the data.
     * Note - a new stream must be returned each time.
     */
    public InputStream getInputStream() throws IOException {
	if (data == null)
	    throw new IOException("no data");
	return new ByteArrayInputStream(data);
    }

    public OutputStream getOutputStream() throws IOException {
	throw new IOException("cannot do this");
    }

    public String getContentType() {
        return type;
    }

    public String getName() {
        return "dummy";
    }
}
