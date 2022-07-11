package test.java.BankApp.service;

import main.BankApp.data.interfaces.AccountDao;
import main.BankApp.models.Account;
import main.BankApp.models.User;
import main.BankApp.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @InjectMocks
    private AccountService accountServ = new AccountService();
    //dependencies that need to be mocked:
    @Mock
    private AccountDao accountDao;

    //methods that need to be tested:
    @Test
    public void createAccount() {
        User mockUser = new User();
        Account mockAccount = new Account();
        String type = "Checking";
        double balance = 300;

        Mockito.when(accountDao.create(mockAccount, type, balance, mockUser)).thenReturn(mockAccount);
        accountServ.createAccount(mockAccount, type, balance, mockUser);
        Mockito.verify(accountDao, Mockito.times(1)).create(mockAccount, type, balance, mockUser);
    }
    @Test
    public void getAccountInfo() {
        Account mockAccount = new Account();
        User mockUser = new User();
        Mockito.when(accountDao.getAccountInfo(mockUser)).thenReturn(mockAccount);
        accountServ.getAccount(mockUser);
        Mockito.verify(accountDao, Mockito.times(1)).getAccountInfo(mockUser);
    }

    @Test
    public void updateBalance() {
        Account mockAccount = new Account();
        double balance = 300;
        String transType = "Deposit";
        double amount = 75;

        Mockito.when(accountDao.updateBalance(mockAccount, balance, transType, amount)).thenReturn(mockAccount);
        accountServ.updateBalance(mockAccount, balance, transType, amount);
        Mockito.verify(accountDao, Mockito.times(1)).updateBalance(mockAccount, balance, transType, amount);
    }
}
