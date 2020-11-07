package logrequest

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

/**
 * Re-readable response
 */
@Slf4j
@CompileStatic
class BufferedHttpServletResponseWrapper extends HttpServletResponseWrapper{

    /**
     * This output stream will copy anything write to ServletOutputStream
     */
    ShadowServletOutputStream shadowOutputStream
    HttpServletResponse wrappedResponse
    PrintWriter writer

    BufferedHttpServletResponseWrapper(HttpServletResponse response) {
        super(response)
        this.wrappedResponse = response
        shadowOutputStream = new ShadowServletOutputStream(response.getOutputStream())
    }

    @Override
    void flushBuffer() throws IOException {
        super.flushBuffer()
        // Solve OutputStreamWriter do not call flush() to dump StreamEncoder data to out-stream issue.
        if (writer) {
            writer.flush()
        }
    }

    @Override
    ServletOutputStream getOutputStream() throws IOException {
        return shadowOutputStream
    }

    @Override
    PrintWriter getWriter() throws IOException {
        // Either this method or getOutputStream may be called to write the body, not both.
        // PrintWriter use getCharacterEncoding() charset or use 'ISO8859-1'. See: javax.servlet.ServletResponse.getWriter()
        if (writer) {
            return writer
        }
        String writerEncoding = wrappedResponse.getCharacterEncoding() ?: "ISO8859-1"
        log.debug("Writer encoding is {}", writerEncoding)

        def streamWriter = new OutputStreamWriter(shadowOutputStream, writerEncoding)
        writer = new PrintWriter(streamWriter, true)
        return writer
    }
}
