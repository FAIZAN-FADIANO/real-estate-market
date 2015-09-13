package com.stolser.menu;

import static com.stolser.MessageFromProperties.*;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.jpa.User;
import com.stolser.jpa.User.UserType;
import com.stolser.user.LoginBean;

@Named("adminMainMenuBean")
@SessionScoped
public class AdminPanelMainMenu implements Serializable {
	static private final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(AdminPanelMainMenu.class);
	
	private MenuModel adminMainMenu;
	private MenuModel adminMainMenuMobile;
	
	private DefaultSubMenu dashboardSubmenu;
	private DefaultSubMenu usersSubmenu;
	private DefaultSubMenu usersSubmenuMobile;
	private DefaultSubMenu realEstateSubmenu;
	private DefaultSubMenu postsSubmenu;
	private DefaultSubMenu adminPanelSubmenu;
	private DefaultSubMenu frontEndSubmenu;
	
	@Inject
	private LoginBean loginBean;
	
	private DefaultSeparator separator;
	private User.UserType loggedInUserType;

	public AdminPanelMainMenu() {}
	
	@PostConstruct
	private void init() {		
		loggedInUserType = loginBean.getLoggedInUser().getType();
		logger.trace("loggedInUserType = {}", loggedInUserType);
				
		separator = new DefaultSeparator();
		
		assembleDashboardSubmenu();
		assembleUsersSubmenu();
		assembleRealEstateSubmenu();
		assemblePostsSubmenu();
		assembleAdminPanelSubmenu();
		assembleFrontEndSubmenu();
		
		assembleAdminMainMenu();
		assembleadminMainMenuMobile();
	}
	
		public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public MenuModel getAdminMainMenu() {
		return adminMainMenu;
	}

	public MenuModel getAdminMainMenuMobile() {
		return adminMainMenuMobile;
	}

	public void setAdminMainMenuMobile(MenuModel mobileAdminMainMenu) {
		this.adminMainMenuMobile = mobileAdminMainMenu;
	}
	
	private boolean isLoggedInUserSuperAdmin() {
		return loggedInUserType == User.UserType.SUPER_ADMIN;
	}
	
	private boolean isLoggedInUserAdmin() {
		return loggedInUserType == User.UserType.ADMIN;
	}
	
	private boolean isLoggedInUserRealtor() {
		return loggedInUserType == User.UserType.REALTOR;
	}
	
	private void assembleDashboardSubmenu() {
		DefaultSubMenu dashboardSubmenu = new DefaultSubMenu(
				createMessageText("dashboardMenuLabel"));
        dashboardSubmenu.setIcon("fa fa-dashboard");
        DefaultMenuItem homeItem = new DefaultMenuItem(
        		createMessageText("homeMenuLabel"));
        homeItem.setOutcome("/adminPanel/home?faces-redirect=true");
        homeItem.setIcon("fa fa-home");
        
        DefaultMenuItem monitorItem = new DefaultMenuItem(
        		createMessageText("monitorMenuLabel"));
        monitorItem.setOutcome("/adminPanel/adminPanelMonitor?faces-redirect=true");
        monitorItem.setIcon("fa fa-video-camera");
        
        dashboardSubmenu.addElement(homeItem);
        if ( ! isLoggedInUserRealtor()) {
        	dashboardSubmenu.addElement(monitorItem);
		}
        
        this.dashboardSubmenu = dashboardSubmenu;
	}
	
