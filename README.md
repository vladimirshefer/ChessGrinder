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

1. Install Java (17 is recommended)
    ```sh
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
    sdk version
    sdk install java
    ```

1. Install [Docker](https://docs.docker.com/get-started/get-docker/)

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

1. Run docker container with postgres:
    ```sh
    make local_postgres_run # see Makefile
    ```

1. Run nginx:
    ```sh
    make local_nginx_run # see Makefile
    ```
6. Start java app with `local` profile
7. Run frontend app:
    ```sh
    # Install Node JS 16
    # Goto frontend directory
    cd frontend
    # Install dependencies
    npm install
    # Run app in development mode
    npm run start
    ```

