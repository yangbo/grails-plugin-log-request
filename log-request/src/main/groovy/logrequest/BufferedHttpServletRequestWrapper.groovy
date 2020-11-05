package logrequest

import groovy.transform.CompileStatic

import javax.servlet.ServletInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper

/**
 * Buffer request input stream. can be re-use again.
 */
@CompileStatic
class BufferedHttpServletRequestWrapper extends HttpServletRequestWrapper {

    BufferBasedServletInputStream bufferBasedServletInputStream

    /**
     * Constructs a request object wrapping the given request.
     */
    BufferedHttpServletRequestWrapper(HttpServletRequest request) {
        super(request)
        ByteArrayOutputStream inputBuffer = readInputStream()
        bufferBasedServletInputStream = new BufferBasedServletInputStream(inputBuffer.toByteArray())
    }

    ByteArrayOutputStream readInputStream() {
        def inputBuffer = new ByteArrayOutputStream()
        def input = request.getInputStream()
        int read = -1
        while ((read = input.read()) != -1) {
            inputBuffer.write(read)
        }
        return inputBuffer
    }

    @Override
    ServletInputStream getInputStream() throws IOException {
        return bufferBasedServletInputStream
    }
}