	private void assembleUsersSubmenu() {
		DefaultSubMenu usersSubmenu = new DefaultSubMenu(
				createMessageText("usersMenuLabel"));
		DefaultSubMenu usersSubmenuMobile = new DefaultSubMenu(
				createMessageText("usersMenuLabel"));
        usersSubmenu.setIcon("fa fa-user-plus");
        DefaultSubMenu showUsersSubmenu = new DefaultSubMenu(
        		createMessageText("showUsersMenuLabel"));
        DefaultMenuItem showAllUsersItem = new DefaultMenuItem(
        		createMessageText("showAllUsersMenuLabel"));
        showAllUsersItem.setOutcome("/adminPanel/userListing?userstatus=notdiscarded");
        showAllUsersItem.setIcon("fa fa-users");
        DefaultMenuItem userRecycleBinItem = new DefaultMenuItem(
        		createMessageText("userRecycleBinMenuLabel"));
        userRecycleBinItem.setOutcome("/adminPanel/userListing?userstatus=discarded");
        userRecycleBinItem.setIcon("fa fa-trash");
        
        showUsersSubmenu.addElement(showAllUsersItem);
        if (isLoggedInUserSuperAdmin()) {
        	showUsersSubmenu.addElement(userRecycleBinItem);
		}
        
        usersSubmenu.addElement(showUsersSubmenu);
        
        usersSubmenuMobile.addElement(showAllUsersItem);
        if (isLoggedInUserSuperAdmin()) {
        	usersSubmenuMobile.addElement(userRecycleBinItem);
		}
        usersSubmenuMobile.addElement(separator);
        
        DefaultSubMenu addNewUserSubmenu = new DefaultSubMenu(
        		createMessageText("addNewUserMenuLabel"));
        DefaultMenuItem addNewAdminItem = new DefaultMenuItem(
        		createMessageText("addNewAdminMenuLabel"));
        addNewAdminItem.setOutcome("/adminPanel/addNewUser?usertype=admin");
        addNewAdminItem.setIcon("fa fa-user-plus");
        DefaultMenuItem addNewRealtorItem = new DefaultMenuItem(
        		createMessageText("addNewRealtorMenuLabel"));
        addNewRealtorItem.setOutcome("/adminPanel/addNewUser?usertype=realtor");
        addNewRealtorItem.setIcon("fa fa-user-plus");
        
        addNewUserSubmenu.addElement(addNewAdminItem);
        addNewUserSubmenu.addElement(addNewRealtorItem);
        
        if (isLoggedInUserSuperAdmin()) {
        	usersSubmenu.addElement(addNewUserSubmenu);
        	usersSubmenuMobile.addElement(addNewAdminItem);
        	usersSubmenuMobile.addElement(addNewRealtorItem);
        	usersSubmenuMobile.addElement(separator);
  		}
                
        DefaultMenuItem myProfileItem = new DefaultMenuItem(
        		createMessageText("myProfileMenuLabel"));
        myProfileItem.setOutcome("/adminPanel/myProfile?faces-redirect=true");
        myProfileItem.setIcon("fa fa-male");
        
        usersSubmenu.addElement(myProfileItem);
        
        usersSubmenuMobile.addElement(myProfileItem);
        usersSubmenuMobile.addElement(separator);
        
        DefaultMenuItem logOutItem = new DefaultMenuItem(
        		createMessageText("logOutMenuLabel"));
        logOutItem.setCommand("#{loginBean.adminPanelLogout}");
        logOutItem.setIcon("fa fa-sign-out");
        usersSubmenuMobile.addElement(logOutItem);
        
        this.usersSubmenu = usersSubmenu;
        this.usersSubmenuMobile = usersSubmenuMobile;
	}
	
	private void assembleRealEstateSubmenu() {
		DefaultSubMenu realEstateSubmenu = new DefaultSubMenu(
				createMessageText("realEstateMenuLabel"));
        DefaultMenuItem showAllEstateItem = new DefaultMenuItem(
        		createMessageText("showAllEstateItemsMenuLabel"));
        showAllEstateItem.setIcon("fa fa-university");
        DefaultMenuItem estateRecycleBinItem = new DefaultMenuItem(
        		createMessageText("estateRecycleBinMenuLabel"));
        estateRecycleBinItem.setIcon("fa fa-trash");
        DefaultMenuItem addNewEstateItem = new DefaultMenuItem(
        		createMessageText("addNewEstateMenuLabel"));
        addNewEstateItem.setIcon("fa fa-plus");
        DefaultMenuItem estateReportsItem = new DefaultMenuItem(
        		createMessageText("estateReportsMenuLabel"));
        estateReportsItem.setIcon("fa fa-line-chart");
        
        realEstateSubmenu.addElement(showAllEstateItem);
        realEstateSubmenu.addElement(estateRecycleBinItem);
        if ( !(loggedInUserType == UserType.ADMIN)) {
        	realEstateSubmenu.addElement(addNewEstateItem);
        	realEstateSubmenu.addElement(estateReportsItem);
		}
        
        this.realEstateSubmenu = realEstateSubmenu;
	}
	
	private void assemblePostsSubmenu() {
		DefaultSubMenu postsSubmenu = new DefaultSubMenu(
				createMessageText("postsMenuLabel"));
        DefaultMenuItem showAllPostsItem = new DefaultMenuItem(
        		createMessageText("showAllPostsMenuLabel"));
        showAllPostsItem.setIcon("fa fa-files-o");
        DefaultMenuItem postRecycleBinItem = new DefaultMenuItem(
        		createMessageText("postRecycleBinMenuLabel"));
        postRecycleBinItem.setIcon("fa fa-trash");
        DefaultMenuItem addNewPostItem = new DefaultMenuItem(
        		createMessageText("addNewPostMenuLabel"));
        addNewPostItem.setIcon("fa fa-file-word-o");
        
        DefaultSubMenu postCategoriesSubmenu = new DefaultSubMenu(
        		createMessageText("postCategoriesMenuLabel"));
        DefaultMenuItem allCategoriesItem = new DefaultMenuItem(
        		createMessageText("allPostCategoriesMenuLabel"));
        allCategoriesItem.setIcon("fa fa-list-ol");
        allCategoriesItem.setOutcome("/adminPanel/posts/categories/listingOfAllPostCategories?faces-redirect=true");
        DefaultMenuItem addNewCategoryItem = new DefaultMenuItem(
        		createMessageText("addNewCategoryMenuLabel"));
        addNewCategoryItem.setIcon("fa fa-plus");
        addNewCategoryItem.setOutcome("/adminPanel/posts/categories/createNewPostCategory?faces-redirect=true");
        postCategoriesSubmenu.addElement(allCategoriesItem);
        postCategoriesSubmenu.addElement(addNewCategoryItem);
        
        
        DefaultMenuItem postCommentsItem = new DefaultMenuItem(
        		createMessageText("postCommentsMenuLabel"));
        postCommentsItem.setIcon("fa fa-comment-o");
        
        postsSubmenu.addElement(showAllPostsItem);
        postsSubmenu.addElement(postRecycleBinItem);
        postsSubmenu.addElement(addNewPostItem);
        postsSubmenu.addElement(postCategoriesSubmenu);
        postsSubmenu.addElement(postCommentsItem);
		
		this.postsSubmenu = postsSubmenu;
	}
	
