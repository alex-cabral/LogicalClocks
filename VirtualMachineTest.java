import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VirtualMachineTest {
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(5000);
	
	VirtualMachine vm = new VirtualMachine("localhost", 5000, 1);
	VirtualMachine vm2 = new VirtualMachine("localhost", 5000, 2);

	@Test
	// make sure the logic to determine who to send messages to works
	void testRecipient() {
		assertEquals(vm.getRecipientID(2),  3);
	}
	
	// make sure clock initializes at 0
	void testClock() {
		assertEquals(vm.getClock(), 0);
	}
	
	// make sure clock updates to a time greater than it is now and adds 1
	void testClockUpdate() {
		vm.updateLogicalClock(10);
		assertEquals(vm.getClock(), 11);
	}
	
	// make sure clock updates when passed in equal time
	void testClockUpdate2() {
		vm.updateLogicalClock(11);
		assertEquals(vm.getClock(), 12);
	}
	
	// make sure clock updates after getting a message with a higher time than itself
	void testSendMessage1() {
		vm.sendMessage(2);
		assertEquals(vm2.getClock(), 13);
	}
	
	// make sure clock doesn't update after getting a message with a lower time than itself
	void testSendMessage2() {
		vm2.sendMessage(1);
		assertEquals(vm.getClock(), 10);
	}

}
