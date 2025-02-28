# SonarQube & Jenkins CI/CD Troubleshooting Report (Using Java 17)

## Overview

Our CI/CD pipeline, which uses SonarQube for static code analysis and Git for updating our deployment file, began failing unexpectedly. The primary issues were:

- **SonarQube/Elasticsearch Issue:**  
  SonarQube’s embedded Elasticsearch was failing to start initially (exit value 1) before eventually recovering. Although the system had ample memory, this was likely due to transient environment or configuration issues.

- **Jenkins Pipeline Issue:**  
  The "Update Deployment File" stage in our Jenkins pipeline was failing silently (no logs produced). Investigation revealed that the repository was not properly checked out (i.e. the absence of a `.git` directory) and that our shell commands were running in separate sessions, causing Git commands to fail.

This document outlines the diagnosis, commands used, and steps taken to resolve these issues using Java 17.

---

## Issues Encountered

### 1. Elasticsearch Startup Failures in SonarQube

**Symptoms:**
```
2025.02.21 16:38:24 WARN  ... Process exited with exit value [ElasticSearch]: 1
2025.02.21 16:38:29 INFO  ... Cluster health status changed from [RED] to [GREEN]
```

**Potential Causes:**
- Changes in the Java environment.
- Transient resource or permission issues.
- Bootstrap checks (e.g., file descriptor limits).

### 2. Silent Failure in "Update Deployment File" Stage

**Symptoms:**
- The stage produced no logs and failed almost immediately.

**Investigation:**
- The repository was not properly checked out; no `.git` folder was present.
- Commands like `git add`, `git commit`, and `sed` were executed in separate shell sessions, which caused the updates and logs to be lost.

---

## Diagnostic and Resolution Steps

### A. Ensuring the Correct Java Environment (Using Java 17)

1. **Verify Installed Java Version:**

   ```bash
   java --version
   # Expected output: OpenJDK 17 (e.g., 17.0.14+7-Ubuntu-122.04.1)
   ```

2. **Configure `update-alternatives` to Use Java 17:**

   ```bash
   sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-17-openjdk-amd64/bin/java 1717
   sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-17-openjdk-amd64/bin/javac 1717
   sudo update-alternatives --config java
   sudo update-alternatives --config javac
   ```

3. **Restart SonarQube:**

   ```bash
   sudo systemctl restart sonarqube
   sudo systemctl status sonarqube
   ```

### B. Checking System Resources and Permissions

- **Memory & Swap:**
  
  ```bash
  free -h
  ```

- **File Descriptor Limits:**

  Ensure the SonarQube user has a high file descriptor limit (e.g., 65536) in `/etc/security/limits.conf`.

- **Directory Permissions:**

  Verify that directories such as `/opt/sonarqube/temp` and `/opt/sonarqube/data` are writable by the SonarQube user.

### C. Verifying Open Ports and SonarQube Service

- **List Open Ports:**

  ```bash
  sudo ss -tulpn
  # or
  sudo netstat -tulpn
  ```

- **Monitor SonarQube Logs:**

  ```bash
  sudo tail -f /opt/sonarqube/logs/sonar.log
  sudo tail -f /opt/sonarqube/logs/es.log
  ```

---

## Conclusion

### Root Issues Identified:
- **SonarQube Service:**  
  Elasticsearch initially failed to start due to transient environment or configuration issues, but eventually recovered.  
- **Jenkins Pipeline:**  
  The "Update Deployment File" stage was failing silently because the repository was not properly checked out and commands were executed in separate shell sessions.

### Resolutions Applied:
- **Java Environment:**  
  We confirmed and set Java 17 as the active JDK using `update-alternatives` and updated the JAVA_HOME in SonarQube’s configuration.
- **System Resources:**  
  Verified memory, swap, file descriptor limits, and directory permissions.
- **Jenkins Pipeline Updates:**  
  Added a proper SCM checkout, diagnostic workspace stages, and combined shell commands in the "Update Deployment File" stage with detailed logging.
- **Diagnostics:**  
  Used commands such as `echo`, `set -x`, `ls -la`, and `cat` to verify workspace structure and file contents.

This documentation serves as a reference for troubleshooting similar issues in the future.

---

*End of Report*
