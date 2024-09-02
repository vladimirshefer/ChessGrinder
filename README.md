# Chess Grinder
App for chess club.

## Backend

### Tech stack
- Java 17
- Spring 6
- Spring Boot 3

## Frontend

### Tech stack
- React


## Project tasks
- EPIC: User profiles
  - DESCRIPTION: As a user, I would like to be able to login using username and password, then access and edit my user profile (username, name, password, email)
  - TECH: Reactive login (rerender components on login/logout)
- EPIC: Tournament participant registration
  - DESCRIPTION: As a user, I would like to register on tournament by my own on the tournament page.
  - UI: Button "Participate in tournament"
- EPIC: Tournament management
  - DESCRIPTION: As a user with admin role, I would like to create and manage tournaments.
    - Create tournament, specify the date and location (address) of the tournament according to the [Swiss System](https://de.wikipedia.org/wiki/Schweizer_System)
    - See the tournament overall results as well as intermediate round results.
      - The results are the table with the participant data such as:
        - Official name and last name
        - Unofficial temporary nickname (for fun)
        - Points (1 point for win, 0 points for loose and 0.5 points for a draw)
        - Buchholz coefficient (see [Wiki](https://ru.wikipedia.org/wiki/%D0%9A%D0%BE%D1%8D%D1%84%D1%84%D0%B8%D1%86%D0%B8%D0%B5%D0%BD%D1%82_%D0%91%D1%83%D1%85%D0%B3%D0%BE%D0%BB%D1%8C%D1%86%D0%B0))


## Project setup for developers

### General setup

#### Docker
Install [Docker](https://docs.docker.com/get-started/get-docker/).

Docker enables setup of other parts with ease. It is required for these instructions. 

#### Postgres

Database for backend.

Run the following command to quickly setup the database with predefined settings.

```sh
docker compose -f ./deployment/local/docker-compose.yml up -d postgres
```

#### Nginx
Nginx is the simplest way to setup reverse proxy (virtual server, unifying frontend and backend).
The application will be available on `http://localhost:12345/`

```sh
docker compose -f ./deployment/local/docker-compose.yml up -d nginx
```

### Frontend

#### Run frontend app in development mode
You can run the application in development mode to be able
to rapidly change the behavior. 
Required for those who want to develop the frontend application.

```sh
# Install Node JS 16
# ...
# Goto frontend directory
cd frontend
# Install dependencies
npm install
# Run app in development mode
npm run start
```

#### Run frontend app in docker
If you are a backend developer and want
to just use the frontend as is, then you can run frontend app in docker.
Both options require nginx to work with backend.

```sh
docker compose -f ./deployment/local/docker-compose.yml build --progress plain frontend
```

### Backend
1. Install Java (17 is recommended)
    ```sh
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    sdk version
    sdk install java
    ```

1. Setup Backend (Java) App properties

   Create new file `./src/main/resources/application-local.properties`.
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:9797/postgres
    spring.datasource.username=postgres
    spring.datasource.password=password
    ## Create yours Google Oauth2 credentials 
    # (https://support.google.com/cloud/answer/6158849?hl=en)
    #spring.security.oauth2.client.registration.google.clientId=<CLIENT ID>
    #spring.security.oauth2.client.registration.google.clientSecret=<CLIENT SECRET>
    ## Get admin privileges locally.
    chessgrinder.security.adminEmail=<YOUR EMAIL>
    ## Allow login with password.
    chessgrinder.feature.auth.signupWithPasswordEnabled=true
    ```

1. Install `javafo` library.

    This library is used to run pairings. Without it the application will not compile. 
    We need to do this manually because it is unavailable on mavencentral.
    ```sh
    # Install maven dependencies to local repository so that they could be used in the pom.xml
    mvn install:install-file -Dfile=./lib/javafo-2.2-main.jar -DgroupId=javafo -DartifactId=javafo -Dversion=2.2 -Dpackaging=jar
    ```

1. Run `ChessGrinderApplication` class with `local` profile.

