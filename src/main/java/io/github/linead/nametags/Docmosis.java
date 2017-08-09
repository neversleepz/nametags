package io.github.linead.nametags;

import io.github.linead.nametags.domain.Attendee;
import io.github.linead.nametags.domain.DocmosisRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class Docmosis {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static String HOST = "https://dws2.docmosis.com/services/rs/render";

    @Autowired
    Environment env;

    @Value("${docmosis-api.key}")
    private String key;

    @Value("${docmosis-template}")
    private String template;

    public byte[] render(Map<String, List<Attendee>> attendeeList) {

        log.info("render() has attendees" + attendeeList);

        DocmosisRequest req = new DocmosisRequest();
        req.setAccessKey(getKey());
        req.setTemplateName(getTemplate()); // "template5.docx"
        req.setOutputName("all_labels.pdf");
        req.setData(attendeeList);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());

        log.info("render() querying URL:" + HOST);
        ResponseEntity<byte[]> response = restTemplate.postForEntity(HOST, req, byte[].class);
        return response.getBody();
    }

    public String getTemplate() { return template; }
    public String getKey() { return key; }

}
