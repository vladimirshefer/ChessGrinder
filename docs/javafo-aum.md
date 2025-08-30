# JAVAFO PAIRING ENGINE ADVANCED USER MANUAL

- [Introduction](#introduction)
- [JaVaFo as a stand-alone program](#javafo-as-a-stand-alone-program)
- [How to input the data](#how-to-input-the-data)
  - [Interpretation extensions](#interpretation-extensions)
  - [Unusual info extensions](#unusual-info-extensions)
  - [Extra codes extensions](#extra-codes-extensions)
  - [TRF(x) sample](#trfx-sample)
- [How to invoke JaVaFo](#how-to-invoke-javafo)
- [How to read the output of JaVaFo](#how-to-read-the-output-of-javafo)
- [Extensions and other options](#extensions-and-other-options)
  - [Absent players](#absent-players)
  - [Scoring point system](#scoring-point-system)
- [Ranking id](#ranking-id)
- [First round pairing](#first-round-pairing)
- [Accelerated rounds](#accelerated-rounds)
  - [Baku Acceleration Method](#baku-acceleration-method)
- [Forbidden pairs](#forbidden-pairs)
- [Check-list](#check-list)
- [Release and build numbers](#release-and-build-numbers)
- [Pairings Checker](#pairings-checker)
- [Random Tournament Generator (RTG)](#random-tournament-generator-rtg)
  - [Reducing randomness by way of a (RTG) configuration file](#reducing-randomness-by-way-of-a-rtg-configuration-file)
  - [Reducing randomness by way of a model tournament](#reducing-randomness-by-way-of-a-model-tournament)
  - [Repeating randomness (by using a seed)](#repeating-randomness-by-using-a-seed)
- [Quick recap](#quick-recap)
- [JaVaFo as Java archive](#javafo-as-java-archive)
  - [Examples](#examples)

## Introduction
JaVaFo is both a stand-alone program (provided that a Java Virtual Machine exists to execute it) and an archive (.jar)
that can be used by a program written in the Java programming language.

This manual describes both possibilities.

## JaVaFo as a stand-alone program
In the following chapters, it will be described how it is possible for an external program to integrate the JaVaFo
Pairing Engine in order to use it to prepare pairings according to the FIDE (Dutch) Swiss System 
(see [section C.04.3 from FIDE Handbook](http://web.archive.org/web/20181223232906/https://www.fide.com/fide/handbook.html?id=170&view=article)).

## How to input the data
Albeit JaVaFo supports many input formats (and other ones could be easily added), the most practical way to input data
to JaVaFo is using the TRF(x), where TRF (also called TRF16 to distinguish it from the TRF06 used in the past), is the
official FIDE Tournament Report File defined in the FIDE handbook (see), and the (x) stands for some extensions that
have to be introduced in such format to make it useful for exchanging data between different programs. To be clearly
understood by JaVaFo, it is recommended that such file is written using the UTF-8 encoding.

The extensions to the TRF16 are partly made by adding some new codes (which are alphabetic in order to be completely
different by the current numeric codes defined for the format [1]), partly by allowing writing in the TRF something that
is not normally found in a TRF, partly by interpreting some data contained in it.

### Interpretation extensions
A TRF is normally used only to generate data at the end of the tournament. The first extension is to allow the TRF to be
generated also during the tournament. This partial TRF is fed to JaVaFo as its input.

It is important to note that the field called Points in the TRF16 definition (position 81-84) has to contain the correct
number of points that each player got, because that number is tentatively used to infer the scoring point system used (
i.e. the classic 1, ½, 0 or another one, like 3, 1, 0; or even weirder ones - for the complete list of values that
JaVaFo tries to infer, see the scoring-point parameters in the section Reducing randomness by way of a (RTG)
configuration file - scoring-point parameters).

However, with version 2.2, the above methodology of computing the scoring-point parameters has been deprecated. Such
parameters, if different from the standard ones, have to be explicitly defined by using the option XXS described in the
chapter dedicated to the Extensions and other options below (see Scoring point system).

### Unusual info extensions
The partial TRF contains information regarding the rounds that have already been played. However, it says nothing
regarding the current round (i.e. the one that should be paired). The most important thing is to tell JaVaFo which
players should be paired or, which is the same as, which players will not play that round.

If everybody plays in the current round, the partial TRF is enough. Otherwise, if somebody is missing, there are two
non-conflicting ways to pass this piece of information to the pairing engine.

The first one consists of inserting into the proper columns for the current round the result code that is normally used
for absent players, i.e.  `0000 - Z`.

For the sake of clarity, it is also possible to use the codes that will be present in the final TRF16 (i.e. `0000 - H`,
for half-point-byes or `0000 - F` for the deprecated full-point-byes), 
but, in these cases, also the field Points (position 81-84, see above) must be updated.

Blank codes are obviously ignored in this instance, as each present player is identified by the absence of a result
code.

Although this methodology has not been deprecated yet, in order to tell the pairing engine which players are not to be
paired in a round, it is preferable to use the option XXZ described in the chapter dedicated to the Extensions and other
options below (see Absent players).

### Extra codes extensions
As said in the introduction regarding input data, some alphabetic codes were added in order to transmit additional
information to the pairing engine. Some of them are not essential (and will be shown in a following chapter), but one
is: the pairing engine must know the total number of rounds in the tournament in order to know if the current round is
the last one.

This is obtained by means of the following new alphabetic code:

XXR number

where number is the number of rounds of the competition.

### TRF(x) sample
In the linked file, there is an example of the TRF(x) used to pair the fifth round of the XX Open Internacional de Gros
which includes everything said above regarding the TRF extensions.

[TRF(x) sample](#trfx-sample-contents)

According to what was discussed before, it should be pretty evident that the players 22, 26 and 43 will not play the
fifth round (look at the field Points for the player 43, though).

## How to invoke JaVaFo
Although the title of this chapter is "JaVaFo as a stand-alone program", this terminology is somewhat simplistic. In
reality, JaVaFo is an executable Java archive (JAR), and here it is described how to invoke such an element.

First of all, a Java Virtual Machine (JVM) is needed. The beauty of this is that the operating system (O.S.) in use is
not important. JaVaFo can run on any O.S. provided that a Java Virtual Machine exists in it. Use the java command to
activate it.

Then the pairing engine itself, javafo.jar, is needed. The latest version of the jar can always be downloaded from the
following website:

http://www.rrweb.org/javafo/current/javafo.jar

You can also find previous versions of javafo.jar replacing current with 1.0, 1.1, 1.2 and so on. Be aware, though, that
the releases until 1.4 do not follow the specification of this manual, but are tied to ancient versions of the Dutch
Rules.

The downloaded jar archive can be stored anywhere in the file system. Be JVF_DIR the pathname of the folder where
javafo.jar has been downloaded.

The first test to see if both the JVM and the pairing engine work is to write in a command prompt:
`java -ea -jar JVF_DIR\javafo.jar` 
or 
`java -ea -jar JVF_DIR/javafo.jar`
depending on the O.S. in use. The first line works for Windows. 
From here on, only the Windows commands are mentioned.
Moreover, for the full command `java -ea -jar JVF_DIR/javafo.jar`, the string javafo is used, as if a file named
javafo.bat exists somewhere in a PATH directory and contains the statement: `@java -ea -jar JVF_DIR\javafo.jar %*`.
If everything is properly set up, the above command should produce the following output (or something similar, more
up-to-date):

```
JaVaFo (rrweb.org/javafo) - Rel. 2.2 (Build 3222)
```

After checking that the pairing engine is ready, a TRF16 file can be input to it (a TRF06 will still work, but the
output is not completely trustworthy). This file can be placed anywhere in the file system. Let TRF_DIR be the pathname of
the folder where the file trn.trfx is located (trn.trfx is just a mnemonic name; dummy.foo is an equally valid name).

It should also be decided where the pairing engine output goes. Let OUT_DIR\outfile.txt be the pathname of said output
file (as above, this file can be called in any way). Be aware that no warning is issued if the file pre-exists before
invoking javafo.jar.

From the same command prompt mentioned above, the following command line is needed to produce the pairings for the
current round:

```
javafo TRF_DIR\trn.trfx -p OUT_DIR\outfile.txt
```

The meaning of the most useful options will be described in a following chapter. 
But the above command is the most generic way to invoke the javafo.jar pairing engine. 
If `OUT_DIR\` is the same as `TRF_DIR\`, then `OUT_DIR` can be dropped, 
so that `javafo TRF_DIR\trn.trfx -p outfile.txt` generates `outfile.txt` in `TRF_DIR`. 
If TRF_DIR is the current directory (i.e. "."), TRF_DIR\ can be dropped.

## How to read the output of JaVaFo
When invoked as shown in the previous command, the output is the file outfile.txt. 
The structure of this file is very simple:

[1]    the first line reports the number of generated pairs (be P)

[2]    from the second to the (P+1)-th line, each line contains a pair for the current round. Such pair is made using
the pairing-id(s) of the players, i.e. the id(s) that are defined in the 001 line of the TRF(x). The first element of
the pair gets white, the second one black. If the number of players to be paired in the round is odd, one of the pairs
is formed by the id of the player that gets the pairing-allocated-bye, followed by 0.

The TRF sample shown above will produce the following output:

``` 
25 
1 2 
3 4 
5 6 
7 13 
11 21 
23 12 
19 16 
17 52 
35 18 
8 24 
9 26 
37 10 
14 29 
45 15 
46 20 
27 38 
34 30 
39 31 
41 32 
42 33 
44 48 
25 49 
40 50 
51 36 
47 0
```

If something goes wrong, the output file is not generated. 
Something is usually displayed on standard output or standard error, 
but it may be quite difficult to interpret.

The most common cause for an error is a malformed TRF(x). 
However, if the TRF(x) is totally correct, then an error must have occurred in the pairing engine. 
This may occasionally happen, but it is a very unlikely event 
(although not an impossible one, of course).

## Extensions and other options
What was presented in the previous chapters covers the majority of the situations that can happen in a tournament.
Sometimes, however, something may happen that requires some extra care.

### Absent players
In Unusual info extensions, it has already been shown a way to register the players who are not going to be paired in a
round. An alternative way is to use the extension code XXZ, the format of which is:

```
XXZ list-of-pairing-id(s)
```

where the pairing-id(s) following XXZ are intuitively the ones of the players that will miss the round to be paired.

There can be multiple `XXZ` records.

### Scoring point system
In Unusual info extensions, it has already been shown a deprecated way to compute the scoring-point parameters. The
safer way is to use the extension code XXS, the format of which is:

```
XXS CODE=VALUE
```

where VALUE is a floating-point number (e.g., 1.5), and CODE is one of the following codes:

| Code | Default | Value | Description                               |
|------|---------|-------|-------------------------------------------|
| WW   | 1.0     |       | Points for win with White                 |
| BW   | 1.0     |       | Points for win with Black                 |
| WD   | 0.5     |       | Points for draw with White                |
| BD   | 0.5     |       | Points for draw with Black                |
| WL   | 0.0     |       | Points for loss with White                |
| BL   | 0.0     |       | Points for loss with Black                |
| ZPB  | 0.0     |       | Points for zero-point-bye                 |
| HPB  | 0.5     |       | Points for half-point-bye                 |
| FPB  | 1.0     |       | Points for full-point-bye                 |
| PAB  | 1.0     |       | Points for pairing-allocated-bye          |
| FW   | 1.0     |       | Points for forfeit win                    |
| FL   | 0.0     |       | Points for forfeit loss                   |
| W    | 1.0     |       | Encompasses all the codes WW, BW, FW, FPB |
| D    | 0.5     |       | Encompasses all the codes WD, BD, HPB     |

The sequence CODE=VALUE can be repeated multiple times in a XXS record, 
or there may be multiple XXS records. 
Codes that are not mentioned in the XXS records are assumed to have the standard value. 
Hence, when the standard scoring point system is used, 
there is no need to put XXS codes in the input TRF(x).

When a shortcut and a code encompassed by such shortcut are both used, 
the order (left-right, top-down) is decisive,
because the latter element overrides the former.

Note that when XXS is used, JaVaFo makes a strict check that there is an absolute correspondence between the field
Points (characters 81-84, in the 001 record of the TRF(x)) and the results. If the check fails, the program may crash.

Examples

1. Although not needed, the standard scoring system may be described by:
    ```
    XXS WW=1 WD=.5 WL=0 BW=1 BD=0.5 BL=0
    XXS FL=0 FW=1
    XXS PAB=1 FPB=1 HPB=.5 ZPB=0
    ```
    or by:
    ```
    XXS W=1 D=0.5 WL=0 BL=0 FL=0 ZPB=0 PAB=1
    ```

2. The sequence:
    ```
    XXS PAB=3 D=1 W=3
    ```
    is enough to describe the 3/1/0 scoring point system (with the PAB equal to a win), and
    ```
    XXS W=3
    XXS D=1
    ```
    is enough to describe the 3/1/0 scoring point system with the PAB equal to a draw (because the original PAB=1 is
    assumed).

3.
    ```
    XXS PAB=.5
    ```
    
    is enough, when a half-point PAB is requested and anything else is standard.

4.
    The 3/2/1/0 score system, which is often talked about, can be represented by:
    ```
    XXS FL=0 W=3 D=2 PAB=2 WL=1 BL=1 ZPB=1
    ```
    (note: this is one possibility - FL=0 may be omitted, PAB may be equal to 3, and ZPB may be equal to 0, and there also
    other possibilities).

5. As an example of overriding:
    ```
    XXS W=3 D=2 WL=1 BL=1
    XXS PAB=3
    XXS FPB=0 HPB=0 ZPB=0
    ```
    is a 3/2/1/0 scoring system where all missed rounds net zero points, but writing:
    ```
    XXS PAB=3
    XXS FPB=0 HPB=0 ZPB=0
    XXS W=3 D=2 WL=1 BL=1
    ```
    will not reach the same goal because W=3 overrides FPB=0 (and D=2 overrides HPB=0).

## Ranking id

JaVaFo identifies players with two numbers, the player-id and the positional-id.

The first one is obvious and it is the one that is associated with the 001 record in the TRF(x). 
The second one is implicitly given by the position of the players in the TRF(x). 
Albeit this is not mandatory, players are normally inserted into the TRF(x) in accordance with their pairing-id, 
so that the two sets of data (pairing-id(s) and
positional-id(s)) are basically coincident.

They may differ, though, and sometimes for good reasons. 
For instance, please have a look at the linked file, which is a modified version of the previous sample: 
the players with a local rating (0 for FIDE) are placed at the end of the list.

Ranked TRF(x) Sample

The positional-id is 1 for Mirzoev, 6 for Lakunza (player-id: 7), 29 for Aizpurua (player-id: 32), 42 for Abalia (
player-id: 42), 48 for Gorrochategui (player-id: 6) and so on.

JaVaFo computes the pairings using the pairing-id(s).
Beware: JaVaFo uses the pairing-id(s), not the ratings, as specified by the FIDE (Dutch) rule A.2.b.
This is a programming choice that cannot be modified.

However, the calling program is not forced to follow the same logics.
If it desires that the rating be prevalent, it has two alternatives:

1. Before calling JaVaFo, redefine the pairing-id(s) in such a way that increasing pairing-id(s) 
    are assigned to players with decreasing rating (which, by the way, is the most standard situation)
2. Insert players in the TRF(x) in order of rating and tell JaVaFo to use the positional-id(s) 
    (also called ranking-id(s)) instead of the pairing-id(s)

The first choice is the recommended one. However, in order to let the calling program use the second alternative, JaVaFo
provides the extension code:

XXC rank

The 'C' in XXC stands for configuration. The word rank tells JaVaFo to use the positional-id(s) in order to produce the
pairings. The output file still contains the pairing-id(s).

If the "XXC rank" clause is added to the file shown in the Ranked TRF(x) Sample, JaVaFo will return the following
output:

```
25 
1 2 
3 4 
5 6 
7 13 
11 21 
23 12 
19 16 
17 52 
35 18 
8 24 
9 26 
45 10 
14 29 
37 15 
46 20 
27 31 
39 32 
44 33 
34 30 
41 38 
42 48 
25 50 
40 49 
51 36 
47 0
```

which is different from the previous one (differences highlighted in pink).

## First round pairing
Although the rule for pairing the first round is very simple and therefore the calling program can generate it directly,
it is recommended to use JaVaFo also to generate the first round.

The only information to pass to the pairing engine is the initial-colour, as defined in the section E of the FIDE (
Dutch) Rules. There are three possibilities:

- a. white 
- b. black 
- c. let JaVaFo make the choice (i.e. random)

The latter choice is the default. Beware that it is a semi-random choice: to compute it, JaVaFo uses the hash of some
data taken from the TRF(x); this means that repeating the process with the same TRF(x) will give the same result each
time. Therefore, in order to use the JaVaFo random choice, the calling program needs to do nothing.

Otherwise, to force the choice [a], the TRF(x) must contain a line

```
XXC white1
```

To force the choice [b], the TRF(x) must contain a line

```
XXC black1
```

Please note that the XXC code is cumulative, so it is followed by all the configuration choices made by the calling
program. For instance:

```
XXC rank black1
```

is a valid extension line and combines what was described in the previous and in the current chapters.

## Accelerated rounds
The standard way to have accelerated rounds is to assign fictitious points to some players. How to assign such points
depends on various methods and only one of them has been codified in the old FIDE Handbook (see). Hence, this is not a
matter of discussion here (on the other hand, see Baku Acceleration Method, below).

However, JaVaFo can be informed of the fictitious points that are assigned to each player, using the extension code XXA.

The format for this code is

```
XXA NNNN pp.p pp.p ...
```

Where:
- `XXA` starts at column 1 
- `NNNN` (player's id - same as in 001) starts at column 5 
- `pp.p` (fictitious points) starts at column 10+5*(r-1), where r is the round in which the fictitious points must be
added.

It is mandatory to keep the full record of the fictitious points assigned round by round, because this record is used to
determine the floaters history of each player (actually, if pairing for round X, it is enough to maintain the fictitious
points history from the rounds from X-3 to the current one - but it seems simpler, also from a pairing-checking
standpoint, to keep the full history).

Here is an example from a real tournament where seven accelerated rounds were used:

[TRF(x) Acceleration Sample](#trfx-acceleration-sample-contents)

### Baku Acceleration Method
Upon request, JaVaFo can pair the current round by applying the Baku Acceleration Method 
(see [C.04.5.1 in the old FIDE Handbook](http://web.archive.org/web/20170912195924/http://www.fide.com/component/handbook/?id=204&view=article)). 
In order to do so, JaVaFo should be invoked using the option -b   
(note: old FIDE Handbook, i.e. works only for tournaments longer than eight rounds).

Beware that JaVaFo applies the Baku methodology only to the current round, not to the already paired rounds. 
JaVaFo relies on the input TRF(x) to correctly convey the information 
(basically the fictitious points) related to the previous rounds.

## Forbidden pairs

Sometimes some players are to be prevented from meeting each other. 
JaVaFo can be directed to fulfill this need by the extension code XXP.

The format of this code is:

```
XXP list-of-pairing-id(s)
```

All the players mentioned in the list will not be paired against each other.

There is no limit on how many times a player can be part of a `XXP` list. So, for instance, if games between members of
two groups of players cannot happen (for instance, assume that <13, 78, 102> and <68, 111> are these two groups), the
following list of XXP extension codes should be generated:

```
XXP 13 68
XXP 13 111
XXP 78 68
XXP 78 111
XXP 102 68
XXP 102 111
```

## Check-list
Upon request, JaVaFo can generate a check-list, i.e. a file that summarizes the situation after the pairing, with the
following contents (taken from the previous TRF(x) sample):

[Check-List Sample](#check-list-sample-contents)

In the check-list, the majority of the fields are pretty intuitive. 
Some more explanation may be needed for the columns
Pref, -1R, -2R and all the columns G-n.

Pref reports the preference of the players. 
The following table explains the symbols and the associated preference:

| White | Black | Meaning                                                                                                                    |
|-------|-------|----------------------------------------------------------------------------------------------------------------------------|
| WWW   | BBB   | Double absolute preference (see A.6.a); both conditions are met: twice-same-colour and colour-difference higher than \|1\| |
| WW    | BB    | Absolute preference for colour-difference higher than \|1\| (see A.6.a)                                                    |
| W1    | B1    | Absolute preference for twice-same-colour and colour-difference equal to 1 (see A.6.a)                                     |
| W     | B     | Absolute preference for twice-same-colour and colour-difference equal to 0 (see A.6.a)                                     |
| (W)   | (B)   | Strong colour preference (see A.6.b)                                                                                       |
| (w)   | (b)   | Mild colour preference (see A.6.c)                                                                                         |
| A     | A     | No preference (see A.6.d)                                                                                                  |


-1R and -2R are references to the floating history of the players. 
They show the kind of float (up or down) respectively in the last and in the penultimate round.

The columns `G-r`, ..., `G-1` represent the opponents of a player in the last g-th game he played 
(unplayed rounds are not considered). 
The presence of an [X] in the first of such columns means that the player cannot receive the
pairing-allocated-bye.

In order to produce the check-list, JaVaFo should be invoked using the option -l, optionally followed by the name of the
file in which to put the check-list. If such a name is missing, JaVaFo will produce the file trn.list, provided that -l
follows the input file name.

For instance, this works:

```bash
javafo TRF_DIR\trn.trfx -p OUT_DIR\outfile.txt -l
```

and this works too:

```bash
javafo TRF_DIR\trn.trfx -p OUT_DIR\outfile.txt -l ANY_DIR\outfile.list
```

As usual, if ANY_DIR is omitted, outfile.list is produced in the TRF_DIR.

## Release and build numbers
As mentioned above, the simple command javafo will print the release version and build on the standard output.

If an input filename is specified, this information is not output unless the -r option is used.

## Pairings Checker
JaVaFo can also be used to check the correctness of a TRF produced by other software: the command line:

```bash
javafo TRF_DIR\trn.trfx -c
```

will produce on standard output something similar to what is shown below 
(related to the tournament described by the TRF(x) Acceleration Sample), 
with obvious meaning:

```
AcceleratedTRFXSample2: Round #1

AcceleratedTRFXSample2: Round #2

AcceleratedTRFXSample2: Round #3

AcceleratedTRFXSample2: Round #4

AcceleratedTRFXSample2: Round #5

AcceleratedTRFXSample2: Round #6

  Checker pairings        Tournament pairings

     55 -  73                 55 -  71

     61 -  71                 74 -  73

     74 -  75                 61 -  83

     80 -  83                 80 -  75

AcceleratedTRFXSample2: Round #7

AcceleratedTRFXSample2: Round #8

AcceleratedTRFXSample2: Round #9

   Checker pairings       Tournament pairings

     76 -  78                 76 -  75

     74 -  75                 61 -  78

     80 -  61                 80 -  74
```

In order to check a single round (and not the whole tournament), 
add the number of the round to the aforementioned command as in:

```
javafo TRF_DIR\trn.trfx -c 6
```

The output of this command for the previous sample is shown below:

```
AcceleratedTRFXSample2: Round #6

  Checker pairings        Tournament pairings

     55 -  73                 55 -  71

     61 -  71                 74 -  73

     74 -  75                 61 -  83

     80 -  83                 80 -  75
```

## Random Tournament Generator (RTG)
In order to help an external pairing-checker, JaVaFo can generate random or quasi-random tournaments against which the
external pairing-checker can be tested.

The command line (in its simplest form):

```
javafo -g -o trn.trf
```

will generate in the current folder (any pathname like `TEST_DIR\trn.trf` can be specified, though) a file `trn.trf`, 
which is a TRF16 of a random tournament, 
with a random number of players (usually between 15 and 415), 
a random number of rounds (usually between 5 and 17) and 
game results that depend on the rating difference between the involved players,
applying a formula elaborated by Otto Milvang (see here, Appendix A, pag. 8) that, 
in the long run, will distribute points based on what actually happens in rated tournaments.

In the same command line, the option `-b` (apply the Baku Acceleration Method) can be added.

The most important utilization of this feature is to generate thousands of tournaments and then test them with the
appropriate checker. Therefore it is advisable to use (on Windows) a statement like that:

```
@for /L %p IN (1000,1,5999) do @javafo -g -o test%p.trf
```

which will generate exactly 5000 random tournaments in the current folder.

The previously mentioned randomness in the generated files can be reduced in two alternative ways, using either a (RTG)
configuration file or a model TRF.

### Reducing randomness by way of a (RTG) configuration file
A (RTG) configuration file is a property-file[2] where the following parameters (properties) may be defined (for the
explanation of each single parameter, please look at the sample below):

| ParameterName  | Default (when the parameter is not defined) |
|----------------|---------------------------------------------|
| PlayersNumber  | A random number between 15 and 415          |
| RoundsNumber   | A random number between 5 and 17            |
| ForfeitRate    | A random number between 10 and 100          |
| QuickgameRate  | A random number between 0 and 20            |
| ZPBRate        | A random number between 0 and 20            |
| HPBRate        | A random number between 5 and 45            |
| FPBRate        | A random number between 0 and 2             |
| HighestRating  | A random number between 2400 and 2800       |
| LowestRating   | A random number between 1400 and 2300       |
| Groups         | A random number between 0 and 10            |
| Separator      | A random number between 20 and 70           |

The following twelve parameters are also called scoring-point parameters
(they are the values that JaVaFo tries to infer using the field Points (position 81-84) of TRF16)

| Parameter | Default | Description                      |
|-----------|---------|----------------------------------|
| WWPoints  | 1.0     | points for win with White        |
| BWPoints  | 1.0     | points for win with Black        |
| WDPoints  | 0.5     | points for draw with White       |
| BDPoints  | 0.5     | points for draw with Black       |
| WLPoints  | 0.0     | points for loss with White       |
| BLPoints  | 0.0     | points for loss with Black       |
| ZPBPoints | 0.0     | points for zero-point-bye        |
| HPBPoints | 0.5     | points for half-point-bye        |
| FPBPoints | 1.0     | points for full-point-bye        |
| PABPoints | 1.0     | points for pairing-allocated-bye |
| FWPoints  | 1.0     | points for forfeit win           |
| FLPoints  | 0.0     | points for forfeit loss          |

An example of a (RTG) configuration file (with comments) is shown here:

[Random Tournament Generator Configuration Sample](#random-tournament-generator-configuration-sample-contents)

In order to being used by the JaVaFo Random Tournament Generator, the (RTG) configuration file must be specified as a
parameter to the `-g` option.
Therefore, the full command line is:

```bash
javafo -g RTG_DIR\rtg.cfg -o TEST_DIR\trn.trf
```
(or)
```bash
javafo -g RTG_DIR\rtg.cfg -b -o TEST_DIR\trn.trf
```

if there is the desire to apply, when feasible, the Baku Acceleration Method.

### Reducing randomness by way of a model tournament
A model tournament is a normal input TRF file with meaningful player ratings, which serves as a model in order to define
all the parameters mentioned above.

For instance, from the input file seen in the
[TRF(x) Acceleration Sample](#trfx-acceleration-sample-contents), 
the following values are automatically
retrieved:

| ParameterName | Value |
|---------------|-------|
| PlayersNumber | 84    |
| RoundsNumber  | 9     |
| ForfeitRate   | 2     |
| QuickgameRate | 0     |
| ZPBRate       | 30    |
| HPBRate       | 0     |
| FPBRate       | 0     |

Note that the ratings parameters `HighestRating`, `LowestRating`, `Groups` and `Separator` are not considered, 
as the ratings are exactly the same as the ones present in the input model file, 
while the scoring-points parameters are not shown as their value is equal to the corresponding default value.

As the model file is a standard input file, the command line to use it is:

```sh
javafo MODEL_DIR\model.trf -g -o TEST_DIR\trn.trf
```

It is also possible to add the option `-b` (apply Baku Acceleration Method).

### Repeating randomness (by using a seed)
The oxymoronic title means that it is possible to generate (or re-generate) the same random TRF by replacing the
configuration file (e.g. `RTG_DIR\rtg.cfg`) with a long integer number (from 0 to 9223372036854775807).

For instance, the command:

```bash
javafo -g 18980522 -o TEST_DIR\trn.trf
```

will always generate the following file:

[Seed=18980522 Tournament Report File](#seed18980522-tournament-report-file-contents)

as long as the build number of `javafo.jar` stays the same.

- Note 1. The seed of any tournament randomly generated by javafo RTG is retrievable from the field `012` 
  (the first line of the output TRF).
- Note 2. If the name of the configuration file is a long integer number, it is taken as a seed.

## Quick recap
Standard invocations:

```
javafo [-r]
javafo [-r] input-file -c [round-number]
javafo [-r] input-file [-b] -p [output-file] [-l [check-list-file]]
javafo [-r] [model-file] -g [-b] -o trf-file
javafo [-r] -g config-file [-b] -o trf-file
```

The square brackets `[ ]` represent something optional.

Some explanations:

| Option / Command       | Description                                                                                                                                                                    |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `javafo`               | Indicates a file named javafo.bat with the following contents: `@java -ea -jar JVF_DIR\javafo.jar %*`                                                                          |
| `-r`                   | Show JaVaFo release and build numbers                                                                                                                                          |
| `input-file`           | Input in TRF(x) format                                                                                                                                                         |
| `model-file`           | Input in TRF(x) format, model file for the random-tournament-generator                                                                                                         |
| `config-file`          | Usually a configuration file for the random-tournament-generator. If config-file is a long integer (0 to 9223372036854775807), it is used as the seed for the random-generator |
| `-c [round-number]`    | Use JaVaFo as a checker. round-number: number of the round to be checked; if missing, all rounds are checked                                                                   |
| `-g`                   | Use JaVaFo as a random-tournament-generator. With this option, at most one between model-file and config-file may be present                                                   |
| `-b`                   | Apply, if feasible, the Baku Acceleration Method                                                                                                                               |
| `-p [output-file]`     | Output file where to write the pairing. If missing, defaults to standard output                                                                                                |
| `-l [check-list-file]` | File where to write the check-list. If missing, defaults to the input-file directory, with the same basename as the input-file and `.list` extension                           |
| `-o trf-file`          | File where to write the (auto)generated TRF file                                                                                                                               |

## JaVaFo as Java archive
As an experimental tool, with versions 2.x, it is possible to include parts of JaVaFo in a Java project and have the
possibility to directly interface JaVaFo as a pairing engine, a free pairing-checker or a random-tournament-generator.

The first operation is to open `javafo.jar` 
(which is an archive that can usually be opened with 
the same tools that open .zip or .rar files) 
and extract the file main.jar. 
The latter is the file to be included in a Java project. 
It exposes a static class, JaVaFoApi, that contains the definition of a method that can be called to invoke, 
depending on the parameters, the pairing engine, the pairing checker or the random generator.

The aforementioned method is:

```java
String JaVaFoApi.exec(int operation, Object... params);
```

that will execute the most common operations of JaVaFo (and a few others).

The first parameter (operation) may assume one of the following values:

#### Operation 

A mandatory four-digit code (Java type int), identifying the particular JaVaFo operation:

| Code | Mnemonics                        | Description                                                                                                                         |
|------|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| 1000 | PAIRING                          | Standard pairing                                                                                                                    |
| 1001 | PAIRING_WITH_BAKU                | Standard pairing, using, if applicable, the Baku Acceleration Method                                                                |
| 1100 | PRE_PAIRING_CHECKLIST            | Check-list before doing the pairing. Note: This operation is undocumented using JaVaFo as a stand-alone program                     |
| 1110 | POST_PAIRING_CHECKLIST           | Check-list after the pairing has been done                                                                                          |
| 1111 | POST_PAIRING_WITH_BAKU_CHECKLIST | Check-list after the pairing has been done using, if applicable, the Baku Acceleration Method                                       |
| 1200 | CHECK_TOURNAMENT                 | Check the correctness of a tournament                                                                                               |
| 1210 | CHECK_ONE_ROUND                  | Check the correctness of a single round of a tournament. Note: This operation is not possible using JaVaFo as a stand-alone program |
| 1300 | RANDOM_GENERATOR                 | Generate a random tournament                                                                                                        |
| 1301 | RANDOM_GENERATOR_WITH_BAKU       | Generate a random tournament using, if applicable, the Baku Acceleration Method                                                     |

Then the parameter list of JaVaFoApi.exec may contain the following parameters in any order (it is their Java type that
identifies them):

#### Input

For RTG operations it is optional. Mandatory for all other operations.

- **InputStream**.
  It is the stream through which to read the TRF(x) of the tournament to be processed (a model for RTG operations).

- **String**.

  Meaningful only if there is no InputStream parameter (otherwise it is ignored). It represents the TRF(x) of the
  tournament to be processed (a model for RTG operations).

- **Properties**.

  Meaningful only for RTG operations if there are neither InputStream nor String parameters (otherwise it is ignored).

  It represents a collection of properties to be used during the generation of the random tournaments (see Reducing
  randomness by way of a (RTG) configuration file for the description of the usable properties).

  In addition to the previous list, also FirstSeed is a usable property, and represents the seed for the
  random-generator
  used by the RTG (the same seed will re-produce the same TRF as long as the same build of javafo.jar is used).

#### Output

Optional OutputStream parameter through which the result of the requested operation is returned. If missing, the result of
the operation will be returned in a String type through the exec call 
(which is null when there is an OutputStream parameter).

#### Extra

Meaningful only for the `CHECK_ONE_ROUND` operation (otherwise it is ignored). 
It is an Integer (or an int) representing the round to be checked.

### Examples

```java
String out = JaVaFoApi.exec(1301, inputStreamTRF, 6);
```

Return into out the TRF of a random-generated-tournament with the Baku Acceleration Method, using a model tournament
read thru the inputStreamTRF input-stream. The actual parameter "6" is ignored.

```java
String out = JaVaFoApi.exec(1210, 6, new FileInputStream("C:\\jvfck\\trnXDW.trf"));
```

Return into out the result of the verification of the 6th round of the tournament 
described by the TRF file `C:\jvfck\trnXDW.trf`.

```java
String out = JaVaFoApi.exec(1110, outputStream, trf);
```

Return through the outputStream output-stream the post-pairing check-list for the tournament of which the TRF is contained
in the trf string. In out, null is returned.

Note: it is possible to use the above operation to retrieve the pairing of the round, but it is not a very practical
way to do it.

```java
JaVaFoApi.exec(1000, "Hello World", inputStreamTRF, outputStream);
```

Return through the outputStream output-stream the pairings for the tournament of which the TRF has been read through the
inputStreamTRF input-stream. The string "Hello World" is ignored.

```java
Properties cfg = new Properties();
cfg.setProperty("PlayersNumber", 289);
cfg.setProperty("RoundsNumber", 9);
JaVaFoApi.exec(1300, cfg, new FileOutputStream("C:\\jvfgen\\trP289R9.trf"));
```

Put into the file c:\jvfgen\trP289R9.trf the TRF of a random-generated-tournament with 289 players and 9 rounds.

---
[1] The idea of alphabetic codes comes from the author of the TRF, Christian Krause, chairman of the Systems of Pairings
and Programs (SPP) FIDE Commission

[2] A property-file is a file where empty lines have no meaning and lines introduced by the symbol # contain a comment.

The meaningful lines have the format PropertyName=PropertyValue, which assigns to the named property (or parameter) the
value specified by PropertyValue.

### TRF(x) Sample Contents
```
012 XX Open Internacional de Gros
022 Donostia
032 ESP
042 24/09/2010
052 02/10/2010
062 52
072 41
082 0
092 Individual: Swiss-System
102 IA Mikel Larreategi Arana (22232540)
112 
122 moves/time, increment
XXR 9
001    1 m  g Mirzoev Azer                      2527 AZE    13400304 1978        4.0    1    26 w 1    13 b 1     8 w 1     4 b 1          
001    2 m  m Argandona Riveiro Inigo           2408 ESP     2212072 1981        4.0    2    27 b 1    15 w 1     7 b 1     5 w 1          
001    3 m  f Hernandez Elvis                   2282 DOM     6400353 1981        3.5    3    29 w 1    14 b 1    10 w 1     6 b =          
001    4 m    Guijarro Galan Jose Luis          2222 ESP    32004478 1972        3.0    5    30 b 1    17 w 1     9 b 1     1 w 0          
001    5 m    Sanz Perez Eduardo                2155 ESP     2207460 1976        3.0    6    31 w 1    16 b 1    12 w 1     2 b 0          
001    6 m    Gorrochategui Torres, Eugenio        0 ESP           0 1950        3.5    4    32 b 1    21 w 1    11 b 1     3 w =          
001    7 m    Lakunza Oyarbide Juan Carlos      2102 ESP     2201836 1961        3.0    7    33 w 1    20 b 1     2 w 0    18 b 1          
001    8 m    Ladron De Guevara Galar Fco J     2087 ESP     2265745 1961        2.0   20    34 b 1    23 w 1     1 b 0    21 w -          
001    9 m    Alvarado Fernandez Mikel          2010 ESP     2276658 1974        2.0   22    35 w 1    22 b 1     4 w 0    13 b 0          
001   10 m    Darrigues Alain                   1996 FRA      634603 1950        2.0   29    36 b 1    25 w 1     3 b 0    23 w 0          
001   11 m    Maruejols Claude                  1990 FRA      611760 1948        3.0   10    37 w 1    24 b 1     6 w 0    22 b 1          
001   12 m    Segovia Sanchez Antonio           1983 ESP     2223597 1976        3.0    9    38 b 1    52 w 1     5 b 0    45 w 1          
001   13 m    Rodriguez Cabrera Fco. Javier     1973 ESP     2298287 1971        3.0    8    39 w +     1 w 0    25 b 1     9 w 1          
001   14 m    Riesco Lecuona Juan Manuel        1931 ESP     2268922 1959        2.0   24    40 b 1     3 w 0  0000 - H    26 b =          
001   15 m    Fernandez Zubitur Jokin           1907 ESP    22222316 1990        2.0   23    41 w 1     2 b 0    18 w 0    49 b 1          
001   16 m    Bello Castano Diego               1893 ESP     2211831 1979        2.5   13    42 b +     5 w 0  0000 - H    27 w 1          
001   17 m    Fuente San Sebastian Ernesto      1880 ESP     2265109 1964        2.5   14    43 w 1     4 b 0    26 w =    31 b 1          
001   18 m    Tung Siu Hung                     1875 ESP     2265346 1946        2.5   15    44 b =    19 w 1    15 b 1     7 w 0          
001   19 m    Zubia Aramburu Mikel              1834 ESP     2226960 1955        2.5   16    45 w =    18 b 0    49 w 1    32 b 1          
001   20 m    Izagirre Alsua Aritz              1823 ESP     2294001 1975        1.5   32    46 b +     7 w 0    34 b =    35 w 0          
001   21 m    Izquierdo Arruferia Joseba        1807 ESP    22202722 1989        3.0   12    47 w 1     6 b 0    41 w 1     8 b +          
001   22 m    Markina Amutxastegi Fernando      1797 ESP     2265583 1956        2.0   26    48 b 1     9 w 0    46 b 1    11 w 0  0000 - Z
001   23 w    Iglesias Gonzalez Veronica        1790 ESP     2290405 1985        3.0   11    49 w 1     8 b 0    50 w 1    10 b 1          
001   24 m    Espin Laborde Jesus Maria         1777 ESP     2280264 1956        2.5   17    50 b 1    11 w 0    52 b =    34 w 1          
001   25 m    Mohseni Neik                      1747 AFG    11700360 1985        1.0   44    51 w 1    10 b 0    13 w 0    52 b 0          
001   26 m    Orube Bona Andoni                 1743 ESP    22227687 1955        2.0   21     1 b 0    38 w 1    17 b =    14 w =          
001   27 m    Lazkano Gaite Tomas               1736 ESP     2278987 1963        1.5   31     2 w 0    37 b =    43 w 1    16 b 0          
001   28 m    Moreno Romero Ernesto             1728 ESP     2298180 1956        0.0   52  0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z
001   29 m    Boo Aldanondo Alex                1679 ESP     2291975 1978        2.0   25     3 b 0    40 w 1  0000 - H  0000 - H          
001   30 m    Santos Martinez Juan Maria           0 ESP    32039387 1974        1.5   35     4 w 0    41 b 0    40 w 1  0000 - H          
001   31 m    Botaya Helios                        0 FRA    20635842 1952        1.5   33     5 b 0    42 w =    44 b 1    17 w 0          
001   32 m    Aizpurua Zufiria Inaki            1638 ESP     2269791 1934        1.5   36     6 w 0    43 b =    42 b 1    19 w 0          
001   33 m    Sanchez Merino Christian          1624 ESP    22244913 1996        1.5   38     7 b 0    46 w 0    48 b 1    39 w =          
001   34 m    Martikorena Endara Patxi          1619 ESP     2278251             1.5   41     8 w 0    47 b 1    20 w =    24 b 0          
001   35 w    Gonzalez Pueyo Patricia           1605 ESP     2211890 1962        2.5   18     9 b 0    48 w 1  0000 - H    20 b 1          
001   36 m    Moreiro Sanchez Silvestre         1600 ESP     2278294 1939        0.5   50    10 w 0    49 b 0  0000 - H    44 w 0          
001   37 m    Lopez Zorzano Alex                   0 ESP    32043740 1989        2.0   28    11 b 0    27 w =    51 b 1  0000 - H          
001   38 m    Garcia Nestar Daniel              1574 ESP    22200266 1985        1.5   42    12 w 0    26 b 0  0000 - H    47 w 1          
001   39 m    Portugal Sarasola Martin          1574 ESP    22235558 1947        1.5   39    13 b -  0000 - H  0000 - H    33 b =          
001   40 m    Navarro Armendariz Jose Leon         0 ESP    22255826 1928        1.0   47    14 w 0    29 b 0    30 b 0    51 w 1          
001   41 m    Arraiza Gozalo Pablo                 0 ESP    32014228 1991        1.5   34    15 b 0    30 w 1    21 b 0  0000 - H          
001   42 m    Abalia Patino Aritz                  0 ESP    22284605 1997        1.5   37    16 w -    31 b =    32 w 0    43 b 1          
001   43 m    Gallas Gautier                    1542 FRA    26015927 2001        1.0   48    17 b 0    32 w =    27 b 0    42 w 0  0000 - H
001   44 m    Berdote Alonso Carlos             1508 ESP    32008414 1994        1.5   40    18 w =    45 b 0    31 w 0    36 b 1          
001   45 m    Irulegi Garmendia Jose Antoni     1503 ESP    22224696 1937        2.0   27    19 b =    44 w 1  0000 - H    12 b 0          
001   46 m    Jauregi Barandiaran Jon              0 ESP    22273638 1996        2.0   30    20 w -    33 b 1    22 w 0    50 b 1          
001   47 m    Azpiroz Dorronsoro Imanol         1480 ESP    22218890 1986        0.5   49    21 b 0    34 w 0  0000 - H    38 b 0          
001   48 m    Otegui Piquer Juan Jose           1471 ESP    32008449 1943        1.0   43    22 w 0    35 b 0    33 w 0  0000 - U          
001   49 m    Elduayen Echave Ramon             1455 ESP    32041292 1998        1.0   46    23 b 0    36 w 1    19 b 0    15 w 0          
001   50 m    Goienetxea Yarza Jesus               0 ESP    22235507 1952        1.0   45    24 w 0  0000 - U    23 b 0    46 w 0          
001   51 m    Garcia De Madinabeitia Jose L        0 ESP     2211882 1931        0.5   51    25 b 0  0000 - H    37 w 0    40 b 0          
001   52 m    Darbinyan Vigen                      0 ESP    32022972 1978        2.5   19  0000 - U    12 b 0    24 w =    25 w 1          
```


### TRF(x) Acceleration Sample Contents
```
012 4° Open Capo D' Orso 2012
022 Palau (SS)
032 ITA
042 2012-02-06
052 2012-09-06
062 84
072 73
082 0
092 IndividualDutch FIDE (JaVaFo)
102 Franco De Sio
112 Roberto Ricca
112 Costantino Biello
122 
132
001    1 M  f Seletsky Grigory                  2338 UKR    14112191 1978        7.5    1    22 w 1    10 b 1     8 w 1     6 b 1    16 w 1     3 b 1     9 b =    11 w =    14 b =   
001    2 M  m Glienke Manfred Dr.               2314 GER     4603702 1954        5.5   13    23 b 1     9 w =    16 w =    19 b =    32 w 1    10 b =     8 w =    22 b 1     5 w 0   
001    3 M  m Messa Roberto                     2305 ITA      800457 1957        5.5   12    24 w 1    14 b 0    45 w 1    18 w 1    15 b 1     1 w 0    46 b 1     4 b =     9 w 0   
001    4 M    Esposito Luca                     2263 ITA      805726 1969        6.0    8    25 b +    11 w =    18 b =    27 w 1    14 b =    13 w =    15 b =     3 w =     7 b 1   
001    5 M    Luciani Carlo                     2222 ITA      800627 1954        6.0    9    26 w 1    16 b 0    47 w 1    28 b 1     9 w 0    22 b 1    12 w 1    14 w 0     2 b 1   
001    6 M  m Ljubisavljevic Zivojin Z          2210 SRB      902896 1941        6.5    3    27 b 1    13 w 1    14 b =     1 w 0    34 b 1    19 w 1     7 b 1     9 w =    11 b =   
001    7 M    Sprotte Norbert                   2180 GER     4607015 1950        5.0   23    28 w 1    20 b =    39 w 1    12 b =    10 w 0    30 b 1     6 w 0    36 b 1     4 w 0   
001    8 M    Baldazzi Stefano                  2178 ITA      803189 1965        5.5   11    29 b 1    15 w 1     1 b 0    48 w 1    17 b 1     9 w 0     2 b =    19 w 1    13 b 0   
001    9 M  f Gromovs Sergejs                   2162 ITA      811394 1965        7.0    2    30 w 1     2 b =    20 w 1    14 w =     5 b 1     8 b 1     1 w =     6 b =     3 b 1   
001   10 M    Galassi Federico                  2160 ITA      818267 1976        5.5   14    31 b 1     1 w 0    36 b 1    54 w 1     7 b 1     2 w =    11 b 0    17 w 0    12 w 1   
001   11 M    Vallifuoco Giovanni               2155 ITA      800783 1954        6.5    5    32 w 1     4 b =    41 w 1    16 b 0    12 w 1    20 b 1    10 w 1     1 b =     6 w =   
001   12 M    Ori Marco                         2127 ITA      805050 1964        5.0   24    33 b =    40 w 1    48 b 1     7 w =    11 b 0    29 w 1     5 b 0    23 w 1    10 b 0   
001   13 M    Cardili Mariano                   2118 ITA      816000 1961        6.5    6    34 w 1     6 b 0    51 w 1    32 b =    39 w 1     4 b =    16 w =    68 b 1     8 w 1   
001   14 M    Danieli Enrico                    2093 ITA      806064 1968        6.5    4    35 b 1     3 w 1     6 w =     9 b =     4 w =    16 b =    20 w 1     5 b 1     1 w = 
001   15 M    Neri Enzo                         2068 ITA      814660 1962        5.5   15    37 w 1     8 b 0    55 w 1    45 b 1     3 w 0    67 b 1     4 w =    16 b 0    32 w 1 
001   16 M    Peat Matthew                      2064 ENG     2401584 1973        6.0    7    38 b 1     5 w 1     2 b =    11 w 1     1 b 0    14 w =    13 b =    15 w 1    17 b = 
001   17 M    Vita Fabrizio                     2062 ITA      807796 1967        6.0   10    39 w 0    43 b 1    57 w 1    60 b 1     8 w 0    45 b =    40 w 1    10 b 1    16 w = 
001   18 M    Messina Giuseppe Ottavio          2048 ITA      816477 1971        5.5   18    40 b =    33 w 1     4 w =     3 b 0    36 b 1    46 w 0    44 b =    69 w 1    34 b 1 
001   19 M    Valet Richard                     2038 GER     4610709 1956        5.5   17    41 w =    39 b =    59 w 1     2 w =    44 b 1     6 b 0    45 w 1     8 b 0    52 w 1 
001   20 M    Neubauer Kai                      2035 ITA      846074 1965        4.5   34    42 b 1     7 w =     9 b 0    38 w 1    41 b 1    11 w 0    14 b 0    43 w 0    54 b 1 
001   21 M    Bermingham Tony                   2026 IRL     2504103 1950        5.0   29    43 w =    41 b 0    35 w 1    39 b 0    51 w =    59 b =    38 w =    62 b 1    45 w 1 
001   22 M    Ebert Christian                   2005 AUT     1627155 1957        5.0   22     1 b 0    44 w 1    38 b =    46 w 1    49 b 1     5 w 0    31 b 1     2 w 0    35 b = 
001   23 M    Blonna Michele                    1996 ITA      811327 1976        5.5   21     2 w 0    45 b 0    62 w 1    66 b 1    37 w =    39 b 1    57 w 1    12 b 0    68 w 1 
001   24 M    Sand Rolf Dr.                     1991 GER     4610334 1951        5.0   30     3 b 0    46 w 1    54 b =    44 w =    33 b =    41 w 1    49 b =    34 w =    43 b = 
001   25 F    Turchetta Gianluca                1976 ITA      820512 1968        0.0   83     4 w -     
001   26 M    Egan Colm                         1945 IRL     2501732 1942        5.5   20     5 b 0    48 w 0    65 b 1    55 w 1    54 b 1    68 w =    69 b =    35 w =    46 b 1 
001   27 M    Rosenfeld Imre                    1945 FRA      614521 1939        5.5   19     6 w 0    53 b 1    84 w 1     4 b 0    45 w 0    64 b 1    51 w 1    52 b =    57 w 1 
001   28 M    Marshall Michael                  1929 DEN     1409956 1945        4.5   38     7 b 0    50 w 1    58 b 1     5 w 0    48 b 1    69 w =    68 b 0    44 w =    53 b = 
001   29 M    Daly Patrick                      1928 IRL     2503131 1945        4.5   41     8 w 0    55 b =    37 w =    57 b 1    60 w 1    12 b 0    52 w 0    64 b =    66 w 1 
001   30 M    Zuccarelli Angelo                 1910 ITA      815470 1960        5.5   16     9 b 0    52 w 1    60 b =    56 w 1    69 b =     7 w 0    48 b 1    46 w =    49 b 1 
001   31 M    Mercandelli Claudio               1907 ITA      820709 1964        4.5   43    10 w 0    57 b =    40 w 1    41 b 0    52 w 1    70 b 1    22 w 0    45 b 0    47 w 1 
001   32 M    Graziani Sergio                   1905 ITA      846040 1965        4.5   32    11 b 0    56 w 1    69 b 1    13 w =     2 b 0    44 w =    53 b 1    49 w =    15 b 0 
001   33 M    Pedrinzani Ivano                  1898 ITA      860441 1961        3.5   61    12 w =    18 b 0    43 w 1    49 b =    24 w =    40 b 0    39 w 0    82 b 1    56 w 0 
001   34 M    Perico Gianvittorio               1881 ITA      851027 1964        4.5   36    13 b 0    58 w =    42 b 1    52 w 1     6 w 0    51 b =    56 w 1    24 b =    18 w 0 
001   35 M    Murray Jim G.                     1871 IRL     2501490 1951        5.0   26    14 w 0    59 b =    21 b 0    67 w =    84 b 1    50 w 1    70 w 1    26 b =    22 w = 
001   36 M    Papale Carmelo                    1866 ITA      809764 1955        4.5   39  0000 - Z    63 b 1    10 w 0    59 b 1    18 w 0    54 w 1    67 b 1     7 w 0    50 b = 
001   37 M    Trussardi Giuseppe                1853 ITA      802409 1940        3.5   62    15 b 0    69 w =    29 b =    58 w 1    23 b =    49 w 0    41 b 0    74 w 1    60 b 0 
001   38 M    Schiappacasse Marcello            1849 ITA      808369 1941        5.0   28    16 w 0    82 b 1    22 w =    20 b 0    66 w 1    57 b 0    21 b =    58 w 1    59 b 1 
001   39 M    Dall`ora Gianfranco               1845 ITA      816620 1959        4.0   51    17 b 1    19 w =     7 b 0    21 w 1    13 b 0    23 w 0    33 b 1    59 w =    69 b 0 
001   40 M    Gozzi Giorgio                     1823 ITA      819468 1958        4.0   54    18 w =    12 b 0    31 b 0    75 w 1    53 b 1    33 w 1    17 b 0  0000 - Z    64 w = 
001   41 M    Stolfa Rodolfo                    1818 ITA      815438 1941        4.0   52    19 b =    21 w 1    11 b 0    31 w 1    20 w 0    24 b 0    37 w 1    57 b 0    65 b = 
001   42 M    Barato Giuseppe                   1810                 120242      3.5   66    20 w 0    84 b =    34 w 0    43 b =    59 w 0    63 b 1    62 w 1    66 b 0    72 w = 
001   43 M    Carbone Maurizio                  1803 ITA      804045 1946        5.0   27    21 b =    17 w 0    33 b 0    42 w =    65 b 1    60 w 1    59 b =    20 b 1    24 w = 
001   44 M    Coralli Nevio                     1799 ITA      860395 1967        4.5   37    64 w 1    22 b 0    63 w 1    24 b =    19 w 0    32 b =    18 w =    28 b =    51 w = 
001   45 M    Garofalo Sergio                   1788 ITA      812056 1956        4.5   33    65 b 1    23 w 1     3 b 0    15 w 0    27 b 1    17 w =    19 b 0    31 w 1    21 b 0 
001   46 M    Rodriguez Jean-Pierre             1780 FRA      617423 1937        4.5   35    66 w 1    24 b 0    68 w 1    22 b 0    56 w 1    18 b 1     3 w 0    30 b =    26 w 0 
001   47 M    Pieri Enzo                        1775 ITA      815373 1958        3.5   64    67 b =    81 w 1     5 b 0    69 w 0    70 b 0    65 w 1    58 b =    60 w =    31 b 0 
001   48 M    Bellafiore Enzo                   1774 ITA      841722 1955        4.0   53    68 w 1    26 b 1    12 w 0     8 b 0    28 w 0    82 b 1    30 w 0    70 b 1    55 w 0 
001   49 M    Squarci Franco                    1768 ITA      809110 1946        4.5   47    69 b 0    71 w 1    78 b 1    33 w =    22 w 0    37 b 1    24 w =    32 b =    30 w 0 
001   50 M    Sigillo Carmelo                   1761 ITA      837695 1957        4.5   48    70 w 1    28 b 0    66 w 0    72 b 1    64 w =    35 b 0    82 w =    63 b 1    36 w = 
001   51 M    Sorcinelli Andrea                 1756 ITA      858420 1972        4.5   44    71 b =    67 w 1    13 b 0    84 w =    21 b =    34 w =    27 b 0    76 w 1    44 b = 
001   52 M    Engelbrecht Kevin P               1754 ENG      419184 1965        4.5   45    72 w 1    30 b 0    81 w 1    34 b 0    31 b 0    76 w 1    29 b 1    27 w =    19 b 0 
001   53 M    Badano Fabio                      1727 ITA      822531 1970        4.5   46    73 b 1    27 w 0    83 b 1  0000 - Z    40 w 0    66 b 1    32 w 0    72 b 1    28 w = 
001   54 M    Marano Daniele                    1727 ITA      828491 1966        3.5   63    74 w =    80 b 1    24 w =    10 b 0    26 w 0    36 b 0    64 w =    75 b 1    20 w 0 
001   55 M    Cautiero Pietro                   1711 ITA      864064 1957        5.0   31    75 b 1    29 w =    15 b 0    26 b 0    67 w 0    71 w 1    76 b =    73 w 1    48 b 1 
001   56 M    Di Dario Dario                    1707 ITA      846015 1961        4.5   50    76 w 1    32 b 0    82 w 1    30 b 0    46 b 0    84 w 1    34 b 0    67 w =    33 b 1 
001   57 M    Vanzan Alessandro                 1706 ITA      820032 1944        4.5   40    77 b 1    31 w =    17 b 0    29 w 0    73 b 1    38 w 1    23 b 0    41 w 1    27 b 0 
001   58 M    Ferraro Pelle Giovan              1700                 240660      3.5   69    78 w 1    34 b =    28 w 0    37 b 0    68 w 0    72 b 1    47 w =    38 b 0    82 w = 
001   59 M    Colas Yves                        1691 FRA      634573 1940        4.0   55    79 b 1    35 w =    19 b 0    36 w 0    42 b 1    21 w =    43 w =    39 b =    38 w 0 
001   60 M    Lynch Peter J.                    1691 IRL     2501252 1947        4.5   49    80 w =    74 b 1    30 w =    17 w 0    29 b 0    43 b 0    61 w 1    47 b =    37 w 1 
001   61 M    Denozza Mauro                     1664 ITA      801950 1954        3.5   76    81 b 0    73 w =    75 b 0    74 w 1    76 b 0    83 w 1    60 b 0    65 w 0    78 w 1 
001   62 M    Dessy Eugenio                     1662 ITA      835323 1965        3.5   72    82 w 0    72 b 1    23 b 0    73 w 0    80 w 1    78 b 1    42 b 0    21 w 0    67 b = 
001   63 M    Casu Marco                        1655 ITA      806692 1963        4.0   59    83 b 1    36 w 0    44 b 0    76 w =    71 b =    42 w 0    84 b 1    50 w 0    73 b 1 
001   64 F    Sand Rosemarie                    1650 GER    12925683 1955        4.0   58    44 b 0    75 w =    73 b =    78 w 1    50 b =    27 w 0    54 b =    29 w =    40 b = 
001   65 M    Fragni Enrico                     1647 ITA      817805 1941        4.0   60    45 w 0    76 b 1    26 w 0    80 b 1    43 w 0    47 b 0    78 w =    61 b 1    41 w = 
001   66 M    Bakkes Frits                      1640 NED             220350      4.0   57    46 b 0    77 w 1    50 b 1    23 w 0    38 b 0    53 w 0    74 b 1    42 w 1    29 b 0 
001   67 M    Lettington David                  1620 ENG             030374      4.0   56    47 w =    51 b 0    74 w 1    35 b =    55 b 1    15 w 0    36 w 0    56 b =    62 w = 
001   68 M    Tamminga Meile                    1600 NED             090756      4.5   42    48 b 0    79 w 1    46 b 0    83 w 1    58 b 1    26 b =    28 w 1    13 w 0    23 b 0 
001   69 M    Neri Luca                         1592 ITA      860433 1997        5.0   25    49 w 1    37 b =    32 w 0    47 b 1    30 w =    28 b =    26 w =    18 b 0    39 w 1 
001   70 M    Passalacqua Tullio                1585 ITA      889105 1961        3.5   70    50 b 0    83 w 0    77 b 1    81 b 1    47 w 1    31 w 0    35 b 0    48 w 0    84 b = 
001   71 M    Menato Nazario                    1551 ITA      801585 1940        3.5   73    51 w =    49 b 0    80 w =    82 b =    63 w =    55 b 0    75 w 0    78 b 1    83 w = 
001   72 M    Murtas Paolo                      1518 ITA      851833 1988        3.5   71    52 b 0    62 w 0    79 b 1    50 w 0    83 b 1    58 w 0    81 b 1    53 w 0    42 b = 
001   73 M    Bonino Silvano                    1514 ITA      872660 1940        3.0   77    53 w 0    61 b =    64 w =    62 b 1    57 w 0    74 b 0    80 w 1    55 b 0    63 w 0 
001   74 M    Levier Jean-francois              1502 FRA    20601646 1931        3.5   75    54 b =    60 w 0    67 b 0    61 b 0  0000 - U    73 w 1    66 w 0    37 b 0    80 b 1 
001   75 M    Granata Gregorio                  1500                 310142      3.0   78    55 w 0    64 b =    61 w 1    40 b 0    82 w 0    80 b 0    71 b 1    54 w 0    76 b = 
001   76 M    Casartelli Luciano                1484 ITA      845957 1952        3.5   67    56 b 0    65 w 0  0000 - U    63 b =    61 w 1    52 b 0    55 w =    51 b 0    75 w = 
001   77 M    Capitani Roberto                  1482 ITA      822221 1936        1.0   82    57 w 0    66 b 0    70 w 0  0000 - U   
001   78 M    Pirillo Agostino                  1476 ITA      856258 1945        2.5   79    58 b 0  0000 - U    49 w 0    64 b 0    81 w 1    62 w 0    65 b =    71 w 0    61 b 0 
001   79 F    Carosielli Giovanna               1470                 220176      0.0   84    59 w 0    68 b 0    72 w 0  0000 - Z   
001   80 M    Corbo Giuseppe                    1460                 100365      2.0   80    60 b =    54 w 0    71 b =    65 w 0    62 b 0    75 w 1    73 b 0    84 w 0    74 w 0 
001   81 M    Ghencea Ioan                      1450                 201172      2.0   81    61 w 1    47 b 0    52 b 0    70 w 0    78 b 0  0000 - U    72 w 0    83 b 0   
001   82 M    Uittenbogaard Jan Robert          1440 NED             070651      3.5   68    62 b 1    38 w 0    56 b 0    71 w =    75 b 1    48 w 0    50 b =    33 w 0    58 b = 
001   83 M    Talu Alessandro                   1430                 291275      3.5   74    63 w 0    70 b 1    53 w 0    68 b 0    72 w 0    61 b 0  0000 - U    81 w 1    71 b = 
001   84 F    Karsenty Madeleine                1380 FRA      605654 1939        3.5   65  0000 - U    42 w =    27 b 0    51 b =    35 w 0    56 b 0    63 w 0    80 b 1    70 w = 
XXA    1  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    2  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    3  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    4  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    5  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    6  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    7  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    8  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA    9  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   10  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   11  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   12  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   13  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   14  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   15  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   16  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   17  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   18  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   19  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   20  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   21  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   22  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   23  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   24  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   25  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   26  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   27  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   28  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   29  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   30  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   31  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   32  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   33  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   34  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   35  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   36  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   37  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   38  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   39  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   40  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   41  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   42  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   43  1.0  1.0  1.0  1.0  1.0  1.0  1.0
XXA   44  0.0  0.0  0.0  0.5  1.0  1.0  1.0
XXA   45  0.0  0.0  0.5  1.0  1.0  1.0  1.0
XXA   46  0.0  0.0  0.0  0.5  0.5  1.0  1.0
XXA   47  0.0  0.0  0.5  0.5  0.5  0.5  0.5
XXA   48  0.0  0.0  0.5  1.0  1.0  1.0  1.0
XXA   49  0.0  0.0  0.0  0.5  1.0  1.0  1.0
XXA   50  0.0  0.0  0.0  0.0  0.0  0.5  0.5
XXA   51  0.0  0.0  0.5  0.5  0.5  1.0  1.0
XXA   52  0.0  0.0  0.0  0.5  0.5  0.5  1.0
XXA   53  0.0  0.0  0.0  0.5  0.5  0.5  1.0
XXA   54  0.0  0.0  0.5  1.0  1.0  1.0  1.0
XXA   55  0.0  0.0  0.5  0.5  0.5  0.5  0.5
XXA   56  0.0  0.0  0.0  0.5  0.5  0.5  1.0
XXA   57  0.0  0.0  0.5  0.5  0.5  1.0  1.0
XXA   58  0.0  0.0  0.5  0.5  0.5  0.5  0.5
XXA   59  0.0  0.0  0.5  0.5  0.5  1.0  1.0
XXA   60  0.0  0.0  0.5  1.0  1.0  1.0  1.0
XXA   61  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   62  0.0  0.0  0.0  0.0  0.0  0.0  0.5
XXA   63  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   64  0.0  0.0  0.0  0.0  0.0  0.5  0.5
XXA   65  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   66  0.0  0.0  0.0  0.5  0.5  0.5  0.5
XXA   67  0.0  0.0  0.0  0.0  0.0  1.0  1.0
XXA   68  0.0  0.0  0.0  0.0  0.0  1.0  1.0
XXA   69  0.0  0.0  0.5  0.5  1.0  1.0  1.0
XXA   70  0.0  0.0  0.0  0.0  0.0  1.0  1.0
XXA   71  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   72  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   73  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   74  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   75  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   76  0.0  0.0  0.0  0.0  0.0  0.5  0.5
XXA   77  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   78  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   79  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   80  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   81  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   82  0.0  0.0  0.0  0.0  0.0  0.5  0.5
XXA   83  0.0  0.0  0.0  0.0  0.0  0.0  0.0
XXA   84  0.0  0.0  0.5  0.5  0.5  0.5  0.5
```


### Check-List Sample Contents
```
ID	Pts	 ----	Pref	-1R	-2R	  Cur	G-4	G-3	G-2	G-1	

 1	4.0	 WBWB	 (w)	   	   	 (2W)	 26	 13	  8	  4	
 2	4.0	 BWBW	 (b)	   	   	 (1B)	 27	 15	  7	  5	

 3	3.5	 WBWB	 (w)	   	   	 (4W)	 29	 14	 10	  6	
 6	3.5	 BWBW	 (b)	   	   	 (5B)	 32	 21	 11	  3	

 4	3.0	 BWBW	 (b)	   	   	 (3B)	 30	 17	  9	  1	
 5	3.0	 WBWB	 (w)	   	   	 (6W)	 31	 16	 12	  2	
 7	3.0	 WBWB	 (w)	 ▲ 	   	(13W)	 33	 20	  2	 18	
11	3.0	 WBWB	 (w)	   	   	(21W)	 37	 24	  6	 22	
12	3.0	 BWBW	 (b)	   	   	(23B)	 38	 52	  5	 45	
13	3.0	  WBW	 (B)	   	   	 (7B)	[X]	  1	 25	  9	
21	3.0	  WBW	 (B)	 ▼ 	   	(11B)	[X]	 47	  6	 41	
23	3.0	 WBWB	 (w)	   	   	(12W)	 49	  8	 50	 10	

16	2.5	   WW	 BBB	   	 ▼ 	(19B)	[X]	   	  5	 27	
17	2.5	 WBWB	 (w)	   	   	(52W)	 43	  4	 26	 31	
18	2.5	 BWBW	 (b)	 ▼ 	 ▼ 	(35B)	 44	 19	 15	  7	
19	2.5	 WBWB	 (w)	   	 ▲ 	(16W)	 45	 18	 49	 32	
24	2.5	 BWBW	 (b)	   	   	 (8B)	 50	 11	 52	 34	
35	2.5	  BWB	 (W)	   	 ▼ 	(18W)	   	  9	 48	 20	
52	2.5	  BWW	  B1	 ▼ 	   	(17B)	[X]	 12	 24	 25	

 8	2.0	  BWB	 (W)	 ▼ 	   	(24W)	   	 34	 23	  1	
 9	2.0	 WBWB	 (w)	   	   	(26W)	 35	 22	  4	 13	
10	2.0	 BWBW	 (b)	   	   	(37B)	 36	 25	  3	 23	
14	2.0	  BWB	 (W)	   	 ▼ 	(29W)	   	 40	  3	 26	
15	2.0	 WBWB	 (w)	   	 ▲ 	(45B)	 41	  2	 18	 49	
26	2.0	 BWBW	 (b)	   	   	 (9B)	  1	 38	 17	 14	
29	2.0	   BW	 (b)	 ▼ 	 ▼ 	(14B)	   	   	  3	 40	
37	2.0	  BWB	 (W)	 ▼ 	   	(10W)	   	 11	 27	 51	
45	2.0	  BWB	 (W)	   	 ▼ 	(15W)	   	 19	 44	 12	
46	2.0	  BWB	 (W)	   	   	(20W)	   	 33	 22	 50	

20	1.5	  WBW	 (B)	   	   	(46B)	[X]	  7	 34	 35	
27	1.5	 WBWB	 (w)	   	   	(38W)	  2	 37	 43	 16	
30	1.5	  WBW	 (B)	 ▼ 	   	(34B)	   	  4	 41	 40	
31	1.5	 BWBW	 (b)	   	   	(39B)	  5	 42	 44	 17	
32	1.5	 WBBW	 (b)	   	   	(41B)	  6	 43	 42	 19	
33	1.5	 BWBW	 (b)	   	   	(42B)	  7	 46	 48	 39	
34	1.5	 WBWB	 (w)	   	   	(30W)	  8	 47	 20	 24	
38	1.5	  WBW	 (B)	   	 ▼ 	(27B)	   	 12	 26	 47	
39	1.5	    B	 (W)	   	 ▼ 	(31W)	   	   	   	 33	
41	1.5	  BWB	 (W)	 ▼ 	   	(32W)	   	 15	 30	 21	
42	1.5	  BWB	 (W)	   	   	(33W)	   	 31	 32	 43	
44	1.5	 WBWB	 (w)	   	   	(48W)	 18	 45	 31	 36	

25	1.0	 WBWB	 (w)	 ▲ 	   	(49W)	 51	 10	 13	 52	
40	1.0	 WBBW	 (b)	 ▲ 	   	(50W)	 14	 29	 30	 51	
48	1.0	  WBW	 (B)	 ▼ 	   	(44B)	[X]	 22	 35	 33	
49	1.0	 BWBW	 (b)	   	 ▼ 	(25B)	 23	 36	 19	 15	
50	1.0	  WBW	 (B)	   	   	(40B)	[X]	 24	 23	 46	

36	0.5	  WBW	 (B)	   	 ▼ 	(51B)	   	 10	 49	 44	
47	0.5	  BWB	 (W)	   	 ▼ 	(pab)	   	 21	 34	 38	
51	0.5	  BWB	 (W)	 ▼ 	   	(36W)	   	 25	 37	 40	
```

### Random Tournament Generator Configuration Sample Contents
```
# Properties to setup a random tournament
#****************************************
# PlayersNumber => number of players
PlayersNumber=100

# RoundsNumber  => number of rounds
RoundsNumber=9

# 'Rate' properties
# GenericRate is the probability (in thousands) that for a game 
# (or for a round) the "Generic" event happens
# GenericRate=10 means one game every 100
# GenericRate=0 means that the Generic event will not happen

# ForfeitRate => rate for forfeited games (scheduled but not played)
ForfeitRate=20

# QuickgameRate => rate for games ended in less than one full move
QuickgameRate=5

# ZPBRate => rate for players announcing their absence in a particular round
ZPBRate=10

# HPBRate => rate for players asking for a half-point-bye
HPBRate=30

# FPBRate => rate for players given a full-point-bye
FPBRate=0

# HighestRating => highest possible rating of a player in the tournament
HighestRating=2650

# LowestRating => lowest possible rating of a player in the tournament
LowestRating=1000

# Groups and Separator work in obscure ways
#
# Indicatively, Separator defines how many points the rating of the median
# player is below the medium point between the highest and the lowest ratings.
Separator=100

# Players rating are distributed in a gaussian way around the rating of the
# median player (in the rating limits expressed above).
# The sigma is indicatively given by "(HighestRating - LowestRating) / (Groups + 1)"
# However, Groups is automatically incremented when too many players would pass 
# the HighestRating
Groups=2

# Scoring point system
# WWPoints  => points for win  with White
# BWPoints  => points for win  with Black
# WDPoints  => points for draw with White
# BDPoints  => points for draw with Black
# WLPoints  => points for loss with White
# BLPoints  => points for loss with Black
# ZPBPoints => points for zero-point-bye
# HPBPoints => points for half-point-bye
# FPBPoints => points for full-point-bye
# PABPoints => points for pairing-allocated-bye (PAB)
# FWPoints  => points for forfeit win
# FLPoints  => points for forfeit loss
#
# Below there is the representation of the scoring point system
# used in some Polish youth tournaments:  
# 3 point for win, 2 for draw, 1 for loss, 0 for missing a game.
# No half or full point-byes are allowed. PAB is 2 or 3 points
WWPoints=3.0
BWPoints=3.0
WDPoints=2.0
BDPoints=2.0
WLPoints=1.0
BLPoints=1.0
ZPBPoints=0.0
HPBPoints=0.0
FPBPoints=0.0
PABPoints=2.0
FWPoints=3.0
FLPoints=0.0
```


### Seed=18980522 Tournament Report File Contents
```
012 AutoTest 18980522
022 F31Q12Z13R0H16F1H2692L1700G4S40WW1BW1WD.5BD.5WL0BL0ZP0HP.5FP1PA1FW1FL0
032 FIDE
042 
052 
062 46
072 46
082 0
092 Individual  Swiss Dutch 2017 rules
102 Auto-Generator
122 
132                                                                                                                                                                                            
001    1      Test0001 Player0001               2583                             7.5    3    24 w 1    16 b 1    19 w 1     3 b =     4 w 0    10 b 1     8 w 1     9 b 1     5 w =     6 b =  
001    2      Test0002 Player0002               2559                             5.5   11    25 b =    27 w L    29 b 1    31 w 1    18 b 1     9 w =     5 b 0    16 w 1     6 b =            
001    3      Test0003 Player0003               2537                             8.0    1    26 w 1    17 b =    22 w 1     1 w =     9 b 0    28 b 1    14 w 1     6 b +    10 b 1     5 b 1  
001    4      Test0004 Player0004               2493                             6.5    4    27 b =    25 w 1    11 b =    12 w 1     1 b 1     7 w =     9 b 0    10 w -    15 w 1    18 w 1  
001    5      Test0005 Player0005               2480                             6.5    5    28 w 1    19 b 0    43 w 1    15 b 1     8 w 0    23 b 1     2 w 1     7 b 1     1 b =     3 w 0  
001    6      Test0006 Player0006               2424                             6.0    8    29 b =    31 w 1    18 b 1     9 w =    14 w 1     8 b =     7 b =     3 w -     2 w =     1 w =  
001    7      Test0007 Player0007               2419                             5.5   10    30 w 1    22 b =    23 w 1     8 b =    28 w 1     4 b =     6 w =     5 w 0    11 b =    14 b 0  
001    8      Test0008 Player0008               2409                             5.5    9    31 b =    29 w 1    27 b 1     7 w =     5 b 1     6 w =     1 b 0    18 w 0    17 b 1     9 w 0  
001    9      Test0009 Player0009               2392                             7.5    2    32 w 1    23 b =    17 w 1     6 b =     3 w 1     2 b =     4 w 1     1 w 0    18 b 1     8 b 1  
001   10      Test0010 Player0010               2348                             6.5    6    33 b 1    37 w 1    14 b 0    16 w =    22 b 1     1 w 0    26 b 1     4 b +     3 w 0    11 w 1  
001   11      Test0011 Player0011               2344                             5.5   15    34 w =    21 b 1     4 w =    28 b L    19 w =    27 b =    20 w 1    23 b 1     7 w =    10 b 0  
001   12      Test0012 Player0012               2300                             5.5   19  0000 - H    40 b =    46 w 1     4 b 0    37 w 0    25 w 1    42 b 1  0000 - Z    29 w 1    20 b =  
001   13      Test0013 Player0013               2283                             5.5   20    35 b -    36 b =    28 w 0    32 b 1    38 w 0    34 w 1    44 b 1    37 b =    40 w 1    23 b =  
001   14      Test0014 Player0014               2283                             6.5    7    36 w 1    46 b 1    10 w 1  0000 - Z     6 b 0    17 b +     3 b 0    17 w =    27 b 1     7 w 1  
001   15      Test0015 Player0015               2252                             5.5   17    37 b 0    33 w 1    42 b 1     5 w 0    25 b =    31 w 1    16 b =    27 w =     4 b 0    38 w 1  
001   16      Test0016 Player0016               2221                             5.0   23    38 w 1     1 w 0    37 b 1    10 b =    23 w 0    40 b 1    15 w =     2 b 0    28 w 0    21 b 1  
001   17      Test0017 Player0017               2214                             4.5   25    39 b 1     3 w =     9 b 0    27 w 1    26 b =    14 w -    28 w 1    14 b =     8 w 0    29 b 0  
001   18      Test0018 Player0018               2194                             5.5   12    40 w =    34 b 1     6 w 0    19 b 1     2 w 0    44 b 1    37 w 1     8 b 1     9 w 0     4 b 0  
001   19      Test0019 Player0019               2192                             4.5   28    41 b 1     5 w 1     1 b 0    18 w 0    11 b =    42 w =    27 b 0    31 w 0    44 b =    25 w 1  
001   20      Test0020 Player0020               2192                             5.5   21    42 w L    38 b =    32 w 1    26 b 0    36 b =    35 w 1    11 b 0    45 w 1    24 b 1    12 w =  
001   21      Test0021 Player0021               2184                             4.0   38    43 b =    11 w 0    35 b 0    33 w 0    24 b -    30 w 1    41 b =    36 w 1    45 b 1    16 w 0  
001   22      Test0022 Player0022               2159                             5.5   18    44 w 1     7 w =     3 b 0    45 b 1    10 w 0    38 b 0    29 w 0    33 b 1    37 w 1    26 b 1  
001   23      Test0023 Player0023               2154                             5.5   14    45 b 1     9 w =     7 b 0    35 w 1    16 b 1     5 w 0    38 b =    11 w 0    31 b 1    13 w =  
001   24      Test0024 Player0024               2153                             4.0   33     1 b 0    35 w =    31 b 0    36 w 0    21 w +    43 b 1    45 w 1    40 b =    20 w 0    37 b 0  
001   25      Test0025 Player0025               2152                             4.0   32     2 w =     4 b 0    38 w =    44 b 1    15 w =    12 b 0    40 w 0    41 b =    35 w 1    19 b 0  
001   26      Test0026 Player0026               2141                             4.5   27     3 b 0    39 w 1    44 b =    20 w 1    17 w =    37 b =    10 w 0  0000 - H    38 b =    22 w 0  
001   27      Test0027 Player0027               2120                             5.5   13     4 w =     2 b W     8 w 0    17 b 0    39 b 1    11 w =    19 w 1    15 b =    14 w 0    28 b 1  
001   28      Test0028 Player0028               2112                             5.0   22     5 b 0    41 w 1    13 b 1    11 w W     7 b 0     3 w 0    17 b 0    42 w 1    16 b 1    27 w 0  
001   29      Test0029 Player0029               2105                             5.5   16     6 w =     8 b 0     2 w 0    46 b 1    40 b 0    39 w 1    22 b 1    38 w 1    12 b 0    17 w 1  
001   30      Test0030 Player0030               2086                             3.5   43     7 b 0    44 w 0    45 b -  0000 - Z    32 w =    21 b 0  0000 - U    39 w 1    34 w =    41 b =  
001   31      Test0031 Player0031               2077                             4.5   29     8 w =     6 b 0    24 w 1     2 b 0    43 w 1    15 b 0  0000 - H    19 b 1    23 w 0    40 b =  
001   32      Test0032 Player0032               2074                             4.0   36     9 b 0    45 w =    20 b 0    13 w 0    30 b =    41 w 0    39 b 1    46 w 1    42 b =  0000 - H  
001   33      Test0033 Player0033               2066                             4.0   35    10 w 0    15 b 0    39 w =    21 b 1    44 w 0    46 b =    36 b 1    22 w 0    41 w 1            
001   34      Test0034 Player0034               2050                             4.5   31    11 b =    18 w 0  0000 - Z  0000 - H    46 w =    13 b 0    43 w 1    44 w =    30 b =    42 b 1  
001   35      Test0035 Player0035               2025                             4.0   37    13 w -    24 b =    21 w 1    23 b 0    45 w =    20 b 0    46 w 1  0000 - Z    25 b 0    44 w 1  
001   36      Test0036 Player0036               2015                             4.0   34    14 b 0    13 w =  0000 - Z    24 b 1    20 w =    45 b 0    33 w 0    21 b 0    43 w 1  0000 - U  
001   37      Test0037 Player0037               2006                             5.0   24    15 w 1    10 b 0    16 w 0    41 b 1    12 b 1    26 w =    18 b 0    13 w =    22 b 0    24 w 1  
001   38      Test0038 Player0038               2005                             4.5   26    16 b 0    20 w =    25 b =    42 w =    13 b 1    22 w 1    23 w =    29 b 0    26 w =    15 b 0  
001   39      Test0039 Player0039               1990                             3.5   42    17 w 0    26 b 0    33 b =  0000 - U    27 w 0    29 b 0    32 w 0    30 b 0    46 b 1    45 w 1  
001   40      Test0040 Player0040               1980                             4.5   30    18 b =    12 w =  0000 - Z    43 b =    29 w 1    16 w 0    25 b 1    24 w =    13 b 0    31 w =  
001   41      Test0041 Player0041               1967                             3.5   41    19 w 0    28 b 0  0000 - U    37 w 0    42 b 0    32 b 1    21 w =    25 w =    33 b 0    30 w =  
001   42      Test0042 Player0042               1962                             3.5   39    20 b W  0000 - Z    15 w 0    38 b =    41 w 1    19 b =    12 w 0    28 b 0    32 w =    34 w 0  
001   43      Test0043 Player0043               1938                             3.0   45    21 w =  0000 - H     5 b 0    40 w =    31 b 0    24 w 0    34 b 0  0000 - U    36 b 0    46 w =  
001   44      Test0044 Player0044               1897                             3.5   40    22 b 0    30 b 1    26 w =    25 w 0    33 b 1    18 w 0    13 w 0    34 b =    19 w =    35 b 0  
001   45      Test0045 Player0045               1846                             3.0   44    23 w 0    32 b =    30 w +    22 w 0    35 b =    36 w 1    24 b 0    20 b 0    21 w 0    39 b 0  
001   46      Test0046 Player0046               1839                             2.5   46  0000 - U    14 w 0    12 b 0    29 w 0    34 b =    33 w =    35 b 0    32 b 0    39 w 0    43 b =  
```