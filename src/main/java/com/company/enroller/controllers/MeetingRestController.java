package com.company.enroller.controllers;

import java.util.Collection;

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
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST) 
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
    	if (meetingService.get(meeting.getId()) != null)
    		return new ResponseEntity<String>("Meeting already exist", HttpStatus.CONFLICT);
    	if (meetingService.add(meeting) == null) 
    		return new ResponseEntity<String>("Unable to add meeting", HttpStatus.INTERNAL_SERVER_ERROR);
    	return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.POST) 
    public ResponseEntity<?> addParticipant(@PathVariable("id") long meetingId, @RequestBody String participantLogin) {
    	Participant participant = participantService.findByLogin(participantLogin);
    	Meeting meeting = meetingService.get(meetingId);
    	if (meeting == null) {
    		return new ResponseEntity<String>("Meeting doesn't exist", HttpStatus.BAD_REQUEST);
    	}
    	if (!meeting.hasParticipant(participant)) {
            Meeting result = (Meeting) meetingService.addParticipant(meeting, participant);
            if (result == null) {
                return new ResponseEntity<String>("Unable to add participant", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<Meeting>(result, HttpStatus.CREATED);
        } else {
    	    // particpant already enrolled
            return new ResponseEntity<String>("Participant already enrolled", HttpStatus.CONFLICT);
        }
    }
}
