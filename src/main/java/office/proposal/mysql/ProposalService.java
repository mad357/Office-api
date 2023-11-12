package office.proposal.mysql;

import exceptions.DtoValidationException;
import exceptions.NotAllowedException;
import exceptions.NotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import office.proposal.ProposalDto;
import office.user.mysql.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@ApplicationScoped
public class ProposalService {
    @Inject
    ProposalRepository proposalRepository;

    @Inject
    ProposalHistoryRepository proposalHistoryRepository;

    final ModelMapper modelMapper;

    public ProposalService() {
        modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Proposal, ProposalDto>() {
            @Override
            protected void configure() {
                map(source.getModifiedBy().getName(), destination.getModifiedBy());
                map(source.getCreatedBy().getName(), destination.getCreatedBy());
            }
        });
    }

    @Transactional
    public long create(Proposal proposal) {
        if (proposal.getName() == null || proposal.getName().length() == 0 || proposal.getContent() == null || proposal.getContent().length() == 0) {
            throw new DtoValidationException("not all required fields are fulfilled");
        }
        proposal.setState(String.valueOf(ProposalDto.State.CREATED));
        proposal.setCreatedBy(new User(1L, "admin"));
        proposal.setCreatedOn(new Timestamp(new Date().getTime()));
        proposalRepository.persist(proposal);

        return proposal.getId();
    }

    @Transactional
    public void update(ProposalDto proposalDto) {
        Proposal origin = proposalRepository.findById(proposalDto.getId());
        if (origin == null) {
            throw new NotFoundException("Proposal was not found");
        }
        ProposalDto originDto = modelMapper.map(origin, ProposalDto.class);
        if (!(ProposalDto.State.valueOf(origin.getState()).equals(ProposalDto.State.CREATED)  || ProposalDto.State.valueOf(origin.getState()).equals(ProposalDto.State.VERIFIED) ))
        {
            throw new NotAllowedException("Cannot edit due to state");
        }


        origin.setContent(proposalDto.getContent());
        origin.setName(proposalDto.getName());
        updateMetadata(origin);
        updateHistory(originDto, proposalDto);

        proposalRepository.getEntityManager().merge(origin);
    }

    @Transactional
    public void changeState(ProposalDto proposalDto) {
        Proposal origin = proposalRepository.findById(proposalDto.getId());
        if (origin == null) {
            throw new NotFoundException("Proposal was not found");
        }

        if (!ProposalDto.isStateChangeValid(ProposalDto.State.valueOf(origin.getState()), ProposalDto.State.valueOf(proposalDto.getState()))) {
            throw new NotAllowedException("Proposal cannot be changed from " + origin.getState() + " to " + proposalDto.getState());
        }
        if (proposalDto.getState().equals(ProposalDto.State.PUBLISHED.toString()) || proposalDto.getState().equals(ProposalDto.State.REJECTED.toString())) {
           origin.setReason(proposalDto.getReason());
        }

        updateStateHistory(origin, ProposalDto.State.valueOf(origin.getState()), ProposalDto.State.valueOf(proposalDto.getState()));
        origin.setState(proposalDto.getState());
        if (proposalDto.getState().equals(ProposalDto.State.PUBLISHED.toString())) {
            Timestamp currentTimestamp = new Timestamp(new Date().getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String formattedTimestamp = sdf.format(currentTimestamp);
            origin.setPublishId(new BigInteger(formattedTimestamp));
        }
        updateMetadata(origin);
        proposalRepository.getEntityManager().merge(origin);
    }

    private void updateStateHistory(Proposal origin, ProposalDto.State oldState,  ProposalDto.State newState) {
        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        long operationId = proposalHistoryRepository.findLastOperationId() + 1;

        addRowToHistory(origin.getId(), "STATE", oldState.toString(), newState.toString(), currentTimestamp, operationId);
    }

    private void updateHistory(ProposalDto originDto, ProposalDto currentDto) {
        Timestamp currentTimestamp = new Timestamp(new Date().getTime());
        long operationId = proposalHistoryRepository.findLastOperationId() + 1;

        if (!originDto.getName().equals(currentDto.getName())) {
            addRowToHistory(originDto.getId(), "NAME", originDto.getName(), currentDto.getName(), currentTimestamp, operationId);
        }
        if (!originDto.getContent().equals(currentDto.getContent())) {
            addRowToHistory(originDto.getId(), "CONTENT", originDto.getContent(), currentDto.getContent(), currentTimestamp, operationId);
        }
    }

    private void addRowToHistory(long proposalId, String column, String oldValue, String newValue, Timestamp operationDate, long operationId) {
        ProposalHistory ph = new ProposalHistory();
        Proposal p = new Proposal();
        p.setId(proposalId);
        ph.setProposal(p);
        ph.setOperationId(operationId);
        ph.setColumnName(column);
        ph.setUserId(new User(1L, "admin"));
        ph.setOperationDate(operationDate);
        ph.setOldValue(oldValue);
        ph.setNewValue(newValue);

        proposalHistoryRepository.persist(ph);
    }
    public void updateMetadata(Proposal proposal) {
        proposal.setModifiedBy(new User(1L, "admin"));
        proposal.setModifiedOn(new Timestamp(new Date().getTime()));
    }

}
