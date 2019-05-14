package com.company.enroller.controllers;

import java.util.Collection;

import com.company.enroller.exceptions.ItemAlreadyExistException;
import com.company.enroller.security.UserProvider;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }

    // TODO refactor to use exceptions to simplify this method conditional statements
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
    	if (meetingService.get(meeting.getId()) != null)
    		return new ResponseEntity<>("Meeting already exist", HttpStatus.CONFLICT);
    	if (meetingService.add(meeting) == null) 
    		return new ResponseEntity<>("Unable to add meeting", HttpStatus.INTERNAL_SERVER_ERROR);
    	return new ResponseEntity<>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipant(@PathVariable("id") long meetingId) {
        String username = UserProvider.getUsername();
    	Participant participant = participantService.findByLogin(username);
    	Meeting meeting = meetingService.get(meetingId);
    	if (meeting == null) {
    		return new ResponseEntity<>("Meeting doesn't exist", HttpStatus.BAD_REQUEST);
    	}
        try {
            Meeting result = (Meeting) meetingService.addParticipant(meeting, participant);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (HibernateException e) {
            return new ResponseEntity<>("Unable to add participant", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ItemAlreadyExistException e) {
            return new ResponseEntity<>("Participant already enrolled", HttpStatus.CONFLICT);
        }
    }

    @RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipant(@PathVariable("id") long meetingId, @PathVariable("login") String login) {
        try {
            Meeting updatedMeeting = meetingService.removeParticipant(meetingId, login);
            return new ResponseEntity<>(updatedMeeting, HttpStatus.OK);
        } catch (HibernateException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") long meetingId) {
        if(!meetingService.get(meetingId).getParticipants().isEmpty())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
            meetingService.delete(meetingId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (HibernateException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
