package com.stolser;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import com.stolser.beans.FrontLocaleBean;

public class LifeCycleListener implements PhaseListener {

	private static final long serialVersionUID = 1L;
	
	//private FrontLocaleBean frontLocale;

	@Override
	public void afterPhase(PhaseEvent event) {
		System.out.println("FINISH phase " + event.getPhaseId());
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		System.out.println("START phase " + event.getPhaseId());
		//frontLocale = (FrontLocaleBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("frontLocale");
				
		
	}

	@Override
	public PhaseId getPhaseId() {
		
		return PhaseId.ANY_PHASE;
	}

}
