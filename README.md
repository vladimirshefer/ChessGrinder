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

