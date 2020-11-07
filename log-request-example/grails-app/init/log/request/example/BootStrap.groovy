package log.request.example

import com.telecwin.grails.tutorials.Role
import com.telecwin.grails.tutorials.User
import com.telecwin.grails.tutorials.UserRole

class BootStrap {

    def init = { servletContext ->
        UserRole.withTransaction {
            def role = new Role(authority: 'admin').save()
            def user = new User(username: 'yangbo', password: 'passwd').save()
            UserRole.create(user, role, true)
        }
    }

    def destroy = {
    }
}
