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
public class ProposalHistoryDto {

    private Long id;

    private Long operationId;

    private String columnName;

    private String oldValue;

    private String newValue;

    private Long userId;

    private LocalDateTime operationDate;

}
