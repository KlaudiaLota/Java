package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StudentProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StudentProfile getStudentProfileSample1() {
        return new StudentProfile()
            .id(1L)
            .bio("bio1")
            .linkedinUrl("linkedinUrl1")
            .githubUrl("githubUrl1")
            .profilePictureUrl("profilePictureUrl1");
    }

    public static StudentProfile getStudentProfileSample2() {
        return new StudentProfile()
            .id(2L)
            .bio("bio2")
            .linkedinUrl("linkedinUrl2")
            .githubUrl("githubUrl2")
            .profilePictureUrl("profilePictureUrl2");
    }

    public static StudentProfile getStudentProfileRandomSampleGenerator() {
        return new StudentProfile()
            .id(longCount.incrementAndGet())
            .bio(UUID.randomUUID().toString())
            .linkedinUrl(UUID.randomUUID().toString())
            .githubUrl(UUID.randomUUID().toString())
            .profilePictureUrl(UUID.randomUUID().toString());
    }
}
