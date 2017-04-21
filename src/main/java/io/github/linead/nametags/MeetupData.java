package io.github.linead.nametags;

import io.github.linead.nametags.domain.Event;
import io.github.linead.nametags.domain.Members;
import io.github.linead.nametags.domain.Members.Member;
import io.github.linead.nametags.domain.Rsvps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Component
public class MeetupData {

    private static final Logger LOG = LoggerFactory.getLogger(MeetupData.class);

    private static final String EVENT_URL = "https://api.meetup.com/{meetup_url_path}/events?scroll=recent_past&photo-host=public&page=20&key={key}";

    private static final String RSVP_URL = "https://api.meetup.com/2/rsvps?&sign=true&key={key}&event_id={event_id}&photo-host=public&page=120&rsvp=yes";

    private static final String MEMBERS_URL = "https://api.meetup.com/2/members?&key={key}&sign=true&photo-host=public&group_id={group_id}&page=100&only=id,joined,photo&offset={page}";

    private static final String HOSTS_URL = "https://api.meetup.com/{meetup_url_path}/events/{event_id}/hosts?key={key}";

    @Value("${meetup-api.key}")
    private String key;

    @Value("${meetup-url-path}")
    private String meetupPath;

    private final RestTemplate restTemplate;

    @Autowired
    public MeetupData(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Event[] getNextMeetups() {
        Map<String, Object> params = new HashMap<>();
        params.put("key", getKey());
        params.put("meetup_url_path", getMeetupPath());

        ResponseEntity<Event[]> responseEntity = restTemplate.getForEntity(EVENT_URL, Event[].class, params);
        return responseEntity.getBody();
    }

    public Rsvps getRsvps(String eventId) {
        Map<String, Object> params = new HashMap<>();
        params.put("event_id", eventId);
        params.put("key", getKey());

        ResponseEntity<Rsvps> responseEntity = restTemplate.getForEntity(RSVP_URL, Rsvps.class, params);
        return responseEntity.getBody();
    }

    @Cacheable(value = "members")
    public Map<String, Member> getMembers(String groupId) {
        Map<String, Object> params = new HashMap<>();
        int page = 0;
        params.put("group_id", groupId);
        params.put("key", getKey());
        params.put("page", page);

        ResponseEntity<Members> responseEntity = restTemplate.getForEntity(MEMBERS_URL, Members.class, params);
        Members members = responseEntity.getBody();

        Map<String, Member> memberMap = nextPageOf(members);

        //read all pages
        while(!StringUtils.isEmpty(members.getMeta().getNext())) {
            LOG.trace("nextUrl = {}", members.getMeta().getNext());
            params.put("page", ++page);
            responseEntity = restTemplate.getForEntity(MEMBERS_URL, Members.class, params);
            members = responseEntity.getBody();

            memberMap.putAll(nextPageOf(members));
        }

        return memberMap;
    }

    private Map<String, Member> nextPageOf(Members members) {
        return Stream.of(members.getResults())
                .filter(m -> m.getId() != null)
                .collect(toMap(Member::getId, Function.identity()));
    }

    public Set<String> getHosts(String eventId) {

        Map<String, Object> params = new HashMap<>();

        params.put("meetup_url_path", getMeetupPath());
        params.put("event_id", eventId);
        params.put("key", getKey());

        ResponseEntity<Member[]> responseEntity = restTemplate.getForEntity(HOSTS_URL, Member[].class, params);

        return Stream.of(responseEntity.getBody())
                .map(Member::getId)
                .collect(toSet());
    }


    public String getKey() { return key; }

    public String getMeetupPath() {
        return meetupPath;
    }
}
