package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.StudentProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StudentProfileDTO implements Serializable {

    private Long id;

    private String bio;

    private String linkedinUrl;

    private String githubUrl;

    private String profilePictureUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StudentProfileDTO)) {
            return false;
        }

        StudentProfileDTO studentProfileDTO = (StudentProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, studentProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StudentProfileDTO{" +
            "id=" + getId() +
            ", bio='" + getBio() + "'" +
            ", linkedinUrl='" + getLinkedinUrl() + "'" +
            ", githubUrl='" + getGithubUrl() + "'" +
            ", profilePictureUrl='" + getProfilePictureUrl() + "'" +
            "}";
    }
}
