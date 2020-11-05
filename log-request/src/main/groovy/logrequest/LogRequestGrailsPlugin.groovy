package logrequest


import grails.plugins.Plugin
import groovy.util.logging.Slf4j
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.core.Ordered

@Slf4j
class LogRequestGrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "4.0.2 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def title = "Log Request" // Headline display name of the plugin
    def author = "Bob Yang"
    def authorEmail = "bob.yang.dev@gmail.com"
    def description = '''\
Log request and response data for debug, including header and body.
'''
    def profiles = ['web']

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/log-request"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() {
        { ->
            // Implement runtime spring config (optional)
            def logRequestConfig = config.grails.plugin.logrequest
            boolean enable = logRequestConfig.enable
            if (!enable) {
                log.debug("Do not register LogRequestFilter.")
                return
            }
            log.debug("Register LogRequestFilter.")
            logRequestFilter(FilterRegistrationBean) {
                // this filterBean's type is GenericBeanDefinition
                filter = bean(LogRequestFilter)
                urlPatterns = ['/*']
                // must be highest or-else the controller's request attribute will
                // use web-container's HttpServletRequest, not our wrapped request.
                order = Ordered.HIGHEST_PRECEDENCE
            }
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        // TODO Implement post initialization spring config (optional)
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
