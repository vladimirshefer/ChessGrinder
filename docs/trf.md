# Format of TRF (Tournament Report File)

Agreed general Data-Exchange Format for tournament results to be submitted to FIDE.
[Source](https://www.fide.com/FIDE/handbook/C04Annex2_TRF16.pdf)

---

**Remark 1** Each line shall have a "CR" (carriage return) as last character  
**Remark 2** The columns **R** and **P** in all the following tables tell the importance of the field for **Rating** and **Pairing** respectively:

- ■ Mandatory
- □ Warning if wrong
- (blank) Not taken into account

---

## Player Section

| Position | Description             | Contents                                 | Rating | Pairing |
|----------|-------------------------|------------------------------------------|--------|---------|
| 1–3      | Data Identification no. | `001` (for player-data)                  | ■      | ■       |
| 5–8      | Starting rank number    | from 1 to 9999                           | ■      | ■       |
| 10       | Sex                     | `m/w`                                    | □      |         |
| 11–13    | Title                   | `GM, IM, WGM, FM, WIM, CM, WFM, WCM`     | □      |         |
| 15–47    | Name                    | Lastname, Firstname                      | □      |         |
| 49–52    | FIDE Rating             |                                          | □      |         |
| 54–56    | FIDE Federation         |                                          | □      |         |
| 58–68    | FIDE Number             | (including 3 digits reserve)             | ■      |         |
| 70–79    | Birth Date              | Format: `YYYY/MM/DD`                     | □      |         |
| 81–84    | Points                  | Tournament points (e.g., `11.5`, `17.0`) | ■      |         |
| 86–89    | Rank                    | Exact definition, especially for Team    | ■      |         |

### For each round

| Position | Description             | Contents                                                                                  | Rating | Pairing |
|----------|-------------------------|-------------------------------------------------------------------------------------------|--------|---------|
| 92–95    | Player/forfeit id       | Starting rank number of opponent (up to 4 digits) <br> `0000` if bye or not paired        | ■      | ■       |
| 97       | Scheduled color/forfeit | `w`, `b`, `-` (bye/not paired)                                                            | ■      | ■       |
| 99       | Result                  | `-` forfeit loss, `+` forfeit win, `W/D/L` unrated, `1/= /0` regular, `H/F/U/Z` bye types | ■      | ■       |
| 102–105  | Id (round 2)            |                                                                                           | ■      | ■       |
| 107      | Color (round 2)         |                                                                                           | ■      | ■       |
| 109      | Result (round 2)        |                                                                                           | ■      | ■       |
| 112–115  | Id (round 3)            |                                                                                           | ■      | ■       |
| 117      | Color (round 3)         |                                                                                           | ■      | ■       |
| 119      | Result (round 3)        |                                                                                           | ■      | ■       |
| ...      |                         | and so on...                                                                              |        |         |

---

## Tournament Section

Data-Identification-number: `??2` (for tournament data)

| Position | Description                                             | Rating | Pairing |
|----------|---------------------------------------------------------|--------|---------|
| 012      | Tournament Name                                         | ■      | ■       |
| 022      | City                                                    | ■      |         |
| 032      | Federation                                              | ■      |         |
| 042      | Date of start                                           |        |         |
| 052      | Date of end                                             |        |         |
| 062      | Number of players                                       |        |         |
| 072      | Number of rated players                                 |        |         |
| 082      | Number of teams                                         |        |         |
| 092      | Type of tournament                                      |        |         |
| 102      | Chief Arbiter                                           | ■      |         |
| 112      | Deputy Chief Arbiter (one line for each arbiter)        | .      |         |
| 122      | Allotted times per game                                 |        |         |
| 132      | Dates of the round Format: `YY/MM/DD`. See Round Dates. | .      |         |

### Round dates

| Position   | Description   |
|------------|---------------|
| 92–99      | Round 1 date  |
| 102–109    | Round 2 date  |
| 112–119    | Round 3 date  |
| …          | and so on     |

---

## Team Section

| Position | Description             | Contents                                       | Rating | Pairing |
|----------|-------------------------|------------------------------------------------|--------|---------|
| 1–3      | Team-Section-Identifier | `013` (for team data)                          | ■      | ■       |
| 5–36     | Team Name               |                                                | ■      | ■       |
| 37–40    | Team 1st player         | Starting Rank Number (from player section 5–8) | ■      | ■       |
| 42–45    | Team 2nd player         |                                                |        |         |
| 47–50    | Team 3rd player         |                                                |        |         |
| …        | Continue as needed      |                                                |        |         |
| 72–75    | Team 8th player         |                                                |        |         |
| 102–105  | Team 14th player        |                                                |        |         |
| …        | and so on               |                                                |        |         |

---

*Christian Krause (Torino, June 1st, 2006)*  
*Updated: Tromsø, August 13th, 2014*  
*Approved: Elista, August 10th, 2015*
