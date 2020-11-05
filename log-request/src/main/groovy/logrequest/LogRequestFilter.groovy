package logrequest

import grails.util.Holders
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@CompileStatic
class LogRequestFilter extends GenericFilterBean {
    boolean logRequestBody
    boolean logResponseBody

    LogRequestFilter() {
        def theConfig = Holders.config
        logRequestBody = theConfig.get('grails.plugin.logrequest.body.request')
        logResponseBody = theConfig.get('grails.plugin.logrequest.body.response')
    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = request as HttpServletRequest
        HttpServletResponse httpServletResponse = response as HttpServletResponse
        def url = httpServletRequest.requestURL
        def filterRequest = request
        def bodyLog = ""
        def method = httpServletRequest.method
        if (logRequestBody) {
            BufferedHttpServletRequestWrapper wrappedRequest = new BufferedHttpServletRequestWrapper(httpServletRequest)
            bodyLog = bodyOfRequest(wrappedRequest)
            bodyLog = "Body[$bodyLog]\n"
            filterRequest = wrappedRequest
        }
        def paramsLog = printMap(httpServletRequest.parameterMap)
        def headersLog = headers(httpServletRequest)
        log.debug("==========\nReQuest: {} {}\nParameters[{}]\nHeaders[{}]\n{}",
                method, url, paramsLog, headersLog, bodyLog)

        def filterResponse = response

        if (logResponseBody) {
            filterResponse = new BufferedHttpServletResponseWrapper(httpServletResponse)
        }
        // call next filters
        chain.doFilter(filterRequest, filterResponse)
        bodyLog = ""
        // only log non-binary response
        if (logResponseBody && logByContentType(httpServletResponse)) {
            bodyLog = bodyOfResponse((BufferedHttpServletResponseWrapper) filterResponse)
            bodyLog = "Body[$bodyLog]\n"
        }
        headersLog = headers(httpServletResponse)
        def code = httpServletResponse.status
        log.debug("==========\nReSponse: {} {} {}\nHeaders[{}]\n{}", code, method, url, headersLog, bodyLog)
    }

    static String printMap(Map map) {
        StringBuilder builder = new StringBuilder()
        map.each { entry ->
            def values = entry.value
            if (entry.value instanceof String[]) {
                Collection valueCollection = entry.value as Collection
                values = valueCollection.join(",")
            }
            builder.append("\n\t").append(entry.key).append(" = ").append("${values}")
        }
        if (builder.size() > 0) {
            builder.append("\n")
        }
        builder
    }

    static String headers(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder()
        request.getHeaderNames().each { String key ->
            List values = enumToList(request.getHeaders(key))
            builder.append("\n\t").append(key).append(" = ").append(values.join("|"))
        }
        if (builder.size() > 0) {
            builder.append("\n")
        }
        return builder
    }

    static List enumToList(Enumeration enumeration) {
        List list = new ArrayList()
        while (enumeration.hasMoreElements()) {
            list.add(enumeration.nextElement())
        }
        return list
    }

    static String headers(HttpServletResponse response) {
        StringBuilder builder = new StringBuilder()
        def names = response.getHeaderNames()
        names.each { String key ->
            List values = new ArrayList()
            values.addAll(response.getHeaders(key))
            builder.append("\n\t").append(key).append(" = ").append(values.join("|"))
        }
        if (builder.size() > 0) {
            builder.append("\n")
        }
        return builder
    }

    static String bodyOfRequest(BufferedHttpServletRequestWrapper httpServletRequest) {
        StringBuilder builder = new StringBuilder()
        String body = httpServletRequest.getInputStream().getText(httpServletRequest.characterEncoding ?: "UTF-8")
        if (body) {
            builder.append("\n").append(body).append("\n")
        }
        // make stream re-readable
        httpServletRequest.getInputStream().reset()
        return builder
    }

    static String bodyOfResponse(BufferedHttpServletResponseWrapper httpServletResponse) {
        StringBuilder builder = new StringBuilder()
        ShadowServletOutputStream shadowServletOutputStream = httpServletResponse.getOutputStream() as ShadowServletOutputStream
        String body = new String(shadowServletOutputStream.bytes, httpServletResponse.getCharacterEncoding() ?: "UTF-8")
        if (body) {
            // format json
            def contentType = httpServletResponse.getHeader("Content-Type")
            if (contentType.contains("json")){
                body = JsonUtils.prettyPrintJson(body)
            }
            builder.append("\n").append(body).append("\n")
        }
        return builder
    }

    /**
     * Log if content type is permitted.
     */
    static boolean logByContentType(HttpServletResponse httpServletResponse) {
        def contentType = httpServletResponse.getHeader("Content-Type")
        if (contentType && !contentType.contains("text/html") && !contentType.contains("text/xhtml")
                && (contentType.contains("text") || contentType.contains("json"))) {
            return true
        }
        return false
    }
}
