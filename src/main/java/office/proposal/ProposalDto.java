package office.proposal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProposalDto {
    public enum State {
        CREATED,
        VERIFIED,
        DELETED,
        REJECTED,
        ACCEPTED,
        PUBLISHED
    }


    private Long id;

    private State state;

    private String name;

    private String content;

    private String createdBy;

    private LocalDateTime createdOn;

    private String modifiedBy;

}
