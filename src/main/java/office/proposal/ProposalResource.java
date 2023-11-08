package office.proposal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import office.proposal.mysql.ProposalResourceMySQL;

import java.util.List;

@Path("/proposal")
@ApplicationScoped
@Named
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProposalResource {
    @Inject
    IProposalResource proposalResource;

    @Context
    public
    UriInfo info;

    public ProposalResource() {
        proposalResource = new ProposalResourceMySQL();
    }

    @GET
    @Path("/list")
    public List<ProposalDto> list() {
        return proposalResource.list(this);
    }

    @GET
    @Path("/list-size")
    public int listSize() {
        return proposalResource.listSize(this);
    }

    @GET
    @Path("/{id}")
    public ProposalDto getProposal(@PathParam("id") long id) {
        return proposalResource.getById(id);
    }

    @POST
    public Response create(ProposalDto proposalDto, @Context UriInfo uriInfo) {
       return proposalResource.create(this, uriInfo, proposalDto);
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") long id, ProposalDto proposalDto) {
        return proposalResource.update(id, proposalDto);
    }

    @PUT
    @Path("/{id}/change-state")
    public Response changeState(@PathParam("id") long id, ProposalDto proposalDto) {
        return proposalResource.changeState(id, proposalDto);
    }

}
