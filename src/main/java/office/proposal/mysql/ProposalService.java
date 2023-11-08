package office.proposal.mysql;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import office.proposal.ProposalDto;
import office.user.mysql.User;
import java.sql.Timestamp;
import java.util.Date;

@ApplicationScoped
public class ProposalService {
    @Inject
    ProposalRepository proposalRepository;

    @Transactional
    public long create(Proposal proposal) {
        proposal.setState(String.valueOf(ProposalDto.State.CREATED));
        proposal.setCreatedBy(new User(1L, "admin"));
        proposal.setCreatedOn(new Timestamp(new Date().getTime()));
        proposalRepository.persist(proposal);

        return proposal.getId();
    }

}
