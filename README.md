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

1. Install Maven and Java 17
    ```sh
    curl -s "https://get.sdkman.io" | bash
    ```
    Then close and open terminal.
    ```sh
    sdk install maven
    ```
2. Run
   ````
   make dev_java_build
   ````
   
3. Setup Backend (Java) App properties 
    
    Create new file `./src/main/resources/application-local.properties` directory.
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:9797/postgres
    spring.datasource.username=postgres
    spring.datasource.password=password
    ## Create yours Google Oauth2 credentials 
    # (https://support.google.com/cloud/answer/6158849?hl=en)
    spring.security.oauth2.client.registration.google.clientId=CLIENT_ID
    spring.security.oauth2.client.registration.google.clientSecret=SECRET_ID
    ```

4. Run to run docker container with postgres:
    ```sh
    docker run -d \
      --name chessgrinder_postgres \
      -e POSTGRES_PASSWORD=password \
      -p 9797:5432\
      postgres
    ```

5. Setup <i>nginx</i>:
    ```sh
    brew install nginx # (command for macOS only)
    ```
    (command for macOS only)
    ```sh
    cd deployment/local
    ```
    
    Edit **local_nginx_setup.sh**, change path to nginx conf directory
  
    ```sh
    sudo chmod +x local_nginx_setup.sh
    ```
    ```
    sudo ./local_nginx_setup.sh
    ```
    ```sh
    sudo brew services restart nginx
    ```
6. Start java app with local profile
7. Run frontend app:
   1. Install NodeJS 16
   2. Frontend App is located in `frontend` directory
       ```shell
       cd frontend
       ```
   3. Install frontend dependencies
       ```sh
       npm install
       ```
   4. Run application in development mode
       ```sh
       npm start
       ```

