package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CourseTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Course getCourseSample1() {
        return new Course().id(1L).title("title1").description("description1");
    }

    public static Course getCourseSample2() {
        return new Course().id(2L).title("title2").description("description2");
    }

    public static Course getCourseRandomSampleGenerator() {
        return new Course().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).description(UUID.randomUUID().toString());
    }
}
