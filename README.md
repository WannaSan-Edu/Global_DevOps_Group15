# Global_DevOps_Group15

## Build Status

Master Build Status

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/WannaSan-Edu/Global_DeveOps_Group15/main.yml?branch=master&style=flat-square)

Develop Build Status

![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/WannaSan-Edu/Global_DeveOps_Group15/main.yml?branch=develop&style=flat-square)

![Workflow](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/actions/workflows/main.yml/badge.svg)

[![License](https://img.shields.io/github/license/WannaSan-Edu/Global_DeveOps_Group15.svg?style=flat-square)](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/blob/main/LICENSE)

[![Releases](https://img.shields.io/github/release/WannaSan-Edu/Global_DeveOps_Group15/all.svg?style=flat-square)](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/releases)

---

## Group Contribution

Group Name: Group 15

| Matric | Code Review 1 | Code Review 2 |
|:---:|---:|---:|
| 40870398 | 16.65 | |
| 40870396 | 16.65 | |
| 40870392 | 16.65 | |
| 40870394 | 16.65 | |
| 40870384 | 16.65 | |
| 40870393 | 16.65 | |
| Total | 100 | |

> Note: The current contribution scores total 99.90. Update the scores according to the team's agreed allocation if the total must equal exactly 100.

---

## 1. System Introduction

Global_DeveOps_Group15 is a Java-based application designed to retrieve and display geographical and population information using a MySQL database. The application supports country and city information retrieval and population reporting for geographical areas covered by its implemented features.

The system uses Java for its core functionality and MySQL for data storage. Apache Maven manages project dependencies and supports the build process. Docker and Docker Compose provide a consistent environment for running the application and database services.

The project applies DevOps principles to improve collaboration, consistency, maintainability, and software quality throughout the software development lifecycle. Git and GitHub support version control and team collaboration, while GitHub Actions automates the build process and other checks configured in the repository.

The team uses an organized development workflow involving feature branches, code integration, pull requests, and project documentation. These practices help team members manage changes, identify potential problems, and maintain a shared codebase.

The overall objective is to develop a reliable, maintainable, and well-documented application while demonstrating the practical use of software development tools and DevOps practices in a collaborative team environment.

---

## 2. System Requirements

System requirements specify the hardware, software, and development tools needed to develop, run, test, and maintain the application.

### 2.1 Hardware Requirements

| Component | Requirement |
|---|---|
| Processor | A modern multi-core processor capable of running the development tools and Docker. |
| Memory (RAM) | 8 GB recommended; additional memory may be beneficial when running multiple containers. |
| Storage | Sufficient free disk space for source code, dependencies, database files, and Docker images. |
| Network | Internet access for repository operations, dependency downloads, and updates. |

*Note: These are recommended development specifications, not verified minimum requirements.*

### 2.2 Software Requirements

| Software / Tool | Purpose |
|---|---|
| Operating System | Windows, macOS, or Linux with compatible development tools. |
| Java Development Kit (JDK) | Provides the Java compiler and runtime required by the application. |
| Apache Maven | Manages dependencies and builds the Java project. |
| IntelliJ IDEA | Provides an environment for editing, running, and debugging Java code. |
| Git | Tracks source-code changes and manages branches. |
| GitHub | Hosts the repository and supports collaboration and pull requests. |
| Docker | Builds and runs containerised application services. |
| Docker Compose | Configures and runs the application and database services together. |
| MySQL | Stores geographical and population data. |
| GitHub Actions | Automates the build and other checks configured in the repository. |

**Version compatibility:** Use the JDK version specified in the Maven configuration and Dockerfile. Ensure that the local development environment and container build use compatible versions.

### 2.3 Project Configuration

| File / Component | Purpose |
|---|---|
| `pom.xml` | Defines Maven dependencies and build configuration. |
| `Dockerfile` | Defines how the application container image is built. |
| `compose.yaml` or `docker-compose.yml` | Configures application and database services, environment variables, networking, and service dependencies. |
| `.github/workflows/main.yml` | Defines GitHub Actions triggers and configured build or testing steps. |
| Java source files | Implement application features and database operations. |
| MySQL database | Stores the geographical data required by the application. |

Database credentials and other environment-specific settings should be managed securely. Real passwords and sensitive credentials must not be committed to the repository.

---

## 3. User Requirements

User requirements describe the functions expected from the application and the quality attributes needed to make the system useful, reliable, and maintainable.

### 3.1 Functional Requirements

| ID | Requirement | Expected Outcome |
|---|---|---|
| FR01 | View country information | Users can retrieve information about a selected country. |
| FR02 | View country population | Users can view the selected country's name and total population when the required data is available. |
| FR03 | View city information | Users can retrieve city information supported by the implemented features. |
| FR04 | View district population reports | Users can retrieve district population information through the implemented report feature. |
| FR05 | Retrieve database information | The application can query MySQL to obtain the required records. |
| FR06 | Handle missing data | The application displays a clear message when requested information is unavailable. |
| FR07 | Handle errors | The application handles expected database connection failures and other errors appropriately. |

*Note: Ensure that every requirement matches the team's actual implementation and agreed coursework scope.*

### 3.2 Non-Functional Requirements

| ID | Requirement | Expected Outcome |
|---|---|---|
| NFR01 | Reliability | The application handles expected failures without terminating unexpectedly. |
| NFR02 | Maintainability | Source code is readable, organised, and structured to support future changes. |
| NFR03 | Usability | Results and error messages are clear and understandable. |
| NFR04 | Compatibility | The application runs with the Java version and supporting tools specified by the project configuration. |
| NFR05 | Security | Database credentials are managed securely, with appropriate database permissions. |
| NFR06 | Reproducibility | Docker Compose provides a consistent method for configuring and starting the application and database services. |
| NFR07 | Testability | Application functionality can be verified through appropriate tests. |

---

## 4. DevOps and Team Collaboration Requirements

The project uses DevOps practices to support collaborative development, controlled code integration, build automation, and consistent application execution.

### 4.1 Version Control and Collaboration

- Team members use Git to track changes to source code and project documentation.
- Feature branches support individual feature development without directly modifying the shared development branch.
- Pull requests support code review and controlled integration of changes.
- Merge conflicts are resolved before the affected changes are integrated.
- Commit messages and pull request descriptions clearly explain the changes made.

### 4.2 Build Automation and Continuous Integration

- Maven manages project dependencies and builds the Java application.
- GitHub Actions executes the steps defined in the repository workflow when its configured triggers occur.
- Automated tests should be included in the workflow where test cases are available and configured.
- Build or test failures should be investigated and resolved before changes are considered ready for integration.

### 4.3 Containerisation and Database Management

- Docker provides a containerized environment for the application.
- Docker Compose coordinates the application and MySQL database services.
- Service configuration uses the correct internal network addresses and ports.
- The application should wait for the database to become ready before attempting database operations.
- Database configuration should be documented so that team members can reproduce the development environment.

### 4.4 Documentation and Project Management

- The README documents the project purpose, requirements, configuration, and setup process.
- GitHub Projects tracks tasks, responsibilities, and progress.
- Team members maintain clear records of their contributions and completed work.
- Team members follow the agreed coding standards and Code of Conduct.
- Documentation is updated when significant implementation or configuration changes occur.

---

## 5. Expected Outcomes

The expected outcomes of the project are:

1. A Java application that retrieves and displays geographical and population information supported by its implemented features.
2. MySQL database integration that enables the application to access the required data.
3. A repeatable application environment supported by Docker and Docker Compose.
4. A collaborative development workflow supported by Git and GitHub.
5. An automated build process using GitHub Actions, together with testing and validation steps where configured.
6. Clear project documentation that supports setup, maintenance, collaboration, and future development.

The project demonstrates the practical application of software development tools and DevOps practices within a collaborative coursework environment.

---

## 6. Project Setup and Execution

### Prerequisites

Before running the application, ensure that the required Java development tools, Maven, Docker, and Docker Compose are installed and available.

### Run the Application with Docker Compose

1. Clone the repository:

   ```bash
   git clone https://github.com/WannaSan-Edu/Global_DeveOps_Group15.git
   ```

2. Navigate to the project directory:

   ```bash
   cd Global_DeveOps_Group15
   ```

3. Build and start the application and database services:

   ```bash
   docker compose up --build
   ```

4. Review the application logs to check whether the database connection is successful and the application starts correctly.

5. To stop the services, run:

   ```bash
   docker compose down
   ```

**Important:** These commands assume that the repository contains a valid Docker Compose configuration and the required database initialization and environment settings. Follow any additional database setup instructions provided by the project.

---

## 7. Licence

Refer to the repository's [LICENSE](https://github.com/WannaSan-Edu/Global_DeveOps_Group15/blob/main/LICENSE) file for the applicable license terms.