	private void assembleAdminPanelSubmenu() {
		DefaultSubMenu adminPanelSubmenu = new DefaultSubMenu(
				createMessageText("adminPanelMenuLabel"));
        DefaultMenuItem adminPanelSettingsItem = new DefaultMenuItem(
        		createMessageText("adminPanelSettingsMenuLabel"));
        adminPanelSettingsItem.setIcon("fa fa-gears");
        
        adminPanelSubmenu.addElement(adminPanelSettingsItem);
        
        this.adminPanelSubmenu = adminPanelSubmenu;
	}

	private void assembleFrontEndSubmenu() {
		DefaultSubMenu frontEndSubmenu = new DefaultSubMenu(
				createMessageText("frontEndMenuLabel"));
        DefaultSubMenu mainPageSubmenu = new DefaultSubMenu(
        		createMessageText("mainPageMenuLabel"));
        DefaultMenuItem mainPageSliderItem = new DefaultMenuItem(
        		createMessageText("mainPageSliderMenuLabel"));
        mainPageSliderItem.setIcon("fa fa-image");
        DefaultMenuItem mainPageEstateItem = new DefaultMenuItem(
        		createMessageText("mainPageEstateItemsMenuLabel"));
        mainPageEstateItem.setIcon("fa fa-home");
        DefaultMenuItem mainPageBlocksItem = new DefaultMenuItem(
        		createMessageText("mainPageBlocksMenuLabel"));
        mainPageBlocksItem.setIcon("fa fa-align-justify");
        
        mainPageSubmenu.addElement(mainPageSliderItem);
        mainPageSubmenu.addElement(mainPageEstateItem);
        mainPageSubmenu.addElement(mainPageBlocksItem);
        
        DefaultMenuItem frontEndLanguagesItem = new DefaultMenuItem(
        		createMessageText("frontEndLanguagesMenuLabel"));
        frontEndLanguagesItem.setIcon("fa fa-language");
        DefaultMenuItem frontEndFontsItem = new DefaultMenuItem(
        		createMessageText("frontEndFontsMenuLabel"));
        frontEndFontsItem.setIcon("fa fa-font");
        
        frontEndSubmenu.addElement(mainPageSubmenu);
        frontEndSubmenu.addElement(frontEndLanguagesItem);
        frontEndSubmenu.addElement(frontEndFontsItem);
        
        this.frontEndSubmenu = frontEndSubmenu;
	}
	
	private void assembleAdminMainMenu() {
		MenuModel adminMainMenu = new DefaultMenuModel();
		adminMainMenu.addElement(dashboardSubmenu);
        adminMainMenu.addElement(usersSubmenu);
        adminMainMenu.addElement(realEstateSubmenu);
        
        if ( ! isLoggedInUserRealtor()) {
        	adminMainMenu.addElement(postsSubmenu);
        	adminMainMenu.addElement(adminPanelSubmenu);
        	adminMainMenu.addElement(frontEndSubmenu);
        }
        
        this.adminMainMenu = adminMainMenu;
	}
	
	private void assembleadminMainMenuMobile() {
		MenuModel adminMainMenuMobile = new DefaultMenuModel();
		adminMainMenuMobile.addElement(dashboardSubmenu);
        adminMainMenuMobile.addElement(usersSubmenuMobile);
        adminMainMenuMobile.addElement(realEstateSubmenu);
        
        if ( ! isLoggedInUserRealtor()) {
        	adminMainMenuMobile.addElement(postsSubmenu);
        	adminMainMenuMobile.addElement(adminPanelSubmenu);
        	adminMainMenuMobile.addElement(frontEndSubmenu);
		}
        
        this.adminMainMenuMobile = adminMainMenuMobile;
        
	}
}
