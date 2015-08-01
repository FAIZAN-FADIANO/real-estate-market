package com.stolser.user;

import java.util.Calendar;
import com.stolser.jpa.Admin;
import com.stolser.jpa.Realtor;
import com.stolser.jpa.RegisteredUser;
import com.stolser.jpa.User;

public final class UserFactory {
	private UserFactory() {}
	
	public static Admin createAdmin() {
		Admin newAdmin = new Admin();
		newAdmin.setType(User.UserType.ADMIN);
		newAdmin.setStatus(User.UserStatusType.ACTIVE);
		newAdmin.setDateOfCreation(Calendar.getInstance().getTime());
		newAdmin.setPhoto(getUserDefaultPhotoPath());
		
		return newAdmin;
	}
	
	public static Realtor createRealtor() {
		Realtor newRealtor = new Realtor();
		newRealtor.setType(User.UserType.REALTOR);
		newRealtor.setStatus(User.UserStatusType.ACTIVE);
		newRealtor.setDateOfCreation(Calendar.getInstance().getTime());
		newRealtor.setPhoto(getUserDefaultPhotoPath());
		
		return newRealtor;
	}
	
	public static RegisteredUser createRegisteredUser() {
		RegisteredUser newRegisteredUser = new RegisteredUser();
		newRegisteredUser.setType(User.UserType.REGISTERED_USER);
		newRegisteredUser.setStatus(User.UserStatusType.ACTIVE);
		newRegisteredUser.setDateOfCreation(Calendar.getInstance().getTime());
		newRegisteredUser.setPhoto(getUserDefaultPhotoPath());
		
		return newRegisteredUser;
	}
	
	private static String getUserDefaultPhotoPath() {
		String userPhotoPath = "/images/unknownUser.jpg";
		return userPhotoPath;
	}

}
