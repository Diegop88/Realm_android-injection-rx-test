package tank.viraj.realm.injections;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tank.viraj.realm.adapter.MainAdapter;
import tank.viraj.realm.dataSource.GitHubUserListDataSource;
import tank.viraj.realm.dataSource.GitHubUserProfileDataSource;
import tank.viraj.realm.presenter.GitHubUserListPresenter;
import tank.viraj.realm.presenter.GitHubUserProfilePresenter;
import tank.viraj.realm.retrofit.GitHubApiInterface;
import tank.viraj.realm.util.InternetConnection;
import tank.viraj.realm.util.RxSchedulerConfiguration;

/**
 * Created by Viraj Tank, 18-06-2016.
 */

@Module
public class ApplicationModule {
    private static final String baseUrl = "https://api.github.com/";
    private Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    InternetConnection provideInternetConnection() {
        return new InternetConnection();
    }

    @Provides
    @Singleton
    RxSchedulerConfiguration provideRxSchedulerConfiguration() {
        return new RxSchedulerConfiguration();
    }

    @Provides
    GitHubUserListDataSource provideGitHubUserListDataSource(GitHubApiInterface gitHubApiInterface,
                                                             InternetConnection internetConnection,
                                                             RxSchedulerConfiguration rxSchedulerConfiguration) {
        return new GitHubUserListDataSource(application, gitHubApiInterface,
                internetConnection, rxSchedulerConfiguration);
    }

    @Provides
    GitHubUserProfileDataSource provideGitHubUserProfileDataSource(GitHubApiInterface gitHubApiInterface,
                                                                   InternetConnection internetConnection,
                                                                   RxSchedulerConfiguration rxSchedulerConfiguration) {
        return new GitHubUserProfileDataSource(application, gitHubApiInterface,
                internetConnection, rxSchedulerConfiguration);
    }

    @Provides
    GitHubUserListPresenter provideGitHubUserPresenter(RxSchedulerConfiguration rxSchedulerConfiguration,
                                                       GitHubUserListDataSource gitHubUserListDataSource) {
        return new GitHubUserListPresenter(rxSchedulerConfiguration, gitHubUserListDataSource);
    }

    @Provides
    GitHubUserProfilePresenter provideGitHubUserProfilePresenter(RxSchedulerConfiguration rxSchedulerConfiguration,
                                                                 GitHubUserProfileDataSource gitHubUserProfileDataSource) {
        return new GitHubUserProfilePresenter(rxSchedulerConfiguration, gitHubUserProfileDataSource);
    }

    @Provides
    MainAdapter provideMainAdapter() {
        return new MainAdapter(application.getApplicationContext());
    }

    /* OkHttpclient for retrofit2 */
    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    /* retrofit2 */
    @Provides
    @Singleton
    GitHubApiInterface provideGitHubApiInterface(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(GitHubApiInterface.class);
    }
}