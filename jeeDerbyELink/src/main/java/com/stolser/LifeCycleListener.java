package com.stolser;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LifeCycleListener implements PhaseListener {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(LifeCycleListener.class);

	@Override
	public void afterPhase(PhaseEvent event) {
		logger.trace("FINISH phase {}.", event.getPhaseId());
	}

	@Override
	public void beforePhase(PhaseEvent event) {
		logger.trace("START phase {}.", event.getPhaseId());
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

}
