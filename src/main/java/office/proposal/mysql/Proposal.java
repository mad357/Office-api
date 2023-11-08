package office.proposal.mysql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import office.user.mysql.User;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import jakarta.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PROPOSAL")
public class Proposal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROPOSAL_ID")
    private Long id;

    @Column(name = "STATE")
    private String state;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CONTENT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_ID",referencedColumnName = "USER_ID")
    private User createdBy;

    @Column(name = "CREATED_ON")
    private Timestamp createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODIFIED_BY_ID",referencedColumnName = "USER_ID")
    private User modifiedBy;

    @Column(name = "MODIFIED_ON")
    private Timestamp modifiedOn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="proposal")
    private List<ProposalHistory> historyList;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "PUBLISH_ID")
    private BigInteger publishId;

}
