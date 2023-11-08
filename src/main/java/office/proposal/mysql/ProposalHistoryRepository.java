package office.proposal.mysql;


import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProposalHistoryRepository implements PanacheRepository<ProposalHistory> {
    public long findLastOperationId() {
        ProposalHistory result = find("SELECT ph from ProposalHistory ph ORDER BY operationId DESC").firstResult();
        return result.getOperationId();
    }

}
