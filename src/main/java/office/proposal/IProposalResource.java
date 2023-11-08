package office.proposal;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

public interface IProposalResource {
    List<ProposalDto> list(ProposalResource proposalResource) ;

    int listSize(ProposalResource proposalResource);

    ProposalDto getById(long id);

    Response create(ProposalResource proposalResource, UriInfo uriInfo, ProposalDto proposalDto);

    Response update(long id, ProposalDto proposalDto);

    Response changeState(long id, ProposalDto proposalDto);
}
