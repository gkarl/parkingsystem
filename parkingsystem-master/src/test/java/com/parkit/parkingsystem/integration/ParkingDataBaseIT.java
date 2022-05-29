package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static final String vehicleRegNumber = "ABCDEF";

    private final static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static DataBasePrepareService dataBasePrepareService;

    @Spy
    private static ParkingSpotDAO parkingSpotDAO;

    @Spy
    private static TicketDAO ticketDAO;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    private ParkingService parkingService;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {
    }



    @DisplayName("Save ticket to DB and Parking table is updated with availability")
    @Test
    public void testParkingACar() {
        int numberOfNextAvailableSlot = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        Mockito.verify(ticketDAO).saveTicket(Mockito.any(Ticket.class));
        Mockito.verify(parkingSpotDAO).updateParking(Mockito.any(ParkingSpot.class));

        // check that a ticket is saved in DB
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
        assertNotNull(ticket);

        // check that Parking table is updated with availability
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR));
        assertFalse(ticket.getParkingSpot().isAvailable());

    }

    @Test
    public void testParkingLotExit(){
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
    }
}