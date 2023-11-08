package office.proposal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProposalDto {

    private static final Map<State, List<State>> allowedStateChanges;

    static {
        allowedStateChanges = new HashMap<>();
        allowedStateChanges.put(State.CREATED, Arrays.asList(State.DELETED, State.VERIFIED));
        allowedStateChanges.put(State.VERIFIED, Arrays.asList(State.REJECTED, State.ACCEPTED));
        allowedStateChanges.put(State.ACCEPTED, Arrays.asList(State.REJECTED, State.PUBLISHED));
    }
    public enum State {
        CREATED,
        VERIFIED,
        DELETED,
        REJECTED,
        ACCEPTED,
        PUBLISHED
    }


    private Long id;

    private String state;

    private String name;

    private String content;

    private String createdBy;

    private LocalDateTime createdOn;

    private String modifiedBy;

    private List<ProposalHistoryDto> historyList;

    private String reason;

    private BigInteger publishId;

    public static boolean isStateChangeValid(State oldState, State newState) {
        List<State> availableStateChangesList = allowedStateChanges.get(oldState);
        if (availableStateChangesList == null) {
            return false;
        }
        State isStateAvailable = availableStateChangesList.stream().filter(x -> x.equals(newState)).findFirst().orElse(null);
        return isStateAvailable != null;
    }

}
