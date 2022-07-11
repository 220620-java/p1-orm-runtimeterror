package test.java.BankApp.service;
import static org.junit.jupiter.api.Assertions.*;

import main.BankApp.data.interfaces.TransDao;
import main.BankApp.dataStructure.ArrayList;
import main.BankApp.models.Account;
import main.BankApp.models.Transaction;
import main.BankApp.service.TransService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TransServiceTest {
    @InjectMocks
    private TransService transServ = new TransService();
    //dependencies that need to be mocked:
    @Mock
    private TransDao transDao;

    //methods that need to be tested:
    @Test
    public void createTrans() {
        Account mockAccount = new Account();
        Transaction mockTrans = new Transaction();
        Mockito.when(transDao.create(mockTrans, 100, "Deposit", 15, mockAccount)).thenReturn(mockTrans);
        transServ.createTrans(mockTrans, 100, "Deposit", 15, mockAccount);
        Mockito.verify(transDao, Mockito.times(1)).create(mockTrans, 100, "Deposit", 15, mockAccount);
    }
    @Test
    public void findTransactions() {
        Account mockAccount = new Account();
        ArrayList t = new ArrayList();
        Mockito.when(transDao.findByAccount(mockAccount)).thenReturn(t);
        transServ.findTransactions(mockAccount);
        Mockito.verify(transDao, Mockito.times(1)).findByAccount(mockAccount);
    }
}
