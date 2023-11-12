package office;

import exceptions.DtoValidationException;
import exceptions.NotAllowedException;
import exceptions.NotFoundException;
import jakarta.persistence.EntityManager;
import office.proposal.ProposalDto;
import office.proposal.mysql.Proposal;
import office.proposal.mysql.ProposalHistoryRepository;
import office.proposal.mysql.ProposalRepository;
import office.proposal.mysql.ProposalService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProposalTest {

    @Mock
    ProposalRepository proposalRepository;

    @Mock
    ProposalHistoryRepository proposalHistoryRepository;

    @InjectMocks
    ProposalService proposalService;

    List<Proposal> mockList;

    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockList = new ArrayList<>();
        Proposal p = new Proposal();
        p.setId(1L);
        p.setName("Some proposal");
        p.setContent("Some content");
        p.setState(ProposalDto.State.CREATED.toString());
        mockList.add(p);
        Proposal p2 = new Proposal();
        p2.setId(2L);
        p2.setName("Other proposal");
        p2.setContent("Other content");
        p2.setState(ProposalDto.State.CREATED.toString());
        mockList.add(p2);
        Proposal p3 = new Proposal();
        p3.setId(3L);
        p3.setName("Another proposal");
        p3.setContent("Another content");
        p3.setState(ProposalDto.State.ACCEPTED.toString());
        mockList.add(p3);

        doAnswer((Answer<Object>) invocation -> {
            Proposal p1 = (Proposal) invocation.getArguments()[0];
            p1.setId(-1L);
            return null;
        }).when(proposalRepository).persist(any(Proposal.class));
        when(proposalRepository.findById(any(Long.class)))
                .thenAnswer(
                        (Answer) invocation -> mockList.stream()
                                .filter(x -> x.getId().equals(invocation.getArguments()[0]))
                                .findFirst().orElse(null)
                );
        when(proposalHistoryRepository.findLastOperationId()).thenReturn(10L);
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(proposalRepository.getEntityManager()).thenReturn(entityManager);
    }

    @Test()
    public void changeState(){
        ProposalDto p = new ProposalDto();
        p.setId(4L);
        p.setState("REJECTED");
        Exception exception = assertThrows(NotFoundException.class, () -> proposalService.update(p));
        assertTrue(exception.getMessage().contains("Proposal was not found"));
        p.setId(1L);
        exception = assertThrows(NotAllowedException.class, () -> proposalService.changeState(p));
        assertTrue(exception.getMessage().contains("Proposal cannot be changed from CREATED to REJECTED"));
        p.setState("DELETED");
        proposalService.changeState(p);
    }
    @Test()
    public void updateProposal(){
        ProposalDto p = new ProposalDto();
        p.setId(4L);
        p.setName("Some proposal 2");
        p.setContent("Some content 2");
        Exception exception = assertThrows(NotFoundException.class, () -> proposalService.update(p));
        assertTrue(exception.getMessage().contains("Proposal was not found"));
        p.setId(3L);
        exception = assertThrows(NotAllowedException.class, () -> proposalService.update(p));
        assertTrue(exception.getMessage().contains("Cannot edit due to state"));
        p.setId(2L);
        proposalService.update(p);
    }
    @Test()
    public void createProposal(){
        Proposal p = new Proposal();
        p.setName("Just proposal");
        Exception exception = assertThrows(DtoValidationException.class, () -> proposalService.create(p));
        assertTrue(exception.getMessage().contains("not all required fields are fulfilled"));
        p.setName(null);
        p.setContent("Just content");
        exception = assertThrows(RuntimeException.class, () -> proposalService.create(p));
        assertTrue(exception.getMessage().contains("not all required fields are fulfilled"));
        p.setName("Just proposal");
        long result =  proposalService.create(p);
        assertEquals(-1L, result);
    }
}
