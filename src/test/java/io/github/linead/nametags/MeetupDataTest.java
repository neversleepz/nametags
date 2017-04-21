package io.github.linead.nametags;

import io.github.linead.nametags.domain.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeetupDataTest {

    @Autowired
    MeetupData meetupData;

    @Test
    public void getNextMeetupsWillReturnAFutureMeetup() throws Exception {
        Event[] events = meetupData.getNextMeetups();

        assertThat(events, hasItemInArray(hasProperty("time", greaterThanOrEqualTo(currentTimeMillis()))));
    }

    @Test
    public void retrievedEventHaveValidName() {
        Event[] events = meetupData.getNextMeetups();

        assertThat(asList(events), everyItem(hasProperty("name", not(isEmptyString()))));
    }

    @Test
    public void retrievedEventHaveValidDuration() {
        Event[] events = meetupData.getNextMeetups();

        assertThat(asList(events), everyItem(hasProperty("duration")));
    }
}