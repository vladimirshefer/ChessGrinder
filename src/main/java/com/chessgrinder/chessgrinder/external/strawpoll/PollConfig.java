package com.chessgrinder.chessgrinder.external.strawpoll;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PollConfig {
    /**
     * Unixtime value of when a poll is closed.
     */
    private Integer deadlineAt;

    /**
     * By default, polls are set to allow only one answer. After activating this, you can choose also set multiple_choice_min and multiple_choice_max to define the allowed range of multiple choices.
     */
    private Boolean isMultipleChoice;

    /**
     * The minimum number of options a voter has to choose. A value of 0 means unlimited.
     */
    private Integer multipleChoiceMin;

    /**
     * The maximum number of options a voter has to choose. A value of 0 means unlimited.
     */
    private Integer multipleChoiceMax;

    /**
     * For ranked choice votings, it might be useful to randomize the poll options list to remove a voting bias.
     */
    private Boolean randomizeOptions;

}
