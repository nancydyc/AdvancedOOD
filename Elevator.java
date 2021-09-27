package AdvancedOOD;

import java.util.LinkedList;
import java.util.Queue;

/**
 * For elevator
 * getCurrentFloor() public 
 * moveUp() private
 * moveDown() private
 * moveToFloor() public
 * 
 * For passenger / button panel
 * sendRequest()
 * 
 * First Come First Serve
 * The elevator statuses:
 * 1. idle for passengers to load
 * 2. the same direction towards the way passenger want to go
 * 3. the same direction towards passesger but opposite to the way passenger want to go
 * 4. going away from the passenger
 * 
 * Shortest Path
 * 1. priority queue / minheap
 * 2. BST
 * 
 * Same Direction
 * A. Go to the end of one direction and go back
 * B. Look and see if there are requests ahead of its way. If no, not move.
 * Tips:
 * At office work peak hour, we want the elevator to be at 1st floor more.
 * 
*/

public class Elevator {
    private int curfloor;
    private E_Status status;
    private int destination;
    private Queue<Integer> requests; 
    
    public Elevator() {
    	curfloor = 0;
    	destination = 0;
        this.requests = new LinkedList<>(); 
    }

    void addRequest(int request) {
        requests.offer(request);
    }

    void removeRequest() {
        requests.poll();
    }
    
    private void processRequest() { // TODO: write in an independent class
    	while (!requests.isEmpty()) {
    		this.destination = requests.peek();
    		this.moveToFloor(destination);
    		this.removeRequest();
    	}
    }
    
    private void moveUp() {
    	System.out.println("Moving up------");
    }

    private void moveDown() {
    	System.out.println("Moving down-----");
    }

    public void moveToFloor(int destination) {
    	if (destination > this.curfloor) {
    		moveUp();
    	} else if (destination < this.curfloor) {
    		moveDown();
    	}
    	this.curfloor = destination;
    	System.out.println("Arrived " + destination + " floor.");
    	if (this.curfloor == destination) {
    		openDoor();
    	}
    }

    private void openDoor() {
    	System.out.println("Door opened.");
    }
    
    public static void main(String[] args) {
    	// Build elevator first, then send request
    	Elevator elevator = new Elevator();
    	Request test1 = new Request(5);
    	Request test2 = new Request(2);
        elevator.addRequest(test1.getFloor());
        elevator.addRequest(test2.getFloor());
    	// Manually call processRequest
    	elevator.processRequest();
    }
}

enum E_Status {
    IDLE,
    MOVING_UP,
    MOVING_DOWN;
}

//class Controller {
//	
//}

class Request { // button panel which receives passenger input
    private int floor;
    private Elevator elevator;
    public Request(int floor) {
        this.floor = floor;
    }
    
    public void sendRequest() {
    	this.elevator.addRequest(floor);
    }
    
    public int getFloor() {
        return this.floor;
    }
}
