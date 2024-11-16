package de.teamnoco.prodeli.web.security

import de.teamnoco.prodeli.service.UserService
import de.teamnoco.prodeli.web.response.Errors
import de.teamnoco.prodeli.web.response.sendResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class SecurityHandler(
    @Qualifier("handlerExceptionResolver") private val resolver: HandlerExceptionResolver,
    private val userService: UserService,
) : AuthenticationEntryPoint, OncePerRequestFilter() {
    @Bean
    fun securityFilterChain(
        httpSecurity: HttpSecurity,
        authManager: AuthenticationManager
    ): SecurityFilterChain {
        httpSecurity {
            csrf {
                disable()
            }

            cors {
                configurationSource = CorsConfigurationSource {
                    CorsConfiguration().applyPermitDefaultValues().apply {
                        allowedMethods = listOf("*")
                    }
                }
            }

            authorizeHttpRequests {
                WHITELISTED_URLS.forEach { url -> authorize(url, permitAll) }
                authorize(anyRequest, authenticated)
            }

            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }

            exceptionHandling {
                accessDeniedHandler = AccessDeniedHandler { _, response, _ ->
                    response.sendResponse(Errors.AuthRequired)
                }

                authenticationEntryPoint = this@SecurityHandler
            }

            authenticationManager = authManager
            addFilterBefore<UsernamePasswordAuthenticationFilter>(this@SecurityHandler)
        }

        return httpSecurity.build()
    }

    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException
    ) {
        resolver.resolveException(request, response, null, authException)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        runCatching {
            if (WHITELISTED_URLS.any { request.requestURI.startsWith(it.substringBefore("**")) }) {
                return@runCatching
            }

            val authHeader = request.getHeader("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                credentialsExpired("Authorization header is missing")
            }

            val token = authHeader.substringAfter("Bearer ")
            if (SecurityContextHolder.getContext().authentication == null) {
                val user = userService.getUserByToken(token)
                    ?: credentialsExpired("User with token $token does not exist")

                val authToken = UsernamePasswordAuthenticationToken(user, null, user.authorities)
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }.onFailure {
            if (it !is CredentialsExpiredException) {
                throw CredentialsExpiredException("Unhandled exception has been thrown!", it)
            } else {
                response.sendResponse(Errors.CredentialsExpired)
            }
        }.onSuccess {
            filterChain.doFilter(request, response)
        }
    }

    private fun credentialsExpired(reason: String, cause: Throwable? = null): Nothing {
        throw CredentialsExpiredException(reason, cause)
    }

    companion object {
        val WHITELISTED_URLS =
            arrayOf(
                "/api/auth/sign-in",
                "/api/ping",
                "/swagger-ui**",
                "/swagger-ui/**",
                "/v3/api-docs**",
                "/v3/api-docs/**",
                "/api/check"
            )
    }
}