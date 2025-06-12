package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstructorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Instructor getInstructorSample1() {
        return new Instructor().id(1L).firstName("firstName1").lastName("lastName1").email("email1").bio("bio1");
    }

    public static Instructor getInstructorSample2() {
        return new Instructor().id(2L).firstName("firstName2").lastName("lastName2").email("email2").bio("bio2");
    }

    public static Instructor getInstructorRandomSampleGenerator() {
        return new Instructor()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .bio(UUID.randomUUID().toString());
    }
}
