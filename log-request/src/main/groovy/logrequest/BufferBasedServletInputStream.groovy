package logrequest

import groovy.transform.CompileStatic

import javax.servlet.ReadListener
import javax.servlet.ServletInputStream
import java.nio.ByteBuffer


/**
 * ByteBuffer based ServletInputStream.
 * Can be used by calling reset() method.
 */
@CompileStatic
class BufferBasedServletInputStream extends ServletInputStream {

    ByteBuffer buffer

    BufferBasedServletInputStream(byte[] buffer) {
        this.buffer = ByteBuffer.wrap(buffer)
    }

    @Override
    boolean isFinished() {
        return buffer.hasRemaining()
    }

    @Override
    boolean isReady() {
        return true
    }

    @Override
    void setReadListener(ReadListener listener) {
        throw new UnsupportedOperationException()
    }

    @Override
    int read() throws IOException {
        return buffer.hasRemaining() ? buffer.get() : -1
    }

    /**
     * Make this input stream usable again.
     */
    synchronized void reset() {
        buffer.rewind()
    }
}
