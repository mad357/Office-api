package office.proposal;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;

public interface IProposalResource {
    List<ProposalDto> list(ProposalResource proposalResource) ;

    int listSize(ProposalResource proposalResource);

    Response create(ProposalResource proposalResource, UriInfo uriInfo, ProposalDto proposalDto);
}
