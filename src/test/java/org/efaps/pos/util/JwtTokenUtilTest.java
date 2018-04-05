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
        final String token = this.jwtTokenUtil.generateToken(new User("testUser", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin"))));
        final Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
        assertEquals("testUser", claims.getSubject());
    }

    @Test
    public void testValidateToken()
    {
        final User userDetails = new User("testUser", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin")));
        final String token = this.jwtTokenUtil.generateToken(userDetails);
        assertTrue(this.jwtTokenUtil.validateToken(token, userDetails));
    }

    @Test
    public void testValidateTokenWrongUser()
    {
        final User userDetails = new User("testUser", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin")));
        final User userDetails2 = new User("testUser2", "superSecretPassword",
                        Collections.singletonList(new SimpleGrantedAuthority("admin")));
        final String token = this.jwtTokenUtil.generateToken(userDetails);
        assertFalse(this.jwtTokenUtil.validateToken(token, userDetails2));
    }

    @Test
    public void testGetUsernameFromToken()
    {
        final Map<String, Object> claims = new HashMap<>();
        final String token = this.jwtTokenUtil.generateToken(claims, "testUser");
        assertEquals("testUser", this.jwtTokenUtil.getUsernameFromToken(token));
    }
}
