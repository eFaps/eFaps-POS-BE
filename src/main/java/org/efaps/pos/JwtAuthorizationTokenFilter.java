package org.efaps.pos;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.efaps.pos.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthorizationTokenFilter
    extends OncePerRequestFilter
{

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthorizationTokenFilter.class);

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthorizationTokenFilter(final UserDetailsService userDetailsService,
                                       final JwtTokenUtil jwtTokenUtil)
    {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain chain)
        throws ServletException, IOException
    {
        LOG.debug("processing authentication for '{}'", request.getRequestURL());

        final String requestHeader = request.getHeader("Authorization");

        String username = null;
        String authToken = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7);
            try {
                username = this.jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (final IllegalArgumentException e) {
                LOG.error("an error occured during getting username from token", e);
            }
        } else {
            LOG.warn("couldn't find bearer string, will ignore the header");
        }

        LOG.debug("checking authentication for user '{}'", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            LOG.debug("security context was null, so authorizating user");

            // It is not compelling necessary to load the use details from the
            // database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // For simple validation it is completely sufficient to just check
            // the token integrity. You don't have to call
            // the database compellingly. Again it's up to you ;)
            if (this.jwtTokenUtil.validateToken(authToken, userDetails)) {
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                LOG.info("authorizated user '{}', setting security context", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
