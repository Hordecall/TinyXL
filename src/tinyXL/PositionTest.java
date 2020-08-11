package tinyXL;

import static org.junit.Assert.*;

import org.junit.Test;

public class PositionTest {

	@Test
	public void testEquality() {
		Cell cell = new Cell(1,1, new Line[2]);
		Position p = new Position(1,1, true);
		
		assertEquals(true,p.isEqualTo(new Position(cell.getLineID(), cell.getCellID(), true)));
	}

}
