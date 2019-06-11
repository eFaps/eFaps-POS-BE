/*
 * Copyright 2003 - 2019 The eFaps Team
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
 *
 */
package org.efaps.pos.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;

@Component
public class JwtTokenUtil
{

    private final Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public Boolean validateToken(final String _token, final UserDetails _userDetails)
    {
        final String username = getUsernameFromToken(_token);
        return username.equals(_userDetails.getUsername()) && !isTokenExpired(_token);
    }

    private Boolean isTokenExpired(final String token)
    {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(this.clock.now());
    }

    public Date getExpirationDateFromToken(final String token)
    {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public String getUsernameFromToken(final String _authToken)
    {
        return getClaimFromToken(_authToken, Claims::getSubject);
    }

    public <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver)
    {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(final String token)
    {
        return Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
    }

    public String generateToken(final UserDetails _userDetails)
    {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("roles", _userDetails.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .collect(Collectors.toSet()));
        return generateToken(claims, _userDetails.getUsername());
    }

    protected String generateToken(final Map<String, Object> claims, final String subject)
    {
        final Date createdDate = this.clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        return Jwts.builder()
                        .setClaims(claims)
                        .setSubject(subject)
                        .setIssuedAt(createdDate)
                        .setExpiration(expirationDate)
                        .signWith(SignatureAlgorithm.HS512, this.secret)
                        .compact();
    }

    private Date calculateExpirationDate(final Date createdDate)
    {
        return new Date(createdDate.getTime() + this.expiration * 1000);
    }
}
