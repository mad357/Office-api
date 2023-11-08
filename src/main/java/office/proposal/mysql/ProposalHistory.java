package office.proposal.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import office.user.mysql.User;

import java.io.Serializable;
import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PROPOSAL_HISTORY")
public class ProposalHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROPOSAL_HISTORY_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROPOSAL_ID",referencedColumnName = "PROPOSAL_ID")
    private Proposal proposal;

    @Column(name = "OPERATION_ID")
    private Long operationId;

    @Column(name = "COLUMN_NAME")
    private String columnName;

    @Column(name = "OLD_VALUE")
    private String oldValue;

    @Column(name = "NEW_VALUE")
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID",referencedColumnName = "USER_ID")
    private User userId;

    @Column(name = "OPERATION_DATE")
    private Timestamp operationDate;

}
