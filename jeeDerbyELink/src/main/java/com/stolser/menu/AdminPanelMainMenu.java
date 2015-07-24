package com.stolser.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSeparator;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stolser.PropertiesLoader;
import com.stolser.jpa.User;
import com.stolser.jpa.User.UserType;
import com.stolser.user.LoginBean;

@ManagedBean(name="adminMainMenuBean")
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
	
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private PropertiesLoader propLoader;
	private Map<String, Properties> propSystemMap;
	private DefaultSeparator separator;
	private User.UserType loggedInUserType;

	public AdminPanelMainMenu() {}
	
	@PostConstruct
	private void init() {
		
		propSystemMap = propLoader.getPropSystemMap();
		
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

/**
 * Returns appropriate Properties object for current local on the front-end
 * */
	private Properties getSystemProperties() {
		String currentLocal = FacesContext.getCurrentInstance().getViewRoot().getLocale().toString();
		Properties currentProperties = propSystemMap.get(currentLocal);
		return currentProperties;
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
		DefaultSubMenu dashboardSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("dashboardMenuLabel"));
        dashboardSubmenu.setIcon("fa fa-dashboard");
        DefaultMenuItem homeItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("homeMenuLabel"));
        homeItem.setOutcome("/adminPanel/home?faces-redirect=true");
        homeItem.setIcon("fa fa-home");
        
        DefaultMenuItem monitorItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("monitorMenuLabel"));
        monitorItem.setOutcome("/adminPanel/adminPanelMonitor?faces-redirect=true");
        monitorItem.setIcon("fa fa-video-camera");
        
        dashboardSubmenu.addElement(homeItem);
        if ( ! isLoggedInUserRealtor()) {
        	dashboardSubmenu.addElement(monitorItem);
		}
        
        this.dashboardSubmenu = dashboardSubmenu;
	}
	
	private void assembleUsersSubmenu() {
		DefaultSubMenu usersSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("usersMenuLabel"));
		DefaultSubMenu usersSubmenuMobile = new DefaultSubMenu(getSystemProperties()
        		.getProperty("usersMenuLabel"));
        usersSubmenu.setIcon("fa fa-user-plus");
        DefaultSubMenu showUsersSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("showUsersMenuLabel"));
        DefaultMenuItem showAllUsersItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("showAllUsersMenuLabel"));
        showAllUsersItem.setOutcome("/adminPanel/userListing?userstatus=notdiscarded");
        showAllUsersItem.setIcon("fa fa-users");
        DefaultMenuItem userRecycleBinItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("userRecycleBinMenuLabel"));
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
        
        DefaultSubMenu addNewUserSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("addNewUserMenuLabel"));
        DefaultMenuItem addNewAdminItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("addNewAdminMenuLabel"));
        addNewAdminItem.setOutcome("/adminPanel/addNewUser?usertype=admin");
        addNewAdminItem.setIcon("fa fa-user-plus");
        DefaultMenuItem addNewRealtorItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("addNewRealtorMenuLabel"));
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
                
        DefaultMenuItem myProfileItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("myProfileMenuLabel"));
        myProfileItem.setOutcome("/adminPanel/myProfile?faces-redirect=true");
        myProfileItem.setIcon("fa fa-male");
        
        usersSubmenu.addElement(myProfileItem);
        
        usersSubmenuMobile.addElement(myProfileItem);
        usersSubmenuMobile.addElement(separator);
        
        DefaultMenuItem logOutItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("logOutMenuLabel"));
        logOutItem.setCommand("#{loginBean.adminPanelLogout}");
        logOutItem.setIcon("fa fa-sign-out");
        usersSubmenuMobile.addElement(logOutItem);
        
        this.usersSubmenu = usersSubmenu;
        this.usersSubmenuMobile = usersSubmenuMobile;
	}
	
	private void assembleRealEstateSubmenu() {
		DefaultSubMenu realEstateSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("realEstateMenuLabel"));
        DefaultMenuItem showAllEstateItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("showAllEstateItemsMenuLabel"));
        showAllEstateItem.setIcon("fa fa-university");
        DefaultMenuItem estateRecycleBinItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("estateRecycleBinMenuLabel"));
        estateRecycleBinItem.setIcon("fa fa-trash");
        DefaultMenuItem addNewEstateItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("addNewEstateMenuLabel"));
        addNewEstateItem.setIcon("fa fa-plus");
        DefaultMenuItem estateReportsItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("estateReportsMenuLabel"));
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
		DefaultSubMenu postsSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("postsMenuLabel"));
        DefaultMenuItem showAllPostsItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("showAllPostsMenuLabel"));
        showAllPostsItem.setIcon("fa fa-files-o");
        DefaultMenuItem postRecycleBinItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("postRecycleBinMenuLabel"));
        postRecycleBinItem.setIcon("fa fa-trash");
        DefaultMenuItem addNewPostItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("addNewPostMenuLabel"));
        addNewPostItem.setIcon("fa fa-file-word-o");
        DefaultMenuItem postCategoriesItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("postCategoriesMenuLabel"));
        postCategoriesItem.setIcon("fa fa-list-ol");
        DefaultMenuItem postCommentsItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("postCommentsMenuLabel"));
        postCommentsItem.setIcon("fa fa-comment-o");
        
        postsSubmenu.addElement(showAllPostsItem);
        postsSubmenu.addElement(postRecycleBinItem);
        postsSubmenu.addElement(addNewPostItem);
        postsSubmenu.addElement(postCategoriesItem);
        postsSubmenu.addElement(postCommentsItem);
		
		this.postsSubmenu = postsSubmenu;
	}
	
	private void assembleAdminPanelSubmenu() {
		DefaultSubMenu adminPanelSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("adminPanelMenuLabel"));
        DefaultMenuItem adminPanelSettingsItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("adminPanelSettingsMenuLabel"));
        adminPanelSettingsItem.setIcon("fa fa-gears");
        
        adminPanelSubmenu.addElement(adminPanelSettingsItem);
        
        this.adminPanelSubmenu = adminPanelSubmenu;
	}

	private void assembleFrontEndSubmenu() {
		DefaultSubMenu frontEndSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("frontEndMenuLabel"));
        DefaultSubMenu mainPageSubmenu = new DefaultSubMenu(getSystemProperties()
        		.getProperty("mainPageMenuLabel"));
        DefaultMenuItem mainPageSliderItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("mainPageSliderMenuLabel"));
        mainPageSliderItem.setIcon("fa fa-image");
        DefaultMenuItem mainPageEstateItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("mainPageEstateItemsMenuLabel"));
        mainPageEstateItem.setIcon("fa fa-home");
        DefaultMenuItem mainPageBlocksItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("mainPageBlocksMenuLabel"));
        mainPageBlocksItem.setIcon("fa fa-align-justify");
        
        mainPageSubmenu.addElement(mainPageSliderItem);
        mainPageSubmenu.addElement(mainPageEstateItem);
        mainPageSubmenu.addElement(mainPageBlocksItem);
        
        DefaultMenuItem frontEndLanguagesItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("frontEndLanguagesMenuLabel"));
        frontEndLanguagesItem.setIcon("fa fa-language");
        DefaultMenuItem frontEndFontsItem = new DefaultMenuItem(getSystemProperties()
        		.getProperty("frontEndFontsMenuLabel"));
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
















