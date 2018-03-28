package org.efaps.pos.sso;

import org.efaps.pos.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SSOClient
{
    private static final Logger LOG = LoggerFactory.getLogger(SSOClient.class);

    private final ConfigProperties config;
    private final RestTemplate restTemplate;

    @Autowired
    public SSOClient(final ConfigProperties _config, final RestTemplate _restTemplate) {
        this.config = _config;
        this.restTemplate = _restTemplate;
    }

    public void login()
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        this.config.getSso().getPostValues().forEach((key, value) -> {
            map.add(key, value);
        });

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        final ResponseEntity<String> response = this.restTemplate.postForEntity(this.config.getSso().getUrl(), request, String.class);
        LOG.info(response.getBody());
    }
}
