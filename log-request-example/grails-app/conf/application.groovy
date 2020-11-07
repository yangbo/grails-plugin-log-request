

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.telecwin.grails.tutorials.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.telecwin.grails.tutorials.UserRole'
grails.plugin.springsecurity.authority.className = 'com.telecwin.grails.tutorials.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS'],
	// Stateless chain for API, 注意顺序，这个必须放在 /** 的前面，否则不起作用
	[
			pattern: '/api/**',
			filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
	],
	// Traditional, stateful chain
	[
			pattern: '/**',
			filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
	]
]

grails.plugin.springsecurity.rest.login.active = true
// 这个是登录端点，也就是拦截器识别的登录URL，看到这个地址，拦截器就会从请求中抽取用户名、密码进行登录验证
grails.plugin.springsecurity.rest.login.endpointUrl = "/api/user/login"
grails.plugin.springsecurity.rest.login.failureStatusCode = 401
