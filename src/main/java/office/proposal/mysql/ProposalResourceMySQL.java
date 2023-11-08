package office.proposal.mysql;

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

    public List<ProposalDto> list(ProposalResource proposalResource) {
        List<Proposal> result;
        if (proposalResource.info.getQueryParameters().size() == 0) {
            result = proposalRepository.listAll();
        } else {
            MultivaluedMap<String, String> requestParameters = new MultivaluedHashMap<>(proposalResource.info.getQueryParameters());
            setDefaultParameters(requestParameters);

            StringBuilder query = new StringBuilder ();
            Map<String, Object> queryParams = new HashMap<>();
            addFilters(query, queryParams, requestParameters);
            appendOrderAndPagination(query, requestParameters);

            result = proposalRepository.find(query.toString(), queryParams).page(Page.of( Integer.parseInt(requestParameters.get("page").get(0)),Integer.parseInt(requestParameters.get("limit").get(0)))).list();
        }

        return result
                .stream()
                .map(element -> modelMapper.map(element, ProposalDto.class))
                .collect(Collectors.toList());

    }

    public int listSize(ProposalResource proposalResource) {
        int result;
        if (proposalResource.info.getQueryParameters().size() == 0) {
            result = proposalRepository.listAll().size();
        } else {
            MultivaluedMap<String, String> requestParameters = new MultivaluedHashMap<>(proposalResource.info.getQueryParameters());
            setDefaultParameters(requestParameters);

            StringBuilder query = new StringBuilder ();
            Map<String, Object> nonNullParams = new HashMap<>();
            addFilters(query, nonNullParams, requestParameters);

            result = proposalRepository.find(query.toString(), nonNullParams).list().size();
        }

        return result;
    }

    public Response create(ProposalResource proposalResource, @Context UriInfo uriInfo, ProposalDto proposalDto) {
        var proposal =  modelMapper.map(proposalDto, Proposal.class);
        long id = proposalService.create(proposal);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(String.valueOf(id));
        return Response.created(uriBuilder.build()).build();
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

        if  (query.indexOf("WHERE ") == query.length() - 6) {
            query.delete(query.length() - 7, query.length() - 1);
        }
        else if  (query.indexOf("AND ") == query.length() - 4) {
            query.delete(query.length() - 5, query.length() - 1);
        }


    }

    private void appendOrderAndPagination(StringBuilder query, MultivaluedMap<String, String> requestParameters) {
        query.append(" order by p.");
        query.append(requestParameters.get("order").get(0));
        if (Boolean.parseBoolean(requestParameters.get("ascending").get(0))) {
            query.append(" ASC");
        } else {
            query.append(" DESC");
        }

    }
    private void setDefaultParameters(MultivaluedMap<String, String> requestParameters) {
        if (requestParameters.get("page") == null){
            requestParameters.put("page", new ArrayList<>() {{add("0");}});
        }
        if (requestParameters.get("order") == null){
            requestParameters.put("order", new ArrayList<>() {{add("id");}});
        }
        if (requestParameters.get("limit") == null){
            requestParameters.put("limit", new ArrayList<>() {{add("10");}});
        }
        if (requestParameters.get("ascending") == null){
            requestParameters.put("ascending", new ArrayList<>() {{add("true");}});
        }

    }
}
