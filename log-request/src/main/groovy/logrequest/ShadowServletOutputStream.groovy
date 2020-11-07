package logrequest

import groovy.transform.CompileStatic

import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

/**
 * ServletOutputStream that can be read again.
 */
@CompileStatic
class ShadowServletOutputStream extends ServletOutputStream {

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()

    ServletOutputStream delegateOutputStream

    ShadowServletOutputStream(ServletOutputStream servletOutputStream){
        this.delegateOutputStream = servletOutputStream
    }

    @Override
    boolean isReady() {
        return this.delegateOutputStream.isReady()
    }

    @Override
    void setWriteListener(WriteListener listener) {
        this.delegateOutputStream.setWriteListener(listener)
    }

    @Override
    void write(int b) throws IOException {
        this.delegateOutputStream.write(b)
        this.byteArrayOutputStream.write(b)
    }

    /**
     * Need override to call delegate output stream object to flush to network.
     * @throws IOException
     */
    @Override
    void flush() throws IOException {
        this.delegateOutputStream.flush()
        this.byteArrayOutputStream.flush()
    }

    @Override
    void close() throws IOException {
        this.delegateOutputStream.close()
        this.byteArrayOutputStream.close()
        super.close()
    }

    /**
     * Get anything that has been written to the output stream.
     * @return
     */
    byte[] getBytes() {
        this.byteArrayOutputStream.toByteArray()
    }
}
