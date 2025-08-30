# JaVaFo, a Chess Pairing Engine

## About

JaVaFo is the name of a program that implements the pairing rules of the Swiss System called "FIDE (Dutch) System",
described in the FIDE Handbook, chapter C.04.3.
JaVaFo is authored and intellectually owned by Roberto Ricca.

## History of the Dutch System

Introduced by Arbiter Geurt Gijssen in the 1990s, the Dutch System became FIDE's primary pairing system. Since 2016, it
has its own handbook section (C.04.3), while other FIDE systems are in section C.04.4.

The Dutch System's rules were initially written like a software algorithm rather than traditional chess rules, as they
were based on the Swiss Master program's implementation. This created confusion between the FIDE handbook rules and
Swiss Master's actual behavior.

Prior to 2011, ambiguous rules in FIDE handbook section C.04.1 led to inconsistent pairings between FIDE-approved
programs. Between 2011-2013, the SPPC rewrote the Dutch rules during Congresses in Krakow, Istanbul and Tallinn to
resolve these issues.

In 2015-2016, at FIDE Congresses in Abu Dhabi and Baku, the SPPC completed revisions to address reported issues and
significantly improved the clarity of the Dutch System rules.

## Relevance

As a member of the SPPC, JaVaFo's author ensures quick compliance with any new rule changes.

While no longer the leading solution since bbpPairings' release in August 2016, JaVaFo remains useful as a reference
implementation. Despite extensive testing against bbpPairings, some issues may still exist.
JaVaFo is still used by several FIDE Endorsed Programs (Section C.04, Appendix A, A.10 - Annex 3).

An Advanced User Manual is available.

The current version of JaVaFo is 2.2 (September 15th, 2018).


## Usage Prerequisites

- JaVaFo is written in Java (there are no relationships between the names Java and JaVaFo - it is just a coincidence).
- JaVaFo requires Java version 7 or higher to run.
- JaVaFo can be used either as a standalone program or as a Java Library.

