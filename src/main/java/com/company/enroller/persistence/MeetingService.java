package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

@Component("meetingService")
public class MeetingService {

    DatabaseConnector connector;

	@Autowired
    private ParticipantService participantService;

    public MeetingService() {
        connector = DatabaseConnector.getInstance();
    }

    public Collection<Meeting> getAll() {
        String hql = "FROM Meeting";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }
    
    public Meeting get(long meetingId) {
    	return (Meeting) connector.getSession().get(Meeting.class, meetingId);
    }

	public Object add(Meeting meeting) {
		try {
			Transaction transaction = connector.getSession().beginTransaction();
			connector.getSession().save(meeting);
			transaction.commit();
			return meeting;
		} catch (HibernateException e) {
			return null;
		}
	}

	public Object addParticipant(Meeting meeting, Participant participant) {
		try {
			meeting.addParticipant(participant);
			Transaction transaction = connector.getSession().beginTransaction();
			connector.getSession().save(meeting);
			transaction.commit();
			return meeting;
		} catch (HibernateException e) {
			return null;
		}
	}

	public Meeting delete(long meetingId, String login) throws HibernateException {
    	Meeting meeting = this.get(meetingId);
		Participant participant = participantService.findByLogin(login);
		Transaction transaction = connector.getSession().beginTransaction();
		meeting.removeParticipant(participant);
    	connector.getSession().update(meeting);
    	transaction.commit();

    	return meeting;
	}

}
