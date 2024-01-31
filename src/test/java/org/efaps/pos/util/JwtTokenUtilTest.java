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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class JwtTokenUtilTest
{

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    public void testGenerateToken()
    {
        final String token = jwtTokenUtil.generateAccessToken(new User("testUser", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin"))));
        final var key = Keys.hmacShaKeyFor(secret.getBytes());
        final Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        assertEquals("testUser", claims.getSubject());
    }

    @Test
    public void testValidateToken()
    {
        final User userDetails = new User("testUser", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin")));
        final String token = jwtTokenUtil.generateAccessToken(userDetails);
        assertTrue(jwtTokenUtil.validateToken(token, userDetails));
    }

    @Test
    public void testValidateTokenWrongUser()
    {
        final User userDetails = new User("testUser", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin")));
        final User userDetails2 = new User("testUser2", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin")));
        final String token = jwtTokenUtil.generateAccessToken(userDetails);
        assertFalse(jwtTokenUtil.validateToken(token, userDetails2));
    }

    @Test
    public void testGetUsernameFromToken()
    {
        final Map<String, Object> claims = new HashMap<>();
        final String token = jwtTokenUtil.generateToken(claims, "testUser");
        assertEquals("testUser", jwtTokenUtil.getUsernameFromAccessToken(token));
    }
}
