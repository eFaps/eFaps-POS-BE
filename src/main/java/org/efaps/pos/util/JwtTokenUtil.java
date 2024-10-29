/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.efaps.pos.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClock;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil
{

    private static Map<String, String> REFRESH_TOCKEN_STORE;

    private final Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.deviation}")
    private Long deviation;

    public Boolean validateToken(final String _token, final UserDetails _userDetails)
    {
        final String username = getUsernameFromAccessToken(_token);
        return username.equals(_userDetails.getUsername()) && !isTokenExpired(_token);
    }

    private Boolean isTokenExpired(final String token)
    {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    public Date getExpirationDateFromToken(final String token)
    {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getUsernameFromAccessToken(final String _authToken)
    {
        return getClaimFromToken(_authToken, Claims::getSubject);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver)
    {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public <T> T getClaimFromToken(final String _token, final String _claimName, final Class<T> _requiredType)
    {
        final Claims claims = getAllClaimsFromToken(_token);
        return claims.get(_claimName, _requiredType);
    }

    private Claims getAllClaimsFromToken(final String token)
    {
        final var key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String generateAccessToken(final UserDetails _userDetails)
    {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("permissions", _userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()));
        return generateToken(claims, _userDetails.getUsername());
    }

    public String generateToken(final Map<String, Object> claims, final String subject)
    {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        final var key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                        .claims(claims)
                        .subject(subject)
                        .issuedAt(createdDate)
                        .expiration(expirationDate)
                        .signWith(key)
                        .compact();
    }

    private Date calculateExpirationDate(final Date createdDate)
    {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

    private Map<String, String> getRefreshTokenStore()
    {
        if (REFRESH_TOCKEN_STORE == null) {
            REFRESH_TOCKEN_STORE = Collections.synchronizedMap(
                            new PassiveExpiringMap<>((expiration + deviation) * 1000));
        }
        return REFRESH_TOCKEN_STORE;
    }

    public String generateRefreshToken(final UserDetails _userDetails)
    {
        final String ret = RandomStringUtils.secure().nextAlphanumeric(128);
        getRefreshTokenStore().put(ret, _userDetails.getUsername());
        return ret;
    }

    public String getUsernameFromRefreshToken(final String _refreshToken)
        throws AuthenticationException
    {
        if (!getRefreshTokenStore().containsKey(_refreshToken)) {
            throw new NonceExpiredException("refresh token expired");
        }
        return getRefreshTokenStore().get(_refreshToken);
    }
}
