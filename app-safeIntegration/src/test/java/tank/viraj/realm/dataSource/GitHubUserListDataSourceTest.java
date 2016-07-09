package tank.viraj.realm.dataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import tank.viraj.realm.dao.GitHubUserDao;
import tank.viraj.realm.model.GitHubUser;
import tank.viraj.realm.retrofit.GitHubApiInterface;
import tank.viraj.realm.util.RxSchedulerConfiguration;

import static org.mockito.Mockito.when;

/**
 * Created by Viraj Tank, 20/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GitHubUserListDataSourceTest {
    @Mock
    GitHubApiInterface gitHubApiInterface;
    @Mock
    GitHubUserDao gitHubUserDao;
    @Mock
    RxSchedulerConfiguration rxSchedulerConfiguration;

    private GitHubUserListDataSource gitHubUserListDataSource;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        gitHubUserListDataSource = new GitHubUserListDataSource(gitHubApiInterface,
                gitHubUserDao, rxSchedulerConfiguration);

        GitHubUser p1 = new GitHubUser(1, "test1", "testName1");
        GitHubUser p2 = new GitHubUser(2, "test2", "testName2");
        List<GitHubUser> gitHubUserList = Arrays.asList(p1, p2);

        when(gitHubUserDao.getGitHubUserList()).thenReturn(gitHubUserList);
        when(gitHubApiInterface.getGitHubUsersList())
                .thenReturn(Observable.just(gitHubUserList));

        when(rxSchedulerConfiguration.getComputationThread()).thenReturn(Schedulers.immediate());
        when(rxSchedulerConfiguration.getComputationThread()).thenReturn(Schedulers.immediate());
    }

    /* get getGitHubUsersList list from Realm */
    @Test
    public void getGitHubUsersFromDaoTest() {
        TestSubscriber<List<GitHubUser>> testSubscriber = new TestSubscriber<>();
        gitHubUserListDataSource.getGitHubUsers(false)
                .subscribe(testSubscriber);

        Assert.assertEquals(1, testSubscriber.getOnNextEvents().size());
        Assert.assertEquals(2, testSubscriber.getOnNextEvents().get(0).size());
        Assert.assertEquals(1, testSubscriber.getOnNextEvents().get(0).get(0).getId());
        Assert.assertEquals("test1", testSubscriber.getOnNextEvents().get(0).get(0).getLogin());
        Assert.assertEquals("testName1", testSubscriber.getOnNextEvents().get(0).get(0).getAvatar_url());
        Assert.assertEquals(2, testSubscriber.getOnNextEvents().get(0).get(1).getId());
        Assert.assertEquals("test2", testSubscriber.getOnNextEvents().get(0).get(1).getLogin());
        Assert.assertEquals("testName2", testSubscriber.getOnNextEvents().get(0).get(1).getAvatar_url());
    }

    /* get getGitHubUsersList list from Retrofit2 and update on Realm */
    @Test
    public void getGitHubUsersFromRetrofitTest() {
        TestSubscriber<List<GitHubUser>> testSubscriber = new TestSubscriber<>();
        gitHubUserListDataSource.getGitHubUsers(true)
                .subscribe(testSubscriber);

        Assert.assertEquals(1, testSubscriber.getOnNextEvents().size());
        Assert.assertEquals(2, testSubscriber.getOnNextEvents().get(0).size());
        Assert.assertEquals(1, testSubscriber.getOnNextEvents().get(0).get(0).getId());
        Assert.assertEquals("test1", testSubscriber.getOnNextEvents().get(0).get(0).getLogin());
        Assert.assertEquals("testName1", testSubscriber.getOnNextEvents().get(0).get(0).getAvatar_url());
        Assert.assertEquals(2, testSubscriber.getOnNextEvents().get(0).get(1).getId());
        Assert.assertEquals("test2", testSubscriber.getOnNextEvents().get(0).get(1).getLogin());
        Assert.assertEquals("testName2", testSubscriber.getOnNextEvents().get(0).get(1).getAvatar_url());
    }
}