package tk.uditsharma.clientapp.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import tk.uditsharma.clientapp.RegisterActivity;
import tk.uditsharma.clientapp.view.LoginActivity;
import tk.uditsharma.clientapp.view.MapsActivity;
import tk.uditsharma.clientapp.view.UserListActivity;
import tk.uditsharma.clientapp.view.UserProfileActivity;
import tk.uditsharma.clientapp.view.WishListActivity;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract WishListActivity bindWishListActivity();

    @ContributesAndroidInjector
    abstract MapsActivity bindMapsActivity();

    @ContributesAndroidInjector
    abstract UserListActivity bindUserListActivity();

    @ContributesAndroidInjector
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector
    abstract RegisterActivity bindRegisterActivity();

    @ContributesAndroidInjector
    abstract UserProfileActivity bindUserProfileActivity();

}
