package com.d2d.dto;

public class SpringBootBasicDTO {

    private String type; /* maven-project */

    private String language; /* java */

    private String bootVersion; /* 3.0.6 */

    private String baseDir; /* Artifact */

    private String groupId; /* com.example */

    private String artifactId; /* Artifact */

    private String name; /* Name */

    private String description; /* Demo project for Spring Boot */

    private String packageName; /* com.example.Artifact */

    private String packaging; /* jar */

    private String javaVersion; /* 11 */

    private String dependencies;  /* web,mysql,security,lombok */

    // Setters and Getters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBootVersion() {
        return bootVersion;
    }

    public void setBootVersion(String bootVersion) {
        this.bootVersion = bootVersion;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }
}
