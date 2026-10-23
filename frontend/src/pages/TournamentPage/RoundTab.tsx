import {MatchDto, MatchResult, RoundDto} from "lib/api/dto/TournamentPageData";
import MatchesTable from "pages/TournamentPage/MatchesTable";
import React from "react";
import {Conditional, usePermissionGranted} from "components/Conditional";
import RoundControlButtons from "pages/TournamentPage/RoundControlButtons";

export default function RoundTab(
    {
        tournamentId,
        round,
        submitMatchResult,
        submitRoundFinished,
        deleteRound,
        reopenRound,
        runPairing,
        hideControls = false,
    }: {
        tournamentId: string,
        round: RoundDto,
        submitMatchResult: (match: MatchDto, result: MatchResult | null) => void,
        submitRoundFinished: () => void,
        deleteRound: () => void,
        reopenRound: () => void,
        runPairing: () => void,
        hideControls?: boolean,
    }
) {
    let isMeModerator = usePermissionGranted(tournamentId, "TournamentEntity", "MODERATOR");

    if (!round) {
        return <>Error</>
    }

    return <div>
        <MatchesTable matches={round.matches || []}
                      canEditResults={!round.isFinished && isMeModerator}
                      submitMatchResult={(match, result) => {
                          submitMatchResult(match, result!!);
                      }}
        />
        <Conditional on={isMeModerator && !hideControls}>
            <RoundControlButtons
                hasUnfinishedRounds={!round.isFinished}
                hasFinishedRounds={!!round.isFinished}
                runPairing={runPairing}
                submitRoundFinished={submitRoundFinished}
                reopenRound={reopenRound}
                deleteRound={deleteRound}
            />
        </Conditional>
    </div>
}
