package tank.viraj.realm.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tank.viraj.realm.MainApplication;
import tank.viraj.realm.R;
import tank.viraj.realm.model.GitHubUserProfile;
import tank.viraj.realm.presenter.GitHubUserProfilePresenter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Viraj Tank, 18-06-2016.
 */
public class GitHubUserProfileFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {
    @Inject
    GitHubUserProfilePresenter gitHubUserProfilePresenter;

    @BindView(R.id.profile_icon)
    ImageView profileIcon;

    @BindView(R.id.profile_name)
    TextView profileName;

    @BindView(R.id.profile_email)
    TextView profileEmail;

    @BindView(R.id.button_name)
    Button buttonName;

    @BindView(R.id.edit_name)
    EditText editName;

    @BindView(R.id.button_id)
    Button buttonId;

    @BindView(R.id.edit_id)
    EditText editId;

    @BindView(R.id.refresh_profile)
    SwipeRefreshLayout pullToRefreshLayout;

    @BindView(R.id.error_message_rl)
    RelativeLayout errorMessageRl;

    @BindView(R.id.main_rl)
    RelativeLayout mainRl;

    private String login;
    private String avatarUrl;
    private Unbinder unbinder;
    private GitHubUserProfile gitHubUserProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        ((MainApplication) getActivity().getApplication()).getApplicationComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        unbinder = ButterKnife.bind(this, view);

        // pull to refresh
        pullToRefreshLayout.setOnRefreshListener(this);
        pullToRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        pullToRefreshLayout.canChildScrollUp();

        buttonId.setOnClickListener(view1 -> {
            try {
                gitHubUserProfilePresenter.editUserId(login, Integer
                        .parseInt(editId.getText().toString()));
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Enter a valid value", Toast.LENGTH_SHORT).show();
            }
        });

        buttonName.setOnClickListener(view1 -> {
            gitHubUserProfilePresenter.editUserName(login, editName.getText().toString());
        });

        /* bind the view and load data from Realm */
        gitHubUserProfilePresenter.bind(this, login, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startRefreshAnimation() {
        pullToRefreshLayout.post(() -> pullToRefreshLayout.setRefreshing(true));
    }

    public void stopRefreshAnimation() {
        pullToRefreshLayout.post(() -> pullToRefreshLayout.setRefreshing(false));
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        gitHubUserProfilePresenter.unBind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        gitHubUserProfilePresenter.unSubscribe();
        super.onDestroy();
    }

    public void setGitHubUserData(String login, String avatarUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
    }

    public void setData(GitHubUserProfile gitHubUserProfile) {
        this.gitHubUserProfile = gitHubUserProfile;
        loadData();
    }

    public void loadData() {
        if (gitHubUserProfile.getName().contains("default")) {
            errorMessageRl.setVisibility(VISIBLE);
            mainRl.setVisibility(GONE);
        } else {
            errorMessageRl.setVisibility(GONE);
            mainRl.setVisibility(VISIBLE);

            Picasso.with(GitHubUserProfileFragment.this.getActivity())
                    .load(avatarUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(profileIcon);
            profileName.setText(String.format("%s%s", GitHubUserProfileFragment.this.getActivity().getString(R.string.name), gitHubUserProfile.getName()));
            profileEmail.setText(String.format("%s%s", GitHubUserProfileFragment.this.getActivity().getString(R.string.email), gitHubUserProfile.getEmail()));
        }
    }

    public void showSnackBar() {
        Snackbar.make(pullToRefreshLayout, "Error loading data!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", view -> {
                    startRefreshAnimation();
                    gitHubUserProfilePresenter.loadGitHubUserProfile(login, true);
                }).show();
    }

    @Override
    public void onRefresh() {
        /* load fresh data */
        gitHubUserProfilePresenter.loadGitHubUserProfile(login, true);
    }
}