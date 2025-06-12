package com.mycompany.myapp.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A StudentProfile.
 */
@Table("student_profile")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("bio")
    private String bio;

    @Column("linkedin_url")
    private String linkedinUrl;

    @Column("github_url")
    private String githubUrl;

    @Column("profile_picture_url")
    private String profilePictureUrl;

    @org.springframework.data.annotation.Transient
    private Student student;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StudentProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return this.bio;
    }

    public StudentProfile bio(String bio) {
        this.setBio(bio);
        return this;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLinkedinUrl() {
        return this.linkedinUrl;
    }

    public StudentProfile linkedinUrl(String linkedinUrl) {
        this.setLinkedinUrl(linkedinUrl);
        return this;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return this.githubUrl;
    }

    public StudentProfile githubUrl(String githubUrl) {
        this.setGithubUrl(githubUrl);
        return this;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    public StudentProfile profilePictureUrl(String profilePictureUrl) {
        this.setProfilePictureUrl(profilePictureUrl);
        return this;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Student getStudent() {
        return this.student;
    }

    public void setStudent(Student student) {
        if (this.student != null) {
            this.student.setProfile(null);
        }
        if (student != null) {
            student.setProfile(this);
        }
        this.student = student;
    }

    public StudentProfile student(Student student) {
        this.setStudent(student);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((StudentProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentProfile{" +
            "id=" + getId() +
            ", bio='" + getBio() + "'" +
            ", linkedinUrl='" + getLinkedinUrl() + "'" +
            ", githubUrl='" + getGithubUrl() + "'" +
            ", profilePictureUrl='" + getProfilePictureUrl() + "'" +
            "}";
    }
}
