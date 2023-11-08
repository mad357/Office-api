package office.proposal.mysql;

import exceptions.DtoValidationException;
import exceptions.NotFoundException;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import office.proposal.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import util.EnumHelper;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
@Named
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProposalResourceMySQL implements IProposalResource {

    @Inject
    ProposalRepository proposalRepository;

    final ModelMapper modelMapper;

    @Inject
    ProposalService proposalService;


    public ProposalResourceMySQL() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Proposal, ProposalDto>() {
            @Override
            protected void configure() {
                map(source.getModifiedBy().getName(), destination.getModifiedBy());
                map(source.getCreatedBy().getName(), destination.getCreatedBy());
            }
        });
    }

    public ProposalDto getById(long id) {
        Proposal proposal = proposalRepository.findById(id);
        if (proposal == null) {
            throw new NotFoundException("Proposal was not found");
        }

        return modelMapper.map(proposal, ProposalDto.class);
    }

    public List<ProposalDto> list(ProposalResource proposalResource) {
        List<Proposal> result;
        MultivaluedMap<String, String> requestParameters = new MultivaluedHashMap<>(proposalResource.info.getQueryParameters());
        setDefaultParameters(requestParameters);
        StringBuilder query = new StringBuilder ();
        Map<String, Object> queryParams = new HashMap<>();
        addFilters(query, queryParams, requestParameters);

        result = proposalRepository.find(query.toString(), queryParams).page(Page.of( Integer.parseInt(requestParameters.get("page").get(0)),Integer.parseInt(requestParameters.get("limit").get(0)))).list();


        return result
                .stream()
                .map(element -> modelMapper.map(element, ProposalDto.class))
                .collect(Collectors.toList());

    }

    public int listSize(ProposalResource proposalResource) {
        int result;
        MultivaluedMap<String, String> requestParameters = new MultivaluedHashMap<>(proposalResource.info.getQueryParameters());
        setDefaultParameters(requestParameters);
        StringBuilder query = new StringBuilder ();
        Map<String, Object> nonNullParams = new HashMap<>();
        addFilters(query, nonNullParams, requestParameters);
        result = proposalRepository.find(query.toString(), nonNullParams).list().size();

        return result;
    }

    public Response create(ProposalResource proposalResource, @Context UriInfo uriInfo, ProposalDto proposalDto) {
        if (proposalDto == null) {
            throw new DtoValidationException("data not provided");
        }
        var proposal =  modelMapper.map(proposalDto, Proposal.class);
        long id = proposalService.create(proposal);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(String.valueOf(id));
        return Response.created(uriBuilder.build()).build();
    }

    public Response update(long id, ProposalDto proposalDto) {
        if (proposalDto == null || id == 0) {
            throw new DtoValidationException("data not provided");
        }
        if (proposalDto.getName() == null || proposalDto.getName().length() == 0 || proposalDto.getContent() == null || proposalDto.getContent().length() == 0) {
            throw new DtoValidationException("not all required fields are fulfilled");
        }
        proposalDto.setId(id);
        proposalService.update(proposalDto);
        return Response.ok().build();
    }

    public Response changeState(long id, ProposalDto proposalDto) {
        if (proposalDto == null || proposalDto.getState() == null) {
            throw new DtoValidationException("Missing data");
        }
        if  (!EnumHelper.isInEnum(proposalDto.getState(), ProposalDto.State.class)) {
            throw new DtoValidationException("Wrong state");
        }

        if (proposalDto.getState().equals(ProposalDto.State.PUBLISHED.toString()) || proposalDto.getState().equals(ProposalDto.State.REJECTED.toString())) {
           if (proposalDto.getReason() == null || proposalDto.getReason().length() == 0) {
               throw new DtoValidationException("Missing reason for state change");
           }
        }
        proposalDto.setId(id);
        proposalService.changeState(proposalDto);

        return Response.ok().build();
    }

    private void addFilters(StringBuilder query, Map<String, Object> params, MultivaluedMap<String, String> requestParameters) {
        query.append("SELECT DISTINCT p from Proposal p WHERE ");

        if (requestParameters.get("name") != null) {
            query.append(" UPPER(p.name) like :name AND ");
            params.put("name", "%" + requestParameters.get("name").get(0).toUpperCase() +"%");
        }

        if (requestParameters.get("state") != null) {
            query.append(" UPPER(p.state) like :state AND ");
            params.put("state", "%" + requestParameters.get("state").get(0).toUpperCase() +"%");
        }

        query.append(" UPPER(p.state) not like 'DELETED' ");

        if  (query.indexOf("WHERE ") == query.length() - 6) {
            query.delete(query.length() - 7, query.length() - 1);
        }
        else if  (query.indexOf("AND ") == query.length() - 4) {
            query.delete(query.length() - 5, query.length() - 1);
        }


    }

    private void setDefaultParameters(MultivaluedMap<String, String> requestParameters) {
        if (requestParameters.get("page") == null){
            requestParameters.put("page", new ArrayList<>() {{add("0");}});
        }
        if (requestParameters.get("limit") == null){
            requestParameters.put("limit", new ArrayList<>() {{add("10");}});
        }
    }
}
