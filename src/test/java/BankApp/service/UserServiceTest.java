package test.java.BankApp.service;

import main.BankApp.data.interfaces.UserDao;
import main.BankApp.models.User;
import main.BankApp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userServ = new UserService();
    //dependencies that need to be mocked:
    @Mock
    private UserDao userDao;
    //methods that need to be tested:
    @Test
    public void createUser() {
        User mockUser = new User();
        Mockito.when(userDao.create(mockUser)).thenReturn(mockUser);
        userServ.createUser(mockUser);
        Mockito.verify(userDao, Mockito.times(1)).create(mockUser);
    }
    @Test
    public void findByUsername() {
        User mockUser = new User();
        String username = "mock";
        Mockito.when(userDao.findByUsername(username)).thenReturn(mockUser);
        userServ.findByUsername(username);
        Mockito.verify(userDao, Mockito.times(1)).findByUsername(username);
    }
    @Test
    public void login() {
        String username = "mock";
        String password = "mock";
        User mockUser = new User(username, password);

        Mockito.when(userDao.findByUsername(username)).thenReturn(mockUser);
        Mockito.when(userDao.getUserInfo(mockUser)).thenReturn(mockUser);
        userServ.login(username, password);
        Mockito.verify(userDao, Mockito.times(1)).getUserInfo(mockUser);
    }
}
